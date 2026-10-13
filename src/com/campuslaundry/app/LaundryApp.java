package com.campuslaundry.app;

import com.campuslaundry.gui.LaundryFrame;
import com.campuslaundry.model.Role;
import com.campuslaundry.repository.InMemoryBookingRepository;
import com.campuslaundry.repository.InMemoryMachineRepository;
import com.campuslaundry.repository.JdbcBookingRepository;
import com.campuslaundry.repository.JdbcMachineRepository;
import com.campuslaundry.repository.UserRepository;
import com.campuslaundry.service.BookingService;
import com.campuslaundry.service.PricingCalculator;
import com.campuslaundry.service.UserService;
import javax.swing.SwingUtilities;

/**
 * Starts the Campus Laundry GUI. Right-click this file and choose Run File.
 */
public class LaundryApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserService userService = new UserService(new UserRepository());
            PricingCalculator pricing = new PricingCalculator();

            // Two sample users so you can test straight away (same IDs as schema.sql).
            userService.register("S001", "Amina", Role.STUDENT);
            userService.register("S002", "Mr. Otieno", Role.STAFF);

            // ---- Storage: in memory (no database needed) ----
            BookingService bookingService = new BookingService(
                    new InMemoryBookingRepository(),
                    new InMemoryMachineRepository(),
                    userService);

            // ---- Storage: MySQL ----
            // To switch to MySQL, delete or comment out the block above and
            // remove the // from the lines below (XAMPP must be running):
            //
            // JdbcMachineRepository machines = new JdbcMachineRepository();
            // BookingService bookingService = new BookingService(
            //         new JdbcBookingRepository(machines), machines, userService);

            new LaundryFrame(userService, pricing, bookingService).setVisible(true);
        });
    }
}
