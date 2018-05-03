import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		
		String loginUser = "root";
        String loginPasswd = "pissoff";
        String loginUrl = "jdbc:mysql://localhost:3306/moviedb";
        
        
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
        	
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		Statement statement = connection.createStatement();
    		
    		
    		String title = request.getParameter("title"),  director = request.getParameter("director");
    		String year = request.getParameter("year"), star_name = request.getParameter("star-name");

    		//Build query string 
    		    		
    		StringBuilder query = new StringBuilder();
    		StringBuilder queryDesc = new StringBuilder();
    		
    		queryDesc.append("Search Results");
    		
    		query.append(" select title, director, year, group_concat(distinct genres.name) as genre_list, group_concat(distinct stars.name) as stars_list, rating");
    		query.append(" from movies, genres_in_movies, genres, stars, stars_in_movies, ratings");
    		query.append(" where movies.id = genres_in_movies.movieId and genres_in_movies.genreId = genres.id");
    		query.append(" and movies.id = stars_in_movies.movieId and stars_in_movies.starId = stars.id");
    		query.append(" and movies.id = ratings.movieId");
    		
    		
    		if(year.length() > 0)
    		{
    			query.append(" and year = " +year);
    		}
    		if(director.length() > 0) 
    		{
    			query.append(" and director like '%" + director + "%'");
    		}
    		
    		if(title.length() > 0)
    		{
    			query.append(" and title like '%" + title + "%'");
    		}
    		
    		query.append(" group by movies.id, title, rating, year, director");
    		
    		if(star_name.length() > 0) 
    		{
    			query.append(" having stars_list like '%" + star_name + "%'");
    		}

    		// execute query
    		ResultSet resultSet = statement.executeQuery(query.toString());
    		
    		out.println("<body>");
    		out.println("<div class=\"title\">");
    		out.println("<h1>" + queryDesc + " </h1>");
    		out.println("</div>");
    		
    		out.println("<div class=\"block\">");
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