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
import java.util.stream.Stream;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.DoubleBufferedVectorDisplay;
import com.chromosundrift.vectorbrat.VectorDisplay;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Polygon;


/**
 * Threadsafe JPanel implementation of VectorDeplay.
 */
public final class DisplayPanel extends JPanel implements VectorDisplay {

    private static final Logger logger = LoggerFactory.getLogger(DisplayPanel.class);
    private static final int MIN_WIDTH = 100;
    private static final int MIN_HEIGHT = 100;
    private final Color colText = Color.getHSBColor(0.83f, 0.5f, 0.9f);
    private final Color colBg = Color.getHSBColor(0, 0, 0.0f);

    private final DoubleBufferedVectorDisplay vectorDisplay;
    private final Font brandingFont;
    private final Config config;
    private Optional<BufferedImage> logo = Optional.empty();
    private final BasicStroke lineStroke;

    public DisplayPanel(Config config) {
        logger.info("initialising DisplayPanel");
        this.config = config;
        try {
            InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(config.logoUrl());
            if (resourceAsStream != null) {
                logo = Optional.of(ImageIO.read(resourceAsStream));
            }
        } catch (IOException e) {
            logger.warn("Unable to load logo from url " + config.logoUrl(), e);
        }
        setBackground(Color.BLACK);
        setForeground(Color.GREEN);
        brandingFont = new Font("HelveticaNeue", Font.PLAIN, 130);
        lineStroke = new BasicStroke(config.getLineWidth());

        setMinimumSize(new Dimension(400, 300));
        setPreferredSize(new Dimension(900, 700));
        this.vectorDisplay = new DoubleBufferedVectorDisplay();
    }

    /**
     * Not threadsafe
     */
    private void unsafePaint(final Graphics g, final Model model) {
        super.paint(g);
        final Dimension s = getSize();
        int imWidth = Math.max(s.width, MIN_WIDTH);
        int imHeight = Math.max(s.height, MIN_HEIGHT);
        float imageScale = 2.0f;

        // TODO ? physical screen resolution so the image can be made at that scaling factor
        BufferedImage im = new BufferedImage((int) (imWidth * imageScale), (int) (imHeight * imageScale), BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = im.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC); // TODO check performance
        g2.setBackground(colBg);

        g2.clearRect(0, 0, im.getWidth(), im.getHeight());

        if (model.isEmpty()) {
            branding(im, g2);
        }

        g2.setColor(Color.LIGHT_GRAY); // TODO line colours

        g2.setStroke(lineStroke);
        Stream<Polygon> polygons = model.polygons();
        polygons.forEach(p -> {
            g2.setColor(p.getColor());
            g2.drawPolygon(p.awt(im.getWidth(), im.getHeight()));
        });
        model.points().forEach(point -> {
            int x = (int) (point.x() * im.getWidth());
            int y = (int) (point.y() * im.getHeight());
            g2.setColor(point.color());
            g2.drawLine(x, y, x, y);
        });
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

        final String mesg = config.getTitle().toUpperCase();
        g2.setFont(brandingFont);
        final FontMetrics fontMetrics = g2.getFontMetrics();
        final Rectangle2D stringBounds = fontMetrics.getStringBounds(mesg, g2);
        final LineMetrics lineMetrics = fontMetrics.getLineMetrics(mesg, g2);
        g2.setColor(Color.BLACK);
        int titleX = (int) (targetCentreX - stringBounds.getWidth() / 2);
        int titleY = (int) (im.getHeight() - lineMetrics.getHeight() * 0.6);

        g2.setColor(colText);
        g2.drawString(mesg, titleX, titleY);
    }

    @Override
    public void paint(final Graphics g) {
        // TODO don't draw over the inset region
        vectorDisplay.withLockAndFlip(model -> {
            unsafePaint(g, model);
            return null;
        });
    }

    @Override
    public void setModel(final Model model) {
        vectorDisplay.setModel(model);
    }
}
