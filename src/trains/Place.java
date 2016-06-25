/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trains;

/**
 * This class in an object based representation of a Petri network place
 * @author Eric Bourque
 */
public class Place {
    /**
     * Name of the place
     */
    String name;  
    /**
     * Tokens contained in this place
     */
    int tokens = 0;
    /**
     * Max allowed token capacity for this place
     */
    int capacity = 1;    
    
    /**
     * Default constructor for XML initialization
     */
    public Place(){
        name = "P0";          
    }    
    /**
     * Gets the index of this place
     * @return The index of this place
     */
    public int getIndex(){
        return Integer.parseInt(name.replace("P",""));
    }
    /**
     * Gets the name of this place
     * @return The name of this place
     */
    public String getName() {
        return name;
    }
    /**
     * Sets the name of this place
     * @param name The name that this place should take
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the ammount of tokens contained in this place
     * @return number of tokens
     */
    public int getTokens() {
        return tokens;
    }

    /**
     * Sets the ammount of tokens contained in this place
     * @param tokens 
     */
    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    /**
     * Gets the token capacity of this place
     * @return number of Max tokens
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the token capacity of this place
     * @param capacity 
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }    
    
    /**
     * Returns an identical copy of this Place object
     * @return object based representation of a Petri network place
     */    
    public Place clone(){        
        Place p = new Place();       
        p.capacity = capacity;
        p.name = name;
        p.tokens = tokens;
        return p;
    }
}
