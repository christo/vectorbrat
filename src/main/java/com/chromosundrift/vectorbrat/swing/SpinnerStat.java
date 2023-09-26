package com.chromosundrift.vectorbrat.swing;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import java.awt.BorderLayout;
import java.util.function.Consumer;

/**
 * Stat with {@link JSpinner} control, for integers only.
 */
public class SpinnerStat extends JPanel implements Stat {
    private final int min;
    private JSpinner control;

    public SpinnerStat(String label, int min, Consumer<Integer> updater) {
        super(new BorderLayout(), true);
        this.min = min;
        this.add(new JLabel(label), BorderLayout.WEST);
        this.control = new JSpinner();
        control.addChangeListener(e -> {
            JSpinner source = (JSpinner) e.getSource();
            int value = (int) source.getValue();
            if (value < min) {
                source.setValue(min);
                updater.accept(min);
            } else {
                updater.accept(value);
            }
        });
        this.add(this.control, BorderLayout.EAST);
    }

    @Override
    public void setValue(long v) {
        setValue((int) v);
    }

    @Override
    public void setValue(float v) {
        setValue((int) v);
    }

    @Override
    public void setValue(int v) {
        this.control.setValue(v);
    }
}
