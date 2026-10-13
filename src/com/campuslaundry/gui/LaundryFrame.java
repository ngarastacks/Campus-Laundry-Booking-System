package com.campuslaundry.gui;

import com.campuslaundry.service.BookingService;
import com.campuslaundry.service.PricingCalculator;
import com.campuslaundry.service.UserService;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class LaundryFrame extends JFrame {

    public LaundryFrame(UserService userService, PricingCalculator pricing, BookingService bookingService) {
        super("Campus Laundry Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Users", new UserPanel(userService));
        tabs.addTab("Bookings", new BookingPanel(bookingService, userService, pricing));
        tabs.addTab("Prices", new PricePanel(userService, pricing));

        add(tabs);
        setSize(860, 560);
        setLocationRelativeTo(null);
    }
}
