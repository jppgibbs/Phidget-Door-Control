package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/DoorChecker")
public class DoorChecker extends HttpServlet{
	
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
    
    public DoorChecker() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println(request.getParameter("doorid"));
        String doorid = request.getParameter("doorid");

        String readerid = doorLookup(doorid);
        PrintWriter out = response.getWriter();
        out.println(readerid);
        out.close();
    }


    
    private String doorLookup(String doorID) {
    	String readerID = "";
    	try {
    	ResultSet rs;
    	String selectDoorReader = "SELECT * FROM doorlookup WHERE doorid='" + doorID + "';";
    	getConnection();
    	rs = stmt.executeQuery(selectDoorReader);
    	
    	while(rs.next()) {
    		rs.getString("readerid");
    	};
    	} catch (SQLException se) {
        	// If query fails display error for debugging
            System.out.println(se);
            System.out.println("\n Query failed - debug using above stack trace");
    	}
    	closeConnection();
    	return readerID;
    }
}
