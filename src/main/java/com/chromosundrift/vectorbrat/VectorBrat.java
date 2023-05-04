package com.chromosundrift.vectorbrat;

import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.swing.DisplayPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowStateListener;
import java.util.Arrays;

import static javax.swing.UIManager.getInstalledLookAndFeels;
import static javax.swing.UIManager.setLookAndFeel;

public class VectorBrat {

    private static final Logger logger = LoggerFactory.getLogger(VectorBrat.class);

    public static void main(String[] args) {
        VectorBrat vectorBrat = new VectorBrat("VectorBrat");
        logger.info("started VectorBrat: " + vectorBrat.getClass());
    }

    public VectorBrat(String title) {
        Arrays.stream(getInstalledLookAndFeels()).filter(i -> "Mac OS X".equals(i.getName())).findAny().ifPresent(i -> {
            try {
                logger.debug("Setting Mac OS X look and feel");
                setLookAndFeel(i.getClassName());

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException e) {
                logger.warn("Could not set Mac OS X look and feel");
            }
        });

        JFrame jframe = new JFrame(title);

        jframe.setPreferredSize(new Dimension(800, 600));

        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jframe.setLayout(new BorderLayout(5, 5));
        DisplayPanel vd = new DisplayPanel();
        vd.setModel(Model.midSquare());
        jframe.add(vd, BorderLayout.CENTER);
        jframe.pack();
        jframe.setVisible(true);
    }


}
