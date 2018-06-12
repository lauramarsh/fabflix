import java.io.File;
import java.io.FileWriter;
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

@WebServlet(name = "search", urlPatterns = "/searchlist")
public class SearchPage extends HttpServlet{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @see HttpServlet#HttpServlet()
     */
	public SearchPage() {
		super();
	    // TODO Auto-generated constructor stub
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// Start timing for TS
		long startTS = System.nanoTime();

		String contextPath = getServletContext().getRealPath("/");

		String filePath=contextPath+"/log.txt";
		
		// create new files
        File file = new File(filePath);
        
        // if file doesn't exist create it
        if(!file.exists())
        {
        	file.createNewFile();	
        }
        
        FileWriter writer = new FileWriter(file, true); 
             
        // create new file in the system
        file.createNewFile();
		
		
		
        response.setContentType("text/html");
        
        PrintWriter out = response.getWriter();   
        out.println("<html>");
        out.println("<head>"
        		+ "<meta charset=\"utf-8\">"
        		+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">"
        		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/>"
        		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"bootstrap.min.css\"/>"
        		+ "<title>Fabflix: Search Results</title>"
        		+ "</head>");
        
        try {
        	
    		// Start timing for TJ
        	long startTJ = System.nanoTime();
        	
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
    		
    		// Query params
    		String title = request.getParameter("title"),  director = request.getParameter("director");
    		String year = request.getParameter("year"), star_name = request.getParameter("star-name");
    		// Sorting params
    		String title_order = request.getParameter("title-order"),  rating_order = request.getParameter("rating-order");
            // Page/nav params
            int page = Integer.parseInt(request.getParameter("page"));
            
            String results = request.getParameter("results");
            int resultLimit = 50;
            if (results != null && !results.equals("")) {
               resultLimit = Integer.parseInt(results); 
            }
            else
            {
            	results = "50";
            }
            int offsetCount = page * resultLimit;
           
    		// Create urls for sorting re-direction 
            String this_url = request.getRequestURL().toString();
    	    String domain_url = this_url.substring(0, this_url.lastIndexOf("/") + 1);
    		String base_url = this_url + "?title=" + title + "&director=" 
    				+ director + "&year=" + year + "&star-name=" + star_name;
    		
    		String url_title_ordered_asc = base_url + "&title-order=asc&rating-order=&page=" + page;
    		String url_rating_ordered_asc = base_url + "&title-order=&rating-order=asc&page=" + page;
    		String url_title_ordered_desc = base_url + "&title-order=desc&rating-order=&page=" + page;
    		String url_rating_ordered_desc = base_url + "&title-order=&rating-order=desc&page=" + page;
    	
    		//Build query string 
    		    		
    		StringBuilder query = new StringBuilder();
    		StringBuilder queryDesc = new StringBuilder();
    		
    		queryDesc.append("Search Results");
    		
    		query.append(" select movies.id, title, director, year, group_concat(distinct genres.name) as genre_list, group_concat(distinct concat(stars.name, \':\', stars.id)) as stars_list, rating");
    		query.append(" from movies, genres_in_movies, genres, stars, stars_in_movies, ratings");
    		query.append(" where movies.id = genres_in_movies.movieId and genres_in_movies.genreId = genres.id");
    		query.append(" and movies.id = stars_in_movies.movieId and stars_in_movies.starId = stars.id");
    		query.append(" and movies.id = ratings.movieId");
    		
    		
    		if(year.length() > 0)
    			query.append(" and year = ?");
    		if(director.length() > 0) 
    			query.append(" and director like ?");
    		if(title.length() > 0)
    			query.append(" and title like ?");
   
    		query.append(" group by movies.id, title, rating, year, director");
    		
    		if(star_name.length() > 0) 
    			query.append(" having stars_list like ?");
    		
    		// Apply sorting: either by rating OR by title name 
    		if(rating_order.length() == 0)
    			//sort by title 
    			query.append( " order by title " + title_order);
    		else if(title_order.length() == 0)
    			//sort by rating 
    			query.append( " order by rating " + rating_order);
    		
    		query.append(" limit " + Integer.toString(resultLimit) + " offset " + Integer.toString(offsetCount) + ";");
    		
    		
    		// create prepared statement
    		PreparedStatement preparedStatement =
    		        connection.prepareStatement(query.toString());

    		// set parameters
    		int setCounter = 1;
    		if(year.length() > 0) {
    			preparedStatement.setString(setCounter++, year);
    		}
    		if(director.length() > 0) {
    			preparedStatement.setString(setCounter++, "%" + director + "%");
    		}
    		if(title.length() > 0) {
    			preparedStatement.setString(setCounter++, "%" + title + "%");
    		}
    		if(star_name.length() > 0) {
    			preparedStatement.setString(setCounter++, "%" + star_name + "%");
    		}
    		
    		System.out.println(query.toString());
    		// execute query
    		ResultSet resultSet = preparedStatement.executeQuery();
    		

   		   // Finish timing for TS 
           long endTJ = System.nanoTime();
           long TJ = endTJ - startTJ; 
           
           writer.write(TJ + ",");
           System.out.print("TJ value: " + TJ);
    		
    		out.println("<body>");
    		
//    		out.println("<h1>" + preparedStatement.toString() + "</h1>"); // check what the prepared statement is
    		
    		out.println("<div class=\"nav-bar table__black\"><a  class =\"btn btn-warning\"  href = \"index.html\">home</a><a  class =\"btn btn-warning\"  href = \"cart\">cart</a><a  class =\"btn btn-warning\"  href = \"login.html\">log Out</a></div>");
    		out.println("<div class=\"title\">");
    		out.println("<h1>" + queryDesc + " </h1>");
    		out.println("</div>");
    		out.println("<div class=\"block block__fat\">");
    		out.println("<table class=\"table table__black\">");

    		out.println("<thead>");
    		out.println("<tr>");
    		out.println("<th class = \"rowHead\"></td>");
    		out.println("<th class = \"rowHead\"></td>");
    		out.println("<th class = \"rowHead\">ID</td>");
    		out.println("<th class = \"rowHead\">"
    				+ "<a href=\"" + url_title_ordered_desc +"\" class = sortButton >&#9661</a> Title "
    				+ "<a href=\""+ url_title_ordered_asc +"\" class = sortButton >&#9651</a></th>");
    		out.println("<th  >Year</td>");
    		out.println("<th>Director</td>");
    		out.println("<th>Genres</td>");
    		out.println("<th>Stars</td>");
    		out.println("<th class = \"rowHead\">"
    				+ "<a href=\"" + url_rating_ordered_desc +"\" class = sortButton >&#9661</a> Rating"
    				+ "<a href=\""+ url_rating_ordered_asc +"\" class = sortButton >&#9651</a></th>");
    		out.println("</tr>");
    		out.println("</thead>");
    		
    		out.println("<tbody>");
    
    		while (resultSet.next()) {
    			// get a star from result set
    			String movieId = resultSet.getString("movies.id");
    			String movieTitle = resultSet.getString("title");
    			String movieYear = resultSet.getString("year");
    			String movieDir = resultSet.getString("director");
    			String movieRating = resultSet.getString("rating");
    			
    			// Unload genres
    			String movieGenres = resultSet.getString("genre_list");
    			String[] genres = movieGenres.split(",");
    			String genresSeparated = "";
    			for(int i=0; i<genres.length; i++) {
    				genresSeparated += "<p>" + genres[i] + "</p>";
    			}
    			
    			// Unload stars_list
    			String movieStars = resultSet.getString("stars_list");
    			String[] starsList = movieStars.split(","); // contains both star and id --> 'star:id'
    			String starsHyperlinked = "";
    			for(int i=0; i<starsList.length; i++) {
    				String[] currentStar = starsList[i].split(":");
    				String starName = currentStar[0];
    				String starID = currentStar[1];
    				starsHyperlinked += "<a href=\"/project1/starpage?starID="+ starID +"\">"+starName+"</a>";
    			}
    			
    			out.println("<tr>");
        		out.println("<td><button class =\"btn btn-danger\" onclick = \"handleAdd(this)\" value=\"" + movieId + "\">Add To Cart</button></td>");
                out.println("<td><img src=\"GenericMoviePoster.jpg\" alt=\"\" border=3 height=200 width=150></img></td>");
    			out.println("<td>" + movieId + "</td>");
    			out.println("<td class=\"link\"> <a href=\"" + domain_url + "moviepage?movie=" + movieTitle + "\">" 
    					+ movieTitle + "</a><p class = \"hiddenText\">spacefillerspacefiller<p></td>");
    			out.println("<td>" + movieYear + "<p class = \"hiddenText\">spacefiller<p></td>");
    			out.println("<td>" + movieDir + "</td>");
    			out.println("<td>" + genresSeparated + "</td>");
    			out.println("<td class=\"link link__scroll\">" + starsHyperlinked + "</td>");
    			out.println("<td>" + movieRating + "<p class = \"hiddenText\">spacefillerspacefiller<p></td>");
    			out.println("</tr>");
    		}
    		out.println("</tbody>");
    		out.println("</table>");
    		
    		// Pagination Navigation
    		String pageUrl = base_url + "&page=" + "&title-order=" + title_order + "&rating-order=" + rating_order;
    		System.out.println(pageUrl);
    		
    		out.println("<form action=\"" + pageUrl + "\">");
    		out.println("<select name=\"results\">");
    		out.println("<option value=\"10\">10</option>");
    		out.println("<option value=\"20\">20</option>");
    		out.println("<option value=\"50\">50</option>");
    		out.println("<option value=\"100\">100</option>");
    		out.println("</select>");
    		out.println("<input type=\"submit\" value=\"view\">");
    		out.println("<input type=\"hidden\" name=\"results\" value=\""+ results + "\">");
    		out.println("<input type=\"hidden\" name=\"page\" value=\"0\">");
    		out.println("<input type=\"hidden\" name=\"title\" value=\"" + title + "\">");
    		out.println("<input type=\"hidden\" name=\"year\" value=\"" + year + "\">");
    		out.println("<input type=\"hidden\" name=\"director\" value=\"" + director + "\">");
    		out.println("<input type=\"hidden\" name=\"star-name\" value=\"" + star_name + "\">");
    		out.println("<input type=\"hidden\" name=\"title-order\" value=\"" + title_order + "\">");
    		out.println("<input type=\"hidden\" name=\"rating-order\" value=\"" + rating_order + "\">");
    		out.println("</form>");
    		
    		out.println("<nav aria-label=\"movie list page nav\">");
    		out.println("<ul class=\"pagination\">");
    		if (page > 0) { //not the first result page
        		out.println("<li class=\"page-item\">"
        				+ "<a class=\"page-link\" href=\""+ base_url + "&page=" + Integer.toString(page-1) 
        				+ "&title-order=" + title_order + "&rating-order=" + rating_order + "&results=" + results + "\">Prev</a></li>");    			
    		}
    		out.println("<li class=\"page-item\">"
    				+ "<a class=\"page-link\" href=\"" + base_url + "&page=" + Integer.toString(page+1) 
    				+ "&title-order=" + title_order + "&rating-order=" + rating_order + "&results=" + results + "\">Next</a></li>");   
    		out.println("</ul>");
    		out.println("</nav>");
    		
    		
    		out.println("</div>");
    		out.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>");
    		out.println("<script src=\"./addMovie.js\"></script>");
    		out.println("</body>");
    		
    		resultSet.close();
    		preparedStatement.close();
    		connection.close();
    			
    		
        } catch (Exception e) {
        	e.printStackTrace();
    		out.println("<body>");
    		out.println("<p>");
    		out.println("Exception in doGet: " + e.getMessage());
    		out.println("</p>");
    		out.print("</body>");
        }
        out.println("</html>");
        out.close();
        
        // Finish timing for TS 
        long endTS = System.nanoTime();
        long TS = endTS - startTS; 
        
        // Write value to file
        writer.write(TS + "\n");
        System.out.print("TS value: " + TS);
        writer.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}


}