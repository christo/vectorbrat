package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.geom.*;
import com.chromosundrift.vectorbrat.laser.BeamTuning;
import com.chromosundrift.vectorbrat.laser.LaserSpec;
import com.chromosundrift.vectorbrat.physics.BeamPhysics;
import com.chromosundrift.vectorbrat.physics.BulletClock;
import com.chromosundrift.vectorbrat.physics.LaserSimulator;
import com.chromosundrift.vectorbrat.physics.LinearBeamPhysics;
import com.chromosundrift.vectorbrat.swing.SimulatorPanel;
import com.chromosundrift.vectorbrat.swing.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;

/**
 * Demo app for LaserSimulator.
 */
public class SimDemo {

    private static final Logger logger = LoggerFactory.getLogger(SimDemo.class);

    public static final float CLOCK_RATE = 0.001f;

    /**
     * Rate of motion in normalised bipolar units per second
     */
    public static final float XY_RATE = 20000f;

    // COLOR_RATE is rgb units per second where 1 unit is the difference between full bright to full dark in the eye
    public static final float COLOR_RATE = 5f;

    public static final float SAMPLE_RATE = 96000f;

    public static void main(String[] args) {
        GraphicsConfiguration gConfig = UiUtil.getPreferredGraphicsConfiguration();
        JFrame jFrame = new JFrame("Laser Simulator Demo", gConfig);
        Config config = new Config();

        BeamPhysics physics = new LinearBeamPhysics(XY_RATE, COLOR_RATE);
        BulletClock clock = new BulletClock(CLOCK_RATE);

        BeamTuning tuning = new BeamTuning(30000, 3, 8, 3, 3, 3);

        LaserSpec laserSpec = LaserSpec.laserWorld1600Pro();
        LaserSimulator sim = new LaserSimulator(laserSpec, tuning, physics, clock);
        sim.setSampleRate(SAMPLE_RATE);

        Model m = Pattern.boxGrid(3, 3, Rgb.CYAN);

        Interpolation interpolation = config.getInterpolation();
        Interpolator pather = new Interpolator(interpolation, tuning);
        pather.plan(m);
        sim.makePath(pather);
        SimulatorPanel simulatorPanel = new SimulatorPanel(sim);
        simulatorPanel.showUpdates(true);
        jFrame.add(simulatorPanel);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setPreferredSize(new Dimension(1000, 800));
        jFrame.pack();

        // move the window to the center of preferred screen
        Dimension actualSize = jFrame.getSize();
        Rectangle bounds = gConfig.getBounds();
        jFrame.setLocation(bounds.width /2 - actualSize.width/2, bounds.height /2 - actualSize.height/2);

        jFrame.setVisible(true);
        sim.start();
        //noinspection InfiniteLoopStatement
        while (true) {
            simulatorPanel.repaint();
            try {
                //noinspection BusyWait
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.warn("interrupted exception (ignoring)", e);
            }
        }
    }

    private static SimplePather getSimplePather(Model m) {
        return new SimplePather(GeomUtils.linePoints(m).toList());
    }

}
