package com.chromosundrift.vectorbrat.geom;

import com.chromosundrift.vectorbrat.Util;

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
        this.red = Util.clampNormal(red);
        this.green = Util.clampNormal(green);
        this.blue = Util.clampNormal(blue);
    }

}
