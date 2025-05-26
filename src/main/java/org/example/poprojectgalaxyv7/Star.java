package org.example.poprojectgalaxyv7;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Star class to track related planets
public class Star extends Gui{
    double x, y;
    int radius;
    int orbitSpacing;
    List<Planet> planets = new ArrayList<Planet>();
    List<Integer> orbitDistances = new ArrayList<>();// Track orbit distances
    private static final int MIN_EXPLOSION = 1;
    private static final int MAX_EXPLOSION = 100;
    private static final Random random = new Random();
    int explosionPoints;
    int idStar;


    public Star(double x, double y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.orbitSpacing = baseOrbitSpacing;
        this.explosionPoints = generateRandomExplosion();
        this.idStar = random.nextInt(1000000000);
    }

    private int generateRandomExplosion() {
        return random.nextInt(MAX_EXPLOSION - MIN_EXPLOSION + 1) + MIN_EXPLOSION;
    }
}
