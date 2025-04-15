package com.example.demo000.Entity;

import com.example.demo000.Configuration.SystemConfig;
import com.example.demo000.Entity.Ticket;
import com.example.demo000.Model.TicketPool;
import com.example.demo000.Service.TicketService;

public class Customer implements Runnable {
    private TicketPool ticketPool;
    private int customerRetrievalRate;
    private int quantity;
    private TicketService ticketService; // Inject TicketService to update status

    public Customer(TicketPool ticketPool, SystemConfig config, int quantity, TicketService ticketService) {
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = config.getCustomerRetrievalRate();
        this.quantity = quantity;
        this.ticketService = ticketService;
    }

    @Override
    public void run() {
        System.out.println("  "+Thread.currentThread().getName() + " Try to buy " + quantity + " tickets");
        ticketService.appendToStatus(Thread.currentThread().getName() + " Try to buy " + quantity + " tickets");
        for (int i = 0; i < quantity; i++) {
            Ticket ticket = ticketPool.buyTicket();
            try {
                Thread.sleep(customerRetrievalRate * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}


