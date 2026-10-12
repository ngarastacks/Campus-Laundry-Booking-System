package com.campuslaundry.model;

public enum ServiceType {
    WASH(100.0, 35),
    DRY(80.0, 45),
    WASH_AND_DRY(170.0, 80);

    private final double price;
    private final int durationMinutes;

    ServiceType(double price, int durationMinutes) {
        this.price = price;
        this.durationMinutes = durationMinutes;
    }

    public double getPrice() {
        return price;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
}
