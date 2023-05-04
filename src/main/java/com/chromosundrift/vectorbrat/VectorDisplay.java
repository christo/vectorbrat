package com.chromosundrift.vectorbrat;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class VectorDisplay extends JPanel {

    private final Color colText = Color.PINK;
    private final Color colBg = Color.DARK_GRAY.darker();
    private final int inset = 50;

    public VectorDisplay(String myTitle) {
        super(true);
        setBackground(Color.BLACK);
        setForeground(Color.GREEN);

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        //setBackground(Color.GREEN);
        final Dimension s = getSize();

        BufferedImage im = new BufferedImage(s.width - inset*2, s.height - inset*2, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = (Graphics2D) im.getGraphics();

        g2.setBackground(colBg);
        g2.setColor(colText);
        g2.clearRect(0, 0, im.getWidth(), im.getHeight());

        g2.drawString("YO DOG", im.getWidth() / 2, im.getHeight() / 2);
        g.drawImage(im, inset, inset, null);
    }
}
