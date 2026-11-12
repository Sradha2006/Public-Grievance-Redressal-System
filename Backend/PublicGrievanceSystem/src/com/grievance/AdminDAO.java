
package com.grievance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    // View all complaints
    public void viewAllComplaints() {

        String sql = "SELECT c.complaint_id, c.title, "
                + "c.location, c.status, d.department_name "
                + "FROM complaints c "
                + "LEFT JOIN departments d "
                + "ON c.department_id = d.department_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                System.out.println("----------------------");
                System.out.println("Complaint ID: " + rs.getInt("complaint_id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Location: " + rs.getString("location"));
                System.out.println("Department: " + rs.getString("department_name"));
                System.out.println("Status: " + rs.getString("status"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update complaint status
    public boolean updateStatus(int complaintId, String status) {

        if (!status.equals("Pending") && !status.equals("In Progress")
                && !status.equals("Resolved") && !status.equals("Rejected")) {

            System.out.println("Invalid status!");
            return false;
        }

        String sql = "UPDATE complaints SET status = ?, "
                + "resolved_at = CASE WHEN ? = 'Resolved' "
                + "THEN CURRENT_TIMESTAMP ELSE NULL END "
                + "WHERE complaint_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, complaintId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Generate resolution report
    public void generateResolutionReport() {

        String sql = "SELECT status, COUNT(*) AS total "
                + "FROM complaints GROUP BY status";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n===== RESOLUTION REPORT =====");

            int total = 0;

            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("total");

                System.out.println(status + ": " + count);
                total += count;
            }

            System.out.println("-----------------------------");
            System.out.println("Total Complaints: " + total);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
