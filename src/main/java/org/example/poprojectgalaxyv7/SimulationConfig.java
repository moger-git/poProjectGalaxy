package org.example.poprojectgalaxyv7;

interface SimulationConfig {
    // Scene dimensions
    int SCENE_WIDTH = 1700;
    int SCENE_HEIGHT = 1000;

    // Default configuration values
    int DEFAULT_STARS_COUNT = 5;
    int DEFAULT_MAX_PLANETS = 10;
    int DEFAULT_MIN_PLANETS = 1;

    // Slider range bounds
    int MIN_STARS_COUNT = 1;
    int MAX_STARS_COUNT = 15;

    int MIN_PLANETS_LOWER_BOUND = 1;
    int MAX_PLANETS_UPPER_BOUND = 15;

    // Configuration
    int maxTries = 100; // Maximum attempts to place a star
    int screenBorderPadding = 20; // Padding from the screen edge

    // Sizing parameters
    int maxStarRadius = 45;
    int minStarRadius = 25;
    int maxPlanetRadius = 15;
    int minPlanetRadius = 5;
    int baseOrbitSpacing = 40; // Spacing between orbits
    int minOrbitSpacing = 20; // Minimum spacing when adapting
}
