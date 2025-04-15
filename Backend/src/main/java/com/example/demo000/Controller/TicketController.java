package com.example.demo000.Controller;

import com.example.demo000.Configuration.SystemConfig;
import com.example.demo000.Entity.Customer;
import com.example.demo000.Entity.Ticket;
import com.example.demo000.Entity.Vendor;
import com.example.demo000.Model.TicketPool;
import com.example.demo000.Service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Allow Cross-Origin Resource Sharing (CORS) from the frontend
@CrossOrigin(origins = "http://localhost:5173") // Allowing CORS for frontend
@RestController
@RequestMapping("/api")
public class TicketController {
    private List<String> statusList = new ArrayList<>(); // Define statusList here

    @Autowired
    private TicketService ticketService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // For sending messages via WebSocket

    private SystemConfig systemConfig; // Store system config globally
    private boolean isSystemRunning = false;
    private TicketPool ticketPool;
    private Thread[] vendorThreads;
    private Thread[] customerThreads;

    // Endpoint to configure the system and save the config
    @PostMapping("/configure")
    public String configureSystem() {
        // Load the system configuration from the JSON file
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            File configFile = new File("SystemInfo.json");

            if (!configFile.exists()) {
                return "Configuration file not found!";
            }

            systemConfig = objectMapper.readValue(configFile, SystemConfig.class);

            // Initialize the TicketPool and prepare threads
            List<Ticket> tickets = new ArrayList<>();
            ticketPool = new TicketPool(tickets, systemConfig, ticketService);

            vendorThreads = new Thread[10];
            customerThreads = new Thread[10];

            // Initialize vendor threads
            for (int i = 0; i < 10; i++) {
                int vendorRandomTicket = (int) (Math.random() * 10) + 1;
                Vendor vendor = new Vendor(ticketPool, systemConfig, vendorRandomTicket, ticketService);
                vendorThreads[i] = new Thread(vendor, "Vendor ID-" + i);
            }

            // Initialize customer threads
            for (int i = 0; i < 10; i++) {
                int customerRandomTicket = (int) (Math.random() * 10) + 1;
                Customer customer = new Customer(ticketPool, systemConfig, customerRandomTicket, ticketService);
                customerThreads[i] = new Thread(customer, "Customer ID-" + i);
            }

            return "System configured successfully using configuration from JSON.";

        } catch (IOException e) {
            return "Error loading configuration: " + e.getMessage();
        }
    }

    // Endpoint to start the threads after user presses "Start" on frontend
    @PostMapping("/start")
    public String startSystem() {
        if (isSystemRunning) {
            return "The system is already running.";
        }

        // Ensure system is configured before starting
        if (systemConfig == null || ticketPool == null) {
            return "System is not configured. Please configure the system first.";
        }

        // Start vendor threads
        for (Thread vendorThread : vendorThreads) {
            if (vendorThread != null) {
                vendorThread.start();
                System.out.println(vendorThread.getName() + " under vendor started.");
            }
        }

        // Start customer threads
        for (Thread customerThread : customerThreads) {
            if (customerThread != null) {
                customerThread.start();
                System.out.println(customerThread.getName() + " started.");
            }
        }

        isSystemRunning = true;
        return "System started successfully. Check the backend for real time processing";
    }

    // Endpoint to stop the system (for cleanup if necessary)
    @PostMapping("/stop")
    public String stopSystem() {
        if (!isSystemRunning) {
            return "The system is not running.";
        }

        // Interrupt vendor threads
        for (Thread vendorThread : vendorThreads) {
            if (vendorThread != null && vendorThread.isAlive()) {
                vendorThread.interrupt(); // Interrupt the thread
                System.out.println(vendorThread.getName() + " has been interrupted.");
            }
        }

        // Interrupt customer threads
        for (Thread customerThread : customerThreads) {
            if (customerThread != null && customerThread.isAlive()) {
                customerThread.interrupt(); // Interrupt the thread
                System.out.println(customerThread.getName() + " has been interrupted.");
            }
        }

        isSystemRunning = false; // Mark the system as stopped
        return "System stopped and threads interrupted.";
    }

    // Endpoint to get the current status
    @GetMapping("/status")
    public List<String> getStatusList() {
        return ticketService.getStatusList();
    }

    // Endpoint to get the latest status (for convenience)
    @GetMapping("/latest-status")
    public String getLatestStatus() {
        return ticketService.getLatestStatus();
    }

    // Method to push real-time updates using WebSocket
    private void sendStatusUpdate(String message) {
        messagingTemplate.convertAndSend("/topic/status", message);
    }

    @PostMapping("/reset")
    public String resetSystem() {
        // Clear the ticket pool and status list
        ticketPool.reset(); // Clears all tickets from the pool
        statusList.clear(); // Clears the status logs

        return "System reset successfully. Ticket pool and logs cleared.";
    }

}
