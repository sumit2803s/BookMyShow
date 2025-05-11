package com.example.BookMyShow.model;
    public class Booking {
        private String bookingId;
        private String user;
        private TimeSlot timeSlot;
        private int persons;
        String showName;


        public Booking(String bookingId, String user,String showName ,TimeSlot timeSlot, int persons) {
            this.bookingId = bookingId;
            this.user = user;
            this.showName=showName;
            this.timeSlot = timeSlot;
            this.persons = persons;
        }

        public String getBookingId() { return bookingId; }
        public String getUser() { return user; }
        public TimeSlot getTimeSlot() { return timeSlot; }
        public int getPersons() { return persons; }
        public String getShowName() {return showName;}
    }
