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

@WebServlet(name = "SearchPage", urlPatterns = "/search")
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
    		out.println("<form ACTION= \"search\" METHOD = \"GET\">");
    		out.println("<input type=\"text\" name = \"query\" placeholder=\"Search...\">");
    		out.print("<select name=\"search by\"><option value=\"title\">Title</option>");
    		out.print("<option value=\"year\">Year</option><option value=\"director\">Director</option>");
    		out.print("<option value=\"star name\">Star Name</option></select>");
    		out.println("<button type=\"submit\"><i class=\"fa fa-search\"></i></button>");
    		out.println("</form>");
    		
    		String search = request.getParameter("query");
    		
    		String query = "SELECT * from movies where title like '%" + search + "'";
    		 // Perform the query
    		System.out.println(query);
            ResultSet rs = statement.executeQuery(query);
    		
            // Create a html <table>
            out.println("<table border>");

            // Iterate through each row of rs and create a table row <tr>
            out.println("<tr><td>ID</td><td>Name</td></tr>");
            while (rs.next()) {
                String ID = rs.getString("ID");
                String title = rs.getString("title");
                out.println(String.format("<tr><td>%s</td><td>%s</td></tr>", ID, title));
            }
            out.println("</table>");
    	
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
