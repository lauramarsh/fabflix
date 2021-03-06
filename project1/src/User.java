import java.util.*;

public class User {

    private final String username;
    private Map<String, Integer> cart = new HashMap<String, Integer>();

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
   
    public void addToCart(String movieId, int quantity) {
    	// add the specified quanity to the cart
    	// if < 0 remove item
    	if(quantity < 0) {
    		this.cart.remove(movieId);
    	}
    	else {
    		this.cart.replace(movieId, quantity);
    	}
    }
    
    public void removeFromCart(String movieId){
    	// - 1 to the quantity of the specified movie 
    	// If movie was not in the cart, set default quantity to 0 and then add 1
    	this.cart.remove(movieId);
    }
    
    public Map<String, Integer> getCart(){
    	return this.cart;
    }
    
    public void clearCart() {
    	this.cart.clear();
    }
	
}