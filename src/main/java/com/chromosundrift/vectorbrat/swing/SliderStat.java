package com.chromosundrift.vectorbrat.swing;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.util.function.Consumer;

/**
 * Slider for float values from 0-1
 */
public class SliderStat extends JPanel implements Stat {

    private final JLabel value;
    private final JSlider slider;

    public SliderStat(String label, float min, float max, Consumer<Float> updater) {
        super(new BorderLayout());
        this.add(new JLabel(label), BorderLayout.WEST);
        this.value = new JLabel("");
        this.add(value, BorderLayout.EAST);
        // JSlider has default range of 0-100
        slider = new JSlider(SwingConstants.HORIZONTAL);

        this.add(slider, BorderLayout.SOUTH);
        setBorder(new EmptyBorder(10, 0, 0, 5));
        slider.addChangeListener(e -> {
            JSlider s = (JSlider) e.getSource();
            float normalised = s.getValue() / 100f;
            value.setText(String.format("%.2f", normalised));
            updater.accept(normalised);
        });
    }

    @Override
    public void setValue(long v) {
        setValue((float) v);

    }

    @Override
    public void setValue(float v) {
        this.value.setText(Float.toString(v));
        this.slider.setValue((int) (v*100));
    }

    @Override
    public void setValue(int v) {
        setValue((float) v);
    }
}
