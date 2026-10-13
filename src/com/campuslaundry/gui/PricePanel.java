package com.campuslaundry.gui;

import com.campuslaundry.model.ServiceType;
import com.campuslaundry.model.User;
import com.campuslaundry.service.PricingCalculator;
import com.campuslaundry.service.UserService;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PricePanel extends JPanel {

    private final UserService userService;
    private final PricingCalculator pricing;

    private final JTextField idField = new JTextField(15);
    private final JComboBox<ServiceType> serviceBox = new JComboBox<>(ServiceType.values());
    private final JLabel resultLabel = new JLabel(" ");

    public PricePanel(UserService userService, PricingCalculator pricing) {
        this.userService = userService;
        this.pricing = pricing;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        add(new JLabel("Student ID:"), c);
        c.gridx = 1;
        add(idField, c);

        c.gridx = 0;
        c.gridy = 1;
        add(new JLabel("Service:"), c);
        c.gridx = 1;
        add(serviceBox, c);

        JButton calculateButton = new JButton("Show price");
        calculateButton.addActionListener(e -> onCalculate());
        c.gridx = 1;
        c.gridy = 2;
        add(calculateButton, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        add(resultLabel, c);
    }

    private void onCalculate() {
        Optional<User> found = userService.findByStudentId(idField.getText());
        if (!found.isPresent()) {
            resultLabel.setText("No user with that student ID. Register them on the Users tab first.");
            return;
        }
        User user = found.get();
        ServiceType service = (ServiceType) serviceBox.getSelectedItem();
        double price = pricing.calculatePrice(user, service);
        resultLabel.setText(user.getName() + " (" + user.getRole() + "): "
                + service + " = " + pricing.formatPrice(price)
                + ", " + service.getDurationMinutes() + " min");
    }
}
