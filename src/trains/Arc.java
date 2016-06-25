/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trains;

/**
 * This class is an object based representation of a Petri network arc.
 * @author Eric Bourque
 */
public class Arc {
    
    /**
     * The type of arc (Not in use)
     */
    Type type;
    /**
     * The amount of tokens that links the input Place/Transition
     * to the output Place/Transition
     */
    int weight = 1;
    /**
     * The name of the input Arc/Transition
     */
    String in;
    /**
     * The name of the output Arc/Transition
     */
    String out;
    
    /**
     * Type of arc
     * 
     * -Normal: Standard Petri Network Arc
     * -Inhibitor: Arc that stops a transition from firing if it's input place contains tokens
     * -Test: Arc that mimics the normal arc without actually firing tokens
     */
    public enum Type {
        Normal, Inhibitor, Test 
    }   
    
    /**
     * Default constructor for XML initialization
     */
    public Arc(){        
        type = Type.Normal;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
