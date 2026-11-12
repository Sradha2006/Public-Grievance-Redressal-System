
package com.grievance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComplaintTrackingDAO {

    public void trackComplaint(int complaintId) {

        String sql = "SELECT c.complaint_id, c.title, "
                + "c.description, c.location, c.status, "
                + "c.submitted_at, d.department_name "
                + "FROM complaints c "
                + "LEFT JOIN departments d "
                + "ON c.department_id = d.department_id "
                + "WHERE c.complaint_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, complaintId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    System.out.println("COMPLAINT DETAILS");
                    System.out.println("-------------------------");
                    System.out.println("Complaint ID: "
                            + rs.getInt("complaint_id"));
                    System.out.println("Title: "
                            + rs.getString("title"));
                    System.out.println("Description: "
                            + rs.getString("description"));
                    System.out.println("Location: "
                            + rs.getString("location"));
                    System.out.println("Department: "
                            + rs.getString("department_name"));
                    System.out.println("Status: "
                            + rs.getString("status"));
                    System.out.println("Submitted At: "
                            + rs.getTimestamp("submitted_at"));

                } else {
                    System.out.println("Complaint Not Found!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Tracking Failed!");
            e.printStackTrace();
        }
    }
}
