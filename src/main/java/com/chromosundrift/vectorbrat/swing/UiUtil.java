package com.chromosundrift.vectorbrat.swing;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import java.awt.Toolkit;

public class UiUtil {
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
