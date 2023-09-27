package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.asteroids.Asteroids;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.laser.LaserDisplay;
import com.chromosundrift.vectorbrat.physics.BulletClock;
import com.chromosundrift.vectorbrat.physics.LaserSimulator;
import com.chromosundrift.vectorbrat.swing.DisplayController;
import com.chromosundrift.vectorbrat.swing.DisplayPanel;
import com.chromosundrift.vectorbrat.swing.UiUtil;
import com.chromosundrift.vectorbrat.swing.VectorBratFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.chromosundrift.vectorbrat.Util.setSystemLibraryPath;

/**
 * Main application class.
 */
public class VectorBrat {

    public static final String THREAD_ANIMATION = "Animation";
    private static final Logger logger = LoggerFactory.getLogger(VectorBrat.class);
    private final VectorBratFrame frame;
    private final LaserDisplay laser;
    private final DisplayPanel displayPanel;
    private final ExecutorService motion;
    private final AppMap appMap;
    private final LaserSimulator simulator;

    public static void main(String[] args) {
        setSystemLibraryPath();
        Util.bridgeJulToSlf4j();
        try {
            UiUtil.setUiGlobals();
            VectorBrat vectorBrat = new VectorBrat();
            vectorBrat.dumpAnimators();
            vectorBrat.start();
        } catch (VectorBratException e) {
            logger.error("can't create vectorbrat", e);
        }
    }

    public VectorBrat() throws VectorBratException {
        logger.info("initialising VectorBrat");
        final Config config = new Config();
        laser = new LaserDisplay(config);
        final BulletClock clock = new BulletClock(1.0f);
        simulator = new LaserSimulator(config.getLaserSpec(), laser.getTuning(), config.getBeamPhysics(), clock);
        DisplayController displayController = new DisplayController(DisplayController.Mode.DISPLAY);
        displayPanel = new DisplayPanel(config, displayController, laser, simulator);
        displayController.setRepaintDisplay(displayPanel::repaint);
        appMap = Demos.mkAppMap(this::setModel, "VECTORBRAT", config.getLaserSpec());
        appMap.setAnimator(Asteroids.NAME);
        Controllers controllers = new Controllers(displayController, laser, appMap);
        frame = new VectorBratFrame(config, displayPanel, controllers, appMap);
        motion = Executors.newSingleThreadExecutor(r -> new Thread(r, THREAD_ANIMATION));
    }

    private void start() throws VectorBratException {
        logger.info("starting VectorBrat");
        setModel(Model.EMPTY);
        frame.start();


        motion.submit(appMap);
        // set initial sample rate, may need to be updated
//        simulator.setSampleRate(Config.DEFAULT_SAMPLE_RATE);
//        simulator.start();
        laser.startup();
        logger.info("started VectorBrat");
    }

    private void setModel(Model m) {
        displayPanel.setModel(m);
        laser.setModel(m);
    }

    private void dumpAnimators() {
        appMap.getAnimators().forEach(a -> logger.info("animator: " + a));
    }

}
