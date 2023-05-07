package com.chromosundrift.vectorbrat.swing;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.Hashtable;

import com.chromosundrift.vectorbrat.Config;

public class ControlPanel extends JPanel {
    private final JSlider ppsControl;
    private final Config config;


    public ControlPanel(Config config) {
        this.config = config;
        setBorder(new EmptyBorder(5, 5, 5, 5));

        ButtonGroup interlock = new ButtonGroup();
        JRadioButton armed = new JRadioButton("Armed");
        JRadioButton safe = new JRadioButton("Safe");
        interlock.add(armed);
        interlock.add(safe);
        safe.setSelected(true);


        // settings controls
        JComponent xy = new DeviceSelector("X/Y Device");
        JComponent rz = new DeviceSelector("Red/Z Device");
        JComponent gb = new DeviceSelector("Green/Blue Device");

        JPanel pps = new JPanel(new BorderLayout());
        JLabel psl = rLabel("Points per second");
        pps.add(psl);
        ppsControl = new JSlider(JSlider.HORIZONTAL, Config.MIN_PPS, Config.MAX_PPS, config.getPps());
        ppsControl.setPaintLabels(true);
        Hashtable<Integer, JLabel> sliderLabels = new Hashtable<>(4);
        sliderLabels.put(5, new JLabel("5"));
        sliderLabels.put(10000, new JLabel("10k"));
        sliderLabels.put(20000, new JLabel("20k"));
        sliderLabels.put(30000, new JLabel("30k"));

        ppsControl.setLabelTable(sliderLabels);
        psl.setLabelFor(ppsControl);
        pps.add(psl, BorderLayout.NORTH);
        pps.add(ppsControl, BorderLayout.SOUTH);

        setLayout(new GridLayout(5, 1, 5, 5));
        add(xy);
        add(rz);
        add(gb);
        add(pps);
    }

    private static JLabel rLabel(String text) {
        final JLabel label;
        label = new JLabel(text, SwingConstants.RIGHT);
        label.setBorder(new EmptyBorder(5, 0, 0, 10));
        return label;
    }

    private static class DeviceSelector extends JPanel {

        private final JLabel label;
        private final JComboBox combo;

        public DeviceSelector(String text) {
            super(new GridLayout(2, 1, 0, 5), true);
            label = rLabel(text);
            combo = new JComboBox(Config.knownDevices().toArray(new String[0]));
            combo.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setLabelFor(combo);
            add(label, BorderLayout.NORTH);
            add(combo, BorderLayout.SOUTH);
        }

    }
}
