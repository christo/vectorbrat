package com.chromosundrift.vectorbrat.swing;

import com.chromosundrift.vectorbrat.physics.LaserSimulator;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class SimulatorPanel extends JPanel {

    private final SimulatorRenderer simulatorRenderer;
    private final LaserSimulator simulator;
    private volatile boolean showUpdates;
    private final Font fontHud;
    private BufferedImage im;
    private Graphics2D g2;

    public SimulatorPanel(LaserSimulator laserSimulator) {
        simulatorRenderer = new SimulatorRenderer(laserSimulator);
        simulator = laserSimulator;
        fontHud = UiUtil.mkFont(16);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final Dimension s = getSize();
        int width = s.width;
        int height = s.height;

        if (im == null || im.getWidth() != width || im.getHeight() != height) {
            im = new BufferedImage(width, height, TYPE_INT_ARGB);
            g2 = im.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setBackground(Color.BLACK);
        }

        g2.clearRect(0, 0, width, height);

        int nPoints = simulatorRenderer.draw(im, g2);
        String[] lines = new String[]{
                "sim time (ns): %s".formatted(simulator.getElapsedTime()),
                "trail points: %s".formatted(nPoints),
                "trailIndex: %s frontIndex: %s/%s".formatted(simulator.getTrailIndex(), simulator.getFrontIndex(), simulator.getFrontSize()),
                "samples per point: %s".formatted(simulator.getSamplesPerPoint()),
                "sample rate Hz: %s".formatted(simulator.getSampleRate()),
                "beamstate: %s".formatted(simulator.getBeamState())
        };
        if (showUpdates) {
            g2.setColor(Color.GREEN);
            UiUtil.hudLines(g2, height, lines, Color.GREEN, fontHud);

        }
        g.drawImage(im, 0, 0, null);
    }

    public void showUpdates(boolean showUpdates) {
        this.showUpdates = showUpdates;
    }
}
