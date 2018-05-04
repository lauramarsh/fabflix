import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

//
@WebServlet(name = "LoginServlet", urlPatterns = "/loginpage")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
    	// TODO Auto-generated method stub
		
    			String loginUser = "root";
    	        String loginPasswd = "pissoff";
    	        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

    	        response.setContentType("text/html");
    	        
    	        PrintWriter out = response.getWriter();
    	        
    	        
        try {
        	
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		Statement statement = connection.createStatement();
	    	        
	        String username = request.getParameter("username");
	        String password = request.getParameter("password");
	       
	        StringBuilder query = new StringBuilder();
	        
	        query.append("select email,password from customers where email = '"+username+"'");
	 
	        
			// execute query
			ResultSet resultSet = statement.executeQuery(query.toString());
			
			// no such user
	        if(resultSet.next() == false){
	        	System.out.println("no user");
	        	JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "fail");
	            responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
	           
	            response.getWriter().write(responseJsonObject.toString());
	        }
	        else if (!resultSet.getString("password").equals(password))
	        {
	        	System.out.println("bad pw");
	        	JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "fail");
	        	responseJsonObject.addProperty("message", "incorrect password");
	        	
	        	 response.getWriter().write(responseJsonObject.toString());
	        }
	        else
	        {
	        	System.out.println("all goood");
	        	// set this user into the session
	            request.getSession().setAttribute("user", new User(username));

	            JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "success");
	            responseJsonObject.addProperty("message", "success");

	            response.getWriter().write(responseJsonObject.toString());
	        }
        } catch (Exception e) {
			e.printStackTrace();
			out.println("<body>");
			out.println("<p>");
			out.println("Exception in doGet: " + e.getMessage());
			out.println("</p>");
			out.print("</body>");
		}
        

    }
      
}