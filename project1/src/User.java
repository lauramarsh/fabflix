import java.util.*;

public class User {

    private final String username;
    private Map<String, String> cart = new HashMap<String, String>();

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
    
    public void addMovie(String movie, int quanity) {
        //do sum sql stuff to add a movie to the users cart
    }
    
    public void increaseQuanity(String movie, int quanity) {
        //do sum sql stuff to increase the quantity meybe 
    }
}