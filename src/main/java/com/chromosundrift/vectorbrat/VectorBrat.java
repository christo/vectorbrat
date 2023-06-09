package com.chromosundrift.vectorbrat;

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
import com.chromosundrift.vectorbrat.geom.BungeeAnimator;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Pattern;
import com.chromosundrift.vectorbrat.geom.Polyline;
import com.chromosundrift.vectorbrat.geom.Rgb;
import com.chromosundrift.vectorbrat.geom.TextEngine;
import com.chromosundrift.vectorbrat.laser.LaserDisplay;
import com.chromosundrift.vectorbrat.swing.Controllers;
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

    public VectorBrat() throws VectorBratException {
        logger.info("initialising VectorBrat");
        final Config config = new Config();

        setLookAndFeel();

        laser = new LaserDisplay(config);
        DisplayController displayController = new DisplayController(false);
        displayPanel = new DisplayPanel(config, displayController, laser);
        displayController.setRepaintDisplay(displayPanel::repaint);
        appMap = makeAppMap();
        Controllers controllers = new Controllers(displayController, laser, appMap);
        frame = new VectorBratFrame(config, displayPanel, controllers);
        motion = Executors.newSingleThreadExecutor(r -> new Thread(r, THREAD_ANIMATION));
    }

    private static void setLookAndFeel() throws VectorBratException {
        try {
            logger.info("setting look and feel");
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

    private AppMap makeAppMap() {
        AppMap ar = new AppMap(this::setModel, System::nanoTime);
        String text = "VECTORBRAT";
        ar.add(new BungeeAnimator(mkTextModel(text), 900, 0.3f, 0.8f));
        Model aModel = mkTextModel("A");
        ar.add(new BungeeAnimator(aModel, 1500, 0.1f, 0.1f));
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

    private static Model mkTextModel(String text) {
        TextEngine te = new TextEngine(Rgb.CYAN, new AsteroidsFont());
        float yScale = (float) (1.0 / text.length());
        return te.textLine(text).scale(0.6f, yScale);
    }

    private void start() throws VectorBratException {
        Model empty = new Model();
        setModel(empty);
        this.frame.start();
        this.laser.start();
        appMap.setAnimator("Asteroids");

        this.motion.submit(appMap);
        logger.info("started VectorBrat");
    }

    private void setModel(Model empty) {
        this.displayPanel.setModel(empty);
        this.laser.setModel(empty);
    }

}
