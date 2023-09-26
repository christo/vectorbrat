package com.chromosundrift.vectorbrat.swing;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.Controllers;
import com.chromosundrift.vectorbrat.laser.BeamTuning;
import com.chromosundrift.vectorbrat.laser.LaserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import java.util.stream.Stream;

import static com.chromosundrift.vectorbrat.swing.DisplayController.Mode.DISPLAY;
import static com.chromosundrift.vectorbrat.swing.DisplayController.Mode.PATH_PLAN;
import static com.chromosundrift.vectorbrat.swing.DisplayController.Mode.SIMULATOR;

class ControlPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(ControlPanel.class);

    public ControlPanel(final Config config, final Controllers controllers) {
        logger.info("initialising ControlPanel");
        LaserController laserController = controllers.laserController();
        final DisplayController dc = controllers.displayController();
        setBorder(new EmptyBorder(5, 5, 5, 5));
        // this is totally gridbag
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.ipady = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(mkArmStart(laserController), gbc);
        add(mkModeSelektor(dc, laserController), gbc);
        add(mkPpsSlider(config, laserController), gbc);
        add(mkStatPanel(laserController), gbc);

        // fill remaining vertical space
        gbc.weighty = 1;
        add(Box.createVerticalBox(), gbc);
    }

    private static Selector mkModeSelektor(DisplayController dc, LaserController lc) {
        List<Selector.Selection> modes = Stream.of(DISPLAY, PATH_PLAN, SIMULATOR)
                .map(m -> new Selector.Selection(m.getUiLabel(), () -> dc.setMode(m)))
                .toList();

        // TODO make sure turning debug mode on does not start the laser and make path planning run without it
        Selector modeSelektor = new Selector("mode", modes);
        modeSelektor.setBorder(new EmptyBorder(6, 0, 7, 0));
        return modeSelektor;
    }


    /**
     * Create the many stats with their labels, values and update listeners.
     *
     * @param laserController to register for updates so statistics are updated live.
     * @return a JPanel containing all the live stats.
     */
    private static JPanel mkStatPanel(LaserController laserController) {
        final StatItem pathPlanTime = new StatItem("path plan (μs)");
        final StatItem sampleRate = new StatItem("sample rate");
        final StatItem bufferSize = new StatItem("buffer size");
        final StatItem vertexPoints = new StatItem("vertex points");
        final StatItem blackPoints = new StatItem("black points");
        final StatItem pointsPerUnit = new StatItem("points per unit");
        final StatItem pointsPerPoint = new StatItem("points per point");
        final StatItem pointsPerPointOffset = new StatItem("offset");
        final StatItem minBrightness = new StatItem("min brightness");


        // update the stats when they change
        laserController.addUpdateListener(lc -> {
            pathPlanTime.setValue(lc.getPathPlanTime() / 1000); // convert to microseconds for UI
            lc.getSampleRate().ifPresent(sampleRate::setValue);
            lc.getBufferSize().ifPresent(bufferSize::setValue);
            BeamTuning tuning = laserController.getTuning();
            vertexPoints.setValue(tuning.getVertexPoints());
            blackPoints.setValue(tuning.getBlackPoints());
            pointsPerUnit.setValue(tuning.getPointsPerUnit());
            pointsPerPoint.setValue(tuning.getPointsPerPoint());
            pointsPerPointOffset.setValue(tuning.getPointsPerUnitOffset());
            minBrightness.setValue(tuning.getMinimumLaserBrightness());
        });


        List<StatItem> details = List.of(
                new StatItem("Make", Config.LASER_MAKE),
                new StatItem("Model", Config.LASER_MODEL),
                pathPlanTime,
                sampleRate,
                bufferSize,
                vertexPoints,
                blackPoints,
                pointsPerUnit,
                minBrightness
        );

        JPanel stats = new JPanel(new GridLayout(details.size(), 1, 5, 5));
        details.forEach(stats::add);
        return stats;
    }

    private static JPanel mkPpsSlider(Config config, LaserController laserController) {
        // future: dynamically make scale based on LaserSpec
        Hashtable<Integer, JLabel> sliderLabels = new Hashtable<>();
        // hard-coded minimum value
        sliderLabels.put(Config.MIN_PPS, new JLabel(Integer.toString(Config.MIN_PPS)));
        // go up in 10k increments
        int maxkPps = Config.MAX_PPS / 1000;
        for (int i = 10; i <= maxkPps; i += 10) {
            int kpps = i * 1000;
            sliderLabels.put(kpps, new JLabel("%sk".formatted(i)));
        }
        final JSlider ppsControl = new JSlider(JSlider.HORIZONTAL, Config.MIN_PPS, Config.MAX_PPS, config.getBeamTuning().getPps());
        ppsControl.setPaintLabels(true);
        ppsControl.setLabelTable(sliderLabels);
        ppsControl.addChangeListener(new PpsListener(config.liveControls(), laserController));
        ppsControl.setEnabled(laserController.isRunning());
        // enable or disable based on the laser controller running state
        laserController.addUpdateListener(lc -> ppsControl.setEnabled(lc.isRunning()));

        final String units = " PPS";
        final JLabel psl = UiUtil.rLabel(laserController.getTuning().getPps() + units);
        psl.setLabelFor(ppsControl);
        // update laser controller and slider label to match slider value
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

        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> {
            if (!lc.isRunning()) {
                // only respond if not already running
                connectButton.setEnabled(false);
                connectButton.setText("Connecting...");
                lc.connect();
            }
        });
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(0, 5, 5, 5));
        buttonPanel.add(connectButton, BorderLayout.EAST);
        armPanel.add(buttonPanel, BorderLayout.SOUTH);

        JCheckBox invertX = new JCheckBox("Invert X");
        invertX.addActionListener(e -> {
            boolean inverted = ((JCheckBox) e.getSource()).isSelected();
            if (lc.getInvertX() != inverted) {
                lc.setInvertX(inverted);
            }
        });
        JCheckBox invertY = new JCheckBox("Invert Y");
        invertY.addActionListener(e -> {
            boolean inverted = ((JCheckBox) e.getSource()).isSelected();
            if (lc.getInvertY() != inverted) {
                lc.setInvertY(inverted);
            }
        });
        JPanel invertPanel = new JPanel(new BorderLayout());
        invertPanel.add(invertX, BorderLayout.WEST);
        invertPanel.add(invertY, BorderLayout.EAST);
        buttonPanel.add(invertPanel, BorderLayout.SOUTH);
        lc.addUpdateListener(laserController -> {
            boolean running = lc.isRunning();

            // start button needs to be disabled if running
            connectButton.setEnabled(!running);
            connectButton.setText(running ? "Connected" : "Connect");
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
