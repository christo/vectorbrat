package com.chromosundrift.vectorbrat.swing;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import com.chromosundrift.vectorbrat.Config;

/**
 * UI element for selecting an audio device (WIP).
 */
class DeviceSelector extends JPanel {

    final JComboBox<String> combo;

    public DeviceSelector(String text) {
        super(new GridLayout(2, 1, 0, 0), true);
        JLabel label = UiUtil.rLabel(text);
        combo = new JComboBox<>(Config.knownDevices().toArray(new String[0]));
        combo.setAlignmentX(Component.RIGHT_ALIGNMENT);
        label.setLabelFor(combo);
        add(label, BorderLayout.NORTH);
        add(combo, BorderLayout.SOUTH);
    }

}
