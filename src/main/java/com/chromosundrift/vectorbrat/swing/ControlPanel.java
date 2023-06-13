package com.chromosundrift.vectorbrat.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Hashtable;
import java.util.List;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.laser.LaserController;
import com.chromosundrift.vectorbrat.laser.LaserTuning;

class ControlPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(ControlPanel.class);

    public ControlPanel(final Config config, final Controllers controllers) {
        logger.info("initialising ControlPanel");
        setBorder(new EmptyBorder(5, 5, 5, 5));
        LaserController laserController = controllers.laserController;

        JCheckBox cb = new JCheckBox("debug");
        DisplayController displayController = controllers.displayController;
        cb.setSelected(displayController.isDrawPathPlan());
        cb.setEnabled(laserController.isRunning());
        cb.addActionListener(e -> displayController.setDrawPathPlan(((JCheckBox) e.getSource()).isSelected()));
        laserController.addUpdateListener(lc -> cb.setEnabled(lc.isRunning()));

        JPanel pps = createPpsSlider(config, laserController);

        final StatPanel pathPlanStat = new StatPanel("path plan (μs)");
        final StatPanel sampleRateStat = new StatPanel("sample rate");
        final StatPanel bufferSize = new StatPanel("buffer size");
        final StatPanel vertexPoints = new StatPanel("vertex points");
        final StatPanel blackPoints = new StatPanel("black points");
        final StatPanel pointsPerUnit = new StatPanel("points per unit");
        final StatPanel pointsPerPoint = new StatPanel("points per point");
        final StatPanel pointsPerPointOffset = new StatPanel("offset");


        // update the stats when they change
        laserController.addUpdateListener(lc -> {
            pathPlanStat.setValue(lc.getPathPlanTime());
            lc.getSampleRate().ifPresent(sampleRateStat::setValue);
            lc.getBufferSize().ifPresent(bufferSize::setValue);
            LaserTuning tuning = laserController.getTuning();
            vertexPoints.setValue(tuning.getVertexPoints());
            blackPoints.setValue(tuning.getBlackPoints());
            pointsPerUnit.setValue(tuning.getPointsPerUnit());
            pointsPerPoint.setValue(tuning.getPointsPerPoint());
            pointsPerPointOffset.setValue(tuning.getPointsPerUnitOffset());
        });


        List<StatPanel> details = List.of(
                new StatPanel("Make", Config.LASER_MAKE),
                new StatPanel("Model", Config.LASER_MODEL),
                pathPlanStat,
                sampleRateStat,
                bufferSize,
                vertexPoints,
                blackPoints,
                pointsPerUnit
        );

        JPanel stats = new JPanel(new GridLayout(details.size(), 1, 5, 5));
        details.forEach(stats::add);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.ipady = 10;
        gbc.anchor = GridBagConstraints.LINE_END;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        List.of(mkArmStart(laserController), pps, cb, stats).forEach(item -> add(item, gbc));
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
        ppsControl.setEnabled(laserController.isRunning());
        laserController.addUpdateListener(lc -> ppsControl.setEnabled(lc.isRunning()));

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

    private JPanel mkArmStart(final LaserController lc) {
        ButtonGroup group = new ButtonGroup();
        final JRadioButton armed = new JRadioButton("Armed", lc.getArmed());
        final JRadioButton safe = new JRadioButton("Safe", !lc.getArmed());
        armed.setEnabled(lc.isRunning());
        safe.setEnabled(lc.isRunning());

        group.add(armed);
        group.add(safe);
        JPanel armPanel = new JPanel(new BorderLayout());
        TitledBorder border = new TitledBorder(
                new LineBorder(Color.LIGHT_GRAY),
                "Laser",
                TitledBorder.CENTER,
                TitledBorder.TOP);
        armPanel.setBorder(border);
        armPanel.add(armed, BorderLayout.WEST);
        armPanel.add(safe, BorderLayout.EAST);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            if (!lc.isRunning()) {
                startButton.setEnabled(false);
                startButton.setText("Starting...");
                lc.start();
            }
        });
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(0, 5, 5, 5));
        buttonPanel.add(startButton, BorderLayout.EAST);
        armPanel.add(buttonPanel, BorderLayout.SOUTH);
        lc.addUpdateListener(laserController -> {
            boolean running = lc.isRunning();

            // start button needs to be disabled if running
            startButton.setEnabled(!running);
            startButton.setText(running ? "Running" : "Start");
            // only enable the arm/safe toggle if the LaserController is running
            armed.setEnabled(running);
            safe.setEnabled(running);
        });

        armed.addActionListener(e -> {
            boolean laserOn = ((JRadioButton) e.getSource()).isSelected();
            if (lc.getArmed() != laserOn) {
                lc.setArmed(laserOn);
            }
        });
        safe.addActionListener(e -> {
            boolean laserOn = !((JRadioButton) e.getSource()).isSelected();
            if (lc.getArmed() != laserOn) {
                lc.setArmed(laserOn);
            }
        });

        return armPanel;
    }

}
