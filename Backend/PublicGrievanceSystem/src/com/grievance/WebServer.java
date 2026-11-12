
package com.grievance;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WebServer {

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
            new InetSocketAddress(8090), 0
        );

        // TEST API
        server.createContext("/api/test", exchange -> {

            String response = "Public Grievance Backend Connected!";

            exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin", "*"
            );

            byte[] result = response.getBytes(StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(200, result.length);
            exchange.getResponseBody().write(result);
            exchange.close();
        });

        // CITIZEN REGISTRATION API
        server.createContext("/api/register", exchange -> {

            exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin", "*"
            );

            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Methods", "POST, OPTIONS"
                );
                exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Headers", "Content-Type"
                );
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            if (!exchange.getRequestMethod().equals("POST")) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            try {
                String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
                );

                Map<String, String> data = new HashMap<>();

                for (String field : body.split("&")) {
                    String[] pair = field.split("=", 2);

                    if (pair.length == 2) {
                        data.put(
                            URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                        );
                    }
                }

                Citizen citizen = new Citizen(
                    data.get("fullName"),
                    data.get("email"),
                    data.get("phone"),
                    data.get("password"),
                    data.get("address")
                );

                boolean success =
                    new CitizenDAO().registerCitizen(citizen);

                sendResponse(exchange,
                    success ? "Registration Successful!"
                            : "Registration Failed!", 200);

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, "Registration Error!", 400);
            }
        });
        
     // CITIZEN LOGIN API
        server.createContext("/api/login", exchange -> {

            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Methods", "POST, OPTIONS"
                );
                exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Headers", "Content-Type"
                );
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            if (!exchange.getRequestMethod().equals("POST")) {
                sendResponse(exchange, "Method Not Allowed", 405);
                return;
            }

            try {
                String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
                );

                Map<String, String> data = new HashMap<>();

                for (String field : body.split("&")) {
                    String[] pair = field.split("=", 2);

                    if (pair.length == 2) {
                        data.put(
                            URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                        );
                    }
                }

                String email = data.get("email");
                String password = data.get("password");

                boolean success = new CitizenDAO().loginCitizen(email, password);

                sendResponse(
                    exchange,
                    success ? "Login Successful!" : "Invalid Email or Password!",
                    success ? 200 : 401
                );

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, "Login Error!", 400);
            }
        });

        // COMPLAINT SUBMISSION API
        server.createContext("/api/complaint", exchange -> {

            exchange.getResponseHeaders().add(
                "Access-Control-Allow-Origin", "*"
            );

            if (exchange.getRequestMethod().equals("OPTIONS")) {
                exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Methods", "POST, OPTIONS"
                );
                exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Headers", "Content-Type"
                );
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            if (!exchange.getRequestMethod().equals("POST")) {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
                return;
            }

            try {
                String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
                );

                Map<String, String> data = new HashMap<>();

                for (String field : body.split("&")) {
                    String[] pair = field.split("=", 2);

                    if (pair.length == 2) {
                        data.put(
                            URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                            URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
                        );
                    }
                }

                Complaint complaint = new Complaint(
                    Integer.parseInt(data.get("citizenId")),
                    Integer.parseInt(data.get("departmentId")),
                    data.get("title"),
                    data.get("description"),
                    data.get("location")
                );

                boolean success =
                    new ComplaintDAO().submitComplaint(complaint);

                sendResponse(exchange,
                    success ? "Complaint Submitted Successfully!"
                            : "Complaint Submission Failed!", 200);

            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, "Invalid complaint details!", 400);
            }
        });
        

server.createContext("/api/track", exchange -> {

    exchange.getResponseHeaders().add(
        "Access-Control-Allow-Origin", "*"
    );

    if (exchange.getRequestMethod().equals("OPTIONS")) {
        exchange.getResponseHeaders().add(
            "Access-Control-Allow-Methods", "POST, OPTIONS"
        );
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return;
    }

    try {
        String body = new String(
            exchange.getRequestBody().readAllBytes(),
            StandardCharsets.UTF_8
        );

        String value = body.split("=", 2)[1];
        int complaintId = Integer.parseInt(
            URLDecoder.decode(value, StandardCharsets.UTF_8)
        );

        String sql = "SELECT c.complaint_id, c.title, c.description, "
            + "c.location, c.status, c.submitted_at, d.department_name "
            + "FROM complaints c LEFT JOIN departments d "
            + "ON c.department_id = d.department_id "
            + "WHERE c.complaint_id = ?";

        try (
            java.sql.Connection con = DBConnection.getConnection();
            java.sql.PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setInt(1, complaintId);

            try (java.sql.ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    String result =
                        "Complaint ID: " + rs.getInt("complaint_id")
                        + "\nTitle: " + rs.getString("title")
                        + "\nDescription: " + rs.getString("description")
                        + "\nLocation: " + rs.getString("location")
                        + "\nDepartment: " + rs.getString("department_name")
                        + "\nStatus: " + rs.getString("status")
                        + "\nSubmitted At: " + rs.getTimestamp("submitted_at");

                    sendResponse(exchange, result, 200);
                } else {
                    sendResponse(exchange, "Complaint Not Found!", 404);
                }
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
        sendResponse(exchange, "Tracking Failed!", 400);
    }
});

//ADMIN: VIEW ALL COMPLAINTS API
server.createContext("/api/admin/complaints", exchange -> {

 exchange.getResponseHeaders().add(
     "Access-Control-Allow-Origin", "*"
 );

 String sql = "SELECT c.complaint_id, c.title, c.description, "
         + "c.location, c.status, c.submitted_at, "
         + "d.department_name "
         + "FROM complaints c "
         + "LEFT JOIN departments d "
         + "ON c.department_id = d.department_id "
         + "ORDER BY c.submitted_at DESC";

 try (
     Connection con = DBConnection.getConnection();
     PreparedStatement ps = con.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()
 ) {
     StringBuilder result = new StringBuilder();

     while (rs.next()) {
         result.append("Complaint ID: ")
             .append(rs.getInt("complaint_id"))
             .append("\nTitle: ")
             .append(rs.getString("title"))
             .append("\nDepartment: ")
             .append(rs.getString("department_name"))
             .append("\nLocation: ")
             .append(rs.getString("location"))
             .append("\nStatus: ")
             .append(rs.getString("status"))
             .append("\n----------------------\n");
     }

     if (result.length() == 0) {
         result.append("No complaints found.");
     }

     sendResponse(exchange, result.toString(), 200);

 } catch (Exception e) {
     e.printStackTrace();
     sendResponse(exchange, "Failed to load complaints!", 500);
 }
});
	
//ADMIN: UPDATE COMPLAINT STATUS API
server.createContext("/api/admin/update", exchange -> {

 exchange.getResponseHeaders().add(
     "Access-Control-Allow-Origin", "*"
 );

 if (exchange.getRequestMethod().equals("OPTIONS")) {
     exchange.getResponseHeaders().add(
         "Access-Control-Allow-Methods", "POST, OPTIONS"
     );
     exchange.getResponseHeaders().add(
         "Access-Control-Allow-Headers", "Content-Type"
     );
     exchange.sendResponseHeaders(204, -1);
     exchange.close();
     return;
 }

 if (!exchange.getRequestMethod().equals("POST")) {
     sendResponse(exchange, "POST method required!", 405);
     return;
 }

 try {
     String body = new String(
         exchange.getRequestBody().readAllBytes(),
         StandardCharsets.UTF_8
     );

     Map<String, String> data = new HashMap<>();

     for (String field : body.split("&")) {
         String[] pair = field.split("=", 2);

         if (pair.length == 2) {
             data.put(
                 URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                 URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
             );
         }
     }

     int complaintId = Integer.parseInt(data.get("complaintId"));
     String status = data.get("status");

     boolean success = new AdminDAO().updateStatus(
         complaintId, status
     );

     sendResponse(
         exchange,
         success ? "Status Updated Successfully!" :
                   "Update Failed! Check Complaint ID and Status.",
         success ? 200 : 400
     );

 } catch (Exception e) {
     e.printStackTrace();
     sendResponse(exchange, "Invalid update details!", 400);
 }
});

//ADMIN: RESOLUTION REPORT API
server.createContext("/api/admin/report", exchange -> {

 exchange.getResponseHeaders().add(
     "Access-Control-Allow-Origin", "*"
 );

 String sql = "SELECT status, COUNT(*) AS total "
         + "FROM complaints GROUP BY status";

 try (
     Connection con = DBConnection.getConnection();
     PreparedStatement ps = con.prepareStatement(sql);
     ResultSet rs = ps.executeQuery()
 ) {
     StringBuilder report = new StringBuilder();
     int total = 0;

     report.append("===== RESOLUTION REPORT =====\n");

     while (rs.next()) {
         String status = rs.getString("status");
         int count = rs.getInt("total");

         report.append(status).append(": ")
               .append(count).append("\n");

         total += count;
     }

     report.append("-----------------------------\n");
     report.append("Total Complaints: ").append(total);

     sendResponse(exchange, report.toString(), 200);

 } catch (Exception e) {
     e.printStackTrace();
     sendResponse(exchange, "Report Generation Failed!", 500);
 }
});
	
//ADMIN LOGIN API
server.createContext("/api/admin/login", exchange -> {

 exchange.getResponseHeaders().add(
     "Access-Control-Allow-Origin", "*"
 );

 if (exchange.getRequestMethod().equals("OPTIONS")) {
     exchange.getResponseHeaders().add(
         "Access-Control-Allow-Methods", "POST, OPTIONS"
     );
     exchange.getResponseHeaders().add(
         "Access-Control-Allow-Headers", "Content-Type"
     );
     exchange.sendResponseHeaders(204, -1);
     exchange.close();
     return;
 }

 try {
     String body = new String(
         exchange.getRequestBody().readAllBytes(),
         StandardCharsets.UTF_8
     );

     Map<String, String> data = new HashMap<>();

     for (String field : body.split("&")) {
         String[] pair = field.split("=", 2);

         if (pair.length == 2) {
             data.put(
                 URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                 URLDecoder.decode(pair[1], StandardCharsets.UTF_8)
             );
         }
     }

     String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";

     try (
         Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)
     ) {
         ps.setString(1, data.get("username"));
         ps.setString(2, data.get("password"));

         try (ResultSet rs = ps.executeQuery()) {
             if (rs.next()) {
                 sendResponse(exchange, "LOGIN_SUCCESS", 200);
             } else {
                 sendResponse(exchange, "Invalid Username or Password!", 401);
             }
         }
     }

 } catch (Exception e) {
     e.printStackTrace();
     sendResponse(exchange, "Login Error!", 500);
 }
});

        server.start();

        System.out.println("Server running on port 8090");
    }

    // COMMON RESPONSE METHOD
    private static void sendResponse(
        com.sun.net.httpserver.HttpExchange exchange,
        String message,
        int status
    ) throws IOException {

        byte[] result = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add(
            "Content-Type", "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, result.length);
        exchange.getResponseBody().write(result);
        exchange.close();
    }
}
