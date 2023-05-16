package com.chromosundrift.vectorbrat.laser;

import org.jaudiolibs.jnajack.Jack;
import org.jaudiolibs.jnajack.JackClient;
import org.jaudiolibs.jnajack.JackException;
import org.jaudiolibs.jnajack.JackOptions;
import org.jaudiolibs.jnajack.JackPort;
import org.jaudiolibs.jnajack.JackPortFlags;
import org.jaudiolibs.jnajack.JackPortType;
import org.jaudiolibs.jnajack.JackStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.locks.ReentrantLock;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.geom.PathPlanner;

/**
 * Handles generation of audio buffers and connection to audio subsystem. Also provides nanosecond time from
 * audio device. Uses Jack and requires the jack server to be running.
 */
public final class LaserDriver {

    public static final EnumSet<JackStatus> NO_STATUS = EnumSet.noneOf(JackStatus.class);
    public static final EnumSet<JackOptions> NO_OPTIONS = EnumSet.noneOf(JackOptions.class);
    public static final EnumSet<JackPortFlags> PHYSICAL_JACK_INPUTS = EnumSet.of(
            JackPortFlags.JackPortIsInput,
            JackPortFlags.JackPortIsPhysical
    );
    private static final Logger logger = LoggerFactory.getLogger(LaserDriver.class);
    private static final EnumSet<JackPortFlags> TO_JACK = EnumSet.of(JackPortFlags.JackPortIsOutput);

    private final JackPort xPort;
    private final JackPort yPort;
    private final JackPort rPort;
    private final JackPort gPort;
    private final JackPort bPort;

    private final Config config;

    private final JackClient client;

    private volatile boolean running = false;

    private final ReentrantLock bufferLock = new ReentrantLock();

    private float[] xBuffer;
    private float[] yBuffer;
    private float[] rBuffer;
    private float[] gBuffer;
    private float[] bBuffer;

    private int index = 0;

    /**
     * Whether laser light and galvo motion should be on or off.
     */
    private boolean isOn;
    private int sampleRate;

    /**
     * Uses the config but doesn't subscribe to all updates.
     *
     * @param config the config
     * @throws VectorBratException if setup fails.
     */
    public LaserDriver(Config config) throws VectorBratException {
        logger.info("initialising LaserDriver");
        this.config = config;

        Config.Channel channelX = config.getChannelX();
        Config.Channel channelY = config.getChannelY();
        Config.Channel channelR = config.getChannelR();
        Config.Channel channelG = config.getChannelG();
        Config.Channel channelB = config.getChannelB();

        Jack jack = getJack();

        try {
            int maxName = jack.getMaximumPortNameSize();
            client = openClient(jack, config.getTinyTitle());

            xPort = registerPort(channelX, client, maxName);
            yPort = registerPort(channelY, client, maxName);
            rPort = registerPort(channelR, client, maxName);
            gPort = registerPort(channelG, client, maxName);
            bPort = registerPort(channelB, client, maxName);

            client.onShutdown(c -> {
                running = false;
                logger.info("Jack shutdown initiated for client " + c.getName());
            });

        } catch (JackException e) {
            throw new VectorBratException("problem with jack", e);
        }
    }

    private static Jack getJack() throws VectorBratException {
        try {
            return Jack.getInstance();
        } catch (JackException e) {
            throw new VectorBratException("cannot get jack instance", e);
        }
    }

    private static JackClient openClient(Jack jack, String title) throws VectorBratException {
        try {
            return jack.openClient(truncate(title, jack.getMaximumClientNameSize()), NO_OPTIONS, NO_STATUS);
        } catch (JackException e) {
            throw new VectorBratException("jack spewed trying to open client", e);
        }
    }

    private static JackPort registerPort(Config.Channel channel, JackClient client, int maxPortName) throws VectorBratException {
        logger.info("registering jack port for " + channel);
        String name = truncate(channel.name(), maxPortName);
        try {
            return client.registerPort(name, JackPortType.AUDIO, TO_JACK);
        } catch (JackException e) {
            throw new VectorBratException("died trying to register port for channel " + channel, e);
        }
    }

    private static String truncate(String name, int maxLen) {
        return name.substring(0, Math.min(name.length() - 1, maxLen - 1));
    }

    /**
     * Blocking call to begin laser. Call {@link this#stop()} to request shutdown.
     *
     * @throws VectorBratException whenever underlying resource barfs.
     */
    public void start() throws VectorBratException {
        logger.info("starting laser driver");
        registerProcessCallback();
        activateClient();
        running = true;
        connect();
    }

    public void stop() {
        this.running = false;
    }

    private void connect() throws VectorBratException {
        logger.info("connecting");
        Jack jack = getJack();

        try {
            // only need output ports for now
            String[] toJack = jack.getPorts(client, null, JackPortType.AUDIO, PHYSICAL_JACK_INPUTS);
            jack.connect(client, xPort.getName(), toJack[config.getChannelX().number() - 1]);
            jack.connect(client, yPort.getName(), toJack[config.getChannelY().number() - 1]);
            jack.connect(client, rPort.getName(), toJack[config.getChannelR().number() - 1]);
            jack.connect(client, gPort.getName(), toJack[config.getChannelG().number() - 1]);
            jack.connect(client, bPort.getName(), toJack[config.getChannelB().number() - 1]);
        } catch (JackException e) {
            throw new VectorBratException("can't connect jack ports", e);
        }
        logger.info("connecting to server");
    }

    private void activateClient() throws VectorBratException {
        logger.info("activating");
        try {
            client.activate();
        } catch (JackException e) {
            throw new VectorBratException("can't activate client", e);
        }
    }

    private void registerProcessCallback() throws VectorBratException {
        try {
            logger.info("registering process callback");
            int sampleRate = client.getSampleRate();
            logger.info("Sample rate: " + sampleRate);
            int bufferSize = client.getBufferSize();
            logger.info("Buffer size = " + bufferSize);
            client.setProcessCallback(this::process);
        } catch (JackException e) {
            throw new VectorBratException("can't register callback", e);
        }
    }

    private boolean process(JackClient client, int nframes) {
        try {
            FloatBuffer xBuffer1 = xPort.getFloatBuffer();
            FloatBuffer yBuffer1 = yPort.getFloatBuffer();
            FloatBuffer rBuffer1 = rPort.getFloatBuffer();
            FloatBuffer gBuffer1 = gPort.getFloatBuffer();
            FloatBuffer bBuffer1 = bPort.getFloatBuffer();

            if (this.isOn && xBuffer != null && yBuffer != null && rBuffer != null && bBuffer != null) {
                sampleRate = client.getSampleRate();
                //int framesPerSecond = sampleRate / nframes;
                try {
                    bufferLock.lock();
                    for (int i = 0; i < nframes; i++) {
                        if (index >= xBuffer.length) {
                            index = 0;
                        }
                        xBuffer1.put(i, xBuffer[index]); // TODO throws ArrayIndexOutOfBoundsException on model change
                        yBuffer1.put(i, yBuffer[index]);
                        rBuffer1.put(i, rBuffer[index]);
                        gBuffer1.put(i, gBuffer[index]);
                        bBuffer1.put(i, bBuffer[index]);
                        index++;

                    }
                } finally {
                    bufferLock.unlock();
                }

            } else {
                // black silence
                for (int i = 0; i < nframes; i++) {
                    xBuffer1.put(i, 0f);
                    yBuffer1.put(i, 0f);
                    rBuffer1.put(i, 0f);
                    gBuffer1.put(i, 0f);
                    bBuffer1.put(i, 0f);
                }
            }
            return running;
        } catch (JackException e) {
            logger.error("exception thrown during process callback", e);
            this.running = false;
        }
        return running;
    }

    /**
     * Called from ui thread.
     *
     * @return whether we are armed.
     */
    public boolean isOn() {
        return isOn;
    }

    /**
     * Called from ui thread.
     *
     * @param on whether we are armed.
     */
    public void setOn(boolean on) {
        isOn = on;
        logger.info("laser %s".formatted(isOn ? "armed" : "safe"));
    }

    public float getSampleRate() {
        return this.sampleRate;
    }

    public int getBufferSize() {
        if (running) {
            try {
                return client.getBufferSize();
            } catch (JackException ignored) {

            }
        }
        return -1;

    }

    public void setPathPlanner(PathPlanner p) {
        ArrayList<Float> xs = p.getXs();
        ArrayList<Float> ys = p.getYs();
        ArrayList<Float> rs = p.getRs();
        ArrayList<Float> gs = p.getGs();
        ArrayList<Float> bs = p.getBs();
        int size = xs.size();
        float[] bx = new float[size];
        float[] by = new float[size];
        float[] br = new float[size];
        float[] bg = new float[size];
        float[] bb = new float[size];
        for (int i = 0; i < size; i++) {
            bx[i] = xs.get(i);
            by[i] = ys.get(i);
            br[i] = rs.get(i);
            bg[i] = gs.get(i);
            bb[i] = bs.get(i);
        }
        try {
            bufferLock.lock();

            this.xBuffer = bx;
            this.yBuffer = by;
            this.rBuffer = br;
            this.gBuffer = bg;
            this.bBuffer = bb;
        } finally {
            bufferLock.unlock();
        }

    }
}
