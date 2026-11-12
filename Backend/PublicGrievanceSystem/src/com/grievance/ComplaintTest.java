
package com.grievance;

public class ComplaintTest {

    public static void main(String[] args) {

        Complaint complaint = new Complaint(
                1,
                1,
                "Damaged Road",
                "The road in our area is badly damaged.",
                "Bhubaneswar, Odisha"
        );

        ComplaintDAO dao = new ComplaintDAO();

        boolean result = dao.submitComplaint(complaint);

        if (result) {
            System.out.println("Complaint Submitted Successfully!");
        } else {
            System.out.println("Complaint Submission Failed!");
        }
    }
}
