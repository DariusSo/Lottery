package org.example;

import java.util.List;

public class Ticket {
    private String uuid;
    private List<Integer> tickets;

    public Ticket(String uuid, List<Integer> tickets) {
        this.uuid = uuid;
        this.tickets = tickets;
    }

    public Ticket() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<Integer> getTickets() {
        return tickets;
    }

    public void setTickets(List<Integer> tickets) {
        this.tickets = tickets;
    }
}
