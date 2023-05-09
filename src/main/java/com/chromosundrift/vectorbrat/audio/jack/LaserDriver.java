package com.chromosundrift.vectorbrat.audio.jack;

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

import java.util.EnumSet;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.VectorBratException;

public class LaserDriver {

    public static final EnumSet<JackStatus> NO_STATUS = EnumSet.noneOf(JackStatus.class);
    public static final EnumSet<JackOptions> NO_OPTIONS = EnumSet.noneOf(JackOptions.class);
    public static final EnumSet<JackPortFlags> PHYSICAL_JACK_INPUTS = EnumSet.of(JackPortFlags.JackPortIsInput, JackPortFlags.JackPortIsPhysical);

    private static final Logger logger = LoggerFactory.getLogger(LaserDriver.class);
    private static final EnumSet<JackPortFlags> TO_JACK = EnumSet.of(JackPortFlags.JackPortIsInput);
    private final JackPort xPort;
    private final JackPort yPort;
    private final JackPort rPort;
    private final JackPort gPort;
    private final JackPort bPort;
    private final Config config;
    private volatile boolean running = false;
    private JackClient client;

    /**
     * Uses the config but doesn't subscribe to all updates.
     * @param config the config
     * @throws VectorBratException if setup fails.
     */
    public LaserDriver(Config config) throws VectorBratException {
        logger.info("initialising laser driver");
        this.config = config;
        String title = config.getTitle();
        Config.Channel channelX = config.getChannelX();
        Config.Channel channelY = config.getChannelY();
        Config.Channel channelR = config.getChannelR();
        Config.Channel channelG = config.getChannelG();
        Config.Channel channelB = config.getChannelB();

        Jack jack = getJack();

        try {
            int maxName = jack.getMaximumPortNameSize();
            client = openClient(jack, title);


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

    private void connect() throws VectorBratException {
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
        try {
            logger.info("activating client");
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
            client.setProcessCallback((client, nframes) -> {
                // TODO
                return false;
            });
        } catch (JackException e) {
            throw new VectorBratException("can't register callback", e);
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
        logger.info("registering jack port for channel " + channel);
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

}
