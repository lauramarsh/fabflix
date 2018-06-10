import com.google.gson.JsonObject;
import java.util.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class addNewStar
 */
@WebServlet(name="addNewStar", urlPatterns="/addNewStar")
public class addNewStar extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public addNewStar() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
    
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        try {
        	
        	// Connect to database using pooling
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/MovieDb");
            
            if (ds == null)
                out.println("ds is null.");

            Connection connection = ds.getConnection();
            if (connection == null)
                out.println("dbcon is null.");
    		
    		
    		// Insert Query Params
    		String name = request.getParameter("name");
    		String year = request.getParameter("year");
    		if(name.equals(null) || name == null || name.length() < 1) {
    			JsonObject responseJsonObject = new JsonObject();
    			responseJsonObject.addProperty("status", "fail");
    			responseJsonObject.addProperty("message", "Please enter the Star Name.");
    			response.getWriter().write(responseJsonObject.toString());
    		}
    		else {
    			// Build Query and use stored procedure 'addStar'
        		StringBuilder query = new StringBuilder();
        		query.append("call addStar('" + name + "', " + year + ", @status);");
        		PreparedStatement ps = connection.prepareStatement(query.toString());
        		ps.executeQuery();
        		
        		JsonObject responseJsonObject = new JsonObject();
    			responseJsonObject.addProperty("status", "success");
    			responseJsonObject.addProperty("message", "Star successfully added!");
    			response.getWriter().write(responseJsonObject.toString());
    		}
    		
    		
		} catch (Exception e) {
        	e.printStackTrace();
        }		
	}

	
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
