

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BrowseServlet
 */
@WebServlet("/browselist")
public class BrowseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrowseServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String loginUser = "root";
        String loginPasswd = "pissoff";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        
    	
	    // Search Params
	    String genre = request.getParameter("genre");
	    String title = request.getParameter("title");
		// Sorting params
		String title_order = request.getParameter("title-order"),  rating_order = request.getParameter("rating-order");
	    // Page/nav params
	    int page = Integer.parseInt(request.getParameter("page"));
	    
	    String results = request.getParameter("results");
	    int resultLimit = 20;
	    if (results != null && !results.equals("")) {
	      resultLimit = Integer.parseInt(results); 
	    }
	    else
	    {
	    	results = "20";
	    }
	    int offsetCount = page * resultLimit;
	  
		// Create urls for sorting re-direction 
	    String this_url = request.getRequestURL().toString();
	    String domain_url = this_url.substring(0, this_url.lastIndexOf("/") + 1);
		String base_url = this_url + "?title=" + title + "&genre=" 
				+ genre;
		
		
		String url_title_ordered_asc = base_url + "&title-order=asc&rating-order=&page=" + page;
		String url_rating_ordered_asc = base_url + "&title-order=&rating-order=asc&page=" + page;
		String url_title_ordered_desc = base_url + "&title-order=desc&rating-order=&page=" + page;
		String url_rating_ordered_desc = base_url + "&title-order=&rating-order=desc&page=" + page;


        // HTML Generating
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>"
        		+ "<meta charset=\"utf-8\">"
        		+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">"
        		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/>"
        		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"bootstrap.min.css\"/>"
        		+ "<title>Fabflix: Browse Results</title>"
        		+ "</head>");
        
        try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		Statement statement = connection.createStatement();
    		String query = "";
    		
    		String sortBy = "";
    		// Apply sorting: either by rating OR by title name 
    		if(rating_order.length() == 0)
    		{
    			//sort by title 
    			sortBy = " order by title " + title_order + " ";
    		}
    		else if(title_order.length() == 0)
    		{
    			//sort by rating 
    			sortBy = " order by rating " + rating_order + " ";
    		}
    		
    		if (genre != "") { //For browse genre selections
    			// QUERIES
        		query = "select movies.id, title, director, year, group_concat(distinct genres.name) as genre_list, group_concat(distinct concat(stars.name, \':\', stars.id)) as stars_list, rating "
        				+ "from movies, genres_in_movies, genres, stars, stars_in_movies, ratings "
        				+ "where movies.id = genres_in_movies.movieId and genres_in_movies.genreId = genres.id "
        				+ "and movies.id = stars_in_movies.movieId and stars_in_movies.starId = stars.id "
        				+ "and movies.id = ratings.movieId "
        				+ "group by movies.id, title, rating, year, director "
        				+ "having genre_list like '%" + genre + "%' "
        				+ sortBy
        				+ "limit " + Integer.toString(resultLimit) + " offset " + Integer.toString(offsetCount) + ";";
    		} else { // For browse title selections
    			query = "select movies.id, title, director, year, group_concat(distinct genres.name) as genre_list, group_concat(distinct concat(stars.name, \':\', stars.id)) as stars_list, rating "
        				+ "from movies, genres_in_movies, genres, stars, stars_in_movies, ratings "
        				+ "where movies.id = genres_in_movies.movieId and genres_in_movies.genreId = genres.id "
        				+ "and movies.id = stars_in_movies.movieId and stars_in_movies.starId = stars.id "
        				+ "and movies.id = ratings.movieId "
        				+ "group by movies.id, title, rating, year, director "
        				+ "having title like '" + title + "%' "
        				+ sortBy
        				+ "limit " + Integer.toString(resultLimit) + " offset " + Integer.toString(offsetCount) + ";";
    		}
    		
    		// execute query
    		ResultSet resultSet = statement.executeQuery(query);
    		
    		out.println("<body>");
    		out.println("<div class = \"cartLinks\">");
    		out.println("<a  class =\"btn btn-danger\"  href = \"login.html\">Log Out </a>");
    		out.println("</div>");
    		out.println("<div class=\"title\">");
    		out.println("<h1>" + genre + title + " Movies</h1>");
    		out.println("</div>");

    		// Page items container
    		out.println("<div class=\"block block__fat\">");
    		
    		// Movie list table
    		out.println("<table class=\"table table__black\">");
    		out.println("<thead>");
    		out.println("<tr>");
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
    			String movieGenres = resultSet.getString("genre_list");
    			String movieRating = resultSet.getString("rating");
    			
    			// Unload stars_list
    			String movieStars = resultSet.getString("stars_list");
    			String[] starsList = movieStars.split(","); // contains both star and id --> 'star:id'
    			String starsHyperlinked = "";
    			for(int i=0; i<starsList.length; i++) {
    				String[] currentStar = starsList[i].split(":");
    				String starName = currentStar[0];
    				String starID = currentStar[1];
    				starsHyperlinked += "<a href=\"/project1/starpage?starID="+starID+"\">"+starName+"</a>";
    			}
    			
    			// Dynamic content
    			out.println("<tr>");
                out.println("<td><img src=\"GenericMoviePoster.jpg\" alt=\"\" border=3 height=200 width=150></img></td>");
    			out.println("<td>" + movieId + "</td>");
    			out.println("<td class=\"link\"> <a href=\"" + domain_url + "moviepage?movie=" + movieTitle + "\">" 
    					+ movieTitle + "</a><p class = \"hiddenText\">spacefillerspacefiller<p></td>");
    			out.println("<td>" + movieYear + "<p class = \"hiddenText\">spacefiller<p></td>");
    			out.println("<td>" + movieDir + "</td>");
    			out.println("<td>" + movieGenres + "</td>");
    			out.println("<td class=\"link link__scroll\">" + starsHyperlinked + "</td>");
    			out.println("<td>" + movieRating + "<p class = \"hiddenText\">spacefillerspacefiller<p></td>");
    			out.println("</tr>");
    		}
    		out.println("</tbody>");
    		out.println("</table>");
    		
    		// Pagination Navigation
    		String pageUrl = base_url + "&page=" + "&title-order=" + title_order + "&rating-order=" + rating_order;
    	    		
    		out.println("<form action=\"/project1/browselist?page=" + Integer.toString(page) + "&genre=" + genre + "&title=" + title + "\">");
    		out.println("<select name=\"results\">");
    		out.println("<option value=\"10\">10</option>");
    		out.println("<option value=\"20\">20</option>");
    		out.println("<option value=\"50\">50</option>");
    		out.println("<option value=\"100\">100</option>");
    		out.println("</select>");
    		out.println("<input type=\"submit\" value=\"view\">");
    		out.println("<input type=\"hidden\" name=\"results\" value=\""+ results + "\">");
    		out.println("<input type=\"hidden\" name=\"page\" value=\"0\">");
    		out.println("<input type=\"hidden\" name=\"genre\" value=\"" + genre + "\">");
    		out.println("<input type=\"hidden\" name=\"title\" value=\"" + title + "\">");
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
    		out.println("</body>");
    		
    		resultSet.close();
    		statement.close();
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
