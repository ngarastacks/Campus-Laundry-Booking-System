package com.campuslaundry.service;

/**
 * The only thing BookingService needs from the user side of the system.
 * Your partner's UserService can implement this interface, so neither
 * of you has to wait for the other's code.
 */
public interface UserLookup {
    /** True if a user with this student ID exists and is allowed to book. */
    boolean isActiveUser(String studentId);
}