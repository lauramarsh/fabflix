

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
 * Servlet implementation class MovieServlet
 */
@WebServlet("/movielist")
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
        out.println("<html>");
        out.println("<head><title>Fabflix</title><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/><link rel=\"stylesheet\" type=\"text/css\" href=\"bootstrap.min.css\"/></head>");
        
        try {
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		Statement statement = connection.createStatement();
    		
    		// QUERYS 
    		String query = "SELECT c.title, c.director, c.year, c.rating, c.genre_list, group_concat(c.star) as stars_list  from " + 
    				"(SELECT b.movieid, b.title, b.director, b.year, b.rating, b.genre_list,  stars.name as star  from " + 
    				"(SELECT a.movieid, a.title, a.director, a.year, a.rating, group_concat(a.genre) as genre_list from " + 
    				"(SELECT movies.id as movieid , title, director, year, rating, genres.name as genre from " + 
    				"movies join ratings on movies.id  = ratings.movieId " + 
    				"join genres_in_movies on movies.id = genres_in_movies.movieId " + 
    				"join genres on genres.id = genres_in_movies.genreId )a " + 
    				"group by a.movieid, a.title, a.rating,a.year, a.director  )b " + 
    				"join stars_in_movies on b.movieid = stars_in_movies.movieId " + 
    				"join stars on stars.id = stars_in_movies.starId )c " + 
    				"group by c.movieid, c.title, c.rating,c.year, c.director " + 
    				"order by c.rating desc, c.title asc " + 
    				"limit 20";

    		// execute query
    		ResultSet resultSet = statement.executeQuery(query);
    		
    		out.println("<body>");
    		out.println("<h1 class=\"block\">Movie List</h1>");
    		out.println("<table border class=\"table table__black\">");
    		
    		out.println("<tr>");
    		out.println("<td>title</td>");
    		out.println("<td>year</td>");
    		out.println("<td>director</td>");
    		out.println("<td>genres</td>");
    		out.println("<td>stars</td>");
    		out.println("<td>rating</td>");
    		out.println("</tr>");
    		
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
    		out.println("</table>");
    		
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
