package com.chromosundrift.vectorbrat.geom;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static com.chromosundrift.vectorbrat.Config.SAMPLE_MAX;
import static com.chromosundrift.vectorbrat.Config.SAMPLE_MIN;
import static com.chromosundrift.vectorbrat.Config.SAMPLE_RANGE;

import com.chromosundrift.vectorbrat.Config;

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
     *
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
        float gap = typeface.gap('m', 'm');
        float charWidth = SAMPLE_RANGE / chars.length;
        Model m = new GlobalModel(""); // merging will merge model name ltr
        for (int i = 0; i < chars.length; i++) {
            // TODO fix gap
            float offset = SAMPLE_MIN + (i * (charWidth + gap));
            if (i != 0) {
                offset += gap;
            }
            Model charModel = typeface.getChar(chars[i]);
            m = m.merge(charModel.scale((charWidth - gap)/SAMPLE_RANGE, 1.0f).offset(offset, 0f));
        }

        return m.colored(this.color);
    }
}
