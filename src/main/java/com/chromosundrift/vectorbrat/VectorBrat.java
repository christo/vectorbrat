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
import com.chromosundrift.vectorbrat.swing.DisplayPanel;
import com.chromosundrift.vectorbrat.swing.VectorBratFrame;

public class VectorBrat {

    private static final Logger logger = LoggerFactory.getLogger(VectorBrat.class);
    private final VectorBratFrame frame;
    private final DisplayPanel displayPanel;
    private final LaserDisplay laser;

    public VectorBrat() throws VectorBratException {
        logger.info("initialising vectorbrat");
        Arrays.stream(getInstalledLookAndFeels()).filter(i -> "Mac OS X".equals(i.getName())).findAny().ifPresent(i -> {
            try {
                logger.debug("Setting Mac OS X look and feel");
                setLookAndFeel(i.getClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException e) {
                logger.warn("Could not set Mac OS X look and feel");
            }
        });

        final Config config = new Config();
        laser = new LaserDisplay(config);
        displayPanel = new DisplayPanel(config);
        frame = new VectorBratFrame(config, displayPanel, laser);
    }

    public static void main(String[] args) {
        setSystemLibraryPath();
        VectorBrat vectorBrat = null;
        try {
            vectorBrat = new VectorBrat();
            vectorBrat.start(Model.testPattern1());
        } catch (VectorBratException e) {
            logger.error("can't create vectorbrat", e);
        }

    }

    private void start(Model model) {
        frame.start(model);
        laser.start(model);
        logger.info("started VectorBrat");
    }

}
