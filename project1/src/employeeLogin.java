

import com.google.gson.JsonObject;
import java.util.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Servlet implementation class employeeLogin
 */
@WebServlet(name = "employeeLogin", urlPatterns = "/api/employeelogin")
public class employeeLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
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
    		
			Statement statement = connection.createStatement();
	    	        
	        String username = request.getParameter("username");
	        String password = request.getParameter("password");
	       
	        StringBuilder query = new StringBuilder();
	        
	        query.append("select * from employees where email = '"+username+"'");
	        
	        // execute query
 			ResultSet resultSet = statement.executeQuery(query.toString());
 			
 			// no such user
 	        if(resultSet.next() == false)
 	        {
 	        	JsonObject responseJsonObject = new JsonObject();
 	            responseJsonObject.addProperty("status", "fail");
 	            responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
 	           
 	            response.getWriter().write(responseJsonObject.toString());
 	        }
 	        else if (!resultSet.getString("password").equals(password))
	        {
	        	JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "fail");
	        	responseJsonObject.addProperty("message", "incorrect password");
	        	
	        	 response.getWriter().write(responseJsonObject.toString());
	        }
 	        else
	        {
 	        	request.getSession().setAttribute("user", new User(username));
	        	JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "success");
	            responseJsonObject.addProperty("message", "success");

	            response.getWriter().write(responseJsonObject.toString());
	        }
		} catch (Exception e) {
			
		}
	}

}
