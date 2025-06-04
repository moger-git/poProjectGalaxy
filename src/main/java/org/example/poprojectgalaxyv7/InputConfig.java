package org.example.poprojectgalaxyv7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InputConfig {
    private int starsCount;
    private int minPlanetsPerStar;
    private int maxPlanetsPerStar;
    private int simulationSpeed;

    public InputConfig() {
        // Default values from SimulationConfig
        this.starsCount = SimulationConfig.DEFAULT_STARS_COUNT;
        this.minPlanetsPerStar = SimulationConfig.DEFAULT_MIN_PLANETS;
        this.maxPlanetsPerStar = SimulationConfig.DEFAULT_MAX_PLANETS;
        this.simulationSpeed = SimulationConfig.DEFAULT_SIMULATION_SPEED;
    }

    public boolean loadFromCsv(String filePath) {
        System.out.println("Loading configuration from: " + filePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Map<String, Integer> configMap = new HashMap<>();

            // Skip header line if it exists
            line = reader.readLine();
            if (line != null) {
                System.out.println("First line: " + line);
                if (line.toLowerCase().contains("parameter")) {
                    // Skip header
                    line = reader.readLine();
                    System.out.println("Skipped header, reading data line: " + line);
                }
            }

            // Process each line
            while (line != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    line = reader.readLine();
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String parameter = parts[0].trim();
                    try {
                        int value = Integer.parseInt(parts[1].trim());
                        // Store with original case and lowercase for flexible matching
                        configMap.put(parameter.toLowerCase(), value);
                        System.out.println("Loaded parameter: " + parameter + " = " + value);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid value for parameter: " + parameter);
                    }
                } else {
                    System.err.println("Invalid line format: " + line);
                }
                line = reader.readLine();
            }

            // Now apply the loaded values with comprehensive matching and validation
            // Stars count
            String[] starsParams = {"stars", "starscount", "starcount", "star"};
            for (String param : starsParams) {
                if (configMap.containsKey(param)) {
                    int value = configMap.get(param);
                    if (value >= SimulationConfig.MIN_STARS_COUNT && value <= SimulationConfig.MAX_STARS_COUNT) {
                        starsCount = value;
                        System.out.println("Set stars count to: " + starsCount);
                        break;
                    } else {
                        System.out.println("Stars count " + value + " out of valid range " + 
                            SimulationConfig.MIN_STARS_COUNT + "-" + SimulationConfig.MAX_STARS_COUNT);
                    }
                }
            }

            // Min planets per star
            String[] minPlanetsParams = {"minplanets", "minplanetsperstar", "minplanet"};
            for (String param : minPlanetsParams) {
                if (configMap.containsKey(param)) {
                    int value = configMap.get(param);
                    if (value >= SimulationConfig.MIN_PLANETS_LOWER_BOUND && value <= maxPlanetsPerStar) {
                        minPlanetsPerStar = value;
                        System.out.println("Set min planets per star to: " + minPlanetsPerStar);
                        break;
                    } else {
                        System.out.println("Min planets " + value + " out of valid range " + 
                            SimulationConfig.MIN_PLANETS_LOWER_BOUND + "-" + maxPlanetsPerStar);
                    }
                }
            }

            // Max planets per star
            String[] maxPlanetsParams = {"maxplanets", "maxplanetsperstar", "maxplanet"};
            for (String param : maxPlanetsParams) {
                if (configMap.containsKey(param)) {
                    int value = configMap.get(param);
                    if (value >= minPlanetsPerStar && value <= SimulationConfig.MAX_PLANETS_UPPER_BOUND) {
                        maxPlanetsPerStar = value;
                        System.out.println("Set max planets per star to: " + maxPlanetsPerStar);
                        break;
                    } else {
                        System.out.println("Max planets " + value + " out of valid range " + 
                            minPlanetsPerStar + "-" + SimulationConfig.MAX_PLANETS_UPPER_BOUND);
                    }
                }
            }

            // Simulation speed
            String[] speedParams = {"speed", "simulationspeed", "simspeed"};
            for (String param : speedParams) {
                if (configMap.containsKey(param)) {
                    int value = configMap.get(param);
                    if (value >= SimulationConfig.MIN_SIMULATION_SPEED && value <= SimulationConfig.MAX_SIMULATION_SPEED) {
                        simulationSpeed = value;
                        System.out.println("Set simulation speed to: " + simulationSpeed);
                        break;
                    } else {
                        System.out.println("Simulation speed " + value + " out of valid range " + 
                            SimulationConfig.MIN_SIMULATION_SPEED + "-" + SimulationConfig.MAX_SIMULATION_SPEED);
                    }
                }
            }

            System.out.println("Configuration loaded successfully");
            return true;
        } catch (IOException e) {
            System.err.println("Error reading input configuration file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public int getStarsCount() {
        return starsCount;
    }

    public int getMinPlanetsPerStar() {
        return minPlanetsPerStar;
    }

    public int getMaxPlanetsPerStar() {
        return maxPlanetsPerStar;
    }

    public int getSimulationSpeed() {
        return simulationSpeed;
    }
}
