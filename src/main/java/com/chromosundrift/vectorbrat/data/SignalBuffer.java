package com.chromosundrift.vectorbrat.data;

import com.chromosundrift.vectorbrat.geom.Pather;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.Rgb;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;

/**
 * Concrete, reusable, performant container for 5-dimensional vector signal stream containing component
 * floats for x, y (bipolar normalised range -1 to 1) and r, g, b (unipolar normalised range 0-1). Not
 * threadsafe. Not immutable.
 */
@NotThreadSafe
public final class SignalBuffer {

    // currently implemented as 5 parallel float buffers

    private final int maxSize;
    private final float[] xBuffer;
    private final float[] yBuffer;
    private final float[] rBuffer;
    private final float[] gBuffer;
    private final float[] bBuffer;

    /**
     * Actual number of entries, may be between 0 and the maximum size. Changed by calls to {@link #fillPath(Pather)} by
     * inheriting its contents.
     */
    private int actualSize;

    public SignalBuffer(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("size must be greater than zero");
        }
        this.maxSize = maxSize;
        xBuffer = new float[maxSize];
        yBuffer = new float[maxSize];
        rBuffer = new float[maxSize];
        gBuffer = new float[maxSize];
        bBuffer = new float[maxSize];
        // start empty
        this.actualSize = maxSize;
    }

    /**
     * Copy the path from the pather into this buffer up to our size. Silently ignores excess data from pather that
     * doesn't fit. Actual size is updated to reflect the number of signal points from pather.
     *
     * @param p the pather to get the data from.
     * @return the number of entries filled. Any data at or beyond this index value is undefined.
     */
    @SuppressWarnings("DuplicatedCode")
    public int fillPath(Pather p) {
        ArrayList<Float> xs = p.getXs();
        ArrayList<Float> ys = p.getYs();
        ArrayList<Float> rs = p.getRs();
        ArrayList<Float> gs = p.getGs();
        ArrayList<Float> bs = p.getBs();
        // set our actual size based on the pather up to our maxSize
        actualSize = Math.min(maxSize, p.size());

        for (int i = 0; i < this.actualSize; i++) {
            xBuffer[i] = xs.get(i);
            yBuffer[i] = ys.get(i);
            rBuffer[i] = rs.get(i);
            gBuffer[i] = gs.get(i);
            bBuffer[i] = bs.get(i);
        }
        return actualSize;
    }

    /**
     * Update all buffer values at given index. No bounds checking done.
     *
     * @param x x value.
     * @param y y value.
     * @param r red value.
     * @param g green value.
     * @param b blue value.
     * @param i index must be > 0 and no greater than maxSize - 1
     */
    public void set(float x, float y, float r, float g, float b, int i) {
        xBuffer[i] = x;
        yBuffer[i] = y;
        rBuffer[i] = r;
        gBuffer[i] = g;
        bBuffer[i] = b;
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
     *
     * @param i the index
     * @return the Rgb colour.
     */
    public Rgb getRgb(int i) {
        return new Rgb(getR(i), getG(i), getB(i));
    }

    public int getActualSize() {
        return this.actualSize;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    /**
     * Fill with zeroes and set actualSize to maxSize.
     */
    public void reset() {
        for (int i = 0; i < maxSize; i++) {
            set(0, 0, 0, 0, 0, i);
        }
        actualSize = maxSize;
    }

    /**
     * Constructs a coloured Point for the signal at the given index.
     *
     * @param i the index.
     * @return a new {@link Point}.
     * @throws ArrayIndexOutOfBoundsException if the index is negative or greater than or equal to the size.
     */
    public Point toPoint(int i) {
        return new Point(getX(i), getY(i), getRgb(i));
    }

    /**
     * Sets the actual size to something not exceeding the max size.
     *
     * @param actualSize the desired size.
     * @return the new actual size.
     */
    public int setActualSize(int actualSize) {
        this.actualSize = Math.min(this.maxSize, actualSize);
        return actualSize;
    }
}
