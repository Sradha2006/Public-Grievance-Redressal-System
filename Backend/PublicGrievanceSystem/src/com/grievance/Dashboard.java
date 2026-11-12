
package com.grievance;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard() {

        setTitle("Public Grievance Redressal System");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));
        panel.setBackground(new Color(235, 242, 250));

        JLabel heading = new JLabel(
                "PUBLIC GRIEVANCE REDRESSAL SYSTEM",
                SwingConstants.CENTER);

        heading.setFont(new Font("Arial", Font.BOLD, 22));

        JButton registerBtn = new JButton("Citizen Registration");

registerBtn.addActionListener(e -> {

    JTextField name = new JTextField();
    JTextField email = new JTextField();
    JTextField phone = new JTextField();
    JPasswordField password = new JPasswordField();
    JTextField address = new JTextField();

    Object[] fields = {
        "Full Name:", name,
        "Email:", email,
        "Phone:", phone,
        "Password:", password,
        "Address:", address
    };

    int result = JOptionPane.showConfirmDialog(
        this, fields, "Citizen Registration",
        JOptionPane.OK_CANCEL_OPTION
    );

    if (result == JOptionPane.OK_OPTION) {

        if (name.getText().trim().isEmpty()
                || email.getText().trim().isEmpty()
                || phone.getText().trim().isEmpty()
                || password.getPassword().length == 0
                || address.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        Citizen citizen = new Citizen(
            name.getText().trim(),
            email.getText().trim(),
            phone.getText().trim(),
            new String(password.getPassword()),
            address.getText().trim()
        );

        CitizenDAO dao = new CitizenDAO();
        boolean success = dao.registerCitizen(citizen);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Citizen Registered Successfully!");
        } else {
            JOptionPane.showMessageDialog(this,
                "Registration Failed! Email may already exist.");
        }
    }
});

        JButton complaintBtn = new JButton("Submit Complaint");
        complaintBtn.addActionListener(e -> {

            JTextField citizenId = new JTextField();
            JTextField departmentId = new JTextField();
            JTextField title = new JTextField();
            JTextField location = new JTextField();
            JTextField description = new JTextField();

            Object[] fields = {
                "Citizen ID:", citizenId,
                "Department ID:", departmentId,
                "Complaint Title:", title,
                "Location:", location,
                "Description:", description
            };

            int result = JOptionPane.showConfirmDialog(
                this, fields, "Submit Complaint",
                JOptionPane.OK_CANCEL_OPTION
            );

            if (result == JOptionPane.OK_OPTION) {
                try {
                    Complaint complaint = new Complaint(
                        Integer.parseInt(citizenId.getText()),
                        Integer.parseInt(departmentId.getText()),
                        title.getText(),
                        description.getText(),
                        location.getText()
                    );

                    ComplaintDAO dao = new ComplaintDAO();
                    boolean success = dao.submitComplaint(complaint);

                    JOptionPane.showMessageDialog(this,
                        success ? "Complaint Submitted Successfully!"
                                : "Complaint Submission Failed!");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Enter valid Citizen ID and Department ID!");
                }
            }
        });
        JButton trackBtn = new JButton("Track Complaint");
        trackBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(
                    this, "Enter Complaint ID:");

            if (id != null && !id.trim().isEmpty()) {
                try {
                    ComplaintTrackingDAO tracker = new ComplaintTrackingDAO();
                    tracker.trackComplaint(Integer.parseInt(id));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Enter a valid ID!");
                }
            }
        });
        JButton adminBtn = new JButton("Admin Dashboard");
        JButton reportBtn = new JButton("Resolution Reports");
        adminBtn.addActionListener(e -> {
            AdminDAO admin = new AdminDAO();
            admin.viewAllComplaints();
        });

        reportBtn.addActionListener(e -> {
            AdminDAO admin = new AdminDAO();
            admin.generateResolutionReport();
        });

        panel.add(heading);
        panel.add(registerBtn);
        panel.add(complaintBtn);
        panel.add(trackBtn);
        panel.add(adminBtn);
        panel.add(reportBtn);

        add(panel);

        setVisible(true);
    }
}
