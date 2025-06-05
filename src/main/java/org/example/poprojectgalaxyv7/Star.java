package org.example.poprojectgalaxyv7;

import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// ABSTRACT CLASS
// Star class to track related planets
public abstract class Star implements SimulationConfig {
    double x, y;
    int radius;
    int orbitSpacing;
    // AGGREGATION
    List<Planet> planets = new ArrayList<>();
    List<Integer> orbitDistances = new ArrayList<>();// Track orbit distances
    protected static final int MIN_EXPLOSION = 1;
    protected static final int MAX_EXPLOSION = 100;
    protected static final Random random = new Random();
    int explosionPoints;
    int idStar;
    // COMPOSITION
    Circle starCircle;

    public Star(double x, double y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.orbitSpacing = baseOrbitSpacing;
        this.explosionPoints = generateRandomExplosion(); // All stars start with 1 explosion point
        this.idStar = random.nextInt(1000000000);
    }

    private int generateRandomExplosion() {
        return random.nextInt(MAX_EXPLOSION - MIN_EXPLOSION + 1) + MIN_EXPLOSION-30;
    }
    // POLYMORPHISM
    // Method to increment explosion points, to be overridden by subclasses
    public abstract void incrementExplosionPoints();

    // Method to check if star can explode
    public boolean canExplode() {
        return explosionPoints >= 100;
    }

    // Method to associate the visual representation with the star
    public void setStarCircle(Circle starCircle) {
        this.starCircle = starCircle;
    }
}

