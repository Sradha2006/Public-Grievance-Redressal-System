
package com.grievance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class CitizenDAO {

    public boolean registerCitizen(Citizen citizen) {

        String sql = "INSERT INTO citizens "
                + "(full_name, email, phone, password, address) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, citizen.getFullName());
            ps.setString(2, citizen.getEmail());
            ps.setString(3, citizen.getPhone());
            ps.setString(4, hashPassword(citizen.getPassword()));
            ps.setString(5, citizen.getAddress());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Registration Failed!");
            e.printStackTrace();
            return false;
        }
    }

    private String hashPassword(String password) throws SQLException {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);

            byte[] hash = md.digest(
                    password.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(salt)
                    + ":" + Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new SQLException("Password hashing failed", e);
        }
    }
    
    public boolean loginCitizen(String email, String password) {

        String sql = "SELECT password FROM citizens WHERE email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (java.sql.ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return verifyPassword(password, storedPassword);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean verifyPassword(String password, String storedPassword) {

        try {
            String[] parts = storedPassword.split(":");

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);

            byte[] enteredHash = md.digest(
                password.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );

            return MessageDigest.isEqual(storedHash, enteredHash);

        } catch (Exception e) {
            return false;
        }
    }
}
