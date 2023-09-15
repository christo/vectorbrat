package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.physics.LaserSimulator;
import com.chromosundrift.vectorbrat.physics.LinearBeamPhysics;
import com.chromosundrift.vectorbrat.physics.SystemClock;
import io.materialtheme.darkstackoverflow.DarkStackOverflowTheme;
import mdlaf.MaterialLookAndFeel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.chromosundrift.vectorbrat.Util.setSystemLibraryPath;

import com.chromosundrift.vectorbrat.asteroids.Asteroid;
import com.chromosundrift.vectorbrat.asteroids.Asteroids;
import com.chromosundrift.vectorbrat.geom.AsteroidsFont;
import com.chromosundrift.vectorbrat.anim.BungeeAnimator;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Pattern;
import com.chromosundrift.vectorbrat.geom.Polyline;
import com.chromosundrift.vectorbrat.geom.Rgb;
import com.chromosundrift.vectorbrat.geom.TextEngine;
import com.chromosundrift.vectorbrat.laser.LaserDisplay;
import com.chromosundrift.vectorbrat.physics.BulletClock;
import com.chromosundrift.vectorbrat.swing.DisplayController;
import com.chromosundrift.vectorbrat.swing.DisplayPanel;
import com.chromosundrift.vectorbrat.swing.VectorBratFrame;

public class VectorBrat {

    public static final String THREAD_ANIMATION = "Animation";
    private static final Logger logger = LoggerFactory.getLogger(VectorBrat.class);
    private final VectorBratFrame frame;
    private final LaserDisplay laser;
    private final DisplayPanel displayPanel;
    private final ExecutorService motion;
    private final AppMap appMap;
    private final LaserSimulator simulator;

    public VectorBrat() throws VectorBratException {
        logger.info("initialising VectorBrat");
        final Config config = new Config();

        setUiGlobals();

        laser = new LaserDisplay(config);
        final BulletClock clock = new BulletClock(1.0f);
        simulator = mkSimulator(config, laser, clock);
        DisplayController displayController = new DisplayController(DisplayController.Mode.DISPLAY);
        displayPanel = new DisplayPanel(config, displayController, laser, simulator);
        displayController.setRepaintDisplay(displayPanel::repaint);
        appMap = makeAppMap();
        Controllers controllers = new Controllers(displayController, laser, appMap);
        frame = new VectorBratFrame(config, displayPanel, controllers);
        motion = Executors.newSingleThreadExecutor(r -> new Thread(r, THREAD_ANIMATION));
    }

    private static LaserSimulator mkSimulator(Config config, LaserDisplay laser, BulletClock clock) {
        // TODO get beam physics from config and modify in control panel
        LinearBeamPhysics physics = new LinearBeamPhysics(1f, 1f);
        return new LaserSimulator(config.getLaserSpec(), laser.getTuning(), physics, clock);
    }

    private static void setUiGlobals() throws VectorBratException {
        try {
            logger.info("setting look and feel");
            System.setProperty("sun.java2d.uiScale","2");
            UIManager.setLookAndFeel(new MaterialLookAndFeel(new DarkStackOverflowTheme()));
        } catch (UnsupportedLookAndFeelException e) {
            throw new VectorBratException(e);
        }
    }

    public static void main(String[] args) {
        setSystemLibraryPath();
        Util.bridgeJulToSlf4j();
        try {
            VectorBrat vectorBrat = new VectorBrat();
            vectorBrat.appMap.getAnimators().forEach(a -> logger.info("animator: " + a));
            vectorBrat.start();
        } catch (VectorBratException e) {
            logger.error("can't create vectorbrat", e);
        }
    }

    private static Model mkTextModel(String text) {
        TextEngine te = new TextEngine(Rgb.CYAN, new AsteroidsFont());
        float yScale = (float) (1.0 / text.length());
        return te.textLine(text).scale(0.6f, yScale);
    }

    private AppMap makeAppMap() {
        AppMap ar = new AppMap(this::setModel, SystemClock.INSTANCE);
        String text = "VECTORBRAT";
        ar.add(new BungeeAnimator(mkTextModel(text), 900, 0.6f, 0.8f));
        Model aModel = mkTextModel("A");
        ar.add(new BungeeAnimator(aModel, 1500, 0.9f, 0.1f));
        ar.add(new Asteroids());

        ar.add(Pattern.testPattern1().scale(0.8f, 0.8f));
        ar.add(Pattern.sineWaves(Rgb.RED));
        ar.add(Pattern.boxGrid(3, 2, Rgb.CYAN));
        ar.add(mkRock());
        return ar;
    }

    private Model mkRock() {
        List<Polyline> pls = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            pls.add(new Asteroid(Asteroid.Size.LARGE, new Random()).toPolyline());
        }
        return new Model("rock", pls);
    }

    private void start() throws VectorBratException {
        Model empty = Model.EMPTY;
        setModel(empty);
        this.frame.start();
        appMap.setAnimator(Asteroids.NAME);

        this.motion.submit(appMap);
        this.simulator.start();
        logger.info("started VectorBrat");
    }

    private void setModel(Model empty) {
        this.displayPanel.setModel(empty);
        this.laser.setModel(empty);
    }

}
