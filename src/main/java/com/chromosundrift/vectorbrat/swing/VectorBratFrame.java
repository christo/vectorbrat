package com.chromosundrift.vectorbrat.swing;


import com.chromosundrift.vectorbrat.AppMap;
import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.Controllers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

public class VectorBratFrame extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(VectorBratFrame.class);

    public VectorBratFrame(Config config, DisplayPanel displayPanel, Controllers controllers, AppMap appMap) {

        logger.info("initialising VectorBratFrame");
        this.setTitle(config.getTitle());
        setBackground(Color.BLACK);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setPreferredSize(new Dimension(Math.min(1600, screenSize.width), Math.min(1000, screenSize.height)));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new BorderLayout(5, 5));

        JSplitPane split = new JSplitPane();
        split.setMinimumSize(new Dimension(200, 100));
        split.setContinuousLayout(true);
        split.setDividerLocation(0.4);


        // big panel with primary display
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(displayPanel, BorderLayout.CENTER);

        split.setRightComponent(mainPanel);
        split.setOneTouchExpandable(true);

        // side panel with configuration/controls
        ControlPanel controlPanel = new ControlPanel(config, controllers, appMap);
        controlPanel.setMaximumSize(null);

        JScrollPane scrollPane = new JScrollPane(controlPanel);
        split.setLeftComponent(scrollPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        setContentPane(split);
    }

    public void start() {
        JFrame f = this;
        pack();
        UiUtil.centerFrame(f);
        setVisible(true);
    }

}
