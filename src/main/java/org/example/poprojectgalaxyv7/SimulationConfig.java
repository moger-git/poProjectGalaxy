package org.example.poprojectgalaxyv7;

interface SimulationConfig {
    // Scene dimensions
    int SCENE_WIDTH = 1500;
    int SCENE_HEIGHT = 700;

    // Default configuration values
    int DEFAULT_STARS_COUNT = 5;
    int DEFAULT_MAX_PLANETS = 10;
    int DEFAULT_MIN_PLANETS = 1;

    // Slider range bounds
    int MIN_STARS_COUNT = 1;
    int MAX_STARS_COUNT = 16;

    int MIN_PLANETS_LOWER_BOUND = 1;
    int MAX_PLANETS_UPPER_BOUND = 16;

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

    // Civilization parameters
    int civilizationInteractionChance = 2; // % chance per frame of interaction
    int powerDisplayDuration = 120; // frames to display power when interacting

    // Simulation speed parameters
    int DEFAULT_SIMULATION_SPEED = 1; // Default frames per tick
    int MIN_SIMULATION_SPEED = 1; // Minimum frames per tick
    int MAX_SIMULATION_SPEED = 10; // Maximum frames per tick

    // File paths
    String DEFAULT_GALAXY_CONFIG_FILE = "src/main/resources/galaxy_config.csv";
    String DEFAULT_OUTPUT_DIR = "./";
}
