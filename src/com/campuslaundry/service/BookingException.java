package com.campuslaundry.service;

/** Thrown when a booking request breaks a rule (double-booking, bad time, etc.). */
public class BookingException extends Exception {
    public BookingException(String message) {
        super(message);
    }
}