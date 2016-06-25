/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trains;

/**
 * This class in an object based representation of a Petri network transition
 * 
 * @author Eric Bourque
 */
public class Transition {
    
    /**
     * The name of the Transition
     */
    String name;   
    /**
     * The timeMode for this transition (Not in use)
     */
    TimeMode timeMode;
    /**
     * The time in ticks before the tokens are ejected (Not in use)
     */
    int timeToFire = 0;
    /**
     * The number of token to fire when this transition is crossed
     */
    int tokensFired = 1;    
    
    /**
     * TimeMode 
     * 
     * -Immediate: Tokens are fired asap
     * -Random: A random time is selected before the tokens are fired
     * -Deterministic: Value determined by the "timeToFire" attribute
     */
    public enum TimeMode {
        Immediate, Random, Deterministic 
    }   
    
    /**
     * Gets the index of this transision
     * @return The index of this transition
     */
    public int getIndex(){
        return Integer.parseInt(name.replace("T",""));
    }
    
    /**\
     * Constructor for XML initialisation
     */
    public Transition(){
        name = "T1";   
        timeMode = TimeMode.Immediate;
    }    

    /**
     * Gets the name of this transition
     * @return The name of this transition
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this transition
     * @param name The name that this transition should take
     */
    public void setName(String name) {
        this.name = name;
    }

    public TimeMode getTimeMode() {
        return timeMode;
    }

    public void setTimeMode(TimeMode timeMode) {
        this.timeMode = timeMode;
    }

    public int getTimeToFire() {
        return timeToFire;
    }

    public void setTimeToFire(int timeToFire) {
        this.timeToFire = timeToFire;
    }

    /**
     * Get the amount of tokens that should be ejected when crossed
     * @return The amount of tokens to fire
     */
    public int getTokensFired() {
        return tokensFired;
    }

    public void setTokensFired(int tokensFired) {
        this.tokensFired = tokensFired;
    }
    
    /**
     * Returns an identical copy of this Transition object
     * @return object based representation of a Petri network trnasition
     */ 
    public Transition clone() {
        Transition t = new Transition();
        t.name = name;
        t.timeMode = timeMode;
        t.timeToFire = timeToFire;
        t.tokensFired = tokensFired;
        return t;
    }
    
}
