

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class metadata
 */
@WebServlet("/metadata")
public class metadata extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public metadata() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        
        PrintWriter out = response.getWriter();   
        out.println("<html>");
        out.println("<head>"
        		+ "<meta charset=\"utf-8\">"
        		+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">"
        		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\"/>"
        		+ "<link rel=\"stylesheet\" type=\"text/css\" href=\"bootstrap.min.css\"/>"
        		+ "<title>Metadata Results</title>"
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
    		
    		// Display database name
    		out.println("<body>");
    		out.println("<div class=\"nav-bar table__black\"><a  class =\"btn btn-warning\"  href = \"employeeIndex.html\">home</a><a class =\"btn btn-warning\" href = \"employeelogin.html\">log Out</a></div>");
    		out.println("<div class=\"title\">");
    		out.println("<h1>MovieDB MetaData</h1>");
    		out.println("</div>");
    		
    		// Display db table names in the h3 tag, collumn info in the h5 tag
    		DatabaseMetaData metadata = connection.getMetaData();
    		ResultSet tablers = metadata.getTables(null, null, "%", null);
    		out.println("<div class=\"block block__fat\">");
    		
    		while (tablers.next()) {
    			ResultSet columnrs = metadata.getColumns(null, null, tablers.getString(3), null);
    			
    			out.println("<h3>" + tablers.getString(3) + "</h3>");
    			while (columnrs.next()) {
    				out.println("<h5>" + columnrs.getString("COLUMN_NAME") + "  " + columnrs.getString("TYPE_NAME") + "</h5>");
    			}
    			
    		}
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
