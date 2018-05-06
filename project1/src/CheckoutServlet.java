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
        		+ "<title>fabflix checkout</title>"
        		+ "</head>");
        
        try {
        	
    		Class.forName("com.mysql.jdbc.Driver").newInstance();
    		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
    		Statement statement = connection.createStatement();
    		
    		// Query params
    		String first_name = request.getParameter("first-name"),  last_name = request.getParameter("last-name");
    		String card_num = request.getParameter("card-num"), exp_date = request.getParameter("exp-date");
    		
    		@SuppressWarnings("unchecked")
			Map<String, Integer> cart = (Map<String, Integer>) request.getSession().getAttribute("cart");
    	
    		StringBuilder query = new StringBuilder();
    	    		
    		query.append(" select * from creditcards where");
    		query.append(" id ='" + card_num + "'");
    		query.append(" and firstName = '" + first_name + "'");
    		query.append(" and lastName = '" + last_name + "'");
    		query.append(" and expiration = '" + exp_date + "'");    		
    	
    		
    		// execute query
    		ResultSet resultSet = statement.executeQuery(query.toString());
    		
    		if(resultSet.next() == false){
    			out.println("<body>");
        		out.println("<div class=\"nav-bar table__black\"><a  class =\"btn btn-warning\"  href = \"index.html\">home</a><a  class =\"btn btn-warning\"  href = \"cart\">cart</a><a  class =\"btn btn-warning\"  href = \"login.html\">log Out</a></div>");
        		
        		out.println("<h1> Error with payment. Unable to complete request.</h1>");
 	        }
    		else {
    			
    			int total =  0;
    			for(String item: cart.keySet()) {
    				for(int i = 0; i < cart.get(item); i++) {
    					
    					//customer id 
    					String idQuery = "select id from customers where firstName = '" + first_name + "' "
    							+  "and lastName = '" + last_name + "' "
    							+  "and ccId = '" + card_num + "' ";
    					
    					// execute query
    		    		ResultSet rs = statement.executeQuery(idQuery);
    					
    		    		rs.next();
    		    		String customerId = rs.getString("id");
    		    		
    		    		String insertQuery = "insert into sales (id, customerId, movieId, saleDate)" 
    		    			+ " values (default, '" + customerId + "', '" + item + "',CURDATE());";
    		    		
    		    		System.out.println(insertQuery);
    		    		
    		    		// execute query
    		    		statement.executeUpdate(insertQuery);
    		    		
    		    		out.println("<body>");
    	        		out.println("<div class = \"cartLinks\">");
    	        		out.println("<div class=\"title\"><h1> Thank you for your purchase!</h1></div>");
    	        		out.println("</body>");
    				}
    			}
    			out.println("<body>");
        		out.println("<div class = \"cartLinks\">");
        		out.println("<a  id = \"backLink\" class =\"btn btn-danger\"  href = \"/project1/cart\">Back to Cart</a>");
        		out.println("<a  class =\"btn btn-danger\"  href = \"login.html\">Log Out </a>");
    			
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