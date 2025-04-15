package com.example.demo000.Model;

import com.example.demo000.Configuration.SystemConfig;
import com.example.demo000.Service.TicketService;
import com.example.demo000.Entity.Ticket;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class TicketPool {
    private List<Ticket> ticketList; // List of tickets in the pool
    private int maximumTicketCapacity; // Max tickets that can be in the pool
    private int ticketID = 1; // Ticket ID generator
    private int count =1;
    private int buyCount=1;
    private TicketService ticketService; // Service for appending status updates

    public TicketPool(List<Ticket> ticketList, SystemConfig config, TicketService ticketService) {
        this.ticketList = Collections.synchronizedList(new ArrayList<>());
        this.maximumTicketCapacity = config.getMaxTicketCapacity();
        this.ticketService = ticketService;
    }

    // Add a ticket to the pool if there is space
    // TicketPool class: synchronized methods with notifyAll to unblock threads.
    public synchronized void addTicket(Ticket ticket) {
        while (ticketList.size() >= maximumTicketCapacity) {
            try {
                System.out.println("Queue is full, waiting to add ticket... " + Thread.currentThread().getName());
                ticketService.appendToStatus("Queue is full, waiting to add ticket... " + Thread.currentThread().getName());
                wait(); // Wait until there is space in the pool
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        //System.out.println("MAX CAPACITY: "+maximumTicketCapacity);
        // Ticket can be added to the pool now.
        this.ticketList.add(ticket);
        System.out.println("Ticket added by " + Thread.currentThread().getName() + " (Ticket ID: " + ticketID + "). Pool size: " + ticketList.size()+" Added ticket Count: "+count);
        ticketService.appendToStatus("Ticket added by " + Thread.currentThread().getName() + " (Ticket ID: " + ticketID + "). Pool size: " + ticketList.size()+" Count added"+count);
        ticketID++;
        count++;
        notifyAll(); // Notify all waiting threads (customers and vendors)
    }

    public synchronized Ticket buyTicket() {
        while (ticketList.isEmpty()) {
            try {
                ticketService.appendToStatus("Queue is empty, waiting to buy a ticket... " + Thread.currentThread().getName());
                System.out.println("Queue is empty, waiting to buy a ticket... " + Thread.currentThread().getName());
                wait(); // Wait until a ticket is available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        // Customer buys the ticket
        Ticket ticket = ticketList.remove(0); // Get the first ticket from the list
        System.out.println("Ticket bought by " + Thread.currentThread().getName() + " (Ticket ID: " + ticketID + "). Pool size: " + ticketList.size()+" Buy count :"+buyCount);
        ticketService.appendToStatus("Ticket bought by " + Thread.currentThread().getName() + " (Ticket ID: " + ticketID + "). Pool size: " + ticketList.size()+" Buy Count: "+buyCount);
        ticketID++;
        buyCount++;
        notifyAll(); // Notify all waiting threads (vendors and customers)
        return ticket;
    }



    // Get the current size of the ticket pool
    public synchronized int getPoolSize() {
        return ticketList.size();
    }

    // Used to clear or reset the pool if needed (not a part of current functionality)
    public synchronized void clearPool() {
        ticketList.clear();
        ticketService.appendToStatus("Ticket pool cleared by " + Thread.currentThread().getName());
    }

    // Getter for the maximum capacity
    public int getMaximumTicketCapacity() {
        return maximumTicketCapacity;
    }

    public List<Ticket> getTicketList() {
        return ticketList;
    }

    public synchronized void reset() {
        ticketList.clear(); // Clears the list of all tickets
        ticketService.appendToStatus("Ticket pool has been reset and all tickets removed.");
    }
}

