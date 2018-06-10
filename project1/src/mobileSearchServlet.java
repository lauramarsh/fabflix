

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
/**
 * Servlet implementation class mobileSearchServlet
 */
@WebServlet("/mobileSearch")
public class mobileSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public mobileSearchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        response.setContentType("application/json");
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
	        
	        String movieTitle = request.getParameter("title");
	        int pageNum = Integer.parseInt(request.getParameter("pageNum"));
	        int offsetCount = 0;
	        if (pageNum > 1) {
	        	offsetCount = pageNum * 10;
	        }
	       
	        StringBuilder query = new StringBuilder();
	        query.append("select movies.id, title, director, year, group_concat(distinct genres.name) as genre_list, group_concat(distinct stars.name) as stars_list from movies, genres_in_movies, genres, stars, stars_in_movies "
	        		+ "where movies.id = genres_in_movies.movieId "
	        		+ "and genres_in_movies.genreId = genres.id "
	        		+ "and movies.id = stars_in_movies.movieId "
	        		+ "and stars_in_movies.starId = stars.id "
	        		+ "and match (title) against ('" + movieTitle + "' in boolean mode) "
	        		+ "group by movies.id, title, year, director limit 10 offset " + Integer.toString(offsetCount) + ";");
	        ResultSet resultSet = statement.executeQuery(query.toString());
	        JsonObject responseJsonObject = new JsonObject();
	        
	        if(resultSet.next() == false) // no results
	        {
	            responseJsonObject.addProperty("status", "fail");
	            response.getWriter().write(responseJsonObject.toString());
	        } else {
	        	JsonArray resultArray = new JsonArray();
	        	while (resultSet.next()) {
	        		JsonObject innerJO = new JsonObject();
	        		String title = resultSet.getString("title");
	        		String director = resultSet.getString("director");
	        		String year = resultSet.getString("year");
	        		String genres = resultSet.getString("genre_list");
	    			String stars = resultSet.getString("stars_list");
	    			
	    			innerJO.addProperty("title", title);
	    			innerJO.addProperty("director", director);
	    			innerJO.addProperty("year", year);
	    			innerJO.addProperty("genres", genres);
	    			innerJO.addProperty("stars", stars);
	    			
	    			resultArray.add(innerJO);
	        	}
	        	responseJsonObject.addProperty("status", "success");
	        	responseJsonObject.add("results", resultArray);
	        	responseJsonObject.addProperty("pageNum", pageNum);
	        }
	        
	        
	        out.write(responseJsonObject.toString());
	        connection.close();
	        statement.close();
	        
		} catch (Exception e) {
			
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
