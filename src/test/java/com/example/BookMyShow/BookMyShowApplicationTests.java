package com.example.BookMyShow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.BookMyShow.service.ShowService;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookMyShowApplicationTests {

	private ShowService service;

	@BeforeEach
	public void setup() {
		service = new ShowService();
	}

	@Test
	public void testRegisterShow() {
		String response = service.registerShow("ShowA", "Comedy");
		assertEquals("ShowA show is registered !!", response);
	}

	@Test
	public void testOnboardSlots_Success() {
		service.registerShow("ShowA", "Comedy");
		Map<String, Integer> slots = new HashMap<>();
		slots.put("10:00-11:00", 10);
		String response = service.onboardSlots("ShowA", slots);
		assertEquals("Done!", response);
	}

	@Test
	public void testOnboardSlots_InvalidSlot() {
		service.registerShow("ShowA", "Comedy");
		Map<String, Integer> slots = new HashMap<>();
		slots.put("10:00-09:00", 5);  // Invalid time range
		String response = service.onboardSlots("ShowA", slots);
		assertTrue(response.startsWith("Invalid slot"));
	}

	@Test
	public void testOnboardSlots_ShowNotFound() {
		Map<String, Integer> slots = new HashMap<>();
		slots.put("09:00-10:00", 5);
		String response = service.onboardSlots("NonExistent", slots);
		assertEquals("Show not found", response);
	}

	@Test
	public void testShowAvailByGenre() {
		service.registerShow("ShowA", "Action");
		Map<String, Integer> slots = Map.of("10:00-11:00", 20);
		service.onboardSlots("ShowA", slots);

		List<String> result = service.showAvailByGenre("Action");
		assertFalse(result.isEmpty());
		assertTrue(result.get(0).contains("ShowA"));
	}

	@Test
	public void testBookTicket_Success() {
		service.registerShow("ShowA", "Drama");
		service.onboardSlots("ShowA", Map.of("09:00-10:00", 5));
		String response = service.bookTicket("user1", "ShowA", "09:00", 2);
		assertTrue(response.startsWith("Booked. Booking id:"));
	}

	@Test
	public void testBookTicket_SlotFull_AddToWaitlist() {
		service.registerShow("ShowA", "Drama");
		service.onboardSlots("ShowA", Map.of("10:00-11:00", 1));
		service.bookTicket("user1", "ShowA", "10:00", 1);
		String response = service.bookTicket("user2", "ShowA", "10:00", 1);
		assertEquals("Slot full. User added to waitlist.", response);
	}

	@Test
	public void testBookTicket_UserAlreadyBooked() {
		service.registerShow("ShowA", "Thriller");
		service.registerShow("ShowB", "Thriller");
		service.onboardSlots("ShowA", Map.of("11:00-12:00", 5));
		service.onboardSlots("ShowB", Map.of("11:00-12:00", 5));

		service.bookTicket("user1", "ShowA", "11:00", 1);
		String response = service.bookTicket("user1", "ShowB", "11:00", 1);
		assertEquals("User already booked another show at same time.", response);
	}

	@Test
	public void testBookTicket_SlotNotFound() {
		service.registerShow("ShowA", "Romance");
		service.onboardSlots("ShowA", Map.of("12:00-13:00", 5));
		String response = service.bookTicket("user1", "ShowA", "14:00", 1);
		assertEquals("Slot not found", response);
	}

	@Test
	public void testCancelBooking_Success() {
		service.registerShow("ShowA", "Sci-fi");
		service.onboardSlots("ShowA", Map.of("13:00-14:00", 5));
		String bookingResponse = service.bookTicket("user1", "ShowA", "13:00", 1);
		String bookingId = bookingResponse.split(": ")[1];

		String cancelResponse = service.cancelBooking(bookingId);
		assertEquals("Booking Canceled", cancelResponse);
	}

	@Test
	public void testCancelBooking_BookingNotFound() {
		String response = service.cancelBooking("nonexistent-id");
		assertEquals("Booking not found", response);
	}

	@Test
	public void testGetBookingsForUser_NoBookings() {
		List<String> response = service.getBookingsForUser("newUser");
		assertEquals(List.of("No bookings found for user."), response);
	}

	@Test
	public void testGetBookingsForUser_WithBookings() {
		service.registerShow("ShowA", "Horror");
		service.onboardSlots("ShowA", Map.of("15:00-16:00", 3));
		String bookingResponse = service.bookTicket("user1", "ShowA", "15:00", 1);
		List<String> bookings = service.getBookingsForUser("user1");

		assertFalse(bookings.isEmpty());
		assertTrue(bookings.get(0).contains("Booking ID"));
	}

	@Test
	public void testTrendingShow() {
		service.registerShow("ShowA", "Action");
		service.registerShow("ShowB", "Comedy");
		service.onboardSlots("ShowA", Map.of("16:00-17:00", 10));
		service.onboardSlots("ShowB", Map.of("17:00-18:00", 10));

		service.bookTicket("user1", "ShowA", "16:00", 3);
		service.bookTicket("user2", "ShowB", "17:00", 2);

		String trending = service.trendingShow();
		assertEquals("ShowA", trending);
	}

	@Test
	public void testTrendingShow_NoBookings() {
		String result = service.trendingShow();
		assertEquals("No bookings yet", result);
	}

}
