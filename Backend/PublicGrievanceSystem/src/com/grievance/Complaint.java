
package com.grievance;

public class Complaint {

    private int complaintId;
    private int citizenId;
    private int departmentId;
    private String title;
    private String description;
    private String location;
    private String status;

    public Complaint(int citizenId, int departmentId,
                     String title, String description,
                     String location) {

        this.citizenId = citizenId;
        this.departmentId = departmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.status = "Pending";
    }

    public int getComplaintId() {
        return complaintId;
    }

    public int getCitizenId() {
        return citizenId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
