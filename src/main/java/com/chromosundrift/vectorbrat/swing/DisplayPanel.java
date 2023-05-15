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
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.JOIN_BEVEL;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.DoubleBufferedVectorDisplay;
import com.chromosundrift.vectorbrat.VectorDisplay;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.PathPlanner;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.Polygon;


/**
 * Threadsafe JPanel implementation of VectorDeplay.
 */
public final class DisplayPanel extends JPanel implements VectorDisplay {

    private static final Logger logger = LoggerFactory.getLogger(DisplayPanel.class);
    private static final int MIN_WIDTH = 100;
    private static final int MIN_HEIGHT = 100;
    public static final Color HUD_COLOR = Color.WHITE;
    private final Color colText = Color.getHSBColor(0.83f, 0.5f, 0.9f);
    private final Color colBg = Color.getHSBColor(0, 0, 0.0f);

    private final DoubleBufferedVectorDisplay vectorDisplay;
    private final Font brandingFont;
    private final Config config;
    private final BasicStroke lineStroke;
    private final DisplayController displayController;
    private final Font hudFont;
    private Optional<BufferedImage> logo = Optional.empty();
    private static final BasicStroke PATH_PLAN_STROKE = new BasicStroke(3f);
    private static final BasicStroke PATH_PLAN_OFF_STROKE =
            new BasicStroke(2f, CAP_BUTT, JOIN_BEVEL, 0, new float[]{1, 5}, 0);
    private static final Color PATH_OFF_COLOR = new Color(0.3f, 0.3f, 0.3f);

    public DisplayPanel(Config config, DisplayController displayController) {
        this.displayController = displayController;
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
        hudFont = new Font("HelveticaNeue", Font.PLAIN, 48);
        lineStroke = new BasicStroke(config.getLineWidth());

        setMinimumSize(new Dimension(400, 300));
        setPreferredSize(new Dimension(900, 700));
        this.vectorDisplay = new DoubleBufferedVectorDisplay();
    }

    /**
     * Not threadsafe, called with model lock.
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
            drawBranding(im, g2);
        } else if (displayController.isDrawPathPlan()) {
            drawPathPlan(model, im, g2);
        } else {
            drawModel(model, im, g2);
        }

        g.drawImage(im, 0, 0, imWidth, imHeight, Color.BLACK, null);
        g2.dispose();
    }

    private void drawModel(Model model, BufferedImage im, Graphics2D g2) {
        g2.setStroke(lineStroke);
        Stream<Polygon> polygons = model.polygons();
        polygons.forEach(p -> {
            g2.setColor(p.getColor());
            g2.drawPolygon(p.awt(im.getWidth(), im.getHeight()));
        });
        model.points().forEach(point -> {
            int x = (int) ((point.x() / 2 + 0.5) * im.getWidth());
            int y = (int) ((point.y() / 2 + 0.5) * im.getHeight());
            g2.setColor(point.color());
            g2.drawLine(x, y, x, y);
        });
    }

    private void drawBranding(final BufferedImage im, final Graphics2D g2) {
        int targetCentreX = im.getWidth() / 2;
        int targetCentreY = im.getHeight() / 2;

        logo.ifPresent(logo -> {
            // TODO maintain aspect, but fill minimum of height or width
            float targeSide = Math.min(im.getWidth(), im.getHeight());
            float sourceSide = Math.min(logo.getWidth(), logo.getHeight());

            int logoX = targetCentreX - logo.getWidth() / 2;
            int logoY = targetCentreY - logo.getHeight() / 2;
            g2.drawImage(logo, logoX, logoY, logo.getWidth(), logo.getHeight(), null);
        });

        // centred string

        final String mesg = config.getTitle().toUpperCase();
        g2.setFont(brandingFont);
        final FontMetrics fontMetrics = g2.getFontMetrics();
        final Rectangle2D stringBounds = fontMetrics.getStringBounds(mesg, g2);
        final LineMetrics lineMetrics = fontMetrics.getLineMetrics(mesg, g2);
        g2.setColor(new Color(0f, 0f, 0f, 0.5f));

        int titleX = (int) (targetCentreX - stringBounds.getWidth() / 2);
        int titleY = (int) (im.getHeight() - lineMetrics.getHeight() * 0.6);
        g2.fillRect(0, (int) (titleY - lineMetrics.getHeight()), im.getWidth(), titleY);
        g2.setColor(colText);
        g2.drawString(mesg, titleX, titleY);
    }

    private void drawPathPlan(final Model model, final BufferedImage im, final Graphics2D g2) {
        int w = im.getWidth();
        int h = im.getHeight();

        // TODO render black as a dark grey dotted line

        Point start = new Point(0f, 0f);
        PathPlanner p = new PathPlanner(5, 30f);
        p.plan(model, start);
        ArrayList<Float> xs = p.getXs();
        ArrayList<Float> ys = p.getYs();
        ArrayList<Float> rs = p.getRs();
        ArrayList<Float> gs = p.getGs();
        ArrayList<Float> bs = p.getBs();
        int s = xs.size();
        int r = 4; // dot size
        float pointAlpha = 0.7f;
        float lineAlpha = 0.6f;

        for (int i = 0; i < s; i++) {

            int x = (int) ((xs.get(i) / 2 + 0.5) * w);
            int y = (int) ((ys.get(i) / 2 + 0.5) * h);

            if (rs.get(i) > 0.01 || gs.get(i) > 0.01 || bs.get(i) > 0.01) {
                g2.setColor(new Color(rs.get(i), gs.get(i), bs.get(i), pointAlpha));
                g2.setStroke(PATH_PLAN_STROKE);
            } else {
                // point is too dark (probably pen up), draw debug line
                g2.setColor(PATH_OFF_COLOR);
                g2.setStroke(PATH_PLAN_OFF_STROKE);
            }
            if (i != 0) {
                int px = (int) ((xs.get(i-1) / 2 + 0.5) * w);
                int py = (int) ((ys.get(i-1) / 2 + 0.5) * h);
                g2.drawLine(px, py, x, y);
            } else {
                int startMarkerRadius = 10;
                int d = startMarkerRadius + startMarkerRadius;
                g2.drawOval(x - startMarkerRadius, y - startMarkerRadius, d, d);
            }
            // draw a dot at the point
            g2.fillOval(x - r, y - r, r + r, r + r);

        }
        // TODO move this to control panel
        String[] hudStats = new String[]{
                s + " PATH POINTS",
                model.countPolygons() + " POLYGONS",
                model.countPoints() + " POINTS",
                model.countVertices() + " VERTICES"
        };
        hudLines(g2, h, hudStats);
    }

    private void hudLines(Graphics2D g2, int h, String[] lines) {
        g2.setColor(HUD_COLOR);
        g2.setFont(hudFont);
        int lineHeight = 60;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            int y = h - (lines.length - i) * lineHeight - 40;
            g2.drawString(line, 50, y);
        }


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
