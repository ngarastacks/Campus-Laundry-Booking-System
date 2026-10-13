package com.campuslaundry.gui;

import com.campuslaundry.model.Booking;
import com.campuslaundry.model.Machine;
import com.campuslaundry.model.ServiceType;
import com.campuslaundry.model.User;
import com.campuslaundry.service.BookingException;
import com.campuslaundry.service.BookingService;
import com.campuslaundry.service.PricingCalculator;
import com.campuslaundry.service.UserService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class BookingPanel extends JPanel {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final BookingService bookingService;
    private final UserService userService;
    private final PricingCalculator pricing;

    private final JTextField idField = new JTextField(12);
    private final JComboBox<ServiceType> serviceBox = new JComboBox<>(ServiceType.values());
    private final JTextField startField = new JTextField(14);
    private final JLabel endLabel = new JLabel("Ends: -");
    private final JComboBox<Machine> machineBox = new JComboBox<>();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Student", "Machine", "Service", "Start", "End", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public BookingPanel(BookingService bookingService, UserService userService, PricingCalculator pricing) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.pricing = pricing;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        startField.setText(LocalDateTime.now().plusHours(1).withMinute(0).withSecond(0).withNano(0).format(FORMAT));

        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        showAll();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Book a machine"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        form.add(new JLabel("Student ID:"), c);
        c.gridx = 1;
        form.add(idField, c);

        c.gridx = 0;
        c.gridy = 1;
        form.add(new JLabel("Service:"), c);
        c.gridx = 1;
        form.add(serviceBox, c);

        c.gridx = 0;
        c.gridy = 2;
        form.add(new JLabel("Start (yyyy-MM-dd HH:mm):"), c);
        c.gridx = 1;
        form.add(startField, c);
        c.gridx = 2;
        form.add(endLabel, c);

        JButton findButton = new JButton("Find available machines");
        findButton.addActionListener(e -> onFindMachines());
        c.gridx = 1;
        c.gridy = 3;
        form.add(findButton, c);

        c.gridx = 0;
        c.gridy = 4;
        form.add(new JLabel("Machine:"), c);
        c.gridx = 1;
        form.add(machineBox, c);

        JButton bookButton = new JButton("Book");
        bookButton.addActionListener(e -> onBook());
        c.gridx = 1;
        c.gridy = 5;
        form.add(bookButton, c);

        return form;
    }

    private JPanel buildButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton cancelButton = new JButton("Cancel selected booking");
        cancelButton.addActionListener(e -> onCancel());

        JButton mineButton = new JButton("Show my bookings");
        mineButton.addActionListener(e -> showMine());

        JButton allButton = new JButton("Show all bookings");
        allButton.addActionListener(e -> showAll());

        buttons.add(cancelButton);
        buttons.add(mineButton);
        buttons.add(allButton);
        return buttons;
    }

    // ---- actions ----

    private void onFindMachines() {
        LocalDateTime start = readStart();
        if (start == null) {
            return;
        }
        LocalDateTime end = endFor(start);
        endLabel.setText("Ends: " + end.format(FORMAT));

        List<Machine> available = bookingService.findAvailableMachines(start, end);
        machineBox.removeAllItems();
        for (Machine m : available) {
            machineBox.addItem(m);
        }
        if (available.isEmpty()) {
            warn("No machines are free at that time. Try another time.");
        }
    }

    private void onBook() {
        LocalDateTime start = readStart();
        if (start == null) {
            return;
        }
        Machine machine = (Machine) machineBox.getSelectedItem();
        if (machine == null) {
            warn("Click \"Find available machines\" and choose a machine first.");
            return;
        }
        ServiceType service = (ServiceType) serviceBox.getSelectedItem();
        String studentId = idField.getText().trim();
        LocalDateTime end = endFor(start);

        try {
            Booking booking = bookingService.createBooking(studentId, machine.getId(), service, start, end);

            String priceText = "";
            Optional<User> user = userService.findByStudentId(studentId);
            if (user.isPresent()) {
                priceText = "\nPrice: " + pricing.formatPrice(pricing.calculatePrice(user.get(), service));
            }
            JOptionPane.showMessageDialog(this,
                    "Booking #" + booking.getId() + " confirmed on " + machine.getName()
                    + "\n" + start.format(FORMAT) + " to " + end.format(FORMAT) + priceText);

            machineBox.removeAllItems();
            showAll();
        } catch (BookingException ex) {
            warn(ex.getMessage());
        }
    }

    private void onCancel() {
        int row = table.getSelectedRow();
        if (row < 0) {
            warn("Select a booking in the table first.");
            return;
        }
        int bookingId = (Integer) tableModel.getValueAt(row, 0);
        String studentId = idField.getText().trim();
        if (studentId.isEmpty()) {
            warn("Type your student ID in the form above. Only the booking's owner can cancel it.");
            return;
        }
        try {
            bookingService.cancelBooking(bookingId, studentId);
            JOptionPane.showMessageDialog(this, "Booking #" + bookingId + " cancelled.");
            showAll();
        } catch (BookingException ex) {
            warn(ex.getMessage());
        }
    }

    // ---- table ----

    private void showAll() {
        fillTable(bookingService.getAllBookings());
    }

    private void showMine() {
        String studentId = idField.getText().trim();
        if (studentId.isEmpty()) {
            warn("Type a student ID first.");
            return;
        }
        fillTable(bookingService.getBookingsForUser(studentId));
    }

    private void fillTable(List<Booking> bookings) {
        tableModel.setRowCount(0);
        for (Booking b : bookings) {
            tableModel.addRow(new Object[]{
                b.getId(),
                b.getStudentId(),
                b.getMachine().getName(),
                b.getServiceType(),
                b.getStartTime().format(FORMAT),
                b.getEndTime().format(FORMAT),
                b.getStatus()
            });
        }
    }

    // ---- helpers ----

    private LocalDateTime readStart() {
        try {
            return LocalDateTime.parse(startField.getText().trim(), FORMAT);
        } catch (DateTimeParseException ex) {
            warn("Enter the start time like 2026-10-12 14:30");
            return null;
        }
    }

    private LocalDateTime endFor(LocalDateTime start) {
        ServiceType service = (ServiceType) serviceBox.getSelectedItem();
        return start.plusMinutes(service.getDurationMinutes());
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Campus Laundry", JOptionPane.WARNING_MESSAGE);
    }
}
