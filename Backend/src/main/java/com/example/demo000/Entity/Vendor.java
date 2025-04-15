package com.example.demo000.Entity;
import com.example.demo000.Configuration.SystemConfig;
import com.example.demo000.Entity.Ticket;
import com.example.demo000.Model.TicketPool;
import com.example.demo000.Service.TicketService;

import java.math.BigDecimal;

public class Vendor implements Runnable {
    private int ticketReleaseRate;
    private TicketPool ticketPool;
    private int noOfAddingTickets;
    private static int totalTickets;
    private TicketService ticketService; // Inject TicketService to update status

    public Vendor(TicketPool ticketPool, SystemConfig config, int noOfAddingTickets, TicketService ticketService) {
        this.ticketReleaseRate = config.getTicketReleaseRate();
        this.ticketPool = ticketPool;
        this.noOfAddingTickets = noOfAddingTickets;
        this.totalTickets = config.getTotalTickets();
        this.ticketService = ticketService;
    }

    @Override
    public void run() {
        while(totalTickets!=0){
            System.out.println(Thread.currentThread().getName()+" Try to Add "+noOfAddingTickets);
            for (int i = 1; i <= totalTickets; i++) {
                Ticket ticket = new Ticket("Event " + Thread.currentThread().getName(), new BigDecimal("100.00"), Thread.currentThread().getName());

                //System.out.println(Thread.currentThread().getName() + " - " + i + " th ticket");
                ticketPool.addTicket(ticket);
                totalTickets--;
                System.out.println("No of tickets can be added "+totalTickets);
                try {
                    Thread.sleep(ticketReleaseRate * 1000); // To calculate to MS
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

    }
}
