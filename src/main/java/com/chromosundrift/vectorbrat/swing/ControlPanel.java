package com.chromosundrift.vectorbrat.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Hashtable;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.laser.LaserController;

public class ControlPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(ControlPanel.class);

    public ControlPanel(final Config config, final Controllers controllers) {
        logger.info("initialising ControlPanel");
        setBorder(new EmptyBorder(5, 5, 5, 5));

        JCheckBox cb = new JCheckBox("debug");
        DisplayController displayController = controllers.displayController;
        cb.setSelected(displayController.isDrawPathPlan());
        cb.addActionListener(e -> displayController.setDrawPathPlan(((JCheckBox) e.getSource()).isSelected()));

        LaserController laserController = controllers.laserController;
        JPanel pps = createPpsSlider(config, laserController);

        StatPanel pathPlanStat = new StatPanel("path plan (μs)");
        StatPanel sampleRateStat = new StatPanel("sample rate");
        laserController.addUpdateListener(lc -> {
            pathPlanStat.setValue(lc.getPathPlanTime());
            lc.getSampleRate().ifPresent(sampleRateStat::setValue);
        });

        JComponent[] details = new JComponent[]{
                new StatPanel("Make", Config.LASER_MAKE),
                new StatPanel("Model", Config.LASER_MODEL),
                pathPlanStat,
                sampleRateStat
        };

        JPanel stats = new JPanel(new GridLayout(details.length, 1, 5, 5));
        for (JComponent item : details) {
            stats.add(item);
        }

        JComponent[] items = new JComponent[]{
                createArmDisarm(laserController),
                pps,
                cb,
                stats
        };

        setLayout(new GridLayout(items.length, 1, 0, 0));
        for (JComponent item : items) {
            add(item);
        }
    }

    private static JPanel createPpsSlider(Config config, LaserController laserController) {

        Hashtable<Integer, JLabel> sliderLabels = new Hashtable<>(5);
        sliderLabels.put(5, new JLabel("5"));
        sliderLabels.put(10000, new JLabel("10k"));
        sliderLabels.put(20000, new JLabel("20k"));
        sliderLabels.put(30000, new JLabel("30k"));
        sliderLabels.put(40000, new JLabel("40k"));

        final JSlider ppsControl = new JSlider(JSlider.HORIZONTAL, Config.MIN_PPS, Config.MAX_PPS, config.getLaserTuning().getPps());
        ppsControl.setPaintLabels(true);
        ppsControl.setLabelTable(sliderLabels);
        ppsControl.addChangeListener(new PpsListener(config.liveControls(), laserController));

        final String units = " PPS";
        final JLabel psl = UiUtil.rLabel(laserController.getPps() + units);
        psl.setLabelFor(ppsControl);

        ppsControl.addChangeListener(e -> {
            JSlider slider = (JSlider) e.getSource();
            int value = slider.getValue();
            psl.setText(value + units);
        });

        final JPanel pps = new JPanel(new BorderLayout());
        pps.add(psl, BorderLayout.NORTH);
        pps.add(ppsControl, BorderLayout.CENTER);

        return pps;
    }

    private JPanel createArmDisarm(final LaserController laserController) {
        ButtonGroup group = new ButtonGroup();
        JRadioButton armed = new JRadioButton("Armed", laserController.getArmed());
        JRadioButton safe = new JRadioButton("Safe", !laserController.getArmed());
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
            if (laserController.getArmed() != laserOn) {
                laserController.setArmed(laserOn);
            }
        });
        safe.addActionListener(e -> {
            boolean laserOn = !((JRadioButton) e.getSource()).isSelected();
            if (laserController.getArmed() != laserOn) {
                laserController.setArmed(laserOn);
            }
        });

        return interlock;
    }

}
