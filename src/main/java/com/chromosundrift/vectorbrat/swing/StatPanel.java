package com.chromosundrift.vectorbrat.swing;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

/**
 * A UI panel that shows single labelled value that can be dynamically updated.
 */
class StatPanel extends JPanel {
    private final JLabel value;

    public StatPanel(String label) {
        this(label, "");
    }

    public StatPanel(String label, String initialValue) {
        super(new BorderLayout());
        this.add(new JLabel(label), BorderLayout.WEST);
        this.value = new JLabel(initialValue);
        this.add(value, BorderLayout.EAST);
        setBorder(new EmptyBorder(10, 0, 0, 5));
    }

    public void setValue(long v) {
        this.value.setText(Long.toString(v));
    }

    public void setValue(float v) {
        this.value.setText(Float.toString(v));
    }

    public void setValue(int v) {
        this.value.setText(Integer.toString(v));
    }
}
