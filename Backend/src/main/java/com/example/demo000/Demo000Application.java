package com.example.demo000;

import com.example.demo000.Configuration.SystemConfig;
import com.example.demo000.Configuration.SystemConfigManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Demo000Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Demo000Application.class, args);
		System.out.println("Backend is running. Use the frontend to configure and start the system.");
	}

	@Override
	public void run(String... args) {
		// The main logic for configuring and starting the system
		SystemConfigManager configManager = new SystemConfigManager();
		SystemConfig config = configManager.configTheSystem();  // Configure the system
		// Display the configured system parameters
		if (config != null) {
			System.out.println("\nSystem Configuration:");
			System.out.println("Customer Retrieval Rate: " + config.getCustomerRetrievalRate() );
			System.out.println("Max Ticket Capacity: " + config.getMaxTicketCapacity() );
			System.out.println("Ticket Release Rate: " + config.getTicketReleaseRate());
			System.out.println("Total Tickets: " + config.getTotalTickets());
		} else {
			System.out.println("Failed to configure the system.");
		}
	}
}
