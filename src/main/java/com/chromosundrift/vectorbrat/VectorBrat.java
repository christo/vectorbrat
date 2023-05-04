package com.chromosundrift.vectorbrat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Arrays;

import static javax.swing.UIManager.getInstalledLookAndFeels;
import static javax.swing.UIManager.setLookAndFeel;

public class VectorBrat {

    private static final Logger logger = LoggerFactory.getLogger(VectorBrat.class);

    private final JFrame jframe;

    public static void main(String[] args) {
        VectorBrat vectorBrat = new VectorBrat("VectorBrat");
        logger.info("started VectorBrat: " + vectorBrat.getClass());
    }

    public VectorBrat(String title) {
        Arrays.stream(getInstalledLookAndFeels()).filter(i -> "Mac OS X".equals(i.getName())).findAny().ifPresent(i -> {
            try {
                setLookAndFeel(i.getClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException e) {
                logger.warn("Could not set Mac OS X look and feel");
            }
        });

        jframe = new JFrame(title);
        jframe.setPreferredSize(new Dimension(800, 600));

        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jframe.setLayout(new BorderLayout(5, 5));
        VectorDisplay vd = new VectorDisplay("Vector View");
        jframe.add(vd, BorderLayout.CENTER);
        jframe.pack();
        jframe.setVisible(true);
    }
}
