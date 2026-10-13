package com.campuslaundry.app;

import com.campuslaundry.model.Booking;
import com.campuslaundry.model.ServiceType;
import com.campuslaundry.repository.InMemoryBookingRepository;
import com.campuslaundry.repository.InMemoryMachineRepository;
import com.campuslaundry.service.BookingException;
import com.campuslaundry.service.BookingService;
import java.time.LocalDateTime;

/**
 * Throwaway test for the booking logic. Right-click and Run File.
 * Delete it (or keep it as a demo) once the real menu/GUI exists.
 */
public class BookingDemo {
    public static void main(String[] args) {
        // Fake user check until your partner's UserService exists:
        // "S001" and "S002" are valid users, anyone else is not.
        BookingService service = new BookingService(
                new InMemoryBookingRepository(),
                new InMemoryMachineRepository(),
                id -> id.equals("S001") || id.equals("S002"));

        ServiceType type = ServiceType.values()[0]; // first value of your enum
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(2);

        // 1. Normal booking: should succeed
        attempt("1. Valid booking", () -> service.createBooking("S001", 1, type, start, end));

        // 2. Same machine, overlapping time: should be rejected
        attempt("2. Double-booking", () -> service.createBooking("S002", 1, type, start.plusHours(1), end.plusHours(1)));

        // 3. Different machine, same time: should succeed
        attempt("3. Other machine", () -> service.createBooking("S002", 2, type, start, end));

        // 4. Unknown user: should be rejected
        attempt("4. Unknown user", () -> service.createBooking("X999", 3, type, start, end));

        // 5. Time in the past: should be rejected
        attempt("5. Past time", () -> service.createBooking("S001", 3, type, start.minusDays(3), end.minusDays(3)));

        // 6. Someone else tries to cancel S001's booking: should be rejected
        attempt("6. Cancel by wrong user", () -> service.cancelBooking(1, "S002"));

        // 7. Owner cancels: should succeed
        attempt("7. Cancel by owner", () -> service.cancelBooking(1, "S001"));

        // 8. Machine 1 is free again, so rebooking should now succeed
        attempt("8. Rebook after cancel", () -> service.createBooking("S002", 1, type, start, end));

        System.out.println("\nAll bookings:");
        for (Booking b : service.getAllBookings()) {
            System.out.println("  " + b);
        }
    }

    private interface Action { Booking run() throws BookingException; }

    private static void attempt(String label, Action action) {
        try {
            System.out.println(label + " -> OK: " + action.run());
        } catch (BookingException e) {
            System.out.println(label + " -> REJECTED: " + e.getMessage());
        }
    }
}