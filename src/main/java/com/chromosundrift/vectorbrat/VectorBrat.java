package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.asteroids.Asteroids;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.laser.LaserDisplay;
import com.chromosundrift.vectorbrat.physics.BulletClock;
import com.chromosundrift.vectorbrat.physics.LaserSimulator;
import com.chromosundrift.vectorbrat.physics.LinearBeamPhysics;
import com.chromosundrift.vectorbrat.swing.DisplayController;
import com.chromosundrift.vectorbrat.swing.DisplayPanel;
import com.chromosundrift.vectorbrat.swing.VectorBratFrame;
import io.materialtheme.darkstackoverflow.DarkStackOverflowTheme;
import mdlaf.MaterialLookAndFeel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.chromosundrift.vectorbrat.Util.setSystemLibraryPath;

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
            VectorBrat vectorBrat = new VectorBrat();
            vectorBrat.dumpAnimators();
            vectorBrat.start();
        } catch (VectorBratException e) {
            logger.error("can't create vectorbrat", e);
        }
    }

    private static void setUiGlobals() throws VectorBratException {
        try {
            logger.info("setting look and feel");
            System.setProperty("sun.java2d.uiScale", "2");
            UIManager.setLookAndFeel(new MaterialLookAndFeel(new DarkStackOverflowTheme()));
        } catch (UnsupportedLookAndFeelException e) {
            throw new VectorBratException(e);
        }
    }

    public VectorBrat() throws VectorBratException {
        logger.info("initialising VectorBrat");
        final Config config = new Config();

        setUiGlobals();

        laser = new LaserDisplay(config);
        final BulletClock clock = new BulletClock(1.0f);
        // TODO get beam physics from config and modify in control panel
        LinearBeamPhysics physics = new LinearBeamPhysics(1f, 1f);
        simulator = new LaserSimulator(config.getLaserSpec(), laser.getTuning(), physics, clock);
        DisplayController displayController = new DisplayController(DisplayController.Mode.DISPLAY);
        displayPanel = new DisplayPanel(config, displayController, laser, simulator);
        displayController.setRepaintDisplay(displayPanel::repaint);
        appMap = Demos.mkAppMap(this::setModel);
        Controllers controllers = new Controllers(displayController, laser, appMap);
        frame = new VectorBratFrame(config, displayPanel, controllers);
        motion = Executors.newSingleThreadExecutor(r -> new Thread(r, THREAD_ANIMATION));
    }

    private void start() throws VectorBratException {
        logger.info("starting VectorBrat");
        setModel(Model.EMPTY);
        this.frame.start();
        appMap.setAnimator(Asteroids.NAME);

        this.motion.submit(appMap);
        this.simulator.start();
        logger.info("started VectorBrat");
    }

    private void setModel(Model m) {
        this.displayPanel.setModel(m);
        this.laser.setModel(m);
    }

    private void dumpAnimators() {
        appMap.getAnimators().forEach(a -> logger.info("animator: " + a));
    }

}
