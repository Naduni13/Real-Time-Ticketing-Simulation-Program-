package com.example.demo000.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TicketService {

    private List<String> statusList;
    private AtomicInteger ticketsSold;
    private AtomicInteger ticketsRemaining;
    private boolean isSystemRunning;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public TicketService() {
        this.statusList = new ArrayList<>();
        this.ticketsSold = new AtomicInteger(0);
        this.ticketsRemaining = new AtomicInteger(0);
        this.isSystemRunning = false;
    }

    public void appendToStatus(String message) {
        // Send message to WebSocket clients in the "/topic/status" channel
        //messagingTemplate.convertAndSend("/topic/status", message);
        statusList.add(message); // Optionally keep the message in the status list
    }

    public synchronized List<String> getStatusList() {
        return new ArrayList<>(statusList);
    }

    public synchronized String getLatestStatus() {
        if (!statusList.isEmpty()) {
            return statusList.get(statusList.size() - 1);
        }
        return "No status available.";
    }

    public synchronized void clearStatus() {
        statusList.clear();
    }

    public String getCurrentStatus() {
        if (isSystemRunning) {
            return "System is running. Tickets sold: " + ticketsSold.get() + ", Tickets remaining: " + ticketsRemaining.get();
        } else {
            return "System is not running.";
        }
    }

    public void startSystem(int totalTickets) {
        isSystemRunning = true;
        ticketsRemaining.set(totalTickets);
        appendToStatus("System started. Total tickets available: " + totalTickets);
    }

    public void stopSystem() {
        isSystemRunning = false;
        appendToStatus("System stopped.");
    }

    public void processTicketSale() {
        if (ticketsRemaining.get() > 0) {
            ticketsSold.incrementAndGet();
            ticketsRemaining.decrementAndGet();
            appendToStatus("Ticket sold. Tickets remaining: " + ticketsRemaining.get());
        } else {
            appendToStatus("No tickets remaining.");
        }
    }

    public int getTicketsSold() {
        return ticketsSold.get();
    }

    public int getTicketsRemaining() {
        return ticketsRemaining.get();
    }
}
