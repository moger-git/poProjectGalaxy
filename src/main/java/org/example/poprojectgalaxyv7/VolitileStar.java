package org.example.poprojectgalaxyv7;

public class VolitileStar extends Star {
    public VolitileStar(double x, double y, int radius) {
        super(x, y, radius);
    }

    @Override
    public void incrementExplosionPoints() {
        // Volatile stars accumulate explosion points each tick
        explosionPoints++;
    }
}
