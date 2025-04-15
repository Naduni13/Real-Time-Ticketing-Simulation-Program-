package com.example.demo000.Entity;

import java.math.BigDecimal;

public class Ticket {
    private String eventName; // Name of the event
    private BigDecimal ticketPrice; // Price of the ticket
    private int ticketID; // Unique ID for each ticket
    private String vendorName;
    // Constructor to initialize the ticket with event name and price
    public Ticket(String eventName, BigDecimal ticketPrice, String vendorName) {
        this.eventName = eventName;
        this.ticketPrice = ticketPrice;
        this.vendorName = vendorName; // Track which vendor created this ticket
    }


    // Getter for eventName
    public String getEventName() {
        return eventName;
    }

    // Setter for eventName
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    // Getter for ticketPrice
    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    // Setter for ticketPrice
    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    // Getter for ticketID
    public int getTicketID() {
        return ticketID;
    }

    // Setter for ticketID
    public void setTicketID(int ticketID) {
        this.ticketID = ticketID;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "eventName='" + eventName + '\'' +
                ", ticketPrice=" + ticketPrice +
                ", ticketID=" + ticketID +
                '}';
    }
}

