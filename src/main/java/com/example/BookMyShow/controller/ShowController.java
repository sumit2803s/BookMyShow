package com.example.BookMyShow.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.BookMyShow.service.ShowService;

@RestController
@RequestMapping("/api")
public class ShowController {
    @Autowired
    private ShowService service;

    @PostMapping("/registerShow")
    public String registerShow(@RequestParam String name, @RequestParam String genre) {
        return service.registerShow(name, genre);
    }

    @PostMapping("/onboardSlots")
    public String onboardSlots(@RequestParam String name, @RequestBody Map<String, Integer> slotMap) {
        return service.onboardSlots(name, slotMap);
    }

    @GetMapping("/availableShows")
    public List<String> showAvailByGenre(@RequestParam String genre) {
        return service.showAvailByGenre(genre);
    }

    @PostMapping("/book")
    public String book(@RequestParam String user, @RequestParam String show,
                       @RequestParam String time, @RequestParam int persons) {
        return service.bookTicket(user, show, time, persons);
    }
    @GetMapping("/user/{user}/bookings")
    public List<String> getUserBookings(@PathVariable String user) {
        return service.getBookingsForUser(user);
    }
    @PostMapping("/cancel")
    public String cancel(@RequestParam String bookingId) {
        return service.cancelBooking(bookingId);
    }
    @GetMapping("/trending")
    public String trending() {
        return service.trendingShow();
    }
}

