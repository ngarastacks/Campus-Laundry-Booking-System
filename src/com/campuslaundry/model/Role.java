package com.campuslaundry.model;

public enum Role {
    STUDENT(0.0),
    STAFF(0.10),
    ADMIN(0.0);

    private final double discountRate;

    Role(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getDiscountRate() {
        return discountRate;
    }
}
