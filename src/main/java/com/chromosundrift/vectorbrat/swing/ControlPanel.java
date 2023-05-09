package com.chromosundrift.vectorbrat.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Hashtable;

import com.chromosundrift.vectorbrat.Config;

public class ControlPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(ControlPanel.class);

    private final Config config;
    private final LaserController laserController;

    public ControlPanel(Config config, LaserController laserController) {
        logger.info("initialising ControlPanel");
        this.config = config;
        this.laserController = laserController;
        setBorder(new EmptyBorder(5, 5, 5, 5));

        // settings controls
        JComponent xy = new DeviceSelector("X/Y Device");
        JComponent rz = new DeviceSelector("Red/Z Device");
        JComponent gb = new DeviceSelector("Green/Blue Device");


        JPanel pps = createPpsSlider(config, laserController);

        JComponent[] details = new JComponent[]{
                rLabel(Config.LASER_MAKE),
                rLabel(Config.LASER_MODEL),
        };
        JPanel detail = new JPanel(new GridLayout(details.length, 1, 5, 5));
        for (JComponent item : details) {
            detail.add(item);
        }

        JComponent[] items = new JComponent[]{
                createArmDisarm(laserController),
                xy,
                rz,
                gb,
                pps,
                detail
        };

        setLayout(new GridLayout(items.length, 1, 5, 5));
        for (JComponent item : items) {
            add(item);
        }
        setPreferredSize(new Dimension(170, 400));
    }

    private static JPanel createPpsSlider(Config config, LaserController laserController) {
        final JPanel pps = new JPanel(new BorderLayout());
        final JSlider ppsControl;
        final String units = " PPS";
        final JLabel psl = rLabel(laserController.getPps() + units);
        pps.add(psl);
        ppsControl = new JSlider(JSlider.HORIZONTAL, Config.MIN_PPS, Config.MAX_PPS, config.getPps());
        ppsControl.setPaintLabels(true);
        Hashtable<Integer, JLabel> sliderLabels = new Hashtable<>(4);
        sliderLabels.put(5, new JLabel("5"));
        sliderLabels.put(10000, new JLabel("10k"));
        sliderLabels.put(20000, new JLabel("20k"));
        sliderLabels.put(30000, new JLabel("30k"));
        sliderLabels.put(40000, new JLabel("40k"));

        ppsControl.setLabelTable(sliderLabels);
        psl.setLabelFor(ppsControl);
        pps.add(psl, BorderLayout.NORTH);
        pps.add(ppsControl, BorderLayout.SOUTH);
        ppsControl.addChangeListener(new PpsListener(config.liveControls(), laserController));
        ppsControl.addChangeListener(e -> {
            JSlider slider = (JSlider) e.getSource();
            int value = slider.getValue();
            psl.setText(value + units);
        });
        pps.setMinimumSize(new Dimension(200, 50));

        return pps;
    }

    private JPanel createArmDisarm(final LaserController laserController) {
        ButtonGroup group = new ButtonGroup();
        JRadioButton armed = new JRadioButton("Armed", laserController.getOn());
        JRadioButton safe = new JRadioButton("Safe", !laserController.getOn());
        group.add(armed);
        group.add(safe);
        JPanel interlock = new JPanel(new BorderLayout());
        TitledBorder border = new TitledBorder(
                new LineBorder(Color.LIGHT_GRAY),
                "Laser",
                TitledBorder.CENTER,
                TitledBorder.TOP);
        interlock.setBorder(border);
        interlock.add(armed, BorderLayout.WEST);
        interlock.add(safe, BorderLayout.EAST);

        armed.addActionListener(e -> {
            boolean laserOn = ((JRadioButton) e.getSource()).isSelected();
            if (laserController.getOn() != laserOn) {
                laserController.setOn(laserOn);
            }
        });
        safe.addActionListener(e -> {
            boolean laserOn = !((JRadioButton) e.getSource()).isSelected();
            if (laserController.getOn() != laserOn) {
                laserController.setOn(laserOn);
            }
        });

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

    private static class PpsListener implements ChangeListener {

        private final boolean live;
        private final LaserController lc;

        public PpsListener(boolean live, LaserController laserController) {
            this.live = live;
            lc = laserController;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (live || !source.getValueIsAdjusting()) {
                lc.setPps(source.getValue());
            }
        }
    }

}
