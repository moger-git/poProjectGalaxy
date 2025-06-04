package org.example.poprojectgalaxyv7;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.scene.paint.Color;

public class SimulationOutput {
    private String outputFilePath;
    private int tickCounter;
    private boolean isActive;

    public SimulationOutput(String filePath) {
        this.outputFilePath = filePath;
        this.tickCounter = 0;
        this.isActive = true;

        // Initialize the file with headers
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
            writer.println("Tick,RemainingStars,CivilizationID,Color,PlanetCount,Power");
        } catch (IOException e) {
            System.err.println("Error initializing output file: " + e.getMessage());
            isActive = false;
        }
    }

    public SimulationOutput() {
        // Create a default file name with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        this.outputFilePath = "galaxy_simulation_" + timestamp + ".csv";
        this.tickCounter = 0;
        this.isActive = true;

        // Initialize the file with headers
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
            writer.println("Tick,RemainingStars,CivilizationID,Color,PlanetCount,Power");
        } catch (IOException e) {
            System.err.println("Error initializing output file: " + e.getMessage());
            isActive = false;
        }
    }

    public void recordTick(List<Star> stars) {
        if (!isActive) return;

        tickCounter++;

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath, true))) {
            // Get unique civilizations
            Map<Integer, CivilizationData> civilizationDataMap = new HashMap<>();

            // Process all planets and civilizations
            for (Star star : stars) {
                for (Planet planet : star.planets) {
                    int civId = planet.civilization.idCivilization;

                    if (!civilizationDataMap.containsKey(civId)) {
                        civilizationDataMap.put(civId, new CivilizationData(
                            civId,
                            colorToHex(planet.civilization.getColor()),
                            planet.civilization.getPowerPoints()
                        ));
                    }

                    // Increment planet count for this civilization
                    civilizationDataMap.get(civId).incrementPlanetCount();
                }
            }

            // Write data for each civilization
            for (CivilizationData data : civilizationDataMap.values()) {
                writer.println(String.format("%d,%d,%d,%s,%d,%d",
                    tickCounter,
                    stars.size(),
                    data.id,
                    data.colorHex,
                    data.planetCount,
                    data.power
                ));
            }
        } catch (IOException e) {
            System.err.println("Error writing to output file: " + e.getMessage());
        }
    }

    private String colorToHex(Color color) {
        return String.format("#%02X%02X%02X",
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }

    private static class CivilizationData {
        int id;
        String colorHex;
        int planetCount;
        int power;

        CivilizationData(int id, String colorHex, int power) {
            this.id = id;
            this.colorHex = colorHex;
            this.planetCount = 0;
            this.power = power;
        }

        void incrementPlanetCount() {
            planetCount++;
        }
    }
}
