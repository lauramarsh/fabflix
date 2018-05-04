

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
    		String query = "select title, director, year, group_concat(distinct genres.name) as genre_list, group_concat(distinct stars.name) as stars_list, rating "
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
    		
    		// Dynamic HTML
    		out.println("<body>");
    		out.println("<div class=\"block block__thin\">");
    		out.println("<div class=\"title\">");
    		out.println("<h2>"+ movieTitle + "</h2>");
    		out.println("</div>");
    		out.println("<div class=\"movie\">");
    		out.println("<img class=\"movie--poster\" src=\"#\">");
    		out.println("<div class=\"movie--info\">");
    		out.println("<h3>");
    		
    		
    	
    		
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
