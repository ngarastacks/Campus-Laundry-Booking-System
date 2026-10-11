package com.campuslaundry.service;

import com.campuslaundry.model.Booking;
import com.campuslaundry.model.BookingStatus;
import com.campuslaundry.model.Machine;
import com.campuslaundry.model.ServiceType;
import com.campuslaundry.repository.BookingRepository;
import com.campuslaundry.repository.MachineRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Core booking rules: create, validate, cancel and look up bookings. */
public class BookingService {
    private final BookingRepository bookingRepo;
    private final MachineRepository machineRepo;
    private final UserLookup userLookup;

    public BookingService(BookingRepository bookingRepo, MachineRepository machineRepo,
                          UserLookup userLookup) {
        this.bookingRepo = bookingRepo;
        this.machineRepo = machineRepo;
        this.userLookup = userLookup;
    }

    /**
     * Creates a booking after checking every rule.
     * synchronized so two threads can't grab the same slot at the same time.
     */
    public synchronized Booking createBooking(String studentId, int machineId,
                                              ServiceType serviceType,
                                              LocalDateTime start, LocalDateTime end)
            throws BookingException {

        if (studentId == null || !userLookup.isActiveUser(studentId)) {
            throw new BookingException("User not found or not allowed to book.");
        }
        if (serviceType == null) {
            throw new BookingException("Please choose a service type.");
        }
        if (start == null || end == null || !end.isAfter(start)) {
            throw new BookingException("End time must be after start time.");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new BookingException("You can't book a time in the past.");
        }

        Machine machine = machineRepo.findById(machineId)
                .orElseThrow(() -> new BookingException("Machine " + machineId + " does not exist."));
        if (!machine.isInService()) {
            throw new BookingException(machine.getName() + " is out of service.");
        }
        if (!isMachineFree(machineId, start, end)) {
            throw new BookingException(machine.getName() + " is already booked for that time.");
        }

        Booking booking = new Booking(bookingRepo.nextId(), studentId, machine, serviceType,
                start, end, BookingStatus.CONFIRMED);
        return bookingRepo.save(booking);
    }

    /** Cancels a booking. Only its owner can cancel, and only before it starts running. */
    public synchronized Booking cancelBooking(int bookingId, String studentId)
            throws BookingException {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking " + bookingId + " does not exist."));

        if (!booking.getStudentId().equals(studentId)) {
            throw new BookingException("You can only cancel your own bookings.");
        }
        if (booking.getStatus() != BookingStatus.PENDING
                && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingException("A " + booking.getStatus() + " booking can't be cancelled.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepo.save(booking);
    }

    /** True if no active booking on this machine overlaps the given time range. */
    public boolean isMachineFree(int machineId, LocalDateTime start, LocalDateTime end) {
        for (Booking b : bookingRepo.findByMachineId(machineId)) {
            if (b.isActive() && b.overlaps(start, end)) {
                return false;
            }
        }
        return true;
    }

    /** Machines that are in service and free for the given time range. */
    public List<Machine> findAvailableMachines(LocalDateTime start, LocalDateTime end) {
        List<Machine> available = new ArrayList<>();
        for (Machine m : machineRepo.findAll()) {
            if (m.isInService() && isMachineFree(m.getId(), start, end)) {
                available.add(m);
            }
        }
        return available;
    }

    public List<Booking> getBookingsForUser(String studentId) {
        return bookingRepo.findByStudentId(studentId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
    }
}