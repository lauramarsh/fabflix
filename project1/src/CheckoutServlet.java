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

import com.google.gson.JsonObject;

@WebServlet(name = "checkoutPage", urlPatterns = "/checkoutPage")
public class CheckoutServlet extends HttpServlet{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @see HttpServlet#HttpServlet()
     */
	public CheckoutServlet() {
		super();
	    // TODO Auto-generated constructor stub
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
        response.setContentType("text/html");
        
        PrintWriter out = response.getWriter();   
        out.println("<html>");
        out.println("<head>"
        		+ "<meta charset=\"utf-8\">"
        		+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">"
        		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/>"
        		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"bootstrap.min.css\"/>"
        		+ "<title>fabflix checkout</title>"
        		+ "</head>");
        
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
    		
    		Statement statement = connection.createStatement();
    		
    		// Query params
    		String first_name = request.getParameter("first-name"),  last_name = request.getParameter("last-name");
    		String card_num = request.getParameter("card-num"), exp_date = request.getParameter("exp-date");
    		
    		@SuppressWarnings("unchecked")
			Map<String, Integer> cart = (Map<String, Integer>) request.getSession().getAttribute("cart");
    	
    		StringBuilder query = new StringBuilder();
    	    		
    		query.append(" select * from creditcards where");
    		query.append(" id = ? ");
    		query.append(" and firstName = ? ");
    		query.append(" and lastName = ? ");
    		query.append(" and expiration = ? ");    
    		 
    		// create prepared statement
    		PreparedStatement preparedStatement =
    		        connection.prepareStatement(query.toString());
 
    		preparedStatement.setString(1, card_num);
    		preparedStatement.setString(2, first_name);
    		preparedStatement.setString(3, last_name);
    		preparedStatement.setString(4, exp_date);
    		
    		// execute query
    		ResultSet resultSet = preparedStatement.executeQuery();
    		
    		out.println("<body>");
    		out.println("<div class=\"nav-bar table__black\"><a  class \"btn btn-warning\"  href = \"index.html\">home</a><a  class \"btn btn-warning\"  href = \"cart\">cart</a><a  class \"btn btn-warning\"  href = \"login.html\">log Out</a></div>");
    		
    		boolean success = false;
    		String customerId =" ";
    		
    		if(resultSet.next() == false){
        		out.println("<h1 class=\"title\"> Error with payment. Unable to complete request.</h1>");
 	        }
    		else {

    			success = true;
    			for(String item: cart.keySet()) {
    				for(int i = 0; i < cart.get(item); i++) {
    					
    					//customer id 
    					String idQuery = "select id from customers where firstName = '" + first_name + "' "
    							+  "and lastName = '" + last_name + "' "
    							+  "and ccId = '" + card_num + "' ";
    					
    					// execute query
    		    		ResultSet rs = statement.executeQuery(idQuery);
    					
    		    		rs.next();
    		    		customerId = rs.getString("id");
    		    		
    		    		String insertQuery = "insert into sales (id, customerId, movieId, saleDate)" 
    		    			+ " values (default, '" + customerId + "', '" + item + "',CURDATE());";
    		    		    		    		
    		    		// execute query
    		    		statement.executeUpdate(insertQuery);
    		    	
    		    		
    				}
    				
	        		
    			}
    			
    			if(success)
    			{
    				
            		
            		String saleQuery = "select id from sales where customerId ='" + customerId  + "' and saleDate = CURDATE()";
            		ResultSet rs = statement.executeQuery(saleQuery);
            		rs.next();
            		
            		out.println("<div class=\"title\"><h1> Thank you for your purchase!</h1></div>");
            		out.println("<div class=\"confirm\"><h3>Order Details:</h3></div>");
            		
            		
		    		String saleId = rs.getString("id");
            		
        			for(Map.Entry<String, Integer> e: cart.entrySet()) {
        					String movieId = e.getKey();
        					Integer amount = e.getValue();
        					
        					String movieQuery = "select title from movies where id ='" + movieId + "';";
        					
        					rs = statement.executeQuery(movieQuery);
        					rs.next();
        		    		String title = rs.getString("title");
        		    		
        		    		String order = "Movie: " + title + "&nbsp;&nbsp;&nbsp;&nbsp; Quantity: " + amount;
        	        		out.println("<div class=\"confirm\"><h3>" + order + "</h3></div>");
        					
        				}
        			out.println("<div class=\"confirm\"><h3>Sale ID: "+ saleId +"</h3></div>");
    				
    			}
    			
    	
    		}
    	
    
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