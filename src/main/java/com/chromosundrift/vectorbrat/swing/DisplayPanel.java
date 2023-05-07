package com.chromosundrift.vectorbrat.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.BasicStroke;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.DoubleBufferedVectorDisplay;
import com.chromosundrift.vectorbrat.VectorDisplay;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Polygon;


/**
 * Threadsafe JPanel implementation of VectorDeplay.
 */
public final class DisplayPanel extends JPanel implements VectorDisplay {

    public static final float ARCHAIC_SCREEN_RESOLUTION = 72f;
    private static final Logger logger = LoggerFactory.getLogger(DisplayPanel.class);
    private static final int MIN_WIDTH = 100;
    private static final int MIN_HEIGHT = 100;
    private final Color colText = Color.getHSBColor(0.83f, 0.5f, 0.9f);
    private final Color colBg = Color.getHSBColor(0, 0, 0.0f);

    private final DoubleBufferedVectorDisplay vectorDisplay;
    private final Font defaultFont;
    private final Config config;
    private Optional<BufferedImage> logo = Optional.empty();


    public DisplayPanel(Config config) {
        this.config = config;
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(config.logoUrl());
            if (resourceAsStream != null) {
                logo = Optional.of(ImageIO.read(resourceAsStream));
            }
        } catch (IOException e) {
            logger.warn("Unable to load logo from url " + config.logoUrl(), e);
        }
        this.vectorDisplay = new DoubleBufferedVectorDisplay();
        setBackground(Color.BLACK);
        setForeground(Color.GREEN);
        defaultFont = new Font("HelveticaNeue", Font.BOLD, 130);
        setMinimumSize(new Dimension(400, 300));
        vectorDisplay.setModel(Model.empty());
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

    /**
     * Not threadsafe
     */
    private void unsafePaint(final Graphics g, final Model model) {
        super.paint(g);
        final Dimension s = getSize();
        int imWidth = Math.max(s.width, MIN_WIDTH);
        int imHeight = Math.max(s.height, MIN_HEIGHT);
        int screenResolution = getToolkit().getScreenResolution();
        float imageScale = screenResolution / ARCHAIC_SCREEN_RESOLUTION;

        // TODO ? physical screen resolution so the image can be made at that scaling factor
        BufferedImage im = new BufferedImage((int) (imWidth * imageScale), (int) (imHeight * imageScale), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = im.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC); // TODO check performance
        g2.setBackground(colBg);

        g2.clearRect(0, 0, im.getWidth(), im.getHeight());

        if (model.size() == 0) {
            branding(im, g2);
        }

        g2.setColor(Color.LIGHT_GRAY);
        model.polygons().map(polymorph(im.getWidth(), im.getHeight())).forEach(g2::drawPolygon);
        g.drawImage(im, 0, 0, imWidth, imHeight, Color.BLACK, null);
        g2.dispose();
    }

    private void branding(final BufferedImage im, final Graphics2D g2) {
        int targetCentreX = im.getWidth() / 2;
        int targetCentreY = im.getHeight() / 2;

        logo.ifPresent(logo -> {
            float targeSide = Math.min(im.getWidth(), im.getHeight());
            float sourceSide = Math.min(logo.getWidth(), logo.getHeight());

            g2.drawImage(logo, targetCentreX - logo.getWidth() / 2, targetCentreY - logo.getHeight() / 2, logo.getWidth(), logo.getHeight(), null);
        });

        // centred string
        g2.setColor(colText);

        g2.setStroke(new BasicStroke(3f));
        final String mesg = config.getTitle().toUpperCase();
        g2.setFont(defaultFont);
        final FontMetrics fontMetrics = g2.getFontMetrics();
        final Rectangle2D stringBounds = fontMetrics.getStringBounds(mesg, g2);
        final LineMetrics lineMetrics = fontMetrics.getLineMetrics(mesg, g2);

        g2.drawString(mesg, (int) (targetCentreX - stringBounds.getWidth() / 2), (int) (im.getHeight() - lineMetrics.getHeight() * 0.6));
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
