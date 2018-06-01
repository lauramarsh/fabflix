import com.google.gson.JsonObject;
import java.util.*;

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

import org.jasypt.util.password.StrongPasswordEncryptor;

//
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;


    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
    	// TODO Auto-generated method stub
        PrintWriter out = response.getWriter();

       /* String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
            out.println("<html>");
            out.println("<head><title>Error</title></head>");
            out.println("<body>");
            out.println("<p>recaptcha verification error</p>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</body>");
            out.println("</html>");
            
            out.close();
            return;}
        */
		
    			String loginUser = "root";
    	        String loginPasswd = "pissoff";
    	        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
    	        
        try {
        	
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		Statement statement = connection.createStatement();
	    	        
	        String username = request.getParameter("username");
	        String password = request.getParameter("password");
	       
	        StringBuilder query = new StringBuilder();
	        query.append("select * from customers where email = '"+username+"'");
	 
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
	        else 
	        {
	        	
	        	boolean success = false;
	        	// get the encrypted password from the database
				String encryptedPassword = resultSet.getString("password");
				
				// use the same encryptor to compare the user input password with encrypted password stored in DB
				success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
				
				System.out.println(password + " " + encryptedPassword + " " + success);
				
				if(success) 
				{
					// set this user into the session
		            request.getSession().setAttribute("user", new User(username));
		            request.getSession().setAttribute("cart", new HashMap<String,Integer>());

		            JsonObject responseJsonObject = new JsonObject();
		            responseJsonObject.addProperty("status", "success");
		            responseJsonObject.addProperty("message", "success");

		            response.getWriter().write(responseJsonObject.toString());
				}
				else 
				{
					JsonObject responseJsonObject = new JsonObject();
		            responseJsonObject.addProperty("status", "fail");
		        	responseJsonObject.addProperty("message", "incorrect password");
		        	
		        	response.getWriter().write(responseJsonObject.toString());
				}
	        }
        } 
        
        
        catch (Exception e) {
			
		}
        
    }
      
}