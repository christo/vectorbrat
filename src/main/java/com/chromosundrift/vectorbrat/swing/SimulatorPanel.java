package com.chromosundrift.vectorbrat.swing;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;

import com.chromosundrift.vectorbrat.laser.LaserSimulator;

/**
 * UI component that draws the {@link com.chromosundrift.vectorbrat.laser.LaserSimulator} output.
 */
public class SimulatorPanel extends JPanel {

    private final LaserSimulator simulator;
    private int[] rgbArray;
    private int previousWidth;
    private int previousHeight;

    public SimulatorPanel(LaserSimulator simulator) {
        this.simulator = simulator;
    }

    // TODO refactor: introduce common base class and pull up common paint code from here and DisplayPanel

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        final Dimension s = getSize();
        int imageScale = 2; // TODO do this retina compensation properly as with DisplayPanel
        int imWidth = s.width;
        int imHeight = s.height;
        BufferedImage im = new BufferedImage(
                imWidth * imageScale,
                imHeight * imageScale,
                BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = im.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setBackground(Color.BLACK);

        g2.clearRect(0, 0, im.getWidth(), im.getHeight());

        // TODO reconstruct rgbArray on panel resize
        if (previousWidth != imWidth || previousHeight != imHeight) {
            previousWidth = imWidth;
            previousHeight = imHeight;
            rgbArray = new int[imWidth * imHeight];
        }
        // construct the image
        simulator.render(rgbArray);
        int scansize = imWidth; // TODO verify this is correct or what is scansize anyway?
        im.setRGB(0, 0, imWidth, imHeight, rgbArray, 0, scansize);

        g.drawImage(im, 0, 0, imWidth, imHeight, Color.BLACK, null);
        g2.dispose();
    }

}
