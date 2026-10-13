package com.campuslaundry.app;

import com.campuslaundry.model.Booking;
import com.campuslaundry.model.ServiceType;
import com.campuslaundry.repository.JdbcBookingRepository;
import com.campuslaundry.repository.JdbcMachineRepository;
import com.campuslaundry.service.BookingException;
import com.campuslaundry.service.BookingService;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Same booking logic as BookingDemo, but the data is stored in MySQL.
 * Run it twice: the bookings from the first run are still there the second
 * time, which proves the database is working.
 */
public class JdbcBookingDemo {
    public static void main(String[] args) {
        JdbcMachineRepository machines = new JdbcMachineRepository();
        BookingService service = new BookingService(
                new JdbcBookingRepository(machines),
                machines,
                id -> id.equals("S001") || id.equals("S002")); // users S001/S002 are in schema.sql

        // A random day in the next ~year, so repeated runs don't clash with earlier ones
        LocalDateTime start = LocalDateTime.now().plusDays(2 + new Random().nextInt(300))
                .withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(2);
        ServiceType type = ServiceType.values()[0];

        try {
            System.out.println("Created: " + service.createBooking("S001", 1, type, start, end));
        } catch (BookingException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
        try {
            service.createBooking("S002", 1, type, start.plusHours(1), end.plusHours(1));
        } catch (BookingException e) {
            System.out.println("Double-booking rejected: " + e.getMessage());
        }

        System.out.println("\nEverything currently stored in MySQL:");
        for (Booking b : service.getAllBookings()) {
            System.out.println("  " + b);
        }
    }
}