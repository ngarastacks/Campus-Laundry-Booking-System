package com.campuslaundry.service;

import com.campuslaundry.model.Role;
import com.campuslaundry.model.ServiceType;
import com.campuslaundry.model.User;
import com.campuslaundry.repository.UserRepository;

public class UserServicesDemo {

    public static void main(String[] args) {
        UserService users = new UserService(new UserRepository());
        PricingCalculator pricing = new PricingCalculator();

        users.register("S001", "Amina", Role.STUDENT);
        users.register("S002", "Mr. Otieno", Role.STAFF);

        try {
            users.register("S001", "Duplicate", Role.STUDENT);
        } catch (IllegalArgumentException e) {
            System.out.println("Duplicate rejected: " + e.getMessage());
        }

        System.out.println("S001 active? " + users.isActive("S001"));
        users.deactivate("S001");
        System.out.println("S001 active after deactivate? " + users.isActive("S001"));
        System.out.println("S999 (unknown) active? " + users.isActive("S999"));

        User student = users.findByStudentId("S001").get();
        User staff = users.findByStudentId("S002").get();

        for (ServiceType s : ServiceType.values()) {
            System.out.println(s
                    + " | student: " + pricing.formatPrice(pricing.calculatePrice(student, s))
                    + " | staff: " + pricing.formatPrice(pricing.calculatePrice(staff, s))
                    + " | " + s.getDurationMinutes() + " min");
        }
    }
}
