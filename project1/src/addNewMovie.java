
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
 * Servlet implementation class addNewMovie
 */
@WebServlet(name="addNewMovie", urlPatterns="/addNewMovie")
public class addNewMovie extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public addNewMovie() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String loginUser = "root";
        String loginPasswd = "pissoff";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        
        
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
    		
    		Statement statement = connection.createStatement();
    		
    		// Insert Query Params
    		String title = request.getParameter("title");
    		String year = request.getParameter("year");
    		String director = request.getParameter("director");
    		String starName = request.getParameter("star");
    		String starBY = request.getParameter("starBY");
    		String genre = request.getParameter("genre");
    				
    		// If missing info return error
    		if(title.equals(null) || title == null || title.length() < 1) {
    			JsonObject responseJsonObject = new JsonObject();
    			responseJsonObject.addProperty("status", "fail");
    			responseJsonObject.addProperty("message", "Please enter a movie title.");
    			response.getWriter().write(responseJsonObject.toString());
    		} else if (year.equals(null) || year == null || year.length() < 1) {
    			JsonObject responseJsonObject = new JsonObject();
    			responseJsonObject.addProperty("status", "fail");
    			responseJsonObject.addProperty("message", "Please enter a release Year.");
    			response.getWriter().write(responseJsonObject.toString());
    		} else if (director.equals(null) || director == null || director.length() < 1) {
    			JsonObject responseJsonObject = new JsonObject();
    			responseJsonObject.addProperty("status", "fail");
    			responseJsonObject.addProperty("message", "Please enter a director name.");
    			response.getWriter().write(responseJsonObject.toString());
    		} else if (starName.equals(null) || starName == null || starName.length() < 1) {
    			JsonObject responseJsonObject = new JsonObject();
    			responseJsonObject.addProperty("status", "fail");
    			responseJsonObject.addProperty("message", "Please enter one(!) star name.");
    			response.getWriter().write(responseJsonObject.toString());
    		} else if (genre.equals(null) || genre == null || genre.length() < 1) {
    			JsonObject responseJsonObject = new JsonObject();
    			responseJsonObject.addProperty("status", "fail");
    			responseJsonObject.addProperty("message", "Please enter one(!) genre name.");
    			response.getWriter().write(responseJsonObject.toString());
    		} else {
    			// Build Query and use stored procedure 'addStar'
        		StringBuilder movieExistsQuery = new StringBuilder();
        		movieExistsQuery.append("select * from movies where title='" + title 
        				+ "' and year=" + year 
        				+ " and director='" + director 
        				+ "';");
        		PreparedStatement ps = connection.prepareStatement(movieExistsQuery.toString());
        		ResultSet rs = ps.executeQuery();
        		
    			if (rs.next()) { // rs is not an empty set - movie already exists
    				JsonObject responseJsonObject = new JsonObject();
	    			responseJsonObject.addProperty("status", "fail");
	    			responseJsonObject.addProperty("message", "Movie already exists.");
	    			response.getWriter().write(responseJsonObject.toString());
    			} else { // rs is an empty set - movie does not exist
    				StringBuilder addMovieQuery = new StringBuilder();
    				addMovieQuery.append("call addMovie('" + title + "', " + year + ", '" + director + "', '" + starName + "', " + starBY + ", '" + genre + "', @status);");
    				PreparedStatement mps = connection.prepareStatement(addMovieQuery.toString());
            		mps.executeQuery();
    				
    				JsonObject responseJsonObject = new JsonObject();
	    			responseJsonObject.addProperty("status", "success");
	    			responseJsonObject.addProperty("message", "Movie successfully added!");
	    			response.getWriter().write(responseJsonObject.toString());
    			}
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
