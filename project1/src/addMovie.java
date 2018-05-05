import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 */
@WebServlet("/addmovie")
public class addMovie extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public addMovie() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

        
        //Get User shopping cart info
        HttpSession session = request.getSession(); // Get a instance of current session on the request
        synchronized(session) {
        
	        @SuppressWarnings("unchecked")
			Map<String, Integer> cart = (Map<String, Integer>) session.getAttribute("cart");
	        /*if(cart == null) {
	        	cart = new HashMap<String, Integer>();
	        	session.setAttribute("cart", cart);
	        }*/
	        
	        String movieId = request.getParameter("movie-id");       
	        
	        if(movieId != null){
	        	cart.put(movieId, cart.getOrDefault(movieId, 0)+1);
	        }
	        
	        System.out.println("CART " + cart);
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
