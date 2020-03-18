package server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.io.*;
import java.sql.*;

@WebServlet("/LockerServer")
public class LockerServer extends HttpServlet {

    private static final long serialVersionUID = 1L;

    Gson gson = new Gson();

    Connection conn = null;
    Statement stmt;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("Server is running on localhost:8080\n");
        }

    private void getConnection() {
    	// Connection details - Modify these if using a different SQL server
        String user = "gibbsj";
        String password = "grumwelL5";
        String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/" + user;

        // Create new database driver instance
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            System.out.println(e);
        }
        // Use the username and password to connect to the databse
        try {
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
        } catch (SQLException se) {
            System.out.println(se);
            System.out.println("\nConnection failed - check details in getConnection() method");
        }
    }

    private void closeConnection() {
        try {
            conn.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void destroy() {
        try {
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public LockerServer() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        
        // Create RFIDdata object to store data
        RFIDdata oneSensor = new RFIDdata("unknown", "unknown");
        
        // Default validation to false
        boolean valid = false;
        
        String sensorJsonString = request.getParameter("RFIDdata");
        // Problem if sensordata parameter not sent, or is invalid json
        if (sensorJsonString != null) {
        	// Convert the json string to an object of type RFIDdata
        	oneSensor = gson.fromJson(sensorJsonString, RFIDdata.class);
        		// Check the validtags table for matches
        		valid = validateRequest(oneSensor);
        		// Update the valid boolean
        		oneSensor.setvalid(valid);
                updateSensorTable(oneSensor);
                // Only update the table if valid = true
                if (valid == true) {
                    PrintWriter out = response.getWriter();
                    out.println(valid);
                    out.close();
                }
            }
        }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Post is same as Get, so pass on parameters and do same
        doGet(request, response);
    }

    private void updateSensorTable(RFIDdata oneSensor){
    	try {
    		// Create the INSERT statement from the parameters
    		// set time inserted to be the current time on database server
    		String updateSQL = 
    	     	"insert into attempts(tagid, readerid, date, valid) " +
    	     	"values('"+oneSensor.gettagId()      + "','" +
    			           oneSensor.getreaderId()  + "','" +
    		 	           oneSensor.getdate()  + "'," +
    	     	           oneSensor.getvalid() + ");";
    	     	           
    	        System.out.println("DEBUG: Update: " + updateSQL);

    	        getConnection();
    	        // Update the attempts table with the tag, reader and date and whether or not it was valid
    	        stmt.executeUpdate(updateSQL);
    	        closeConnection();
    	        
    	        System.out.println("DEBUG: Update successful ");
    	} catch (SQLException se) {
    		// Problem with update, return failure message
    	    System.out.println(se);
            System.out.println("\nDEBUG: Update error - see error trace above for help. ");
    	    return;
    	}
    }
    
    private boolean validateRequest(RFIDdata oneSensor) {
        try {
            ResultSet rs;

            // Query to check if ids in the validtags database match the ids of the reader and tag presented
            String selectValidTags = "SELECT * FROM validtags WHERE tagid='" + oneSensor.gettagId() + "' AND readerid='" + oneSensor.getreaderId() + "';";

            System.out.println("Executing query: " + selectValidTags);
            
            // Create connection, check the validtags table for matches and update the valid boolean
            getConnection();
            rs = stmt.executeQuery(selectValidTags);
            boolean valid = rs.next();
            
            // Close connection and print results
            closeConnection();
            System.out.println("Update successful ");
            System.out.println(valid);
            return valid;
            
        } catch (SQLException se) {
        	// If either query fails display error for debugging
            System.out.println(se);
            System.out.println("\n Query failed - debug using above stack trace");
            return false;
        }
    }
}