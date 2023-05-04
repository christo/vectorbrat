package com.chromosundrift.vectorbrat.swing;

import com.chromosundrift.vectorbrat.DoubleBufferedVectorDisplay;
import com.chromosundrift.vectorbrat.VectorDisplay;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Polygon;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.function.Function;


/**
 * Threadsafe JPanel implementation of VectorDeplay.
 */
public final class DisplayPanel extends JPanel implements VectorDisplay {

    public static final int IMAGE_SCALE = 2;
    private final Color colText = Color.getHSBColor(0.4f, 0.4f, 0.25f);
    private final Color colBg = Color.getHSBColor(0, 0, 0.1f);
    private final int inset = 50;
    private final DoubleBufferedVectorDisplay vectorDisplay;
    private final Font defaultFont;

    public DisplayPanel() {
        this.vectorDisplay = new DoubleBufferedVectorDisplay();
        setBackground(Color.BLACK);
        setForeground(Color.GREEN);
        defaultFont = new Font("HelveticaNeue", Font.BOLD, 80);
    }

    /**
     * Not threadsafe
     */
    private void unsafePaint(final Graphics g, final Model model) {
        super.paint(g);
        //setBackground(Color.GREEN);
        final Dimension s = getSize();

        int imWidth = s.width - inset * 2;
        int imHeight = s.height - inset * 2;
        // TODO ? physical screen resolution so the image can be made at that scaling factor
        BufferedImage im = new BufferedImage(imWidth * IMAGE_SCALE, imHeight * IMAGE_SCALE, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = im.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setBackground(colBg);

        g2.clearRect(0, 0, im.getWidth(), im.getHeight());

        branding(im, g2);

        g2.setColor(Color.LIGHT_GRAY);
        model.polygons().map(polymorph(im.getWidth(), im.getHeight())).forEach(g2::drawPolygon);
        g.drawImage(im, inset, inset, imWidth, imHeight, Color.BLACK, null);
        g2.dispose();
    }

    private void branding(BufferedImage im, Graphics2D g2) {
        // centred string
        g2.setColor(colText);
        final String mesg = im.getCapabilities(null).isAccelerated() ? "VectorBrat >> accel" : "VectorBrat";
        g2.setFont(defaultFont);
        FontMetrics fontMetrics = g2.getFontMetrics();
        final Rectangle2D stringBounds = fontMetrics.getStringBounds(mesg, g2);
        //g2.drawRect((int) (int) ((im.getWidth() / 2) - stringBounds.getWidth() / 2), (int) ((im.getHeight() / 2) - stringBounds.getHeight() / 2), (int) stringBounds.getWidth(), (int) stringBounds.getHeight());
        LineMetrics lineMetrics = fontMetrics.getLineMetrics(mesg, g2);
        g2.drawString(mesg, (int) ((im.getWidth() / 2) - stringBounds.getWidth() / 2), (int) ((im.getHeight() / 2) + lineMetrics.getAscent() / 2));
    }

    /**
     * Converts our {@link Polygon} to a {@link java.awt.Polygon} scaling from normalised using the given
     * factors.
     *
     * @param xScale the x-axis scaling factor
     * @param yScale the y-axis scaling factor
     * @return the awt Polygon
     */
    private static Function<? super Polygon, java.awt.Polygon> polymorph(final int xScale, final int yScale) {
        return p -> {
            final java.awt.Polygon polygon = new java.awt.Polygon();
            p.points().forEachOrdered(pt -> polygon.addPoint((int) (pt.x() * xScale), (int) (pt.y() * yScale)));
            return polygon;
        };
    }

    @Override
    public void paint(final Graphics g) {
        vectorDisplay.withLockAndFlip(model -> {
            unsafePaint(g, model);
            return null;
        });
    }

    @Override
    public VectorDisplay setModel(final Model model) {
        vectorDisplay.setModel(model);
        return this;
    }
}
