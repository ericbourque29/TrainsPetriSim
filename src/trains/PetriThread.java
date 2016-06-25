/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trains;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Custom runnable object to simulate the behavior of a Petri Network activity.
 * 
 * The thread initializes with a Place object and the PreMatrix (matrix that links Places to Transitions).  
 * <p>
 * When the thread is finished, one of the linked Transitions is randomly 
 * selected (and printed) and inserted into the eventsBuffer array
 * 
 * This class extends java.lang.Thread
 * @author Eric Bourque
 */
public class PetriThread extends Thread {

  /**
   * The Place that this thread will be simulating an activity for
   */
  Place place;
  /**
   * An array to hold the possible transitions for token injections
   */
  ArrayList<Transition> select = new ArrayList<>();
  /**
   * [Syncronized]
   * Buffer to hold recently selected Transitions
   */
  ArrayList<Transition> eventsBuffer = new ArrayList<>();

  /**
   * Customised thread constructor that pre-loads all transitions out-linked to
   * the place parameter using the preMatrix.
   * 
   * The eventsBuffer is where the result of this thread will be outputed
   * 
   * @param place The Place that this thread will be simulating an activity for
   * @param map object based representation of a Petri network
   * @param preMatrix the matrix that links places to transitions
   * @param eventsBuffer Buffer to hold recently selected Transitions
   */
  public PetriThread(Place place, PetriMap map, int[][] preMatrix, ArrayList<Transition> eventsBuffer){
        try{
            this.place = place.clone();
            this.eventsBuffer = eventsBuffer;
            String s = "Thread "+ place.name + " [INIT: ";
            for(int i = 0; i < map.transitions.size(); i++){
                if(preMatrix[place.getIndex()][i] > 0){
                    select.add(map.getTrans(i).clone());
                    s += map.getTrans(i).name + ", ";
                }
            }      
            System.out.println(s+"]");
        }catch(Exception ex){
            System.out.println(ex.toString());
        }
  }
  
  /**
   * Runs a sub-program that sleeps for 10 ms and then randomly selects one of the output transitions
   * and puts in into the synchronized event buffer before exiting.
   * 
   * -Information on the selected transition is printed befor return
   */
  @Override
  public void run(){ //TODO
    try {      
        //System.out.println(place.name + " [Start]");
        Thread.sleep(10);    
        
        //TODO: make sure transition is sensitized
        long seed = System.nanoTime();
        Collections.shuffle(select, new Random(seed));
        
        if(select.size() > 0){
            synchronized (Trains.bufferLock){
                eventsBuffer.add(select.get(0));
                System.out.println("Thread "+place.name + " [Finished, selected: "+select.get(0).name+"]");    
            }
        }
    } catch(InterruptedException e) {
        System.out.println("Thread "+place.name + " [Interrupted]");      
    }
  }
}
