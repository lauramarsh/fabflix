

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
        
        // Page/Nav Params
        int page = Integer.parseInt(request.getParameter("page"));
        String results = request.getParameter("results");
        int resultLimit = 20;
        if (results != null && !results.equals("")) {
        	resultLimit = Integer.parseInt(results); 
        }
        int offsetCount = page * resultLimit;
   
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
    		
    		if (genre != "") { //For browse genre selections
    			// QUERIES
        		query = "select title, director, year, group_concat(distinct genres.name) as genre_list, group_concat(distinct stars.name) as stars_list, rating "
        				+ "from movies, genres_in_movies, genres, stars, stars_in_movies, ratings "
        				+ "where movies.id = genres_in_movies.movieId and genres_in_movies.genreId = genres.id "
        				+ "and movies.id = stars_in_movies.movieId and stars_in_movies.starId = stars.id "
        				+ "and movies.id = ratings.movieId "
        				+ "group by movies.id, title, rating, year, director "
        				+ "having genre_list like '%" + genre + "%' "
        				+ "order by rating desc, title asc "
        				+ "limit " + Integer.toString(resultLimit) + " offset " + Integer.toString(offsetCount) + ";";
    		} else { // For browse title selections
    			query = "select title, director, year, group_concat(distinct genres.name) as genre_list, group_concat(distinct stars.name) as stars_list, rating "
        				+ "from movies, genres_in_movies, genres, stars, stars_in_movies, ratings "
        				+ "where movies.id = genres_in_movies.movieId and genres_in_movies.genreId = genres.id "
        				+ "and movies.id = stars_in_movies.movieId and stars_in_movies.starId = stars.id "
        				+ "and movies.id = ratings.movieId "
        				+ "group by movies.id, title, rating, year, director "
        				+ "having title like '" + title + "%' "
        				+ "order by rating desc, title asc "
        				+ "limit " + Integer.toString(resultLimit) + " offset " + Integer.toString(offsetCount) + ";";
    		}
    		
    		// execute query
    		ResultSet resultSet = statement.executeQuery(query);
    		
    		out.println("<body>");
    		out.println("<div class=\"title\">");
    		out.println("<h1>" + genre + title + " Movies</h1>");
    		out.println("</div>");
    		
    		// Page items container
    		out.println("<div class=\"block block__fat\">");
    		// Movie list table
    		out.println("<table class=\"table table__black\">");
    		out.println("<thead>");
    		out.println("<tr>");
    		out.println("<th>title</td>");
    		out.println("<th>year</td>");
    		out.println("<th>director</td>");
    		out.println("<th>genres</td>");
    		out.println("<th>stars</td>");
    		out.println("<th>rating</td>");
    		out.println("</tr>");
    		out.println("</thead>");
    		// Movie list items
    		out.println("<tbody>");
    		while (resultSet.next()) {
    			// get a star from result set
    			String movieTitle = resultSet.getString("title");
    			String movieYear = resultSet.getString("year");
    			String movieDir = resultSet.getString("director");
    			String movieGenres = resultSet.getString("genre_list");
    			String movieStars = resultSet.getString("stars_list");
    			String movieRating = resultSet.getString("rating");
    			
    			out.println("<tr>");
    			out.println("<td>" + movieTitle + "</td>");
    			out.println("<td>" + movieYear + "</td>");
    			out.println("<td>" + movieDir + "</td>");
    			out.println("<td>" + movieGenres + "</td>");
    			out.println("<td>" + movieStars + "</td>");
    			out.println("<td>" + movieRating + "</td>");
    			out.println("</tr>");
    		}
    		out.println("</tbody>");
    		out.println("</table>");
    		
    		// Pagination Navigation
    		out.println("<form action=\"http://localhost:8080/project1/browselist?page=" + Integer.toString(page) + "&genre=" + genre + "&title=" + title + "\">");
    		out.println("<select name=\"results\">");
    		out.println("<option value=\"10\">10</option>");
    		out.println("<option value=\"20\">20</option>");
    		out.println("<option value=\"50\">50</option>");
    		out.println("<option value=\"100\">100</option>");
    		out.println("</select>");
    		out.println("<input type=\"submit\" value=\"view\">");
    		out.println("<input type=\"hidden\" name=\"page\" value=\"0\">");
    		out.println("<input type=\"hidden\" name=\"genre\" value=\"" + genre + "\">");
    		out.println("<input type=\"hidden\" name=\"title\" value=\"" + title + "\">");
    		out.println("</form>");
    		out.println("<nav aria-label=\"movie list page nav\">");
    		out.println("<ul class=\"pagination\">");
    		if (page > 0) { //not the first result page
        		out.println("<li class=\"page-item\">"
        				+ "<a class=\"page-link\" href=\"http://localhost:8080/project1/browselist?page=" + Integer.toString(page-1) 
        				+ "&genre=" + genre + "&title=" + title + "\">Prev</a></li>");    			
    		}
    		out.println("<li class=\"page-item\">"
    				+ "<a class=\"page-link\" href=\"http://localhost:8080/project1/browselist?page=" + Integer.toString(page+1) 
    				+ "&genre=" + genre + "&title=" + title + "\">Next</a></li>");
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
