
package com.grievance;

public class RegistrationTest {

    public static void main(String[] args) {

        Citizen citizen = new Citizen(
                "Test Citizen",
                "testcitizen@gmail.com",
                "9876543210",
                "Test@123",
                "Bhubaneswar, Odisha"
        );

        CitizenDAO dao = new CitizenDAO();

        boolean result = dao.registerCitizen(citizen);

        if (result) {
            System.out.println("Citizen Registered Successfully!");
        } else {
            System.out.println("Registration Failed!");
        }
    }
}
