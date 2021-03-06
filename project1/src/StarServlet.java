

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StarServlet
 */
@WebServlet("/starpage")
public class StarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StarServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get Movie ID parameter
        String starID = request.getParameter("starID");
        
        // Begin html output
        out.println("<html>");
        out.println("<head><title>Fabflix Star Page</title><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/><link rel=\"stylesheet\" type=\"text/css\" href=\"bootstrap.min.css\"/></head>");
        
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
    		
    		// Query 
    		StringBuilder query = new StringBuilder();
    		query.append("select stars.id, name, birthYear, group_concat(distinct movies.title) as movie_list "
    				+ "from stars, stars_in_movies, movies "
    				+ "where stars.id = stars_in_movies.starId "
    				+ "and stars_in_movies.movieId = movies.id "
    				+ "and stars.id = ? "
    				+ "group by stars.id, name, birthYear; ");

    		// ResultSet should be 1 movie, resultSet.next() is null
    		PreparedStatement ps = connection.prepareStatement(query.toString());
    		ps.setString(1, starID);
    		ResultSet resultSet = ps.executeQuery();
    		
    		while (resultSet.next()) {
    			String starName = resultSet.getString("name");
    			String starYear = resultSet.getString("birthYear");
    			String starMovies = resultSet.getString("movie_list");
    			
    			// Hyperlink Movies
    			String[] movies = starMovies.split(","); 
    			String moviesHyperlinked = "";
    			for(int i=0; i<movies.length; i++) {
    				moviesHyperlinked += "<a href=\"/project1/moviepage?movie="+movies[i]+"\">"+movies[i]+"</a>";
    			}
    			    			
    			// Dynamic HTML
        		out.println("<body>");
        		out.println("<div class=\"nav-bar table__black\"><a  class =\"btn btn-warning\"  href = \"index.html\">home</a><a  class =\"btn btn-warning\"  href = \"cart\">cart</a><a  class =\"btn btn-warning\"  href = \"login.html\">log Out</a></div>");
        		out.println("<div class=\"title\">");
        		out.println("<h1>"+ starName + "</h1>");
        		out.println("</div>");
        		out.println("<div class=\"block block__thin\">");
        		out.println("<div class=\"movie\">");
        		out.println("<img class=\"movie--poster\" src=\"kona.jpg\">");
        		out.println("<div class=\"movie--info\">");
        		out.println("<table class=\"table table__black\">");
        		out.println("<tbody>");
        		out.println("<tr><th scope=\"row\">Birth Year</th><td>" + starYear + "</td></tr>");
        		out.println("<tr><th scope=\"row\">Movies</th><td class=\"link link__scroll\">" + moviesHyperlinked + "</td></tr>");
       
    		}
    		
    		out.println("</tbody>");
    		out.println("</table>");
    		out.println("</div>"); // movie--info
    		out.println("</div>"); // movie
    		
    		
    		out.println("</div>"); // block
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
        out.close();	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
