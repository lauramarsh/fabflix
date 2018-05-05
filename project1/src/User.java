import java.util.*;

public class User {

    private final String username;
    private Map<Integer, Integer> cart = new HashMap<Integer, Integer>();

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
   
    public void addToCart(int movieId, int quantity) {
    	// add the specified quanity to the cart
    	// if < 0 remove item
    	if(quantity < 0) {
    		this.cart.remove(movieId);
    	}
    	else {
    		this.cart.replace(movieId, quantity);
    	}
    }
    
    public void removeFromCart(int movieId){
    	// - 1 to the quantity of the specified movie 
    	// If movie was not in the cart, set default quantity to 0 and then add 1
    	this.cart.remove(movieId);
    }
    
    public int getCount(int movieId){
    	// - 1 to the quantity of the specified movie 
    	// If movie was not in the cart, set default quantity to 0 and then add 1
    	return this.cart.get(movieId);
    }
    
    public Map<Integer, Integer> getCart(){
    	return this.cart;
    }
    
    public void clearCart() {
    	this.cart.clear();
    }
	
}