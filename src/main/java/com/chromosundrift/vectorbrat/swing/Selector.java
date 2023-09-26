package com.chromosundrift.vectorbrat.swing;

import javax.annotation.Nonnull;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.util.List;

/**
 * UI selection from a list.
 */
class Selector extends JPanel {

    final JComboBox<Selection> combo;

    /**
     * Selector with no label.
     */
    @SuppressWarnings("unchecked")
    public Selector(List<Selection> choices) {
        super(new GridLayout(2, 1, 0, 0), true);
        combo = new JComboBox<>();
        choices.forEach(combo::addItem);
        combo.setAlignmentX(Component.RIGHT_ALIGNMENT);
        add(combo, BorderLayout.SOUTH);
        combo.addActionListener(e -> {
            JComboBox<Selection> jcb = (JComboBox<Selection>) e.getSource();
            Selection selectedItem = (Selection) jcb.getSelectedItem();
            if (selectedItem != null) {
                selectedItem.onSelect.run();
            }
        });
    }

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
