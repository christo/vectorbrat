package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;

import static com.chromosundrift.vectorbrat.Config.SAMPLE_MIN;
import static com.chromosundrift.vectorbrat.Config.SAMPLE_RANGE;

public class TextEngine {

    private final Color color;
    private final Typeface typeface;

    public TextEngine(Color color, Typeface typeface) {
        this.color = color;
        this.typeface = typeface;
    }

    /**
     * Creates a model with text occupying the full extent of the model range in x and y as defined by
     * {@link com.chromosundrift.vectorbrat.Config#SAMPLE_MIN} and
     * {@link com.chromosundrift.vectorbrat.Config#SAMPLE_MAX}.
     * Unsupported characters are replaced with X in a box.
     * <p>
     * TODO whole call graph is unoptimised and quite dumb
     *
     * @param text the text to render.
     * @return the model containing the text.
     */
    public Model textLine(String text) {
        if (text.length() < 1 || text.contains("\n") || text.contains("\r")) {
            throw new IllegalArgumentException("text must not be empty or contain newlines or carriage returns");
        }
        char[] chars = text.toCharArray();

        // calculate spacing
        float[] gaps = new float[chars.length];
        gaps[0] = 0f;
        float spaceSpace = 0f;
        for (int i = 1; i < chars.length; i++) {
            float gap = typeface.gap(chars[i - 1], chars[i]);
            spaceSpace += gap;
            gaps[i] = gap;
        }
        // letters same width for now, but kerning is defined by typeface
        // character width in sample units
        float charWidth = (SAMPLE_RANGE - spaceSpace) / chars.length;

        // TODO move anything out of loop that doesn't depend on i
        Model m = new GlobalModel();
        for (int i = 0; i < chars.length; i++) {
            float offset = (SAMPLE_MIN + (gaps[i] + charWidth) * i);

            float charScale = charWidth / SAMPLE_RANGE;
            Model charModel = typeface.getChar(chars[i]);
            Model charInSitu = charModel.scale(charScale, 1.0f).offset(offset, 0f);
            m = m.merge(charInSitu);
        }

        return m.colored(this.color);
    }
}
