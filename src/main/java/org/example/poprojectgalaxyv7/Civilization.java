package org.example.poprojectgalaxyv7;

import javafx.scene.paint.Color;

import java.util.Random;

public class Civilization {
    // ENCAPSULATION
    private Color color;
    private int powerPoints;
    private static final Random random = new Random();
    private static final int MIN_POWER = 50;
    private static final int MAX_POWER = 100;
    public int idCivilization;

    /**
     * Create a new civilization with the specified color and random power points.
     *
     * @param color The color representing this civilization
     */
    public Civilization(Color color) {
        this.color = color;
        this.powerPoints = generateRandomPower();
        this.idCivilization = random.nextInt(1000);
    }

    /**
     * Generate a random power value for the civilization.
     *
     * @return A random integer between MIN_POWER and MAX_POWER
     */
    private int generateRandomPower() {
        return random.nextInt(MAX_POWER - MIN_POWER + 1) + MIN_POWER;
    }

    /**
     * Get the color of this civilization.
     *
     * @return The civilization's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the color of this civilization (used when conquered).
     *
     * @param color The new color for the civilization
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Get the power points of this civilization.
     *
     * @return The civilization's power points
     */
    public int getPowerPoints() {
        return powerPoints;
    }

    /**
     * Set the power points of this civilization.
     *
     * @param powerPoints The new power points value
     */
    public void setPowerPoints(int powerPoints) {
        this.powerPoints = powerPoints;
    }

    /**
     * Attack another civilization. If this civilization is stronger,
     * the target will adopt this civilization's color.
     *
     * @param target The civilization being attacked
     * @return true if the attack was successful, false otherwise
     */
    // POLYMORPHISM
    public boolean attack(Civilization target) {
        if (this.powerPoints >= target.powerPoints) {
            target.setColor(this.color);
            target.idCivilization = this.idCivilization;
            return true;
        }
        return false;
    }

    public void summonCivilization(Civilization civilization) {
        if ((this.powerPoints > 1000) || (this.powerPoints + civilization.powerPoints > 1000)) {
            this.powerPoints = 1000;
        } else {
            this.powerPoints += civilization.powerPoints-30;
            civilization.powerPoints = this.powerPoints;
        }
    }

}