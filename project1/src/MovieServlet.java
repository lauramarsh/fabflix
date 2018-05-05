

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
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class MovieServlet
 */
@WebServlet("/moviepage")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieServlet() {
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
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
       
        // Get Movie ID parameter
        String movieTitle = request.getParameter("movie");
        
        // Begin html output
        out.println("<html>");
        out.println("<head><title>Fabflix Movie Page</title><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/><link rel=\"stylesheet\" type=\"text/css\" href=\"bootstrap.min.css\"/></head>");
        
        try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		Statement statement = connection.createStatement();
    		
    		// Query 
    		String query = "select movies.id, title, director, year, group_concat(distinct genres.name) as genre_list, group_concat(distinct concat(stars.name, \':\', stars.id)) as stars_list, rating "
    				+ "from movies, genres_in_movies, genres, stars, stars_in_movies, ratings "
    				+ "where movies.id = genres_in_movies.movieId "
    				+ "and genres_in_movies.genreId = genres.id "
    				+ "and movies.id = stars_in_movies.movieId "
    				+ "and stars_in_movies.starId = stars.id "
    				+ "and movies.id = ratings.movieId "
    				+ "group by movies.id, title, rating, year, director "
    				+ "having title like '" + movieTitle + "';";

    		// ResultSet should be 1 movie, resultSet.next() is null
    		ResultSet resultSet = statement.executeQuery(query);
    		
    		while (resultSet.next()) {
    			String movieID = resultSet.getString("movies.id");
    			String movieYear = resultSet.getString("year");
    			String movieDir = resultSet.getString("director");
    			String movieGenres = resultSet.getString("genre_list");
    			String movieRating = resultSet.getString("rating");
    			
    			// Hyperlink Genres
    			String[] genres = movieGenres.split(","); 
    			String genresHyperlinked = "";
    			for(int i=0; i<genres.length; i++) {
    				genresHyperlinked += "<a href=\"/project1/browselist?page=0&genre="+genres[i]+"&title=&title-order=&rating-order=desc\">"+genres[i]+"</a>";
    			}
    			
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
    			    			
    			// Dynamic HTML
        		out.println("<body>");
        		out.println("<div class = \"cartLinks\">");
        		out.println("<a  class =\"btn btn-danger\"  href = \"/project1/cart\">View Cart</a>");
        		out.println("<a  class =\"btn btn-danger\"  href = \"login.html\">Log Out </a>");
        		out.println("</div>");
        		out.println("<div class=\"title\">");
        		out.println("<h1>"+ movieTitle + "</h1>");
        		out.println("<button  class =\"btn btn-danger\" onclick = \"handleAdd(this)\" value=\"" + movieID + "\">Add To Cart</button>");
        		out.println("</div>");
        		out.println("<div class=\"block block__thin\">");
        		out.println("<div class=\"movie\">");
        		out.println("<img class=\"movie--poster\" src=\"GenericMoviePoster.jpg\">");
        		out.println("<div class=\"movie--info\">");
        		out.println("<table class=\"table table__black\">");
        		out.println("<tbody>");
        		out.println("<tr><th scope=\"row\">ID</th><td>" + movieID + "</td></tr>");
        		out.println("<tr><th scope=\"row\">Rating</th><td>" + movieRating + "</td></tr>");
        		out.println("<tr><th scope=\"row\">Year</th><td>" + movieYear + "</td></tr>");
        		out.println("<tr><th scope=\"row\">Director</th><td>" + movieDir + "</td></tr>");
        		out.println("<tr><th scope=\"row\">Genre</th><td class=\"link\">" + genresHyperlinked + "</td></tr>");
        		out.println("<tr><th scope=\"row\">Stars</th><td class=\"link link__scroll\">" + starsHyperlinked + "</td></tr>");
       
    		}
    		
    		out.println("</tbody>");
    		out.println("</table>");
    		out.println("</div>"); // movie--info
    		out.println("</div>"); // movie
    		
    		out.println("</div>"); // block
    		
    		out.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>");
    		out.println("<script src=\"./addMovie.js\"></script>");
    		out.println("</body>");
    		
    		
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
