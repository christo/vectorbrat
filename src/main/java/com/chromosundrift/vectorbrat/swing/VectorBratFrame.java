package com.chromosundrift.vectorbrat.swing;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.geom.Model;

public class VectorBratFrame extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(VectorBratFrame.class);

    private final Config config;
    private final JPanel rootPanel;

    private final JSplitPane split;
    private final JPanel enginePanel;
    private final JScrollPane scrollPane;

    private final DisplayPanel vd;

    public VectorBratFrame(Config config, DisplayPanel displayPanel, LaserController laserController) {
        logger.info("initialising VectorBratFrame");
        this.config = config;
        this.vd = displayPanel;
        this.setTitle(config.getTitle());
        setBackground(Color.BLACK);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setPreferredSize(new Dimension(Math.min(1600, screenSize.width), Math.min(1000, screenSize.height)));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        rootPanel = new JPanel(new BorderLayout(), true);
        rootPanel.setMinimumSize(new Dimension(200, 100));

        split = new JSplitPane();
        split.setContinuousLayout(true);
        split.setDividerLocation(0.4);
        rootPanel.add(split, BorderLayout.CENTER);

        enginePanel = new JPanel();
        enginePanel.setLayout(new BorderLayout());

        enginePanel.setBorder(UiUtil.titledBorder("Engine", UiUtil.HAlign.CENTRE));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(UiUtil.titledBorder("Vector Simulator", UiUtil.HAlign.CENTRE));
        mainPanel.add(displayPanel, BorderLayout.CENTER);

        split.setLeftComponent(enginePanel);
        split.setRightComponent(mainPanel);
        split.setOneTouchExpandable(true);

        scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        enginePanel.add(scrollPane, BorderLayout.NORTH);
        enginePanel.add(Box.createVerticalBox(), BorderLayout.CENTER);

        ControlPanel controlPanel = new ControlPanel(config, laserController);
        scrollPane.setViewportView(controlPanel);

        setContentPane(rootPanel);
    }

    public void start(Model model) {
        vd.setModel(model);
        JFrame f = this;
        pack();
        UiUtil.centerFrame(f);
        setVisible(true);
    }

}
