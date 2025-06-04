package org.example.poprojectgalaxyv7;
    
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Gui extends Application implements SimulationConfig {

    private VBox createParameterSlider(String name, int min, int max, int initialValue, javafx.util.Callback<Number, Void> updateCallback) {
        VBox container = new VBox(5);

        // Create label with current value
        Label label = new Label(name + ": " + initialValue);
        label.setTextFill(Color.WHITE);

        // Create slider
        Slider slider = new Slider(min, max, initialValue);
//        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit((double) (max - min) / 3);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);

        // Make slider snap to integer values
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int roundedValue = (int) Math.round(newVal.doubleValue());
            slider.setValue(roundedValue);
            label.setText(name + ": " + roundedValue);
            updateCallback.call(roundedValue);
        });

        // Create tooltip
        Tooltip tooltip = new Tooltip("Adjust " + name.toLowerCase());
        Tooltip.install(slider, tooltip);

        // Add elements to a container
        container.getChildren().addAll(label, slider);
        return container;
    }

    private final Random random = new Random();

    // Organize stars and planets for animation
    private final List<Star> stars = new ArrayList<>();
    private final List<Planet> arrayPlanet = new ArrayList<>();

    // Mutable simulation parameters
    private int starsCount = DEFAULT_STARS_COUNT;
    private int maxPlanetsPerStar = DEFAULT_MAX_PLANETS;
    private int minPlanetsPerStar = DEFAULT_MIN_PLANETS;
    private int simulationSpeed = DEFAULT_SIMULATION_SPEED;

    private final HashMap<Circle, Label> powerLabels = new HashMap<>();

    // Root pane for galaxy display
    private Pane galaxyPane;
    private VBox colorTableContainer;
    private AnimationTimer timer;
    private boolean isAnimationRunning = true;

    // Simulation input/output handling
    private SimulationOutput simulationOutput;
    private FileChooser fileChooser = new FileChooser();

    @Override
    public void start(Stage primaryStage) {
        // Create the main border pane layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: rgba(0,0,0);");

        // Create the galaxy pane for the center
        galaxyPane = new Pane();
        galaxyPane.setStyle("-fx-background-color: #000000;");

        // Create the color table for the left side
        colorTableContainer = new VBox(10);
        colorTableContainer.setPadding(new Insets(20));
        colorTableContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-border-color: #444444; -fx-border-width: 1;");
        colorTableContainer.setAlignment(Pos.TOP_CENTER);
        colorTableContainer.setPrefWidth(200);

        // Add title to color table
        Label colorTableTitle = new Label("Civilizations");
        colorTableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        colorTableTitle.setTextFill(Color.WHITE);
        colorTableTitle.setPadding(new Insets(0, 0, 10, 0));
        colorTableContainer.getChildren().add(colorTableTitle);

        // Create sliders for configuration parameters
        VBox slidersContainer = new VBox(15);
        slidersContainer.setPadding(new Insets(15));
        slidersContainer.setStyle("-fx-background-color: rgba(20, 20, 20, 0.7); -fx-border-color: #444444; -fx-border-width: 1;");

        // Add title for a sliders section
        Label slidersTitle = new Label("Simulation Parameters");
        slidersTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        slidersTitle.setTextFill(Color.WHITE);
        slidersTitle.setPadding(new Insets(0, 0, 10, 0));
        slidersContainer.getChildren().add(slidersTitle);

        // Create sliders for configuration
        slidersContainer.getChildren().addAll(
                createParameterSlider("Stars Count", MIN_STARS_COUNT, MAX_STARS_COUNT, starsCount, value -> {
                    starsCount = (int) value;
                    return null;
                }),
                createParameterSlider("Min Planets Per Star", MIN_PLANETS_LOWER_BOUND, maxPlanetsPerStar, minPlanetsPerStar, value -> {
                    minPlanetsPerStar = (int) value;
                    // Ensure max is never less than min
                    if (maxPlanetsPerStar < minPlanetsPerStar) {
                        maxPlanetsPerStar = minPlanetsPerStar;
                    }
                    return null;
                }),
                createParameterSlider("Max Planets Per Star", minPlanetsPerStar, MAX_PLANETS_UPPER_BOUND, maxPlanetsPerStar, value -> {
                    maxPlanetsPerStar = (int) value;
                    // Ensure min is never more than max
                    if (minPlanetsPerStar > maxPlanetsPerStar) {
                        minPlanetsPerStar = maxPlanetsPerStar;
                    }
                    return null;
                }),
                createParameterSlider("Simulation Speed", MIN_SIMULATION_SPEED, MAX_SIMULATION_SPEED, simulationSpeed, value -> {
                    simulationSpeed = (int) value;
                    return null;
                })
        );

        // Create buttons for the bottom left corner (control buttons)
        VBox controlButtonContainer = new VBox(10);
        controlButtonContainer.setPadding(new Insets(15));
        controlButtonContainer.setAlignment(Pos.BOTTOM_LEFT);

        // Reset button
        Button resetButton = new Button("Reset");
        resetButton.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-font-weight: bold;");
        resetButton.setPadding(new Insets(10, 20, 10, 20));
        resetButton.setOnAction(e -> resetGalaxy());

        // Toggle animation button (Stop/Continue)
        Button toggleAnimationButton = getButton();

        // Add control buttons to the left container
        controlButtonContainer.getChildren().addAll(resetButton, toggleAnimationButton);

        // Create right panel for file operations
        VBox rightPanel = new VBox(20);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setStyle("-fx-background-color: rgba(20, 20, 20, 0.7); -fx-border-color: #444444; -fx-border-width: 1;");
        rightPanel.setPrefWidth(180);
        rightPanel.setAlignment(Pos.TOP_CENTER);

        // Add title for file operations section
        Label fileOperationsTitle = new Label("File Operations");
        fileOperationsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        fileOperationsTitle.setTextFill(Color.WHITE);
        fileOperationsTitle.setPadding(new Insets(0, 0, 15, 0));

        // Input configuration file button
        Button loadConfigButton = new Button("Load Config");
        loadConfigButton.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-font-weight: bold;");
        loadConfigButton.setPadding(new Insets(10, 20, 10, 20));
        loadConfigButton.setPrefWidth(150);
        loadConfigButton.setOnAction(e -> loadConfigFromFile(primaryStage));

        // Output file selection button
        Button setOutputButton = new Button("Set Output File");
        setOutputButton.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: white; -fx-font-weight: bold;");
        setOutputButton.setPadding(new Insets(10, 20, 10, 20));
        setOutputButton.setPrefWidth(150);
        setOutputButton.setOnAction(e -> selectOutputFile(primaryStage));

        // Add file buttons to the right panel
        rightPanel.getChildren().addAll(fileOperationsTitle, loadConfigButton, setOutputButton);

        // Create a BorderPane to arrange elements properly in the left region
        BorderPane leftPane = new BorderPane();

        // Create a VBox to hold both color table and sliders in the top area
        VBox topContainer = new VBox(20);
        topContainer.getChildren().addAll(colorTableContainer, slidersContainer);

        leftPane.setTop(topContainer);
        leftPane.setBottom(controlButtonContainer);
        leftPane.setStyle("-fx-background-color: black;");

        // Set up the main layout
        root.setCenter(galaxyPane);
        root.setLeft(leftPane);
        root.setRight(rightPanel);

        // Try to load default config file if exists
        File defaultConfig = new File(DEFAULT_GALAXY_CONFIG_FILE);
        if (defaultConfig.exists()) {
            InputConfig config = new InputConfig();
            if (config.loadFromCsv(defaultConfig.getAbsolutePath())) {
                starsCount = config.getStarsCount();
                minPlanetsPerStar = config.getMinPlanetsPerStar();
                maxPlanetsPerStar = config.getMaxPlanetsPerStar();
                simulationSpeed = config.getSimulationSpeed();
                System.out.println("Loaded default configuration from " + DEFAULT_GALAXY_CONFIG_FILE);
            }
        }

        // Create the galaxy
        createGalaxy();

        // Initialize simulation output with default file
        simulationOutput = new SimulationOutput();

        // Animation timer for planet orbits
        timer = new AnimationTimer() {
            private int frameCount = 0;
            private int tickCounter = 0;

            @Override
            public void handle(long now) {
                // Only update on specific frames based on simulation speed
                tickCounter++;
                if (tickCounter >= simulationSpeed) {
                    updatePlanetPositions();

                    // Process civilization interactions occasionally
                    if (frameCount % 45 == 0) { // Every 45 frames
                        processCivilizationInteractions();
                        processStarExplosion(root);

                        // Record simulation state to CSV
                        if (simulationOutput != null) {
                            simulationOutput.recordTick(stars);
                        }
                    }

                    // Update power display durations
                    updatePowerDisplays();

                    frameCount++;
                    tickCounter = 0;
                }
            }
        };
        timer.start();

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setTitle("Galaxy");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // The rest of the class remains the same...
    private Button getButton() {
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
        return toggleAnimationButton;
    }

    private void loadConfigFromFile(Stage stage) {
        fileChooser.setTitle("Open Configuration File");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            System.out.println("Selected config file: " + file.getAbsolutePath());
            InputConfig config = new InputConfig();
            if (config.loadFromCsv(file.getAbsolutePath())) {
                // Update simulation parameters
                starsCount = config.getStarsCount();
                minPlanetsPerStar = config.getMinPlanetsPerStar();
                maxPlanetsPerStar = config.getMaxPlanetsPerStar();
                simulationSpeed = config.getSimulationSpeed();

                System.out.println("Applied configuration:\n" +
                        "Stars Count: " + starsCount + "\n" +
                        "Min Planets Per Star: " + minPlanetsPerStar + "\n" +
                        "Max Planets Per Star: " + maxPlanetsPerStar + "\n" +
                        "Simulation Speed: " + simulationSpeed);

                // Reset galaxy with new configuration
                resetGalaxy();
            } else {
                System.err.println("Failed to load configuration from " + file.getAbsolutePath());
            }
        }
    }

    private void selectOutputFile(Stage stage) {
        fileChooser.setTitle("Set Output File");
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            // Create new output handler with selected file
            simulationOutput = new SimulationOutput(file.getAbsolutePath());
        }
    }

    private void resetGalaxy() {
        // Clear all existing elements
        galaxyPane.getChildren().clear();
        stars.clear();
        powerLabels.clear();
        arrayPlanet.clear();

        // Clear color table contents (except the title)
        while (colorTableContainer.getChildren().size() > 1) {
            colorTableContainer.getChildren().remove(1);
        }

        // Reset simulation output with a new file
        simulationOutput = new SimulationOutput();

        // Create a new galaxy
        createGalaxy();
    }

    private void updateColorTable() {
        // Make sure we only have the title remaining
        while (colorTableContainer.getChildren().size() > 1) {
            colorTableContainer.getChildren().remove(1);
        }

        // Create maps to count planets and track power by civilization color
        HashMap<Color, Integer> civilizationCounts = new HashMap<>();
        HashMap<Color, Integer> civilizationPower = new HashMap<>();

        // Count planets and get power for each civilization color
        for (Star star : stars) {
            for (Planet planet : star.planets) {
                Color civColor = planet.civilization.getColor();
                civilizationCounts.put(civColor,
                        civilizationCounts.getOrDefault(civColor, 0) + 1);

                // Track the power points (we just need one value per civilization)
                civilizationPower.put(civColor, planet.civilization.getPowerPoints());
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

            // Create a VBox to hold multiple labels for better formatting
            VBox labelContainer = new VBox(1);

            // Main civilization label with number
            Label civilizationLabel = new Label("Civ " + (count + 1));
            civilizationLabel.setTextFill(Color.WHITE);
            civilizationLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));

            // Planets count
            Label planetLabel = new Label("Planets: " + civilizationCounts.get(civColor));
            planetLabel.setTextFill(Color.LIGHTGRAY);
            planetLabel.setFont(Font.font("Arial", 10));

            // Power points (highlighted)
            Label powerLabel = new Label("Power: " + civilizationPower.get(civColor));
            powerLabel.setTextFill(Color.LIGHTGRAY);
            powerLabel.setFont(Font.font("Arial", 10));

            labelContainer.getChildren().addAll(civilizationLabel, planetLabel, powerLabel);

            // Add to the color entry
            colorEntry.getChildren().addAll(colorSample, labelContainer);

            // Add to the color table
            colorTableContainer.getChildren().add(colorEntry);


            count++;
            // Limit the number of civilizations shown to prevent UI overflow
            if (count >= 4 && civilizationCounts.size() > 4) {
                Label moreLabel = new Label("+" + (civilizationCounts.size() - 4) + " more civilizations");
                moreLabel.setTextFill(Color.LIGHTGRAY);
                moreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                moreLabel.setPadding(new Insets(5, 0, 0, 0));
                colorTableContainer.getChildren().add(moreLabel);
                break;
            }
        }
    }

    private void createGalaxy() {

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

                // Calculate the max orbit radius for this star based on the number of planets
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
                        System.out.println("Warning: Star system #" + (i + 1) + " is large relative to available space");
                    }
                    continue; // Try again with new parameters
                }

                // Ensure the star system fits within the screen by adjusting placement bounds
                int minX = maxOrbitRadius + screenBorderPadding;
                int maxX = SCENE_WIDTH - 380 - maxOrbitRadius - screenBorderPadding; // Account for both left and right panels
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
                        System.out.println("Warning: Cannot place star system #" + (i + 1) + " - too large for screen");
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
                        // Create a Star object for tracking - randomly either Volatile or Stable
                        Star star;
                        if (random.nextBoolean()) {
                            star = new VolitileStar(starX, starY, starRadius);
                        } else {
                            star = new StableStar(starX, starY, starRadius);
                        }
                        star.orbitSpacing = orbitSpacing;
                        stars.add(star);

                        // First, draw all the orbit paths for this star
                        drawOrbitsForStar(star, galaxyPane, planetsPerStar);

                        // Then add the star itself
                        Circle starCircle = new Circle(starX, starY, starRadius);
                        starCircle.setFill(star instanceof VolitileStar ? Color.ORANGE : Color.YELLOW);
                        galaxyPane.getChildren().add(starCircle);
                        star.setStarCircle(starCircle);


                        // Finally, add planets for this star
                        createPlanetsForStar(star, galaxyPane, planetsPerStar);

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
                    }
                }
            }

            if (!placementSuccess) {
                System.out.println("Warning: Failed to place star system #" + (i + 1) + " after " + maxTries + " attempts");
            } else {
                System.out.println("Star #" + (i + 1) + " placed with " + stars.getLast().planets.size() +
                        " planets, radius " + stars.getLast().radius + ", orbit spacing " + stars.getLast().orbitSpacing);
            }
        }


        // Update the color table with all planet colors
        updateColorTable();
    }
    private int calculateMaxOrbitRadiusForStar(int starRadius, int planetCount, int orbitSpacing) {
        // Calculate the maximum orbit radius for a star
        // First orbit is now at a consistent distance: starRadius + orbitSpacing
        return starRadius + (planetCount * orbitSpacing) + maxPlanetRadius;
    }

    private void drawOrbitsForStar(Star star, Pane root, int planetCount) {
        // Draw all orbits with consistent spacing
        for (int j = 0; j < planetCount; j++) {
            // All orbits are spaced at consistent intervals from the star
            // First orbit is one full spacing away from the star edge
            int orbitDistance = star.radius + ((j + 1) * star.orbitSpacing);
            drawOrbitPath(root, star, orbitDistance);

            // Store the orbit distance in the star for later reference
            star.orbitDistances.add(orbitDistance);
        }
    }

    private void createPlanetsForStar(Star star, Pane root, int planetCount) {
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
            double distanceFactor = (double) (j + 1) / planetCount; // 0.2 for the first planet in the 5-planet system, 1.0 for last
            double orbitSpeed = baseFactor * (1 - 0.7 * distanceFactor); // Slow down outer planets more

            double initialAngle = random.nextDouble() * 2 * Math.PI;

            // Create a planet at initial position
            int planetX = (int) (star.x + orbitDistance * Math.cos(initialAngle));
            int planetY = (int) (star.y + orbitDistance * Math.sin(initialAngle));

            // Generate a random color for the planet's civilization
            Color planetColor = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());

            // Add the color to our list for the color table


            Circle planetCircle = new Circle(planetX, planetY, planetRadius);
            planetCircle.setFill(planetColor);
            planetCircle.setStroke(Color.BLACK);
            root.getChildren().add(planetCircle);

            // Create a civilization for this planet
            Civilization civilization = new Civilization(planetColor);

            // Add a planet to star's collection
            Planet planet = new Planet(planetCircle, orbitDistance, initialAngle, orbitSpeed, planetRadius, civilization);
            star.planets.add(planet);

            arrayPlanet.add(planet);
        }
    }

    private void drawOrbitPath(Pane root, Star star, int orbitDistance) {
        // Draw orbit circles with small white dots
        int dotCount = 60;
        for (int i = 0; i < dotCount; i++) {
            double angle = (i / (double) dotCount) * 2 * Math.PI;
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

                // Calculate a new position
                double newX = star.x + planet.orbitDistance * Math.cos(planet.angle);
                double newY = star.y + planet.orbitDistance * Math.sin(planet.angle);

                // Update planet position
                planet.circle.setCenterX(newX);
                planet.circle.setCenterY(newY);

                // Update civilization color on the circle
                planet.circle.setFill(planet.civilization.getColor());

                // Update the power label position if visible
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
        for (Planet ignored : arrayPlanet) {
            List<Planet> planets = arrayPlanet;

            // Only process if there are at least 2 planets
            if (planets.size() < 2) continue;

            // Check each planet against others in the same star system
            for (int i = 0; i < planets.size(); i++) {
                Planet planet1 = planets.get(i);

                for (int j = i + 1; j < planets.size(); j++) {
                    Planet planet2 = planets.get(j);

                    if (planet1.civilization.idCivilization == planet2.civilization.idCivilization) break;

                    // Calculate distance between planets
                    double distance = Math.sqrt(
                            Math.pow(planet1.circle.getCenterX() - planet2.circle.getCenterX(), 2) +
                                    Math.pow(planet1.circle.getCenterY() - planet2.circle.getCenterY(), 2)
                    );

                    // If planets are close and random chance hits, they interact
                    double interactionDistance = planet1.radius + planet2.radius + 500;
                    if (distance <= interactionDistance && random.nextInt(100) < civilizationInteractionChance) {
//
                        // Determine if civ1 attacks civ2 or vice versa (random)
                        boolean civ1Attacks = random.nextBoolean();

                        Planet attacker = civ1Attacks ? planet1 : planet2;
                        Planet defender = civ1Attacks ? planet2 : planet1;


                        if (attacker.circle.getFill() == Color.RED) {
                            attacker.circle.setStroke(Color.DARKRED);
                        } else {
                            attacker.circle.setStroke(Color.RED);
                        }
                        attacker.circle.setStrokeWidth(2);

                        if (defender.circle.getFill() == Color.WHITE) {
                            defender.circle.setStroke(Color.GRAY);
                        } else {
                            defender.circle.setStroke(Color.WHITE);
                        }
                        defender.circle.setStrokeWidth(2);


                        // Process the attack
                        boolean attackSuccess = attacker.civilization.attack(defender.civilization);

                        // Show power values
                        showPowerValue(attacker, attacker.civilization.getPowerPoints());
                        showPowerValue(defender, defender.civilization.getPowerPoints());

                        // If attack succeeded, update the defender's planet color
                        if (attackSuccess) {
                            defender.circle.setFill(attacker.civilization.getColor());

                            // After interaction, summon power to win civilizations
                            attacker.civilization.summonCivilization(defender.civilization);

                        }

                        updatePowerCivilization(attacker, attacker.civilization.getPowerPoints());

                        // Update the color table to reflect changes
                        updateColorTable();
                    }
                }
            }
        }
    }

    private void showPowerValue(Planet planet, int power) {
        // Create or get an existing power label
        Label powerLabel = powerLabels.get(planet.circle);

        if (powerLabel == null) {
            // Create a new label
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

        // Set the remaining display time
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
                            strokeUpdate();
                        }
                    }
                }
            }
        }
    }

    private void strokeUpdate() {
        for (Planet planet : arrayPlanet) {
            planet.circle.setStroke(Color.BLACK);
        }
    }

    public void updatePowerCivilization(Planet attacker, int power) {
        for (Planet planet : arrayPlanet) {
            if (planet.civilization.idCivilization == attacker.civilization.idCivilization) {
                planet.civilization.setPowerPoints(power);
            }
        }
    }

    //For star explosion, check if explosion points >= 100 and increment points for volatile stars
    private void processStarExplosion(BorderPane root) {
        // First, increment explosion points for all volatile stars
        for (Star star : stars) {
            star.incrementExplosionPoints();
        }

        // Then check for any stars that can explode
        List<Star> starsToExplode = new ArrayList<>();
        for (Star star : stars) {
            if (star.canExplode()) {
                starsToExplode.add(star);
            }
        }

        // Explode stars that reached the threshold
        for (Star star : starsToExplode) {
            explodeStar(root, star);
        }
    }

    // For star explosion - now takes the actual Star object
    private void explodeStar(BorderPane ignoredRoot, Star star) {
        // Remove the original circle from the scene
        galaxyPane.getChildren().remove(star.starCircle);

        // Remove all planets associated with this star
        List<Planet> planetsToRemove = new ArrayList<>(star.planets);
        for (Planet planet : planetsToRemove) {
            // Remove a planet from the scene
            galaxyPane.getChildren().remove(planet.circle);

            // Remove any power labels
            Label powerLabel = powerLabels.get(planet.circle);
            if (powerLabel != null) {
                galaxyPane.getChildren().remove(powerLabel);
                powerLabels.remove(planet.circle);
            }

            // Remove from a global planet list
            arrayPlanet.remove(planet);
        }

        // Clear the star's planet list
        star.planets.clear();

        // Create a list to hold all explosion particles
        List<Circle> particles = new ArrayList<>();

        // Create explosion particles
        int particleCount = 20;
        double radius = star.radius;
        Color color = (Color) star.starCircle.getFill();

        for (int i = 0; i < particleCount; i++) {
            Circle particle = new Circle(radius / 4);
            particle.setFill(color);
            particle.setCenterX(star.x);
            particle.setCenterY(star.y);

            // Add some variation to particle colors
            if (random.nextDouble() > 0.7) {
                particle.setFill(color.brighter());
            } else if (random.nextDouble() > 0.7) {
                particle.setFill(color.darker());
            }

            particles.add(particle);
            galaxyPane.getChildren().add(particle);
        }

        // Create animations for each particle
        Timeline explosion = new Timeline();

        for (Circle particle : particles) {
            // Random angle and distance for particle to travel
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = 50 + random.nextDouble() * 100;

            // Calculate final position
            double finalX = particle.getCenterX() + Math.cos(angle) * distance;
            double finalY = particle.getCenterY() + Math.sin(angle) * distance;

            // Create keyframes for movement and fading
            KeyValue xValue = new KeyValue(particle.centerXProperty(), finalX);
            KeyValue yValue = new KeyValue(particle.centerYProperty(), finalY);
            KeyValue opacityValue = new KeyValue(particle.opacityProperty(), 0);
            KeyValue scaleValue = new KeyValue(particle.scaleXProperty(), 0.1);
            KeyValue scaleYValue = new KeyValue(particle.scaleYProperty(), 0.1);

            KeyFrame keyFrame = new KeyFrame(Duration.millis(800),
                    xValue, yValue, opacityValue, scaleValue, scaleYValue);

            explosion.getKeyFrames().add(keyFrame);
        }

        // When animation finishes, remove all particles and reset the star
        explosion.setOnFinished(e -> {
            galaxyPane.getChildren().removeAll(particles);

            // Reset the star's explosion points to 1
            star.explosionPoints = 1;

            // Remove a star from a star list
            stars.remove(star);

            // Update the color table to reflect the changes
            updateColorTable();
        });

        // Play the explosion animation
        explosion.play();
    }

    public static void main(String[] args) {
        launch(args);
    }

    
}