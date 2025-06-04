package org.example.poprojectgalaxyv7;

import javafx.scene.shape.Circle;

// Planet class for animation
public class Planet extends Gui {
    //COMPOSITION
    Circle circle;
    double orbitDistance;
    double angle;
    double speed;
    int radius;
    // COMPOSITION
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