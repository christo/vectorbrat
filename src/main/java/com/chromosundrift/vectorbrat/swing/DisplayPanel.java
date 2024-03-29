package com.chromosundrift.vectorbrat.swing;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.DoubleBufferedVectorDisplay;
import com.chromosundrift.vectorbrat.VectorDisplay;
import com.chromosundrift.vectorbrat.geom.Interpolator;
import com.chromosundrift.vectorbrat.geom.Line;
import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.geom.Point;
import com.chromosundrift.vectorbrat.geom.Rgb;
import com.chromosundrift.vectorbrat.laser.LaserController;
import com.chromosundrift.vectorbrat.physics.LaserSimulator;
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
import java.awt.Stroke;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.JOIN_ROUND;


/**
 * Threadsafe JPanel implementation of VectorDeplay. Shows the ideal model in the app or the path plan with
 * connecting lines indicating the blank path, also interpolated points.
 */
public final class DisplayPanel extends JPanel implements VectorDisplay<RasterTuning> {

    public static final Color HUD_COLOR = new Color(255, 255, 255, 80);
    public static final float MINIMUM_BRIGHTNESS = 0f;
    private static final Logger logger = LoggerFactory.getLogger(DisplayPanel.class);
    private static final int MIN_WIDTH = 100;
    private static final int MIN_HEIGHT = 100;
    private static final Stroke STROKE_PATH = new BasicStroke(1f);
    private static final Stroke STROKE_PATH_OFF =
            new BasicStroke(0.5f, CAP_BUTT, JOIN_ROUND, 0, new float[]{1, 5}, 0);
    private static final Color COL_PATH_OFF = new Color(0.6f, 0.6f, 0.7f, 0.4f);
    private static final Stroke LASER_ON_DOT = new BasicStroke(5f, BasicStroke.CAP_ROUND, JOIN_ROUND);
    private static final Stroke LASER_OFF_DOT = new BasicStroke(3f, BasicStroke.CAP_ROUND, JOIN_ROUND);
    private static BufferedImage im;
    private final Color colText = Color.getHSBColor(0.83f, 0.5f, 0.9f);
    private final Color colBg = Color.getHSBColor(0, 0, 0f);
    private final DoubleBufferedVectorDisplay<RasterTuning> vectorDisplay;
    private final Font fontBranding;
    private final Config config;
    private final BasicStroke strokeLine;
    private final DisplayController displayController;
    private final LaserController laserController;
    private final Font fontHud;
    private final RasterTuning tuning;
    private final LaserSimulator laserSimulator;
    private final SimulatorRenderer simulatorRenderer;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<BufferedImage> logo;

    public DisplayPanel(Config config, DisplayController dc, LaserController lc, LaserSimulator laserSimulator) {
        logger.info("initialising DisplayPanel");
        this.displayController = dc;
        this.laserController = lc;
        this.tuning = new RasterTuning();
        this.config = config;
        logo = loadImage(config.logoUrl());
        fontBranding = UiUtil.mkFont(130);
        fontHud = UiUtil.mkFont(48);
        strokeLine = new BasicStroke(config.getLineWidth());
        this.laserSimulator = laserSimulator;
        simulatorRenderer = new SimulatorRenderer(this.laserSimulator);

        setMinimumSize(new Dimension(400, 300));
        setPreferredSize(new Dimension(900, 600));
        vectorDisplay = new DoubleBufferedVectorDisplay<>(true, tuning);
    }

    private static Optional<BufferedImage> loadImage(String imageUrl) {
        try {
            InputStream stream = DisplayPanel.class.getClassLoader().getResourceAsStream(imageUrl);
            if (stream != null) {
                return Optional.of(ImageIO.read(stream));
            }
        } catch (IOException e) {
            logger.warn("Unable to load logo from url " + imageUrl, e);
        }
        return Optional.empty();
    }

    private static BufferedImage getImage(int imWidth, int imHeight) {
        if (im == null || imWidth != im.getWidth() || imHeight != im.getHeight()) {
            // future: use physical screen resolution to calculate scaling factor for the image
            im = new BufferedImage(imWidth * 2, imHeight * 2, BufferedImage.TYPE_INT_ARGB);
        }
        return im;
    }

    /**
     * Not threadsafe, must only be called with model lock.
     */
    private void unsafePaint(final Graphics g, final Model model) {
        final Dimension s = getSize();
        int imWidth = Math.max(s.width, MIN_WIDTH);
        int imHeight = Math.max(s.height, MIN_HEIGHT);

        BufferedImage im = getImage(imWidth, imHeight);
        final Graphics2D g2 = im.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setBackground(colBg);

        g2.clearRect(0, 0, im.getWidth(), im.getHeight());

        if (model.isEmpty()) {
            drawBranding(im, g2);
        } else {
            switch (displayController.getMode()) {
                case PATH_PLAN -> drawPathPlan(model, im, g2);
                case DISPLAY -> drawModel(model, im, g2);
                case SIMULATOR -> drawSimulator(im, g2);
            }
        }

        g.drawImage(im, 0, 0, imWidth, imHeight, Color.BLACK, null);
        g2.dispose();
    }

    private void drawSimulator(BufferedImage im, Graphics2D g2) {
        Interpolator p = getPathPlan();
        if (p != null && !p.isEmpty()) {
            simulatorRenderer.draw(im, g2);
        }
    }

    private Color toColor(Rgb rgb) {
        return new Color(rgb.red(), rgb.green(), rgb.blue());
    }

    private void drawModel(Model model, BufferedImage im, Graphics2D g2) {
        g2.setStroke(strokeLine);
        Stream<Line> lines = model.lines();
        int xScale = im.getWidth();
        int yScale = im.getHeight();
        lines.forEach(l -> {
            g2.setColor(toColor(l.from().getColor()));
            Line line = l.scaleOffset(0.5f, 0.5f, 0.5f, 0.5f);
            Point from = line.from().scale(xScale, yScale);
            Point to = line.to().scale(xScale, yScale);
            g2.drawLine((int) from.x(), (int) from.y(), (int) to.x(), (int) to.y());
        });
        model.isoPoints().forEach(point -> {
            int x = (int) ((point.x() / 2 + 0.5) * xScale);
            int y = (int) ((point.y() / 2 + 0.5) * yScale);
            g2.setColor(toColor(point.getColor()));
            g2.drawLine(x, y, x, y);
        });
    }

    private void drawBranding(final BufferedImage im, final Graphics2D g2) {
        int targetCentreX = im.getWidth() / 2;
        int targetCentreY = im.getHeight() / 2;

        logo.ifPresent(logo -> {
            int logoX = targetCentreX - logo.getWidth() / 2;
            int logoY = targetCentreY - logo.getHeight() / 2;
            g2.drawImage(logo, logoX, logoY, logo.getWidth(), logo.getHeight(), null);
        });

        // centred string title
        final String mesg = config.getTitle().toUpperCase();
        g2.setFont(fontBranding);
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
        Interpolator p = getPathPlan();
        if (p != null && !p.getXs().isEmpty()) {
            int w = im.getWidth();
            int h = im.getHeight();


            ArrayList<Float> xs = p.getXs();
            ArrayList<Float> ys = p.getYs();
            ArrayList<Float> rs = p.getRs();
            ArrayList<Float> gs = p.getGs();
            ArrayList<Float> bs = p.getBs();
            int s = xs.size();

            float pointAlpha = 0.6f;
            int blackPoints = 0;
            final float xInvert = laserController.getInvertX() ? -1f : 1f;
            final float yInvert = laserController.getInvertY() ? -1f : 1f;
            for (int i = 0; i < s; i++) {

                // TODO the following sometimes throws ArrayIndexOutOfBoundsException but why?
                int x = (int) (((xs.get(i) * xInvert) / 2 + 0.5) * w);
                int y = (int) (((ys.get(i) * yInvert) / 2 + 0.5) * h);

                boolean laserOn = rs.get(i) > 0.01 || gs.get(i) > 0.01 || bs.get(i) > 0.01;
                if (laserOn) {
                    g2.setColor(new Color(rs.get(i), gs.get(i), bs.get(i), pointAlpha));
                    g2.setStroke(STROKE_PATH);
                } else {
                    // point is too dark (probably pen up), draw debug line
                    g2.setColor(COL_PATH_OFF);
                    g2.setStroke(STROKE_PATH_OFF);
                    blackPoints++;
                }
                if (i != 0) {
                    int px = (int) (((xs.get(i - 1) * xInvert) / 2 + 0.5) * w);
                    int py = (int) (((ys.get(i - 1) * yInvert) / 2 + 0.5) * h);
                    g2.drawLine(px, py, x, y);
                }
                // draw a dot at the point
                // dot size
                g2.setStroke(laserOn ? LASER_ON_DOT : LASER_OFF_DOT);
                g2.drawLine(x, y, x, y);

            }
            g2.setColor(Color.WHITE);
            // draw start and end markers
            int markerRadius = 10;
            int d = markerRadius + markerRadius;
            int x = (int) ((xs.get(0) / 2 + 0.5) * w);
            int y = (int) ((ys.get(0) / 2 + 0.5) * h);
            g2.drawOval(x - markerRadius, y - markerRadius, d, d);
            x = (int) ((xs.get(xs.size() - 1) / 2 + 0.5) * w);
            y = (int) ((ys.get(ys.size() - 1) / 2 + 0.5) * h);
            g2.drawLine(x - markerRadius, y - markerRadius, x + markerRadius, y + markerRadius);
            g2.drawLine(x + markerRadius, y - markerRadius, x - markerRadius, y + markerRadius);

            // TODO this was moved to control panel?
            String[] hudStats = new String[]{
                    s + " PATH POINTS",
                    model.countLines() + " LINES",
                    model.countPoints() + " ISOPOINTS",
                    model.countVertices() + " VERTICES",
                    blackPoints + " BLACK POINTS"
            };
            UiUtil.hudLines(g2, h, hudStats, HUD_COLOR, fontHud);
        }
    }

    private Interpolator getPathPlan() {
        return laserController.getInterpolator();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        vectorDisplay.withLock(model -> {
            unsafePaint(g, model);
            return null;
        });
    }

    @Override
    public void setModel(final Model model) {
        vectorDisplay.setModel(model);
        this.repaint();
    }

    @Override
    public boolean supportsBlank() {
        return true;
    }

    @Override
    public RasterTuning getTuning() {
        return tuning;
    }
}
