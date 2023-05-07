package com.chromosundrift.vectorbrat.swing;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import com.chromosundrift.vectorbrat.Config;
import com.chromosundrift.vectorbrat.geom.Model;

public class VectorBratFrame extends JFrame {

    private final Config config;
    private final DisplayPanel vd;
    private final JPanel rootPanel;

    private final JSplitPane split;
    private final JPanel enginePanel;
    private final JScrollPane scrollPane;

    private final JPanel mainPanel;

    public VectorBratFrame(Config config) {
        this.config = config;
        this.setTitle(config.getTitle());

        setPreferredSize(new Dimension(900, 600));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        rootPanel = new JPanel(new BorderLayout(), true);
        rootPanel.setBackground(Color.CYAN);
        rootPanel.setMinimumSize(new Dimension(200, 100));

        split = new JSplitPane();
        split.setContinuousLayout(true);
        split.setDividerLocation(600);
        rootPanel.add(split, BorderLayout.CENTER);

        enginePanel = new JPanel();
        enginePanel.setLayout(new BorderLayout());

        enginePanel.setBorder(UiUtil.titledBorder("Engine", UiUtil.HAlign.CENTRE));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(UiUtil.titledBorder("Vector Simulator", UiUtil.HAlign.CENTRE));
        vd = new DisplayPanel(config);
        mainPanel.add(vd, BorderLayout.CENTER);

        split.setLeftComponent(mainPanel);
        split.setRightComponent(enginePanel);
        split.setOneTouchExpandable(true);
        scrollPane = new JScrollPane();


        enginePanel.add(scrollPane, BorderLayout.NORTH);
        enginePanel.add(Box.createVerticalBox(), BorderLayout.CENTER);

        ControlPanel controlPanel = new ControlPanel(config);
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
