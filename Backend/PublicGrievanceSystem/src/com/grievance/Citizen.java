
package com.grievance;

public class Citizen {

    private int citizenId;
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String address;

    public Citizen(String fullName, String email,
                   String phone, String password, String address) {

        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }
}
