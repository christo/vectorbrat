package com.chromosundrift.vectorbrat.audio;

import org.jaudiolibs.audioservers.AudioClient;
import org.jaudiolibs.audioservers.AudioConfiguration;
import org.jaudiolibs.audioservers.AudioServer;
import org.jaudiolibs.audioservers.AudioServerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.util.List;

/**
 * Aphasic pair of oscillators over two channels.
 */
public class LissajouClient implements AudioClient {


    private static final Logger logger = LoggerFactory.getLogger(LissajouClient.class);
    private static final float MIN_FREQ = 0.1f;
    private static final float MAX_FREQ = 20000f;

    public static void main(String[] args) throws Exception {
        logger.info("starting");
        ServiceBridge sb = new ServiceBridge(ServiceBridge.JACK);
        AudioConfiguration audioConfig = ServiceBridge.getStereoAudioConfiguration();
        AudioClient client = new LissajouClient(220.0f, 220.0f * 1.01f, 0.3);
        AudioServerProvider provider = sb.getProvider();
        final AudioServer server = provider.createServer(audioConfig, client);
        ServerRunner runner = new ServerRunner(server);
        runner.start();
        System.out.println("hit enter to stop");
        int ignore = System.in.read();
        runner.stop();
        logger.info("finishing");
    }

    private float freqL;
    private float freqR;
    private final int xChannelIndex;
    private final int yChannelData;
    private boolean run;
    private float[] dataLeft;
    private float[] dataRight;
    private float[] bufferLeft;
    private float[] bufferRight;
    private int idxLeft;
    private int idxRight;
    private double amp;

    public LissajouClient(float freqL, float freqR, double amp) {
        this.freqL = clampFreq(freqL);
        this.freqR = clampFreq(freqR);
        this.amp = amp;
        xChannelIndex = 0;
        yChannelData = 1;
        run = true;
    }

    private static float clampFreq(float freqL) {
        return Math.min(Math.max(MIN_FREQ, freqL), MAX_FREQ);
    }

    public void stop() {
        run = false;
    }

    public float getFreqL() {
        return freqL;
    }

    public void setFreqL(float freqL) {
        this.freqL = clampFreq(freqL);
    }

    public float getFreqR() {
        return freqR;
    }

    public void setFreqR(float freqR) {
        this.freqR = clampFreq(freqR);
    }

    public double getAmp() {
        return amp;
    }

    public void setAmp(double amp) {
        this.amp = amp;
    }

    public void configure(AudioConfiguration context)  {
        logger.info("output channels: " + context.getOutputChannelCount());
        logger.info("input channels: " + context.getInputChannelCount());
        float sampleRate = context.getSampleRate();
        logger.info("sample rate: " + sampleRate);
        dataLeft = waveData(sampleRate, freqL);
        dataRight = waveData(sampleRate, freqR);
    }

    private float[] waveData(float sampleRate, float freq) {
        int framesPerCycle = (int) (sampleRate / freq);
        float[] data = new float[framesPerCycle];
        for (int i = 0; i < framesPerCycle; i++) {
            double rads = (double) i / (double) framesPerCycle;
            float sample = (float) (amp * Math.sin(rads * Math.TAU));
            data[i] = sample;
        }
        return data;
    }

    public boolean process(long time, List<FloatBuffer> inputs, List<FloatBuffer> outputs, int nframes) {
        FloatBuffer xBuffer = outputs.get(xChannelIndex);
        FloatBuffer yBuffer = outputs.get(yChannelData);

        if (bufferLeft == null || bufferLeft.length != nframes) {
            bufferLeft = new float[nframes];
        }
        if (bufferRight == null || bufferRight.length != nframes) {
            bufferRight = new float[nframes];
        }

        for (int i = 0; i < nframes; i++) {
            bufferLeft[i] = dataLeft[idxLeft];
            idxLeft++;
            if (idxLeft == dataLeft.length) {
                idxLeft = 0;
            }

            bufferRight[i] = dataRight[idxRight];
            idxRight++;
            if (idxRight == dataRight.length) {
                idxRight = 0;
            }
        }

        xBuffer.put(bufferLeft);
        yBuffer.put(bufferRight);

        return run;
    }

    public void shutdown() {
        //dispose resources.
        dataLeft = null;
    }


}
