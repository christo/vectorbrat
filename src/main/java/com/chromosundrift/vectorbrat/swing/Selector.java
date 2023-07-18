package com.chromosundrift.vectorbrat.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * UI selection from a list.
 */
class Selector extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(Selector.class);

    final JComboBox<Selection> combo;

    @SuppressWarnings("unchecked")
    public Selector(String text, List<Selection> choices) {
        super(new GridLayout(2, 1, 0, 0), true);
        JLabel label = UiUtil.rLabel(text);

        combo = new JComboBox<>();
        choices.forEach(combo::addItem);
        combo.setAlignmentX(Component.RIGHT_ALIGNMENT);
        label.setLabelFor(combo);
        add(label, BorderLayout.NORTH);
        add(combo, BorderLayout.SOUTH);
        combo.addActionListener(e -> {
            JComboBox<Selection> jcb = (JComboBox<Selection>) e.getSource();
            Selection selectedItem = (Selection) jcb.getSelectedItem();
            if (selectedItem != null) {
                selectedItem.onSelect.run();
            }
        });
    }

    public record Selection(@Nonnull String label, @Nonnull Runnable onSelect) {

        @Override
        public String toString() {
            return label;
        }
    }
}
