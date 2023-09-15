package com.chromosundrift.vectorbrat.anim;

import com.chromosundrift.vectorbrat.geom.Model;

import javax.annotation.Nonnull;

public class BungeeAnimator extends AbstractAnimator {

    private final int msPeriod;
    private final float amplitude;
    private final Model model;
    private final float offset;

    /**
     * Sinusoidal low frequency oscillator that scales the model using the given period and amplitude.
     * Negative amplitudes are equivalent to a 50% phase shift. Resulting animation will scale the given
     * model between 0 and amplitude.
     *
     * @param m         the model to animate
     * @param msPeriod  period of one cycle in milliseconds - must be greater than zero
     * @param amplitude amplitude in model units
     */
    public BungeeAnimator(@Nonnull Model m, int msPeriod, float amplitude, float offset) {
        super(m.getName());
        this.offset = offset;
        if (msPeriod <= 0) {
            throw new IllegalArgumentException("period was not greater than zero: " + msPeriod);
        }
        this.model = m;
        this.msPeriod = msPeriod;
        this.amplitude = amplitude;
    }

    @Override
    public Model update(long nsTime) {
        float scale = calculateScale(nsTime);
        return model.scale(scale, scale);
    }

    float calculateScale(long nsTime) {
        long msCycleTime = (nsTime / 1000000) % msPeriod;
        double theta = msCycleTime * Math.TAU / msPeriod;
        double normalised = (Math.sin(theta) + 1) / 2;
        return (float) (normalised * amplitude) + offset;
    }
}
