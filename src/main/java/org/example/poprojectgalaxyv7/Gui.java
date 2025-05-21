package org.example.poprojectgalaxyv7;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    
    // Scene dimensions
    private final int SCENE_WIDTH = 1500;
    private final int SCENE_HEIGHT = 800;
    
    // Configuration
    private int starsCount = 5;
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
    
    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: black;");
        
        createGalaxy(root);
        
        // Animation timer for planet orbits
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updatePlanetPositions();
            }
        };
        timer.start();
        
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setTitle("Galaxy");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void createGalaxy(Pane root) {
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
                int maxX = SCENE_WIDTH - maxOrbitRadius - screenBorderPadding;
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
                        drawOrbitsForStar(star, root, planetsPerStar);
                        
                        // Then add the star itself
                        Circle starCircle = new Circle(starX, starY, starRadius);
                        starCircle.setFill(Color.YELLOW);
                        root.getChildren().add(starCircle);
                        arrayCircles.put(circleId++, starCircle);
                        
                        // Finally add planets for this star
                        createPlanetsForStar(star, root, circleId, planetsPerStar);
                        
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
                System.out.println("Star #" + (i+1) + " placed with " + stars.get(i).planets.size() + 
                                  " planets, radius " + stars.get(i).radius + ", orbit spacing " + stars.get(i).orbitSpacing);
            }
        }
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
            
            Circle planetCircle = new Circle(planetX, planetY, planetRadius);
            planetCircle.setFill(Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble()));
            root.getChildren().add(planetCircle);
            arrayCircles.put(circleId++, planetCircle);
            
            // Add planet to star's collection
            Planet planet = new Planet(planetCircle, orbitDistance, initialAngle, orbitSpeed, planetRadius);
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
        
        public Planet(Circle circle, double orbitDistance, double angle, double speed, int radius) {
            this.circle = circle;
            this.orbitDistance = orbitDistance;
            this.angle = angle;
            this.speed = speed;
            this.radius = radius;
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}