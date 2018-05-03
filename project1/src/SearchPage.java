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

@WebServlet(name = "search", urlPatterns = "/search")
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
        out.println("<head><title>Fabflix</title><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/></head>");
        out.println("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
        
        try {
        	
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		Statement statement = connection.createStatement();
    		
    		// Create search form 
    		out.println("<form class = Search ACTION= \"search\" METHOD = \"GET\">");
    		out.println("<div class=searchItem><div class=searchLabel><h3>Title</h3></div>");
    		out.println("<input type=\"text\" name = \"title\" placeholder=\"Title...\"></div>");
    		
    		out.println("<div class=searchItem><div class=searchLabel><h3>Year</h3></div>");
    		out.println("<input type=\"text\" name = \"year\" placeholder=\"Year...\"></div>");
    		
    		out.println("<div class=searchItem><div class=searchLabel><h3>Director</h3></div>");
    		out.println("<input type=\"text\" name = \"director\" placeholder=\"Director...\"></div>");
    		
    		out.println("<div class=searchItem><div class=searchLabel><h3>Star Name</h3></div>");
    		out.println("<input type=\"text\" name = \"star-name\" placeholder=\"Star Name...\"></div>");
    		
    		String title = request.getParameter("title"),  director = request.getParameter("director");
    		String year = request.getParameter("year"), star_name = request.getParameter("star-name");
    	
    		String url = request.getRequestURL() + "?" + request.getQueryString();
    		System.out.println(url);
    		out.println("<button type=\"button\" onclick=\"location.href='"+ request.getQueryString() + "'\"> Search</button>");

    		out.println("</form>");

    		//Build query string 
    		    		
    		StringBuilder query = new StringBuilder();
    		
    		query.append("select title, director, year, group_concat(distinct genres.name) as genre_list, group_concat(distinct stars.name) as stars_list, rating");
    		query.append(" from movies, genres_in_movies, genres, stars, stars_in_movies, ratings");
    		query.append(" where movies.id = genres_in_movies.movieId and genres_in_movies.genreId = genres.id");
    		query.append(" and movies.id = stars_in_movies.movieId and stars_in_movies.starId = stars.id");
    		query.append(" and movies.id = ratings.movieId");
    		
    		if(title.length() > 0) query.append(" and title like '%" + title + "%'");
    		if(director.length() > 0) query.append(" and director like '%" + director + "%'");
    		if(year.length() > 0) query.append(" and year = " +year);
    		
    		query.append(" group by movies.id, title, rating, year, director");
    		if(star_name.length() > 0) query.append(" having stars_list like '%" + star_name + "%'");
    		
    		System.out.print(query.toString());
    		
    		ResultSet resultSet = statement.executeQuery(query.toString());
    	
    		
            // Create a html <table>
            out.println("<table border>");

        	out.println("<body>");
    		out.println("<h1 class=\"block\">Movie List</h1>");
    		out.println("<table border class=\"block\">");
    		
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