package org.example.poprojectgalaxyv7;

public class StableStar extends Star {
    public StableStar(double x, double y, int radius) {
        super(x, y, radius);
    }

    @Override
    public void incrementExplosionPoints() {
        // Their explosion points remain constant at 1
    }
}
