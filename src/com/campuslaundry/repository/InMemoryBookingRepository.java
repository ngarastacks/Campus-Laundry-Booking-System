package com.campuslaundry.repository;

import com.campuslaundry.model.Booking;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Keeps bookings in memory. Data is lost when the program closes. */
public class InMemoryBookingRepository implements BookingRepository {
    private final Map<Integer, Booking> store = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    @Override
    public Booking save(Booking booking) {
        store.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(int id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Booking> findByStudentId(String studentId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : store.values()) {
            if (b.getStudentId().equals(studentId)) {
                result.add(b);
            }
        }
        return result;
    }

    @Override
    public List<Booking> findByMachineId(int machineId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : store.values()) {
            if (b.getMachine().getId() == machineId) {
                result.add(b);
            }
        }
        return result;
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public int nextId() {
        return idCounter.getAndIncrement();
    }
}