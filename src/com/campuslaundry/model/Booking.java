package com.campuslaundry.model;

import java.time.LocalDateTime;

/** A reservation of one machine, by one student, for one time slot. */
public class Booking {
    private final int id;
    private final String studentId;      // matches the user's student ID
    private final Machine machine;
    private final ServiceType serviceType;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private BookingStatus status;

    public Booking(int id, String studentId, Machine machine, ServiceType serviceType,
                   LocalDateTime startTime, LocalDateTime endTime, BookingStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.machine = machine;
        this.serviceType = serviceType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public int getId() { return id; }
    public String getStudentId() { return studentId; }
    public Machine getMachine() { return machine; }
    public ServiceType getServiceType() { return serviceType; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    /** A booking blocks its machine unless it has been cancelled or finished. */
    public boolean isActive() {
        return status != BookingStatus.CANCELLED && status != BookingStatus.COMPLETED;
    }

    /** True if this booking's time range overlaps the given range. */
    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return startTime.isBefore(otherEnd) && endTime.isAfter(otherStart);
    }

    @Override
    public String toString() {
        return "Booking #" + id + " | " + studentId + " | " + machine.getName()
                + " | " + serviceType + " | " + startTime + " -> " + endTime
                + " | " + status;
    }
}