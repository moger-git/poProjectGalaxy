package org.example.poprojectgalaxyv7;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Gui extends Application {

    private Random random = new Random();
    private static HashMap<Integer, Circle> arrayCircles = new HashMap<>();
    
    // Organize stars and planets for animation
    private List<Star> stars = new ArrayList<>();
    
    // Planet colors to display in the color table
    private List<Color> planetColors = new ArrayList<>();
    
    // Scene dimensions
    private final int SCENE_WIDTH = 1700;
    private final int SCENE_HEIGHT = 1000;
    
    // Configuration
    private int starsCount = 1;
    private int maxPlanetsPerStar = 10; // Maximum possible planets per star
    private int minPlanetsPerStar = 1; // Minimum planets per star
    private int maxTries = 100; // Maximum attempts to place a star
    private int screenBorderPadding = 20; // Padding from screen edge
    
    // Sizing parameters
    private int maxStarRadius = 45;
    private int minStarRadius = 25;
    private int maxPlanetRadius = 15;
    private int minPlanetRadius = 5;
    private int baseOrbitSpacing = 40; // Spacing between orbits
    private int minOrbitSpacing = 20; // Minimum spacing when adapting
    
    // Civilization parameters
    private int civilizationInteractionChance = 2; // % chance per frame of interaction
    private int powerDisplayDuration = 120; // frames to display power when interacting
    private HashMap<Circle, Label> powerLabels = new HashMap<>();

    // Root pane for galaxy display
    private Pane galaxyPane;
    private VBox colorTableContainer;
    private AnimationTimer timer;
    private boolean isAnimationRunning = true;

    @Override
    public void start(Stage primaryStage) {
        // Create the main border pane layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");

        // Create the galaxy pane for the center
        galaxyPane = new Pane();
        galaxyPane.setStyle("-fx-background-color: black;");

        // Create the color table for the left side
        colorTableContainer = new VBox(10);
        colorTableContainer.setPadding(new Insets(20));
        colorTableContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-border-color: #444444; -fx-border-width: 1;");
        colorTableContainer.setAlignment(Pos.TOP_CENTER);
        colorTableContainer.setPrefWidth(180);

        // Add title to color table
        Label colorTableTitle = new Label("Civilizations");
        colorTableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        colorTableTitle.setTextFill(Color.WHITE);
        colorTableTitle.setPadding(new Insets(0, 0, 10, 0));
        colorTableContainer.getChildren().add(colorTableTitle);

        // Create buttons for the bottom left corner
        VBox buttonContainer = new VBox(10);
        buttonContainer.setPadding(new Insets(15));
        buttonContainer.setAlignment(Pos.BOTTOM_LEFT);

        // Reset button
        Button resetButton = new Button("Reset");
        resetButton.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-font-weight: bold;");
        resetButton.setPadding(new Insets(10, 20, 10, 20));
        resetButton.setOnAction(e -> resetGalaxy());

        // Toggle animation button (Stop/Continue)
        Button toggleAnimationButton = new Button("Stop");
        toggleAnimationButton.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-font-weight: bold;");
        toggleAnimationButton.setPadding(new Insets(10, 20, 10, 20));
        toggleAnimationButton.setOnAction(e -> {
            if (isAnimationRunning) {
                // Stop animation
                timer.stop();
                toggleAnimationButton.setText("Continue");
                isAnimationRunning = false;
            } else {
                // Continue animation
                timer.start();
                toggleAnimationButton.setText("Stop");
                isAnimationRunning = true;
            }
        });

        // Add buttons to the container
        buttonContainer.getChildren().addAll(resetButton, toggleAnimationButton);

        // Create a BorderPane to arrange elements properly in the left region
        BorderPane leftPane = new BorderPane();
        leftPane.setTop(colorTableContainer);
        leftPane.setBottom(buttonContainer);
        leftPane.setStyle("-fx-background-color: black;");

        // Set up the main layout
        root.setCenter(galaxyPane);
        root.setLeft(leftPane);

        // Create the galaxy
        createGalaxy();

        // Animation timer for planet orbits
        timer = new AnimationTimer() {
            private int frameCount = 0;

            @Override
            public void handle(long now) {
                updatePlanetPositions();

                // Process civilization interactions occasionally
                if (frameCount % 30 == 0) { // Every 30 frames
                    processCivilizationInteractions();
                }

                // Update power display durations
                updatePowerDisplays();

                frameCount++;
            }
        };
        timer.start();

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setTitle("Galaxy");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void resetGalaxy() {
        // Clear all existing elements
        galaxyPane.getChildren().clear();
        stars.clear();
        arrayCircles.clear();
        planetColors.clear();
        powerLabels.clear();

        // Clear color table contents (except the title)
        while (colorTableContainer.getChildren().size() > 1) {
            colorTableContainer.getChildren().remove(1);
        }

        // Create new galaxy
        createGalaxy();
    }

    private void updateColorTable() {
        // Make sure we only have the title remaining
        while (colorTableContainer.getChildren().size() > 1) {
            colorTableContainer.getChildren().remove(1);
        }

        // Create a map to count planets by civilization color
        HashMap<Color, Integer> civilizationCounts = new HashMap<>();

        // Count planets for each civilization color
        for (Star star : stars) {
            for (Planet planet : star.planets) {
                Color civColor = planet.civilization.getColor();
                civilizationCounts.put(civColor,
                    civilizationCounts.getOrDefault(civColor, 0) + 1);
            }
        }

        // Add color entries to the table
        int count = 0;
        for (Color civColor : civilizationCounts.keySet()) {
            HBox colorEntry = new HBox(10);
            colorEntry.setAlignment(Pos.CENTER_LEFT);

            // Create a circle with the civilization color
            Circle colorSample = new Circle(8);
            colorSample.setFill(civColor);
            colorSample.setStroke(Color.WHITE);
            colorSample.setStrokeWidth(1);

            // Create a label for the civilization with planet count
            Label civilizationLabel = new Label("Civ " + (count + 1) + " (" +
                civilizationCounts.get(civColor) + " planets)");
            civilizationLabel.setTextFill(Color.WHITE);
            civilizationLabel.setFont(Font.font("Arial", 12));

            // Add to the color entry
            colorEntry.getChildren().addAll(colorSample, civilizationLabel);

            // Add to the color table
            colorTableContainer.getChildren().add(colorEntry);

            count++;
            // Limit the number of civilizations shown to prevent UI overflow
            if (count >= 19 && civilizationCounts.size() > 20) {
                Label moreLabel = new Label("+" + (civilizationCounts.size() - 20) + " more civilizations");
                moreLabel.setTextFill(Color.LIGHTGRAY);
                moreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                moreLabel.setPadding(new Insets(5, 0, 0, 0));
                colorTableContainer.getChildren().add(moreLabel);
                break;
            }
        }
    }

    private void createGalaxy() {
        int circleId = 0;

        // Calculate approximate area per star system
        double availableArea = (SCENE_WIDTH - 2 * screenBorderPadding) * (SCENE_HEIGHT - 2 * screenBorderPadding);
        double areaPerStar = availableArea / starsCount;
        double avgStarSystemRadius = Math.sqrt(areaPerStar / Math.PI) * 0.8; // 80% of theoretical max

        System.out.println("Approximate radius per star system: " + avgStarSystemRadius);

        // Create stars with adaptive sizing
        for (int i = 0; i < starsCount; i++) {
            boolean placementSuccess = false;
            int triesCount = 0;

            // Start with ideal parameters
            int starRadius = maxStarRadius;
            int planetsPerStar = maxPlanetsPerStar;
            int orbitSpacing = baseOrbitSpacing;

            while (!placementSuccess && triesCount < maxTries) {
                triesCount++;

                // Calculate max orbit radius for this star based on number of planets
                int maxOrbitRadius = calculateMaxOrbitRadiusForStar(starRadius, planetsPerStar, orbitSpacing);

                // If the star system is too large, reduce parameters
                if (maxOrbitRadius * 2 > avgStarSystemRadius * 2) {
                    // Try reducing parameters to make the system smaller
                    if (planetsPerStar > minPlanetsPerStar) {
                        planetsPerStar--;
                    } else if (orbitSpacing > minOrbitSpacing) {
                        orbitSpacing -= 5;
                    } else if (starRadius > minStarRadius) {
                        starRadius -= 5;
                    } else {
                        // Can't reduce any further, try placement anyway
                        System.out.println("Warning: Star system #" + (i+1) + " is large relative to available space");
                    }
                    continue; // Try again with new parameters
                }

                // Ensure the star system fits within the screen by adjusting placement bounds
                int minX = maxOrbitRadius + screenBorderPadding;
                int maxX = SCENE_WIDTH - 200 - maxOrbitRadius - screenBorderPadding;
                int minY = maxOrbitRadius + screenBorderPadding;
                int maxY = SCENE_HEIGHT - maxOrbitRadius - screenBorderPadding;

                // Check if there's enough space for this star system
                if (minX >= maxX || minY >= maxY) {
                    // Reduce the system size further
                    if (planetsPerStar > minPlanetsPerStar) {
                        planetsPerStar--;
                    } else if (orbitSpacing > minOrbitSpacing) {
                        orbitSpacing -= 5;
                    } else if (starRadius > minStarRadius) {
                        starRadius -= 5;
                    } else {
                        System.out.println("Warning: Cannot place star system #" + (i+1) + " - too large for screen");
                        break; // Can't make it fit, skip this star
                    }
                    continue;
                }

                // Try to place the star several times
                for (int attempt = 0; attempt < 20; attempt++) {
                    // Spread stars within safe boundaries
                    int starX = random.nextInt(minX, maxX);
                    int starY = random.nextInt(minY, maxY);

                    // Check if this position overlaps with existing stars' orbits
                    boolean overlapping = false;
                    for (Star existingStar : stars) {
                        int existingMaxOrbit = calculateMaxOrbitRadiusForStar(
                            existingStar.radius,
                            existingStar.planets.size(),
                            existingStar.orbitSpacing
                        );

                        double distance = Math.sqrt(
                            Math.pow(starX - existingStar.x, 2) +
                            Math.pow(starY - existingStar.y, 2)
                        );

                        // Check if orbits would overlap
                        if (distance < (maxOrbitRadius + existingMaxOrbit + 10)) {
                            overlapping = true;
                            break;
                        }
                    }

                    // If not overlapping, we found a good position
                    if (!overlapping) {
                        // Create a Star object for tracking
                        Star star = new Star(starX, starY, starRadius);
                        star.orbitSpacing = orbitSpacing;
                        stars.add(star);

                        // First draw all the orbit paths for this star
                        drawOrbitsForStar(star, galaxyPane, planetsPerStar);

                        // Then add the star itself
                        Circle starCircle = new Circle(starX, starY, starRadius);
                        starCircle.setFill(Color.YELLOW);
                        galaxyPane.getChildren().add(starCircle);
                        arrayCircles.put(circleId++, starCircle);

                        // Finally add planets for this star
                        createPlanetsForStar(star, galaxyPane, circleId, planetsPerStar);

                        placementSuccess = true;
                        break;
                    }
                }

                // If we couldn't place the star after several attempts, reduce parameters
                if (!placementSuccess) {
                    if (planetsPerStar > minPlanetsPerStar) {
                        planetsPerStar--;
                    } else if (orbitSpacing > minOrbitSpacing) {
                        orbitSpacing -= 5;
                    } else if (starRadius > minStarRadius) {
                        starRadius -= 5;
                    } else {
                        // Can't reduce anymore, will try completely different locations in next iteration
                    }
                }
            }

            if (!placementSuccess) {
                System.out.println("Warning: Failed to place star system #" + (i+1) + " after " + maxTries + " attempts");
            } else {
                System.out.println("Star #" + (i+1) + " placed with " + stars.get(stars.size()-1).planets.size() +
                                  " planets, radius " + stars.get(stars.size()-1).radius + ", orbit spacing " + stars.get(stars.size()-1).orbitSpacing);
            }
        }

        // Update the color table with all planet colors
        updateColorTable();
    }

    private int calculateMaxOrbitRadiusForStar(int starRadius, int planetCount, int orbitSpacing) {
        // Calculate the maximum orbit radius for a star
        // First orbit is now at consistent distance: starRadius + orbitSpacing
        return starRadius + (planetCount * orbitSpacing) + maxPlanetRadius;
    }

    private void drawOrbitsForStar(Star star, Pane root, int planetCount) {
        // Draw all orbits with consistent spacing
        for (int j = 0; j < planetCount; j++) {
            // All orbits are spaced at consistent intervals from the star
            // First orbit is one full spacing away from star edge
            int orbitDistance = star.radius + ((j + 1) * star.orbitSpacing);
            drawOrbitPath(root, star, orbitDistance);

            // Store the orbit distance in the star for later reference
            star.orbitDistances.add(orbitDistance);
        }
    }

    private void createPlanetsForStar(Star star, Pane root, int circleId, int planetCount) {
        for (int j = 0; j < planetCount; j++) {
            // All planets use the consistently spaced orbits
            int orbitDistance = star.radius + ((j + 1) * star.orbitSpacing);

            // Planet radius - scaled based on orbit position
            // Outer planets can be larger than inner planets
            int maxSize = Math.min(maxPlanetRadius, star.orbitSpacing / 2);
            int planetRadius = random.nextInt(minPlanetRadius, maxSize);

            // Calculate speed based on distance from star - farther planets move slower
            // This follows Kepler's laws more closely
            double baseFactor = 0.015;
            double distanceFactor = (double)(j + 1) / planetCount; // 0.2 for first planet in 5-planet system, 1.0 for last
            double orbitSpeed = baseFactor * (1 - 0.7 * distanceFactor); // Slow down outer planets more

            double initialAngle = random.nextDouble() * 2 * Math.PI;

            // Create planet at initial position
            int planetX = (int) (star.x + orbitDistance * Math.cos(initialAngle));
            int planetY = (int) (star.y + orbitDistance * Math.sin(initialAngle));

            // Generate a random color for the planet's civilization
            Color planetColor = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());

            // Add the color to our list for the color table
            planetColors.add(planetColor);

            Circle planetCircle = new Circle(planetX, planetY, planetRadius);
            planetCircle.setFill(planetColor);
            root.getChildren().add(planetCircle);
            arrayCircles.put(circleId++, planetCircle);

            // Create a civilization for this planet
            Civilization civilization = new Civilization(planetColor);

            // Add planet to star's collection
            Planet planet = new Planet(planetCircle, orbitDistance, initialAngle, orbitSpeed, planetRadius, civilization);
            star.planets.add(planet);
        }
    }

    private void drawOrbitPath(Pane root, Star star, int orbitDistance) {
        // Draw orbit circles with small white dots
        int dotCount = 60;
        for (int i = 0; i < dotCount; i++) {
            double angle = (i / (double)dotCount) * 2 * Math.PI;
            int x = (int) (star.x + orbitDistance * Math.cos(angle));
            int y = (int) (star.y + orbitDistance * Math.sin(angle));

            Circle dot = new Circle(x, y, 1);
            dot.setFill(Color.rgb(255, 255, 255, 0.3)); // Semi-transparent
            root.getChildren().add(dot);
        }
    }

    private void updatePlanetPositions() {
        // Update position of each planet
        for (Star star : stars) {
            for (Planet planet : star.planets) {
                // Update angle based on orbit speed
                planet.angle += planet.speed;

                // Calculate new position
                double newX = star.x + planet.orbitDistance * Math.cos(planet.angle);
                double newY = star.y + planet.orbitDistance * Math.sin(planet.angle);

                // Update planet position
                planet.circle.setCenterX(newX);
                planet.circle.setCenterY(newY);

                // Update civilization color on the circle
                planet.circle.setFill(planet.civilization.getColor());

                // Update power label position if visible
                Label powerLabel = powerLabels.get(planet.circle);
                if (powerLabel != null && powerLabel.isVisible()) {
                    powerLabel.setLayoutX(newX - powerLabel.getWidth() / 2);
                    powerLabel.setLayoutY(newY - planet.radius - 20);
                }
            }
        }
    }

    private void processCivilizationInteractions() {
        // For each star, check for close planets that might interact
        for (Star star : stars) {
            List<Planet> planets = star.planets;

            // Only process if there are at least 2 planets
            if (planets.size() < 2) continue;

            // Check each planet against others in same star system
            for (int i = 0; i < planets.size(); i++) {
                Planet planet1 = planets.get(i);

                for (int j = i + 1; j < planets.size(); j++) {
                    Planet planet2 = planets.get(j);

                    // Calculate distance between planets
                    double distance = Math.sqrt(
                        Math.pow(planet1.circle.getCenterX() - planet2.circle.getCenterX(), 2) +
                        Math.pow(planet1.circle.getCenterY() - planet2.circle.getCenterY(), 2)
                    );

                    // If planets are close and random chance hits, they interact
                    double interactionDistance = planet1.radius + planet2.radius + 200;
                    if (distance < interactionDistance && random.nextInt(100) < civilizationInteractionChance) {
                        // Determine if civ1 attacks civ2 or vice versa (random)
                        boolean civ1Attacks = random.nextBoolean();

                        Planet attacker = civ1Attacks ? planet1 : planet2;
                        Planet defender = civ1Attacks ? planet2 : planet1;

                        // Process the attack
                        boolean attackSuccess = attacker.civilization.attack(defender.civilization);

                        // Show power values
                        showPowerValue(attacker, attacker.civilization.getPowerPoints());
                        showPowerValue(defender, defender.civilization.getPowerPoints());

                        // If attack succeeded, update the defender's planet color
                        if (attackSuccess) {
                            defender.circle.setFill(attacker.civilization.getColor());

                            // After interaction, summon power for win civilizations
                            attacker.civilization.summonCivilization(defender.civilization);
                        }



                        // Update color table to reflect changes
                        updateColorTable();
                    }
                }
            }
        }
    }

    private void showPowerValue(Planet planet, int power) {
        // Create or get existing power label
        Label powerLabel = powerLabels.get(planet.circle);

        if (powerLabel == null) {
            // Create new label
            powerLabel = new Label(String.valueOf(power));
            powerLabel.setTextFill(Color.WHITE);
            powerLabel.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 2px 5px; -fx-background-radius: 3;");

            // Position the label above the planet
            powerLabel.setLayoutX(planet.circle.getCenterX() - powerLabel.getWidth() / 2);
            powerLabel.setLayoutY(planet.circle.getCenterY() - planet.radius - 20);

            // Add to tracking and to scene
            powerLabels.put(planet.circle, powerLabel);
            galaxyPane.getChildren().add(powerLabel);
        } else {
            // Update existing label
            powerLabel.setText(String.valueOf(power));
            powerLabel.setVisible(true);
        }

        // Set remaining display time
        planet.powerDisplayTime = powerDisplayDuration;
    }

    private void updatePowerDisplays() {
        // Update display time for power labels
        for (Star star : stars) {
            for (Planet planet : star.planets) {
                if (planet.powerDisplayTime > 0) {
                    planet.powerDisplayTime--;

                    // If time's up, hide the label
                    if (planet.powerDisplayTime <= 0) {
                        Label powerLabel = powerLabels.get(planet.circle);
                        if (powerLabel != null) {
                            powerLabel.setVisible(false);
                        }
                    }
                }
            }
        }
    }

    // Star class to track related planets
    private class Star {
        double x, y;
        int radius;
        int orbitSpacing;
        List<Planet> planets = new ArrayList<>();
        List<Integer> orbitDistances = new ArrayList<>(); // Track orbit distances

        public Star(double x, double y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.orbitSpacing = baseOrbitSpacing;
        }
    }

    // Planet class for animation
    private class Planet {
        Circle circle;
        double orbitDistance;
        double angle;
        double speed;
        int radius;
        Civilization civilization;
        int powerDisplayTime = 0;

        public Planet(Circle circle, double orbitDistance, double angle, double speed,
                     int radius, Civilization civilization) {
            this.circle = circle;
            this.orbitDistance = orbitDistance;
            this.angle = angle;
            this.speed = speed;
            this.radius = radius;
            this.civilization = civilization;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}