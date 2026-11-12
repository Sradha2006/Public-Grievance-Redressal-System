
package com.grievance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ComplaintDAO {

    public boolean submitComplaint(Complaint complaint) {

        String sql = "INSERT INTO complaints "
                + "(citizen_id, department_id, title, description, location, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, complaint.getCitizenId());
            ps.setInt(2, complaint.getDepartmentId());
            ps.setString(3, complaint.getTitle());
            ps.setString(4, complaint.getDescription());
            ps.setString(5, complaint.getLocation());
            ps.setString(6, complaint.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Complaint Submission Failed!");
            e.printStackTrace();
            return false;
        }
    }
}
