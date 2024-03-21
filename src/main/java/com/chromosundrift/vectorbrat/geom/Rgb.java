package com.chromosundrift.vectorbrat.geom;

import com.chromosundrift.vectorbrat.Util;
import com.chromosundrift.vectorbrat.data.Maths;

/**
 * Implementation of colour value with red, green and blue components specified in the range from 0f to 1f.
 * If the supplied component parameters are outside the range of 0f-1f, the nearest valid value will be used.
 *
 * @param red   red component.
 * @param green green component.
 * @param blue  blue component.
 */
public record Rgb(float red, float green, float blue) {

    public static final Rgb BLACK = new Rgb(0f, 0f, 0f);
    public static final Rgb WHITE = new Rgb(1f, 1f, 1f);
    public static final Rgb RED = new Rgb(1f, 0f, 0f);
    public static final Rgb GREEN = new Rgb(0f, 1f, 0f);
    public static final Rgb BLUE = new Rgb(0f, 0f, 1f);
    public static final Rgb CYAN = new Rgb(0f, 1f, 1f);
    public static final Rgb MAGENTA = new Rgb(1f, 0f, 1f);
    public static final Rgb YELLOW = new Rgb(1f, 1f, 0f);
    public static final Rgb GREY = new Rgb(0.5f, 0.5f, 0.5f);
    public static final Rgb LIGHT_GREY = new Rgb(0.75f, 0.75f, 0.75f);
    public static final Rgb DARK_GREY = new Rgb(0.25f, 0.25f, 0.25f);
    public static final Rgb ORANGE = new Rgb(1f, 0.75f, 0f);

    public Rgb(float red, float green, float blue) {
        this.red = Maths.clamp0to1(red);
        this.green = Maths.clamp0to1(green);
        this.blue = Maths.clamp0to1(blue);
    }

    public static Rgb boundedLerp(float demandR, float demandG, float demandB, long nsTimeStep, float colourRate1, Rgb rgb) {
        float maxColorDelta = colourRate1 * nsTimeStep / Util.NANOS_F;
        float r = rgb.red() + Math.min(demandR - rgb.red(), maxColorDelta);
        float g = rgb.green() + Math.min(demandG - rgb.green(), maxColorDelta);
        float b = rgb.blue() + Math.min(demandB - rgb.blue(), maxColorDelta);
        if (r < 0 || r > 1 || g < 0 || g > 1 || b < 0 || b > 1) {
            throw new IllegalStateException("Invalid beam colour: %s, %s, %s".formatted(r, g, b));
        }
        Rgb newRgb = new Rgb(r, g, b);
        return newRgb;
    }

    /**
     * Blends this colour with the other using multiply mode. Resulting colour will always be darker.
     *
     * @param other the colour to multiply with.
     * @return the product colour.
     */
    public Rgb multiply(Rgb other) {
        // avoid constructing objects for degenerate cases
        if (other.equals(WHITE)) {
            return this;
        } else if (this == WHITE) {
            return other;
        } else if (other.equals(BLACK) || this.equals(BLACK)) {
            return BLACK;
        } else {
            return new Rgb(red * other.red, green * other.green, blue * other.blue);
        }
    }

    /**
     * Returns a normalised result of multiplying r, g, b components by the given factor. Colours can
     * be brightened by multiplication by factors greater than 1f however resulting components are
     * clamped between 0f-1f.
     *
     * @param f the factor.
     * @return normalised colour.
     */
    public Rgb multiply(float f) {
        if (f == 0f) {
            return BLACK;
        } else if (f == 1f) {
            return this;
        } else {
            return new Rgb(red * f, green * f, blue * f);
        }
    }

    /**
     * Screen blend mode f(a,b)=1-(1-a)(1-b)
     *
     * @param other the other colour to screen with
     * @return the resulting colour.
     */
    public Rgb screen(Rgb other) {
        if (other == WHITE) {
            return this;
        } else if (this == WHITE) {
            return other;
        } else {
            float newRed = 1f - (1f - red) * (1f - other.red);
            float newGreen = 1f - (1f - green) * (1f - other.green);
            float newBlue = 1f - (1f - blue) * (1f - other.blue);
            return new Rgb(newRed, newGreen, newBlue);
        }
    }

    public Rgb invert() {
        if (this.equals(WHITE)) {
            return BLACK;
        } else if (this.equals(BLACK)) {
            return WHITE;
        } else {
            return new Rgb(1f - red, 1f - green, 1f - blue);
        }
    }

    public float maxDelta(Rgb to) {
        float deltaRed = Math.abs(this.red() - to.red());
        float deltaGreen = Math.abs(this.green() - to.green());
        float deltaBlue = Math.abs(this.blue() - to.blue());
        return Math.min(deltaRed, Math.min(deltaGreen, deltaBlue));
    }

    @Override
    public String toString() {
        return "RGB(%.2f, %.2f, %.2f)".formatted(red, green, blue);
    }
}
