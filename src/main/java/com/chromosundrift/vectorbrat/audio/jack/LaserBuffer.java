package com.chromosundrift.vectorbrat.audio.jack;

import org.jaudiolibs.jnajack.JackClient;

import java.nio.FloatBuffer;

public class LaserBuffer {

    private float[] xBuffer;
    private float[] yBuffer;
    private float[] rBuffer;
    private float[] gBuffer;
    private float[] bBuffer;
    private float[] xData;
    private float[] yData;
    private float[] rData;
    private float[] gData;
    private float[] bData;
    private int xi;
    private int yi;
    private int ri;
    private int gi;
    private int bi;

    @SuppressWarnings("DuplicatedCode")
    private void copyBuffers(JackClient client,
                             FloatBuffer xb,
                             FloatBuffer yb,
                             FloatBuffer rb,
                             FloatBuffer gb,
                             FloatBuffer bb,
                             int nframes) {


        if (xBuffer == null || xBuffer.length != nframes) {
            xBuffer = new float[nframes];
        }
        if (yBuffer == null || yBuffer.length != nframes) {
            yBuffer = new float[nframes];
        }
        if (rBuffer == null || rBuffer.length != nframes) {
            rBuffer = new float[nframes];
        }
        if (gBuffer == null || gBuffer.length != nframes) {
            gBuffer = new float[nframes];
        }
        if (bBuffer == null || bBuffer.length != nframes) {
            bBuffer = new float[nframes];
        }

        for (int i = 0; i < nframes; i++) {
            xBuffer[i] = xData[xi];
            xi++;
            if (xi == xData.length) {
                xi = 0;
            }
            yBuffer[i] = yData[yi];
            yi++;
            if (yi == yData.length) {
                yi = 0;
            }
            rBuffer[i] = rData[ri];
            ri++;
            if (ri == rData.length) {
                ri = 0;
            }
            gBuffer[i] = gData[ri];
            gi++;
            if (gi == gData.length) {
                gi = 0;
            }
            bBuffer[i] = bData[ri];
            ri++;
            if (bi == bData.length) {
                bi = 0;
            }
            xb.put(xBuffer);
            yb.put(yBuffer);
            rb.put(rBuffer);
            gb.put(gBuffer);
            bb.put(bBuffer);
        }

    }
}
