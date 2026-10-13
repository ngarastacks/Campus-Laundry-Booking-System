package com.campuslaundry.repository;

import com.campuslaundry.model.Booking;
import java.util.List;
import java.util.Optional;

/**
 * Storage contract for bookings. BookingService only knows this interface, so
 * the in-memory version can later be swapped for a database version
 * without changing any service code.
 */
public interface BookingRepository {
    /** Saves a new booking (or updates an existing one) and returns it. */
    Booking save(Booking booking);
    Optional<Booking> findById(int id);
    List<Booking> findByStudentId(String studentId);
    List<Booking> findByMachineId(int machineId);
    List<Booking> findAll();
    /** Next free booking id. */
    int nextId();
}