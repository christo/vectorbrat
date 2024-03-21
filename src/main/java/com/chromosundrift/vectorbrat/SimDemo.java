package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.geom.*;
import com.chromosundrift.vectorbrat.laser.BeamTuning;
import com.chromosundrift.vectorbrat.laser.LaserSpec;
import com.chromosundrift.vectorbrat.physics.BeamPhysics;
import com.chromosundrift.vectorbrat.physics.BulletClock;
import com.chromosundrift.vectorbrat.physics.ConstAccelBeamPhysics;
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
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.chromosundrift.vectorbrat.physics.LaserSimulator.colorRate;
import static com.chromosundrift.vectorbrat.physics.LaserSimulator.xyRate;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Demo app for LaserSimulator.
 */
public class SimDemo {

    private static final Logger logger = LoggerFactory.getLogger(SimDemo.class);

    public static final BulletClock CLOCK = new BulletClock(0.0008f);

    // COLOR_RATE is rgb units per second where 1 unit is the difference between full bright to full dark in the eye
    public static final float COLOR_RATE = colorRate(10, MILLISECONDS);

    public static final BeamPhysics LBP = new LinearBeamPhysics(xyRate(100, MICROSECONDS), COLOR_RATE);

    public static final float MAX_SPEED = 20_000f;
    public static final float XY_ACCEL = MAX_SPEED * 2000;

    public static final BeamPhysics CABP = new ConstAccelBeamPhysics(XY_ACCEL, MAX_SPEED, COLOR_RATE);

    public static final float SAMPLE_RATE = 192000f;

    public static void main(String[] args) {
        GraphicsConfiguration gConfig = UiUtil.getPreferredGraphicsConfiguration();
        JFrame jFrame = new JFrame("Laser Simulator Demo", gConfig);
        Config config = new Config();

        BeamTuning tuning = config.getBeamTuning();

        LaserSpec laserSpec = LaserSpec.laserWorld1600Pro();
        LaserSimulator sim = new LaserSimulator(laserSpec, tuning, CABP, CLOCK);
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
}
