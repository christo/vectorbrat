package com.chromosundrift.vectorbrat.swing;

import com.chromosundrift.vectorbrat.AppMap;
import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.Controllers;
import com.chromosundrift.vectorbrat.VectorBratException;
import com.chromosundrift.vectorbrat.laser.BeamTuning;
import com.chromosundrift.vectorbrat.laser.LaserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Stream;

import static com.chromosundrift.vectorbrat.swing.DisplayController.Mode.DISPLAY;
import static com.chromosundrift.vectorbrat.swing.DisplayController.Mode.PATH_PLAN;
import static com.chromosundrift.vectorbrat.swing.DisplayController.Mode.SIMULATOR;

class ControlPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(ControlPanel.class);

    public ControlPanel(final Config config, final Controllers controllers, AppMap appMap) {
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
        add(mkAppSelektor(appMap), gbc);
        add(mkPpsSlider(config, laserController), gbc);
        add(mkStatPanel(laserController), gbc);

        // fill remaining vertical space
        gbc.weighty = 1;
        add(Box.createVerticalBox(), gbc);
    }

    private Selector mkAppSelektor(AppMap appMap) {
        List<Selector.Selection> apps = appMap.getAnimators().stream()
                .map(name -> new Selector.Selection(name, () -> {
                    final String previous = appMap.getAnimator();
                    try {
                        appMap.setAnimator(name);
                    } catch (VectorBratException e) {
                        logger.error("failed setting app to {}, returning to {}", name, previous);
                        try {
                            appMap.setAnimator(previous);
                        } catch (VectorBratException ex) {
                            logger.error("failed to restore previous animator: {}", previous);
                            throw new RuntimeException(ex);
                        }
                    }
                }))
                .sorted(Selector.Selection.BY_LABEL)
                .toList();

        return new Selector("apps", apps, appMap.getAnimator());
    }

    private static Selector mkModeSelektor(DisplayController dc, LaserController lc) {
        List<Selector.Selection> modes = Stream.of(DISPLAY, PATH_PLAN, SIMULATOR)
                .map(m -> new Selector.Selection(m.getUiLabel(), () -> dc.setMode(m)))
                .toList();

        Selector modeSelektor = new Selector(modes);
        modeSelektor.setBorder(new EmptyBorder(5, 0, 7, 0));
        return modeSelektor;
    }

    /**
     * Create the many stats with their labels, values and update listeners.
     *
     * @param laserController to register for updates so statistics are updated live.
     * @return a JPanel containing all the live stats.
     */
    private static JPanel mkStatPanel(LaserController laserController) {

        BeamTuning tuning = laserController.getTuning();
        final SpinnerStat vertexPoints = new SpinnerStat("vertex points", 1, tuning::setVertexPoints);
        final SpinnerStat blackPoints = new SpinnerStat("black points", 0, tuning::setBlackPoints);
        final SpinnerStat pointsPerPoint = new SpinnerStat("points per point", 1, tuning::setPointsPerPoint);
        final SpinnerStat pointsPerUnit = new SpinnerStat("points per unit", 0, tuning::setPointsPerUnit);
        final SpinnerStat pointsPerPointOffset = new SpinnerStat("offset", 0, tuning::setPointsPerUnitOffset);

        // TODO make min brightness settable in BeamTuning
        final LabelStat minBrightness = new LabelStat("min brightness");

        final LabelStat pathPlanTime = new LabelStat("path plan (μs)");
        final LabelStat sampleRate = new LabelStat("sample rate");
        final LabelStat bufferSize = new LabelStat("buffer size");

        // update the stats when they change
        laserController.addUpdateListener(lc -> {
            vertexPoints.setValue(tuning.getVertexPoints());
            blackPoints.setValue(tuning.getBlackPoints());
            pointsPerUnit.setValue(tuning.getPointsPerUnit());
            pointsPerPoint.setValue(tuning.getPointsPerPoint());
            pointsPerPointOffset.setValue(tuning.getPointsPerUnitOffset());
            minBrightness.setValue(tuning.getMinimumLaserBrightness());

            pathPlanTime.setValue(lc.getPathPlanTime() / 1000); // convert to microseconds for UI
            lc.getSampleRate().ifPresent(sampleRate::setValue);
            lc.getBufferSize().ifPresent(bufferSize::setValue);
        });


        List<JPanel> details = List.of(
                vertexPoints,
                blackPoints,
                pointsPerUnit,
                pointsPerPointOffset,
                minBrightness,
                // TODO add interpolator chooser here
                pathPlanTime,
                sampleRate,
                bufferSize,
                new LabelStat(Config.LASER_MAKE, Config.LASER_MODEL)
        );

        JPanel stats = new JPanel();
        stats.setLayout(new BoxLayout(stats, BoxLayout.Y_AXIS));
        details.forEach(stats::add);
        return stats;
    }

    private static JPanel mkPpsSlider(Config config, LaserController laserController) {
        // future: dynamically make scale based on LaserSpec
        // future: button: reset to default
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
            laserController.getTuning().setPps(value);
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
            if (!lc.isConnected()) {
                // only respond if not already connected
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
            boolean connected = lc.isConnected();

            // start button needs to be disabled if running
            connectButton.setEnabled(!connected);
            connectButton.setText(connected ? "Connected" : "Connect");
            // only enable the arm/safe toggle if the LaserController is running
            armed.setEnabled(connected);
            safe.setEnabled(connected);
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
