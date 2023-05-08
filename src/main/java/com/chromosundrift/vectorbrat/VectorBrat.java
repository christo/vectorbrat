package com.chromosundrift.vectorbrat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.util.Arrays;

import static com.chromosundrift.vectorbrat.Util.setSystemLibraryPath;
import static javax.swing.UIManager.getInstalledLookAndFeels;
import static javax.swing.UIManager.setLookAndFeel;

import com.chromosundrift.vectorbrat.geom.Model;
import com.chromosundrift.vectorbrat.swing.UiUtil;
import com.chromosundrift.vectorbrat.swing.VectorBratFrame;

public class VectorBrat {

    private static final Logger logger = LoggerFactory.getLogger(VectorBrat.class);
    private final VectorBratFrame frame;

    public VectorBrat() {
        Arrays.stream(getInstalledLookAndFeels()).filter(i -> "Mac OS X".equals(i.getName())).findAny().ifPresent(i -> {
            try {
                logger.debug("Setting Mac OS X look and feel");
                setLookAndFeel(i.getClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException e) {
                logger.warn("Could not set Mac OS X look and feel");
            }
        });

        frame = vectorBratFrame();
    }

    public static void main(String[] args) {
        setSystemLibraryPath();
        VectorBrat vectorBrat = new VectorBrat();
        logger.info("started VectorBrat: " + vectorBrat.getClass());
    }

    private static VectorBratFrame vectorBratFrame() {
        Config config = new Config();
        VectorBratFrame f = new VectorBratFrame(config);
        f.start(Model.testPattern1());
        return f;
    }

}
