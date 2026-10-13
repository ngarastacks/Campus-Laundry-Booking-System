package com.campuslaundry.gui;

import com.campuslaundry.model.Role;
import com.campuslaundry.model.User;
import com.campuslaundry.service.UserService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
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

public class UserPanel extends JPanel {

    private final UserService userService;

    private final JTextField idField = new JTextField(15);
    private final JTextField nameField = new JTextField(15);
    private final JComboBox<Role> roleBox = new JComboBox<>(Role.values());

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Student ID", "Name", "Role", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public UserPanel(UserService userService) {
        this.userService = userService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        refreshTable();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Register a new user"));
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
        form.add(new JLabel("Name:"), c);
        c.gridx = 1;
        form.add(nameField, c);

        c.gridx = 0;
        c.gridy = 2;
        form.add(new JLabel("Role:"), c);
        c.gridx = 1;
        form.add(roleBox, c);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> onRegister());
        c.gridx = 1;
        c.gridy = 3;
        form.add(registerButton, c);

        return form;
    }

    private JPanel buildButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton deactivateButton = new JButton("Deactivate selected");
        deactivateButton.addActionListener(e -> onSetActive(false));

        JButton reactivateButton = new JButton("Reactivate selected");
        reactivateButton.addActionListener(e -> onSetActive(true));

        buttons.add(deactivateButton);
        buttons.add(reactivateButton);
        return buttons;
    }

    private void onRegister() {
        try {
            userService.register(idField.getText(), nameField.getText(), (Role) roleBox.getSelectedItem());
            idField.setText("");
            nameField.setText("");
            refreshTable();
            JOptionPane.showMessageDialog(this, "User registered.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot register", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onSetActive(boolean active) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user in the table first.");
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        if (active) {
            userService.reactivate(id);
        } else {
            userService.deactivate(id);
        }
        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getStudentId(),
                u.getName(),
                u.getRole(),
                u.isActive() ? "Active" : "Inactive"
            });
        }
    }
}
