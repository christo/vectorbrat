package com.chromosundrift.vectorbrat.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

/**
 * Initial experiment with JavaFX
 */
public class VectorBratFx extends Application {

    @Override
    public void start(Stage stage) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        l.setBackground(Background.EMPTY);
        l.setTextFill(Color.WHITE);
        Polygon shape = generatePolygon(3);
        shape.setFill(Color.RED);
        shape.setStroke(Color.ORANGE);
        StackPane sp = new StackPane(l, shape);
        sp.setBackground(Background.fill(Color.BLACK));
        Scene scene = new Scene(sp, 640, 480);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.show();
    }

    private Polygon generatePolygon(int sides) {
        Polygon polygon = new Polygon();
        polygon.getPoints().addAll(0.0, 0.0,
                20.0, 10.0,
                10.0, 20.0);
        return polygon;
    }

    /**
     * Don't run this directly, the module system will barf with missing runtime deps.
     * Use {@link MainFx} instead.
     *
     * @param args application arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
