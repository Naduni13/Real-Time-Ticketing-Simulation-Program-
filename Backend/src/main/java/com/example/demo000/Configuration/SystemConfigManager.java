package com.example.demo000.Configuration;

import com.example.demo000.Configuration.SystemConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class SystemConfigManager { //manages the configuration process for the system
    private final Scanner scanner = new Scanner(System.in);
    protected static final String CONFIG_FILE = "SystemInfo.json";

    public SystemConfig configTheSystem() {
        SystemConfig config = null;

        // Check if the configuration file exists
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            System.out.print("An existing configuration file was found. Would you like to load it? (yes/no): ");
            String choice = scanner.next().trim().toLowerCase();

            if (choice.equals("yes")) {
                try {
                    // Load existing configuration
                    config = loadJsonFile();
                    System.out.println("Configuration loaded successfully: " + config);
                    return config;
                } catch (IOException e) {
                    System.out.println("Error loading JSON file: " + e.getMessage());
                }
            } else {
                System.out.println("Proceeding with new configuration setup...");
            }
        }

        // If no file or user chooses not to load, configure system manually
        config = new SystemConfig();
        while (true) {
            try {
                System.out.print("Enter total number of tickets (must be a positive integer): ");
                int totalTickets = getValidatedPositiveInt();
                config.setTotalTickets(totalTickets);

                System.out.print("Enter ticket release rate (must be a positive number): ");
                int ticketReleaseRate = getValidatedPositiveInt();
                config.setTicketReleaseRate(ticketReleaseRate);

                System.out.print("Enter customer retrieval rate (must be a positive number): ");
                int customerRetrievalRate = getValidatedPositiveInt();
                config.setCustomerRetrievalRate(customerRetrievalRate);

                System.out.print("Enter max ticket capacity (must be a positive number): ");
                int maxTicketCapacity = getValidatedPositiveInt();
                config.setMaxTicketCapacity(maxTicketCapacity);

                break; // Exit loop once valid data is entered
            } catch (InputMismatchException e) {
                System.out.println("Invalid input type. Please enter numbers only.");
                scanner.next(); // Clear invalid input
            }
        }

        // Save the new configuration to a JSON file
        try {
            writeJsonFile(config);
        } catch (IOException e) {
            System.out.println("Error writing JSON file: " + e.getMessage());
        }

        return config;
    }

    // Method to get validated positive integer
    private int getValidatedPositiveInt() {
        while (true) {
            try {
                int value = scanner.nextInt();
                if (value <= 0) {
                    throw new IllegalArgumentException("Value must be a positive number. Try again: ");
                }
                return value;
            } catch (InputMismatchException e) {
                System.out.print("Invalid input type. Please enter a positive number: ");
                scanner.next(); // Clear invalid input
            } catch (IllegalArgumentException e) {
                System.out.print(e.getMessage());
            }
        }
    }

    // Method to load the configuration from the JSON file
    private SystemConfig loadJsonFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(CONFIG_FILE), SystemConfig.class);
    }

    // Method to write the configuration to the JSON file
    private void writeJsonFile(SystemConfig systemConfig) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(CONFIG_FILE), systemConfig);
        System.out.println("JSON file created successfully!");
    }
}
