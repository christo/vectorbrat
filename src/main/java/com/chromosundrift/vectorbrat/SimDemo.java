package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.geom.GeomUtils;
import com.chromosundrift.vectorbrat.geom.Interpolator;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Pattern;
import com.chromosundrift.vectorbrat.geom.Rgb;
import com.chromosundrift.vectorbrat.geom.SimplePather;
import com.chromosundrift.vectorbrat.laser.BeamTuning;
import com.chromosundrift.vectorbrat.laser.LaserSpec;
import com.chromosundrift.vectorbrat.physics.BeamPhysics;
import com.chromosundrift.vectorbrat.physics.BulletClock;
import com.chromosundrift.vectorbrat.physics.LaserSimulator;
import com.chromosundrift.vectorbrat.physics.LinearBeamPhysics;
import com.chromosundrift.vectorbrat.physics.SystemClock;
import com.chromosundrift.vectorbrat.swing.SimulatorPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;

/**
 * Demo app for LaserSimulator.
 */
public class SimDemo {

    private static final Logger logger = LoggerFactory.getLogger(SimDemo.class);

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Laser Simulator");

        BeamPhysics physics = new LinearBeamPhysics(0.001f, 1f);
        BeamTuning tuning = BeamTuning.noInterpolation(30000);
        BulletClock clock = new BulletClock(SystemClock.INSTANCE, 0.1f);
        LaserSimulator sim = new LaserSimulator(LaserSpec.laserWorld1600Pro(), tuning, physics, clock);
        sim.setSampleRate(Config.DEFAULT_SAMPLE_RATE);

        Model m = Pattern.boxGrid(5, 5, Rgb.CYAN);
        Config config = new Config();
        config.setBeamTuning(tuning);

        Interpolator pather = getInterpolator(m, config);
        sim.makePath(pather);
        SimulatorPanel simulatorPanel = new SimulatorPanel(sim);
        simulatorPanel.showUpdates(true);
        jFrame.add(simulatorPanel);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setPreferredSize(new Dimension(900, 700));
        jFrame.pack();
        jFrame.setVisible(true);
        sim.setSampleRate(100f);
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

    private static Interpolator getInterpolator(Model m, Config config) {
        Interpolator pather = new Interpolator(config);
        pather.plan(m);
        return pather;
    }

    private static SimplePather getSimplePather(Model m) {
        return new SimplePather(GeomUtils.linePoints(m).toList());
    }

}
