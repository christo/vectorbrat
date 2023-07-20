package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.geom.GeomUtils;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Pattern;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.Rgb;
import com.chromosundrift.vectorbrat.geom.SimplePather;
import com.chromosundrift.vectorbrat.laser.BeamTuning;
import com.chromosundrift.vectorbrat.laser.LaserSpec;
import com.chromosundrift.vectorbrat.physics.BeamPhysics;
import com.chromosundrift.vectorbrat.physics.BulletClock;
import com.chromosundrift.vectorbrat.physics.Clock;
import com.chromosundrift.vectorbrat.physics.LaserSimulator;
import com.chromosundrift.vectorbrat.physics.LinearBeamPhysics;
import com.chromosundrift.vectorbrat.physics.SystemClock;
import com.chromosundrift.vectorbrat.swing.SimulatorPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.util.List;

/**
 * Demo app for LaserSimulator.
 */
public class SimDemo {

    private static final Logger logger = LoggerFactory.getLogger(SimDemo.class);

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("Laser Simulator");

        Config config = new Config();
        LaserSimulator laserSimulator = slowLinear(1f);
        Model m = Pattern.boxGrid(5, 5, Rgb.CYAN);
        List<Point> points = GeomUtils.linePoints(m).toList();
        SimplePather simplePather = new SimplePather(points);

        laserSimulator.makePath(simplePather);
        SimulatorPanel simulatorPanel = new SimulatorPanel(laserSimulator);
        simulatorPanel.showUpdates(true);
        jFrame.add(simulatorPanel);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setPreferredSize(new Dimension(600, 500));
        jFrame.pack();
        jFrame.setVisible(true);
        laserSimulator.setSampleRate(1000f);
        laserSimulator.start();
        while (true) {

            simulatorPanel.repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }

        }
    }

    public static LaserSimulator slowLinear(float secondsToCrossScreen) {
        float unitsPerScreen = Config.SAMPLE_RANGE;
        float msPerUnit = secondsToCrossScreen * 1000 / unitsPerScreen;
        float colorRate = msPerUnit / 4; // arbitrary
        BeamPhysics physics = new LinearBeamPhysics(msPerUnit, colorRate);
        Config c = new Config();
        BeamTuning tuning = BeamTuning.noInterpolation(10);

        LaserSimulator sim = new LaserSimulator(LaserSpec.laserWorld1600Pro(), tuning, physics, SystemClock.INSTANCE);
        sim.setSampleRate(Config.DEFAULT_SAMPLE_RATE);

        return sim;
    }
}
