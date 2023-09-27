package com.chromosundrift.vectorbrat.swing;

import javax.annotation.Nonnull;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Comparator.comparing;

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
        super(new BorderLayout(), true);
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

    public Selector(String text, List<Selection> choices, String selected) {
        this(text, choices);
        Stream<Selection> stream = choices.stream();
        Optional<Selection> sel = stream.filter(s -> s.label.equals(selected)).findFirst();
        combo.setSelectedItem(sel.orElseThrow());
    }

    public record Selection(@Nonnull String label, @Nonnull Runnable onSelect) {

        public static final Comparator<Selection> BY_LABEL = comparing(o -> o.label);

        @Override
        public String toString() {
            return label;
        }
    }
}
