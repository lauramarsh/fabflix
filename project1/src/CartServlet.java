
import java.util.*;
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
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * Servlet implementation class CartServlet
 */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CartServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		synchronized(session) {

			
	        response.setContentType("text/html");
	        PrintWriter out = response.getWriter();
	
	        // Get User Cart
	        Map<String, Integer> userCart = (Map<String, Integer>) request.getSession().getAttribute("cart");
	        if(userCart.size() == 0) {
	        	System.out.println("NOTHING INSIDE____");
	        }
	        
	        // Begin html output
	        out.println("<html>");
	        out.println("<head><title>Fabflix Movie Page</title><link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/><link rel=\"stylesheet\" type=\"text/css\" href=\"bootstrap.min.css\"/></head>");
	        out.println("<body>");
    		out.println("<div class=\"nav-bar table__black\"><a  class =\"btn btn-warning\"  href = \"index.html\">home</a><a  class =\"btn btn-warning\"  href = \"login.html\">log Out</a></div>");
	        
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
	    	
	    		out.println("<div class=\"block block__thin\">");
	    		out.println("<div class=\"title\"><h2>Cart</h2></div>");
	    		
	    		if(userCart.isEmpty()) {
	    			out.println("<h3 class=\"title\">Cart is empty!</h3>");
	    		}else {
	    			
	    			// Checkout Button
		    		out.println("<a  class =\"btn btn-danger\"  href = \"checkout.html\">Check Out</a>");
		    		
		    		// Cart is not empty
		    		out.println("<div class=\"cart\">");
		    		out.println("<table class=\"table table__black\">");
		    		
		    		out.println("<thead>");
		    		out.println("<tr><th></th><th>Title</th><th>Quantity</th></tr>");
		    		out.println("</thead>");
		    		
		    		out.println("<tbody>");
		    		
	    			int indexCount = 0;
		    		for (Map.Entry<String, Integer> entry: userCart.entrySet()) {
		    			String movieID = entry.getKey();
		    			int movieQuant = entry.getValue();
		    			
		    			// Query 
		        		String query = "select title from movies where id='" + movieID + "';";
		        		// ResultSet should be 1 movie, resultSet.next() is null
		        		PreparedStatement ps = connection.prepareStatement(query);
		        		ResultSet resultSet = ps.executeQuery();
		        		
		        		String cartMovieTitle = "";
		        		while(resultSet.next()) {
		        			cartMovieTitle = resultSet.getString("title");
		        		}
		        		out.println("<tr><td><img src=\"GenericMoviePoster.jpg\" alt=\"\" border=3 height=200 width=150></img></td>"
		    					+ "<td>" + cartMovieTitle + "</td>"
		    					+ "<td class=\"quantity\">"
		    					+ "<input class=\"quantInput\" type=\"text\" name=\"name\" value=\"" + Integer.toString(movieQuant) + "\">"
		    					+ "<input class=\"movieId\" type=\"hidden\" value=\""+ movieID + "\">"
		    					+ "<button type=\"button\" name=\"" + Integer.toString(indexCount) + "\" class=\"btn btn-warning btn--plus\" onclick=\"handleEdit(this)\">edit</button>"
		    					+ "<button class=\"btn btn-danger btn--plus\" type=\"button\" name=\"" + Integer.toString(indexCount) + "\" onclick=\"handleDelete(this)\">X</button>"
		    					+ "</td></tr>");
		        		
		        		resultSet.close();
		        		indexCount++;
		    		}
	    		}
	    		
	    		out.println("</tbody>");

	    		out.println("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>");
	    		out.println("<script src=\"./addMovie.js\"></script>");
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
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
