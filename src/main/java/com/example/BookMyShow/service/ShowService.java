package com.example.BookMyShow.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.BookMyShow.model.Booking;
import com.example.BookMyShow.model.Show;
import com.example.BookMyShow.model.Slot;
import com.example.BookMyShow.model.TimeSlot;

@Service
public class ShowService {
    private final Map<String, Show> shows = new HashMap<>();
    private final Map<String, Booking> bookings = new HashMap<>();
    private final Map<String, Integer> trending = new HashMap<>();
    private final Map<String, List<Booking>> userBookings = new HashMap<>();

    public String registerShow(String name, String genre) {
        shows.put(name, new Show(name, genre));
        return name + " show is registered !!";
    }

    public String onboardSlots(String name, Map<String, Integer> slotMap) {
        Show show = shows.get(name);
        if (show == null) return "Show not found";

        for (Map.Entry<String, Integer> entry : slotMap.entrySet()) {
            String[] times = entry.getKey().split("-");
            TimeSlot slot = new TimeSlot(times[0], times[1]);
            if (!slot.isValid()) return "Invalid slot: " + entry.getKey();
            show.addSlot(slot, entry.getValue());
        }
        return "Done!";
    }

    public List<String> showAvailByGenre(String genre) {
        return shows.values().stream()
                .filter(s -> s.getGenre().equalsIgnoreCase(genre))
                .flatMap(s -> s.getSlots().stream()
                        .map(slot -> slot.displayAvailability(s.getName())))
                .collect(Collectors.toList());
    }

    public String bookTicket(String user, String showName, String time, int persons) {
        Show show = shows.get(showName);
        if (show == null) return "Show not found";

        LocalTime start = LocalTime.parse(time);
        TimeSlot slot = new TimeSlot(time, start.plusHours(1).toString());

        for (Show s : shows.values()) {
            Slot sSlot = s.getSlotMap().get(slot);
            if (sSlot != null && sSlot.hasUser(user))
                return "User already booked another show at same time.";
        }

        Slot bookingSlot = show.getSlotMap().get(slot);
        if (bookingSlot == null) return "Slot not found";

        if (!bookingSlot.isAvailable(persons)) {
            bookingSlot.addToWaitlist(user, persons);
            return "Slot full. User added to waitlist.";
        }

        Booking booking = bookingSlot.book(user, persons);
        bookings.put(booking.getBookingId(), booking);
        userBookings.computeIfAbsent(user, k -> new ArrayList<>()).add(booking);
        trending.put(showName, trending.getOrDefault(showName, 0) + persons);
        return "Booked. Booking id: " + booking.getBookingId();
    }

    public String cancelBooking(String bookingId) {
        Booking booking = bookings.remove(bookingId);
        if (booking == null) return "Booking not found";

        Show show = shows.values().stream()
                .filter(s -> s.getSlotMap().containsKey(booking.getTimeSlot()))
                .findFirst().orElse(null);

        if (show == null) return "Show not found";
        userBookings.getOrDefault(booking.getUser(), new ArrayList<>())
                .removeIf(b -> b.getBookingId().equals(bookingId));

        Slot slot = show.getSlotMap().get(booking.getTimeSlot());
        slot.cancel(booking.getUser());

        Booking waitlisted = slot.tryFulfillWaitlist();
        if (waitlisted != null)
            bookings.put(waitlisted.getBookingId(), waitlisted);

        return "Booking Canceled";
    }
    public List<String> getBookingsForUser(String user) {
        List<Booking> list = userBookings.get(user);
        if (list == null || list.isEmpty()) return List.of("No bookings found for user.");
        return list.stream()
                .map(b -> "Booking ID: " + b.getBookingId() +
                        ", Show: " + b.getShowName() +
                        ", Time: " + b.getTimeSlot()+
                        ", Seats: " + b.getPersons())
                .collect(Collectors.toList());
    }

    public String trendingShow() {
        return trending.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No bookings yet");
    }
}
