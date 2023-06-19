package com.chromosundrift.vectorbrat.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

/**
 * UI element for selecting an audio device (WIP).
 */
class Selector extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(Selector.class);

    final JComboBox<Selection> combo;

    public Selector(String text, Selection[] choices) {
        super(new GridLayout(2, 1, 0, 0), true);
        JLabel label = UiUtil.rLabel(text);
        combo = new JComboBox<>(choices);
        combo.setAlignmentX(Component.RIGHT_ALIGNMENT);
        label.setLabelFor(combo);
        add(label, BorderLayout.NORTH);
        add(combo, BorderLayout.SOUTH);
        combo.addActionListener(e -> {
            JComboBox<Selection> jcb = (JComboBox<Selection>) e.getSource();
            ((Selection) jcb.getSelectedItem()).onSelect.run();
        });
    }
    public record Selection(@Nonnull String label, @Nonnull Runnable onSelect) {

        @Override
        public String toString() {
            return label;
        }
    }
}
