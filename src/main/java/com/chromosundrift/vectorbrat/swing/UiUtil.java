package com.chromosundrift.vectorbrat.swing;

import com.chromosundrift.vectorbrat.VectorBratException;
import io.materialtheme.darkstackoverflow.DarkStackOverflowTheme;
import mdlaf.MaterialLookAndFeel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.util.Comparator;
import java.util.Optional;

import static java.util.Arrays.stream;

public class UiUtil {
    public static final Comparator<GraphicsDevice> WIDEST = Comparator.comparingInt(o -> o.getDisplayMode().getWidth());
    private static final Logger logger = LoggerFactory.getLogger(UiUtil.class);

    public static void centerFrame(JFrame f) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation(screenSize.width / 2 - f.getWidth() / 2, screenSize.height / 2 - f.getHeight() / 2);
    }

    public static TitledBorder titledBorder(String title, HAlign hAlign) {
        return BorderFactory.createTitledBorder(
                null, title, hAlign.titledBorderMagic,
                TitledBorder.DEFAULT_POSITION, null, null);
    }

    public static JLabel rLabel(String text) {
        final JLabel label;
        label = new JLabel(text, SwingConstants.RIGHT);
        label.setBorder(new EmptyBorder(5, 0, 0, 10));
        return label;
    }

    /**
     * Gets the screen with the biggest resolution
     */
    public static GraphicsConfiguration getPreferredGraphicsConfiguration() {
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screenDevices = localGraphicsEnvironment.getScreenDevices();
        Optional<GraphicsDevice> first = stream(screenDevices).max(WIDEST);
        //noinspection OptionalGetWithoutIsPresent
        return first.get().getDefaultConfiguration();
    }
    /**
     * Sets ui scaling and look-and-feel.
     *
     * @throws VectorBratException if it can't even.
     */
    public static void setUiGlobals() throws VectorBratException {
        try {
            logger.info("setting look and feel");
            System.setProperty("sun.java2d.uiScale", "2");
            UIManager.setLookAndFeel(new MaterialLookAndFeel(new DarkStackOverflowTheme()));
        } catch (UnsupportedLookAndFeelException e) {
            throw new VectorBratException(e);
        }
    }

    public enum HAlign {

        LEFT(TitledBorder.LEFT),
        CENTRE(TitledBorder.CENTER),
        RIGHT(TitledBorder.RIGHT);

        private final int titledBorderMagic;

        HAlign(int titledBorderMagic) {
            this.titledBorderMagic = titledBorderMagic;
        }
    }
}
