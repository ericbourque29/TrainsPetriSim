/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trains;

import java.util.ArrayList;
import java.util.List;

/**
 * Object based representation of a Petri network (map)
 * @author Eric Bourque
 */
public class PetriMap {
    List<Transition> transitions = new ArrayList<>();
    List<Place> places = new ArrayList<>();
    List<Arc> arcs = new ArrayList<>();
    
    /**
     * Contructor for XML initialization
     */
    public PetriMap(){
        transitions.add(new Transition());
        transitions.add(new Transition());           
        places.add(new Place());
        places.add(new Place());
        arcs.add(new Arc());
        arcs.add(new Arc());
        arcs.add(new Arc());
        arcs.get(0).in = "T0";
        arcs.get(0).in = "T1";
        arcs.get(0).out = "P0";
        arcs.get(0).out = "P1";
    }
    
    /**
     * Gets the transition located at a particular index
     * @param index
     * @return 
     */
    public Transition getTrans(int index){
        return transitions.get(index);
    }
    
    /**
     * Gets the transition that matches a particular name
     * @param name
     * @return 
     */
    public Transition getTrans(String name){
        for(Transition t : transitions){
            if(t.name == name)
                return t;
        }
        return null;
    }
    
    /**
     * Gets the Place located at a particular index
     * @param index
     * @return 
     */
    public Place getPlace(int index){
        return places.get(index);
    }    
    /**
     * Gets the place that matches a particular name
     * @param name
     * @return 
     */
    public Place getPlace(String name){
        for(Place p : places){
            if(p.name == name)
                return p;
        }
        return null;
    }
}
