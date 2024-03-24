package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.geom.Interpolation;
import com.chromosundrift.vectorbrat.geom.Interpolator;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Pattern;
import com.chromosundrift.vectorbrat.geom.Rgb;
import com.chromosundrift.vectorbrat.laser.BeamTuning;
import com.chromosundrift.vectorbrat.laser.LaserSpec;
import com.chromosundrift.vectorbrat.physics.BeamPhysics;
import com.chromosundrift.vectorbrat.physics.BulletClock;
import com.chromosundrift.vectorbrat.physics.ConstAccelBeamPhysics;
import com.chromosundrift.vectorbrat.physics.LaserSimulator;
import com.chromosundrift.vectorbrat.physics.LinearBeamPhysics;
import com.chromosundrift.vectorbrat.physics.PropAccelBeamPhysics;
import com.chromosundrift.vectorbrat.swing.SimulatorPanel;
import com.chromosundrift.vectorbrat.swing.UiUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import java.awt.GraphicsConfiguration;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


/**
 * Demo app for LaserSimulator.
 */
public class SimDemo {

    private static final Logger logger = LoggerFactory.getLogger(SimDemo.class);

    public static final BulletClock CLOCK = new BulletClock(0.005f);

    // COLOR_RATE is rgb units per second where 1 unit is the difference between full bright to full dark in the eye
    public static final float COLOR_RATE = 4000f;

    public static final BeamPhysics LBP = new LinearBeamPhysics(0.000d, COLOR_RATE);

    public static final double MAX_SPEED = 50_000_000d;
    public static final double XY_ACCEL = MAX_SPEED * 50000;

    public static final BeamPhysics CABP = new ConstAccelBeamPhysics(XY_ACCEL, MAX_SPEED, COLOR_RATE);

    public static final BeamPhysics PABP = new PropAccelBeamPhysics(10_000_000d, COLOR_RATE);

    public static final float SAMPLE_RATE = 96000f;

    public static void main(String[] args) {
        GraphicsConfiguration gConfig = UiUtil.getPreferredGraphicsConfiguration();
        JFrame jFrame = new JFrame("Laser Simulator Demo", gConfig);
        Config config = new Config();

        BeamTuning tuning = config.getBeamTuning();
//        BeamTuning tuning = BeamTuning.noInterpolation(30000);
//        BeamTuning tuning = new BeamTuning(30000, 5f, 15f, 3f, 4f, 2f);

        LaserSpec laserSpec = LaserSpec.laserWorld1600Pro();
        LaserSimulator sim = new LaserSimulator(laserSpec, tuning, CABP, CLOCK);
        sim.setSampleRate(SAMPLE_RATE);


//        Model m = testPattern1();
//        Model m = simpleModel();
        Model m = complexModel();

        Interpolation interpolation = config.getInterpolation();
        Interpolator pather = new Interpolator(interpolation, tuning);
        pather.plan(m);
        sim.makePath(pather);
        SimulatorPanel simulatorPanel = new SimulatorPanel(sim);
        simulatorPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_SPACE) {
                    simulatorPanel.toggleShowUpdates();
                }
            }
        });
        simulatorPanel.showUpdates(true);
        jFrame.add(simulatorPanel);
        UiUtil.typifyFrame(jFrame, 1000, 800);
        sim.start();
        //noinspection InfiniteLoopStatement
        while (true) {

            simulatorPanel.repaint();
            try {
                //noinspection BusyWait
                Thread.sleep(1);
            } catch (InterruptedException e) {
                logger.warn("interrupted exception (ignoring)", e);
            }
        }
    }

    private static Model simpleModel() {
        return Pattern.boxGrid(2, 2, Rgb.ORANGE);
    }

    private static Model complexModel() {
        return Pattern.boxGrid(3, 3, Rgb.CYAN).scale(0.9f, 0.9f).merge(
                Pattern.boxGrid(2, 2, Rgb.ORANGE)
        );
    }
}
