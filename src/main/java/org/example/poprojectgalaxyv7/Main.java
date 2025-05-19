package org.example.poprojectgalaxyv7;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


import java.util.HashMap;
import java.util.Random;


public class Main extends Application {

    Random random = new Random();

    static HashMap<Integer, Circle> arrayCircles = new HashMap<>();

    int r = 45;
    int countPlanets = 20;

    @Override
    public void start(Stage primaryStage) {

        Pane root = new Pane();



        for (int i = 0; i < countPlanets; i++) {
            int x = random.nextInt(1,14) * 100;
            int y = random.nextInt(1,7) * 100;
            Circle circle = new Circle(x, y, r);
            circle.setFill(Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
            root.getChildren().add(circle);
            arrayCircles.put(i, circle);
        }

        Scene scene = new Scene(root, 1500, 800);
        root.setStyle("-fx-background-color: black;");
        primaryStage.setTitle("Galaxy");
        primaryStage.setScene(scene);
        primaryStage.show();

        arrayCircles.forEach((key, value) -> System.out.println(key + " = " + value));

    }

    public static void main(String[] args) {
        launch(args);
    }
}
