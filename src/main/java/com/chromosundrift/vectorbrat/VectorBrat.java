package com.chromosundrift.vectorbrat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.UnsupportedLookAndFeelException;
import java.util.Arrays;

import static com.chromosundrift.vectorbrat.Util.setSystemLibraryPath;
import static javax.swing.UIManager.getInstalledLookAndFeels;
import static javax.swing.UIManager.setLookAndFeel;

import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.laser.LaserDisplay;
import com.chromosundrift.vectorbrat.swing.DisplayController;
import com.chromosundrift.vectorbrat.swing.DisplayPanel;
import com.chromosundrift.vectorbrat.swing.VectorBratFrame;

public class VectorBrat {

    private static final Logger logger = LoggerFactory.getLogger(VectorBrat.class);
    private final VectorBratFrame frame;
    private final LaserDisplay laser;
    private final DisplayPanel displayPanel;
    private float t;
    private Model model;

    public VectorBrat(Model model) throws VectorBratException {
        logger.info("initialising VectorBrat");
        final Config config = new Config();
        String laf = config.getLaf();

        Arrays.stream(getInstalledLookAndFeels()).filter(i -> laf.equals(i.getName())).findAny().ifPresent(i -> {
            try {
                logger.debug("Setting {} look and feel", laf);
                setLookAndFeel(i.getClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException e) {
                logger.warn("Could not set {} look and feel", laf);
            }
        });

        laser = new LaserDisplay(config);
        DisplayController displayController = new DisplayController(true);
        displayPanel = new DisplayPanel(config, displayController, laser);
        displayController.setRepaintDisplay(displayPanel::repaint);
        frame = new VectorBratFrame(config, displayPanel, displayController, laser);
        this.model = model;
        this.t = System.currentTimeMillis() / 1000f;
    }

    public static void main(String[] args) {
        setSystemLibraryPath();

        try {
            Model m = Model.testPattern1();
            VectorBrat vectorBrat = new VectorBrat(m);
            Model model = m.scale(0.5f);
            vectorBrat.start(model);
        } catch (VectorBratException e) {
            logger.error("can't create vectorbrat", e);
        }

    }

    private void start(Model model) {
        this.displayPanel.setModel(model);
        this.laser.setModel(model);
        this.frame.start();
        this.laser.start(model);
        logger.info("started VectorBrat");
    }

}
