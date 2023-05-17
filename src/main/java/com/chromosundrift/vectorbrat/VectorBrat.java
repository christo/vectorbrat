package com.chromosundrift.vectorbrat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Color;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.chromosundrift.vectorbrat.Util.setSystemLibraryPath;
import static javax.swing.UIManager.getInstalledLookAndFeels;
import static javax.swing.UIManager.setLookAndFeel;

import com.chromosundrift.vectorbrat.asteroids.Asteroids;
import com.chromosundrift.vectorbrat.geom.GlobalModel;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.ModelAnimator;
import com.chromosundrift.vectorbrat.geom.Pattern;
import com.chromosundrift.vectorbrat.geom.StaticAnimator;
import com.chromosundrift.vectorbrat.laser.LaserDisplay;
import com.chromosundrift.vectorbrat.swing.Controllers;
import com.chromosundrift.vectorbrat.swing.DisplayController;
import com.chromosundrift.vectorbrat.swing.DisplayPanel;
import com.chromosundrift.vectorbrat.swing.VectorBratFrame;

public class VectorBrat {

    private static final Logger logger = LoggerFactory.getLogger(VectorBrat.class);
    private final VectorBratFrame frame;
    private final LaserDisplay laser;
    private final DisplayPanel displayPanel;
    private final ExecutorService motion;
    private final AppRunnable appRunnable;

    public VectorBrat() throws VectorBratException {
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
        DisplayController displayController = new DisplayController(false);
        displayPanel = new DisplayPanel(config, displayController, laser);
        displayController.setRepaintDisplay(displayPanel::repaint);
        appRunnable = makeAppRunnable();
        Controllers controllers = new Controllers(displayController, laser, appRunnable);
        frame = new VectorBratFrame(config, displayPanel, controllers);
        motion = Executors.newSingleThreadExecutor(r -> new Thread(r, "VBrat-Motion"));
    }

    private AppRunnable makeAppRunnable() {
        TreeMap<String, ModelAnimator> animators = new TreeMap<>();
        animators.put("Test Pattern 1", new StaticAnimator(Pattern.testPattern1().scale(0.5f)));
        animators.put("Sine Waves", new StaticAnimator(Pattern.sineWaves(Color.RED)));
        animators.put("Box Grid", new StaticAnimator(Pattern.boxGrid(6, 4, Color.CYAN)));
        animators.put("Asteroids", new Asteroids());
        // TODO move the time supplier out of here (use jack)

        return new AppRunnable(animators, "Sine Waves", this::setModel, System::nanoTime);
    }

    public static void main(String[] args) {
        setSystemLibraryPath();

        try {

            VectorBrat vectorBrat = new VectorBrat();
            vectorBrat.start();
        } catch (VectorBratException e) {
            logger.error("can't create vectorbrat", e);
        }

    }

    private void start() throws VectorBratException {
        GlobalModel empty = new GlobalModel();
        setModel(empty);
        this.frame.start();
        this.laser.start();
        this.appRunnable.setAnimator("Test Pattern 1");
        this.motion.submit(appRunnable);
        logger.info("started VectorBrat");
    }

    private void setModel(Model empty) {
        this.displayPanel.setModel(empty);
        this.laser.setModel(empty);
    }

}
