package com.example.BookMyShow.model;

public class BookingRequest {
    private String user;
    private int persons;

    public BookingRequest(String user, int persons) {
        this.user = user;
        this.persons = persons;
    }

    public String getUser() {
        return user;
    }

    public int getPersons() {
        return persons;
    }
}
