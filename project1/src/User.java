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
    

}