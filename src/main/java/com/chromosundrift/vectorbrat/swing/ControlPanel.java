package com.chromosundrift.vectorbrat.swing;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
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
        add(createInterlock());
        add(xy);
        add(rz);
        add(gb);
        add(pps);
    }

    private JPanel createInterlock() {
        ButtonGroup group = new ButtonGroup();
        JRadioButton armed = new JRadioButton("Armed");
        JRadioButton safe = new JRadioButton("Safe");
        group.add(armed);
        group.add(safe);
        safe.setSelected(true);
        JPanel interlock = new JPanel(new BorderLayout());
        TitledBorder border = new TitledBorder(
                new LineBorder(Color.LIGHT_GRAY),
                "Laser",
                TitledBorder.CENTER,
                TitledBorder.TOP);
        interlock.setBorder(border);
        interlock.add(armed, BorderLayout.WEST);
        interlock.add(safe, BorderLayout.EAST);
        return interlock;
    }

    private static JLabel rLabel(String text) {
        final JLabel label;
        label = new JLabel(text, SwingConstants.RIGHT);
        label.setBorder(new EmptyBorder(5, 0, 0, 10));
        return label;
    }

    private static class DeviceSelector extends JPanel {

        final JComboBox combo;

        public DeviceSelector(String text) {
            super(new GridLayout(2, 1, 0, 5), true);
            JLabel label = rLabel(text);
            combo = new JComboBox(Config.knownDevices().toArray(new String[0]));
            combo.setAlignmentX(Component.RIGHT_ALIGNMENT);
            label.setLabelFor(combo);
            add(label, BorderLayout.NORTH);
            add(combo, BorderLayout.SOUTH);
        }

    }
}
