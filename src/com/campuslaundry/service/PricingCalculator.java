package com.campuslaundry.service;

import com.campuslaundry.model.ServiceType;
import com.campuslaundry.model.User;

public class PricingCalculator {

    public double calculatePrice(User user, ServiceType service) {
        double discount = user.getRole().getDiscountRate();
        double price = service.getPrice() * (1 - discount);
        return Math.round(price * 100.0) / 100.0;
    }

    public String formatPrice(double amount) {
        return String.format("KES %.2f", amount);
    }
}
