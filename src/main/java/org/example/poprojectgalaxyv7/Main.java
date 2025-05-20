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


    int starsCount = 5;

    int planetsCount = 15;

    int lineCount = 1000;

    @Override
    public void start(Stage primaryStage) {

        Pane root = new Pane();

        int circleId = 0;

        for (int i = 0; i < starsCount; i++) {
            int starX = random.nextInt(2,14) * 100 + 50;
            int starY = random.nextInt(2,7) * 100 + 50;
            int starRadius = 45;
            Circle star = new Circle(starX, starY, starRadius);
            star.setFill(Color.YELLOW);
            root.getChildren().add(star);
            arrayCircles.put(circleId++, star);

            double planetAngleRemember = 0;
            double planetAngle = 0;
            double angleRemember = 0;
            double angle = 0;

            int orbitDistance = starRadius + 30;

            for (int j = 0; j < planetsCount; j++) {
//                int orbitDistance = starRadius + 30 + (j * 40);


                if (angleRemember == angle) {
                    angle = (random.nextDouble()+ 10) * 2 * Math.PI;
                }

                int planetX = (int) (starX + orbitDistance * Math.cos(angle));
                int planetY = (int) (starY + orbitDistance * Math.sin(angle));


//                int planetX = random.nextInt(1,14) * (starX - starRadius - 10);
//                int planetY = random.nextInt(1,7) * (starY - starRadius - 10);
                int planetsRadius = random.nextInt(3,15);
                Circle planet = new Circle(planetX, planetY, planetsRadius);
                planet.setFill(Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
                root.getChildren().add(planet);
                arrayCircles.put(circleId++, planet);

                int orbitDistanceLine = orbitDistance;

                for(int k = 0; k < lineCount; k++){

                    if (angleRemember == angle) {
                        angle = (random.nextDouble()+ 10) * 2 * Math.PI;
                    }

                    int lineX = (int) (starX + orbitDistanceLine * Math.cos(angle));
                    int lineY = (int) (starY + orbitDistanceLine * Math.sin(angle));

                    Circle line = new Circle(lineX, lineY, 2);
                    line.setFill(Color.WHITE);
                    root.getChildren().add(line);
                }
                angleRemember = angle;
                orbitDistance += 15;
            }

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
