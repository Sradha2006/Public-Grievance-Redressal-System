
package com.grievance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DepartmentDAO {

    public ArrayList<String> getDepartments() {

        ArrayList<String> departments = new ArrayList<>();

        String sql = "SELECT department_name FROM departments";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                departments.add(rs.getString("department_name"));
            }

        } catch (SQLException e) {
            System.out.println("Department Fetch Failed!");
            e.printStackTrace();
        }

        return departments;
    }
}
