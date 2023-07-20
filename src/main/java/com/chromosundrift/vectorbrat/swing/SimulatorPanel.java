package com.chromosundrift.vectorbrat.swing;

import com.chromosundrift.vectorbrat.physics.LaserSimulator;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class SimulatorPanel extends JPanel {

    private final SimulatorRenderer simulatorRenderer;
    private final LaserSimulator simulator;
    private volatile boolean showUpdates;

    public SimulatorPanel(LaserSimulator laserSimulator) {
        simulatorRenderer = new SimulatorRenderer(laserSimulator);
        simulator = laserSimulator;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Dimension s = getSize();
        int width = s.width;
        int height = s.height;

        final BufferedImage im = new BufferedImage(width, height, TYPE_INT_ARGB);

        final Graphics2D g2 = im.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setBackground(Color.BLACK);
        g2.clearRect(0, 0, width, height);

        int nPoints = simulatorRenderer.draw(im, g2);
        if (showUpdates) {
            g2.setColor(Color.GREEN);
            g2.drawString("sim time (ns): %s".formatted(simulator.getTime()), 20, 20);
            g2.drawString("points: %s".formatted(nPoints), 20, 35);
            g2.drawString("trail: %s front: %s/%s".formatted(simulator.getTrailIndex(), simulator.getFrontIndex(), simulator.getFrontSize()), 20, 50);
        }
        g.drawImage(im, 0, 0, null);
    }

    public void showUpdates(boolean showUpdates) {
        this.showUpdates = showUpdates;
    }
}
