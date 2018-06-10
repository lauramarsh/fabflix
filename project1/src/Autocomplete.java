import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

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

// server endpoint URL
@WebServlet("/autocomplete")
public class Autocomplete extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	/*
	 * Key is search query, value is the json object filled with suggestions 
	 */
	
    public Autocomplete() {
        super();
    }

    /*
     * For example, if the query is "super":
     * The JSON response look like this:
     * [
     * 	{ "value": "Superman", "data": { "category": "dc", "heroID": 101 } },
     * 	{ "value": "Supergirl", "data": { "category": "dc", "heroID": 113 } }
     * ]
    
     * The response contains a list of suggestions.
     * In each suggestion object, the "value" is the item string shown in the dropdown list,
     *   the "data" object can contain any additional information.
     * 
     * 
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			
			// Connect to database using pooling
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                response.getWriter().write("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/MovieDb");
            
            if (ds == null)
                response.getWriter().write("ds is null.");

            Connection connection = ds.getConnection();
            if (connection == null)
                response.getWriter().write("dbcon is null.");   		
	        
			// setup the response json arrray
			JsonArray jsonArray = new JsonArray();
			
    		// Query params as string list
    		String searchContent = request.getParameter("query");
    		String[] searchList = searchContent.split(" ");
    		System.out.println(searchContent);
			
			// return the empty json array if query is null or empty
			if (searchContent == null || searchContent.trim().isEmpty()) {
				response.getWriter().write(jsonArray.toString());
				return;
			}
		
			else
			{
				
				//Build query string 
	    		StringBuilder query = new StringBuilder();
	    		StringBuilder queryDesc = new StringBuilder();
	    		
	    		queryDesc.append("Search Results");
	    		
	    		query.append(" select id, title from movies ");
	    		
	    		//perform full text search
    			query.append("  where match (title) against (");
    			for(int i = 0; i < searchList.length; i++)
				{
					query.append("? ");
				}	
    			query.append(" in boolean mode)");
	    		
    			// set limit specified in rubric
	    		query.append(" limit 10");
	    	
	    		// create prepared statement
	    		PreparedStatement preparedStatement =
	    		        connection.prepareStatement(query.toString());
	    		
	    		// set parameters
	    		for(int i = 0; i < searchList.length; i++)
				{
					preparedStatement.setString(i+1, searchList[i] + "* ");
				}	
	    
	    		System.out.println(query.toString());
	    		// execute query
	    		ResultSet resultSet = preparedStatement.executeQuery();
	    		
	    		while (resultSet.next()) 
	    		{
	    			// get movie data from result set
	    			String movieId = resultSet.getString("movies.id");
	    			String movieTitle = resultSet.getString("title");
	    			jsonArray.add(generateJsonObject(movieId, movieTitle, ""));
	    		}
			}

			response.getWriter().write(jsonArray.toString());
			return;
		} 
		catch (Exception e) 
		{
			System.out.println(e);
			response.sendError(500, e.getMessage());
		}
	}
	
	/*
	 * Generate the JSON Object from hero and category to be like this format:
	 * {
	 *   "value": "Iron Man",
	 *   "data": { "category": "marvel", "heroID": 11 }
	 * }
	 * 
	 */
	private static JsonObject generateJsonObject(String movieId, String movieTitle, String categoryName) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", movieTitle);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("category", categoryName);
		additionalDataJsonObject.addProperty("movieId", movieId);
		
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}


}