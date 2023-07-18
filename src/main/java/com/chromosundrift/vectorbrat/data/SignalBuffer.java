package com.chromosundrift.vectorbrat.data;

import java.util.ArrayList;

import com.chromosundrift.vectorbrat.geom.Pather;
import com.chromosundrift.vectorbrat.geom.Rgb;

/**
 * Concrete, reusable, performant container for 5-dimensional vector signal stream containing component
 * floats for x, y (bipolar normalised range -1 to 1) and r, g, b (unipolar normalised range 0-1). Not
 * threadsafe. Not immutable.
 */
public final class SignalBuffer {

    // currently implemented as 5 parallel float buffers

    private final int size;
    private final float[] xBuffer;
    private final float[] yBuffer;
    private final float[] rBuffer;
    private final float[] gBuffer;
    private final float[] bBuffer;

    public SignalBuffer(int size) {
        this.size = size;
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than zero");
        }
        xBuffer = new float[size];
        yBuffer = new float[size];
        rBuffer = new float[size];
        gBuffer = new float[size];
        bBuffer = new float[size];
    }

    /**
     * Copy the path from the pather into this buffer up to our size. Silently ignores excess data from pather that
     * doesn't fit.
     *
     * @param p the pather to get the data from.
     * @return `the number of entries filled. Data beyond this value is junk.
     */
    public int fillPath(Pather p) {
        ArrayList<Float> xs = p.getXs();
        ArrayList<Float> ys = p.getYs();
        ArrayList<Float> rs = p.getRs();
        ArrayList<Float> gs = p.getGs();
        ArrayList<Float> bs = p.getBs();
        int s = xs.size();

        int length = Math.min(size, s);
        for (int i = 0; i < length; i++) {
            xBuffer[i] = xs.get(i);
            yBuffer[i] = ys.get(i);
            rBuffer[i] = rs.get(i);
            gBuffer[i] = gs.get(i);
            bBuffer[i] = bs.get(i);
        }
        return length;
    }

    public float getX(int i) {
        return xBuffer[i];
    }

    public float getY(int i) {
        return yBuffer[i];
    }

    public float getR(int i) {
        return rBuffer[i];
    }

    public float getG(int i) {
        return gBuffer[i];
    }

    public float getB(int i) {
        return bBuffer[i];
    }

    /**
     * Constructs an {@link Rgb} object for the given index using the internal r, g and b buffers.
     * @param i the index
     * @return the Rgb colour.
     */
    public Rgb getRgb(int i) {
        return new Rgb(getR(i), getG(i), getB(i));
    }
}
