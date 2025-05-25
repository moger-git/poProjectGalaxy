package org.example.poprojectgalaxyv7;

import javafx.scene.paint.Color;

import java.util.Random;

public class Civilization {
    private Color color;
    private int powerPoints;
    private static final Random random = new Random();
    private static final int MIN_POWER = 1;
    private static final int MAX_POWER = 100;

    /**
     * Create a new civilization with the specified color and random power points.
     * @param color The color representing this civilization
     */
    public Civilization(Color color) {
        this.color = color;
        this.powerPoints = generateRandomPower();
    }

    /**
     * Generate a random power value for the civilization.
     * @return A random integer between MIN_POWER and MAX_POWER
     */
    private int generateRandomPower() {
        return random.nextInt(MAX_POWER - MIN_POWER + 1) + MIN_POWER;
    }

    /**
     * Get the color of this civilization.
     * @return The civilization's color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the color of this civilization (used when conquered).
     * @param color The new color for the civilization
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Get the power points of this civilization.
     * @return The civilization's power points
     */
    public int getPowerPoints() {
        return powerPoints;
    }

    /**
     * Set the power points of this civilization.
     * @param powerPoints The new power points value
     */
    public void setPowerPoints(int powerPoints) {
        this.powerPoints = powerPoints;
    }

    /**
     * Attack another civilization. If this civilization is stronger,
     * the target will adopt this civilization's color.
     * @param target The civilization being attacked
     * @return true if the attack was successful, false otherwise
     */
    public boolean attack(Civilization target) {
        if (this.powerPoints > target.powerPoints) {
            target.setColor(this.color);
            return true;
        }
        return false;
    }
    
    /**
     * Regenerate the power points randomly.
     */
    public void regeneratePower() {
        this.powerPoints = generateRandomPower();
    }
    public void summonCivilization(Civilization civilization) {
        this.powerPoints += civilization.powerPoints;
    }
}