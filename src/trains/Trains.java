/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trains;

import java.util.ArrayList;
import java.util.Collections;
//import java.util.ArrayList;
import java.util.Random;


/**
 * Main runnable class for running the "Train" simulation
 * 
 * -Contains the static main(String[] args)
 * 
 * @author Eric Bourque
 */
public class Trains {
    
    /**
    * A locking object for the events buffer
    */
    public static final Object bufferLock = new Object();
    
    /**
    * An object based representation of a Petri network
    */
    public static PetriMap map;
    
    /**
    * Array of threads to simulate place activities 
    */
    public static Thread[] threads;
    
    /**
    * Array of intergers to count thread starts 
    */
    public static int[] threadCounters;
    
    /**
    * Array of sensitized transitions
    */
    public static ArrayList<Transition> sensitized = new ArrayList<>(); 
    
    /**
    * [SYNCHRONIZED]
    * Event buffer to know when activities are finished.
    */
    public static ArrayList<Transition> events = new ArrayList<>(); //Synch
    
    /**
    * Array of transitions that can be fired
    */
    public static ArrayList<Transition> crossed = new ArrayList<>();    
    
    /**
    * The matrix that links places to transitions
    */
    public static int[][] preMatrix; 
    
    /**
    * The matrix that links transitions to places
    */
    public static int[][] postMatrix;

    /**
    * Number of tokens in each place before the update
    */
    public static int[] preMarkings; //Initial zero  
    
    /**
    * Number of tokens in each place after the update
    */
    public static int[] postMarkings; 
        
    /**
     * Main loop for the Trains simulation
     * 
     * Creating a .bat file with this content will make the simulation run for 500 ticks and redirect
     * the output to output.txt inside the execution folder:
     * 
     * java -jar Trains.jar "greater than symbol" output.txt trains.ArgsTest 500
     * 
     * @param args Arguments for running this program. 
     * args[0] parameter for arguments selection, 
     * args[1] the numbers ticks to run this program for
     */
    public static void main(String[] args) {        
        System.out.println("Program Arguments:");
        for (String arg : args) {
            System.out.println("\t" + arg);
        }     
        System.out.println("\n\n");
        int maxTicks;
        try{
            maxTicks = Integer.valueOf(args[1]);
        }catch (Exception ex){
            maxTicks = 100;
        }
        int counter = 0;
        
        map = Parser.getPetriMap();
        threads = new PetriThread[map.places.size()];
        threadCounters = new int[map.places.size()];
        
        preMatrix = new int[map.places.size()][map.transitions.size()]; 
        postMatrix = new int[map.places.size()][map.transitions.size()];
        
        preMarkings  = new int[map.places.size()]; //Initial zero  
        postMarkings  = new int[map.places.size()];     
        
        initMatrices(map, preMatrix, postMatrix);          
        
        //The post markings start with the initial number of tokens in each place. This allows transitions to be sensitized at start. 
        for(Place p : map.places){
            postMarkings[p.getIndex()] = p.tokens;
        }
        //Main Looper
        while(true){                         
            try{    
                System.out.println("");
                System.out.println("------- Tick "+String.valueOf(counter)+ " -------");
                counter++;
                
                String s = "preMarkings:  ";
                for(int i : preMarkings){
                    s += String.valueOf(i) + ", ";
                }
                System.out.println(s);
                
                s = "postMarkings: ";
                for(int i : postMarkings){
                    s += String.valueOf(i) + ", ";
                }
                System.out.println(s);
                
                //Φ1 Pour chaque Place Pi du RDP faire
                checkAndStartActivities(threads, threadCounters, map, preMarkings, postMarkings, preMatrix, events); //si MarquagePOST(Pi) > MarquagePRE(Pi) alors lancer activité(Ai)
                PostMarkingsToPreMarkings(preMarkings, postMarkings); //MarquagePRE = MarquagePOST //nouveau marquage courant
                
                //Φ2
                sensitized = getSensitizedTransitionsRand(map, preMatrix, preMarkings); //Pour chaque Transi3on Tj du RDP vérifier si sensibilisée = f(PRE, marquagePRE)) et mefre
                                                                                        //dans liste LISTESENSIBILISEES
                                                                                        //Ranger dans ordre aléatoire LISTESENSIBILISEES                
                synchronized(bufferLock){
                    
                    s = "Sensitized: ";
                    s = sensitized.stream().map((t) -> t.name + ", ").reduce(s, String::concat);
                    System.out.println(s);
                    
                    s = "Events: ";
                    s = events.stream().map((t) -> t.name + ", ").reduce(s, String::concat);
                    System.out.println(s);                      
                    
                    for(Transition ts : sensitized){
                        if(transitionHasNoIncommingPlaces(map, ts, preMatrix)){
                            crossed.add(ts.clone());
                        }
                    }
                    
                    for(Transition te : events){ 
                        for(Transition ts : sensitized){ //Φ3: Pour chaque Tj LISTESENSIBILISEES faire {
                            if(te.name.equals(ts.name)){ //Si ET(LISTEEVENTS(Tj)) alors {
                                ejectTokensFromPlaces(map, ts, preMatrix, preMarkings); //MarquagePRE = MarquagePRE – PRE(Ti, *)
                                crossed.add(ts.clone()); //mettre Ti dans LISTEFRANCHIES
                                break;
                            }
                        }
                        for(Transition ts : crossed){ //extraire Ti de LISTESENSIBILISEES 
                            for(int i = 0; i < sensitized.size(); i++){
                                if(ts.name.equals(sensitized.get(i).name)){
                                    sensitized.remove(i);
                                    i--;
                                }
                            }
                        }
                        sensitized = getSensitizedTransitionsRand(map, preMatrix, preMarkings); //Mettre à jour LISTESENSIBILISEES
                    }
                    
                    for(Transition t : crossed){ //Pour chaque Tj LISTEFRANCHIES faire
                        for(int i = 0; i < events.size(); i++){
                            if(t.name.equals(events.get(i).name)){
                                events.remove(i);
                                i--;
                            }
                        }
                    }                    
                }
                
                PreMarkingsToPostMarkings(preMarkings, postMarkings); //Φ4: MarquagePOST = MarquagePRE //nouveau marquage courant intermédiaire
                
                s = "Crossed: ";
                s = crossed.stream().map((t) -> t.name + ", ").reduce(s, String::concat);
                System.out.println(s);  
                //System.out.println("\n");
                
                for(Transition t : crossed){ //Pour chaque Tj LISTEFRANCHIES faire
                    insertTokensFromTransitions(map, t, postMatrix, postMarkings);//MarquagePOST = MarquagePOST + POST(Ti, *)
                }
                crossed.clear(); //extraire Ti de LISTEFRANCHIES
                
                Thread.sleep(10); //Tick time = 1s
                
                if(counter > maxTicks) break;
            }catch(Exception e){
                System.out.println(e.toString());                
            }
        }
        
        Util.printStatistics(threadCounters, map);       
    }    
    
    /**
    * Initialises the pre and post matrices
    *
    * @param  map object based representation of a Petri network
    * @param  preMatrix the matrix that links places to transitions
    * @param  postMatrix the matrix that links transitions to places    
    */    
    public static void initMatrices(PetriMap map, int[][] preMatrix, int[][] postMatrix){        
        for(Arc arc : map.arcs){            
            if(arc.in.contains("P")){ //P -> T               
                preMatrix[map.getPlace(arc.in).getIndex()][map.getTrans(arc.out).getIndex()] += arc.weight;
                
            }else if(arc.in.contains("T")){
                postMatrix[map.getPlace(arc.out).getIndex()][map.getTrans(arc.in).getIndex()] += arc.weight;
            }
        }
        Util.printMatrices(map, preMatrix, postMatrix);        
    }   
    
    /**
    * Scans the petri network and starts an activity for each place that
    * has a post-marking greater than it's pre-marking  
    * -The activity only start if the pre-marking was equal to zero (empty)
    *
    * @param  threads array of threads  
    * @param  counters array of counters 
    * @param  map object based representation of a Petri network
    * @param  preMarkings number of tokens in each place before the update
    * @param  postMarkings number of tokens in each place after the update
    * @param  preMatrix the matrix that links places to transitions
    * @param  events the events that will be returned by the threads    
    */  
    public static void checkAndStartActivities(Thread[] threads, int[] counters, PetriMap map, int[] preMarkings, int[] postMarkings, int[][] preMatrix, ArrayList<Transition> events){                
        for(Place p : map.places){           
            if(postMarkings[p.getIndex()] > preMarkings[p.getIndex()]){// && preMarkings[p.getIndex()] == 0){                
                threads[p.getIndex()] = new PetriThread(p, map, preMatrix, events);
                threads[p.getIndex()].start();   
                counters[p.getIndex()] ++;
            }
        }           
    }
    
    /**
    * Copies the post markings into the pre markings
    *    
    * @param  preMarkings number of tokens in each place before the update
    * @param  postMarkings number of tokens in each place after the update     
    */  
    public static void PostMarkingsToPreMarkings(int[] preMarkings, int[] postMarkings){
        for(int i = 0; i < preMarkings.length; i++){
            preMarkings[i] = postMarkings[i];
        }
    }
    
    /**
    * Copies the pre markings into the post markings
    *    
    * @param  preMarkings number of tokens in each place before the update
    * @param  postMarkings number of tokens in each place after the update    
    */ 
    public static void PreMarkingsToPostMarkings(int[] preMarkings, int[] postMarkings){
        for(int i = 0; i < preMarkings.length; i++){
            postMarkings[i] = preMarkings[i];
        }
    }
    
    /**
    * Returns a shuffled list of all transitions that are sensitized
    * <p>
    * Transition is sensitized if each of it's corresponding places tokens (preMarkings)
    * is greater or equal to the values defined by the preMatrix
    * 
    * @param  map object based representation of a Petri network
    * @param  preMatrix the matrix that links places to transitions    
    * @param  preMarkings number of tokens in each place before the update    
    * @return a list of sensitized transitions
    */ 
    public static ArrayList<Transition> getSensitizedTransitionsRand(PetriMap map, int[][] preMatrix, int[] preMarkings){
        ArrayList<Transition> sensT = new ArrayList<>();
        Boolean sens;
        for(Transition t : map.transitions){
            sens = true;
            for(int i = 0; i < map.places.size(); i++){
                if(preMarkings[i] < preMatrix[i][t.getIndex()]){
                    sens = false;
                }
            }
            if(sens) sensT.add(t);
        }
        long seed = System.nanoTime();
        Collections.shuffle(sensT, new Random(seed));        
        return sensT;
    }   
    
    /**
    * Pulls tokens out of the pre-markings list.
    * The amount of tokens to eject is defined by the preMatrix
    * 
    * @param  map object based representation of a Petri network
    * @param  transition the transition that will consume tokens    
    * @param  preMatrix the matrix that links places to transitions    
    * @param  preMarkings number of tokens in each place before the update      
    */ 
    public static void ejectTokensFromPlaces(PetriMap map, Transition transition, int[][] preMatrix, int[] preMarkings){
        for(Place p : map.places){
            
            if(preMatrix[p.getIndex()][transition.getIndex()] > 0){
                if(preMarkings[p.getIndex()] > 0){
                    preMarkings[p.getIndex()] --;
                }
            }
        }
    }
    
    /**
    * Pushes tokens out of the post-markings list.
    * The amount of tokens to eject is defined by the postMatrix
    * 
    * @param  map object based representation of a Petri network
    * @param  transition the transition that will eject tokens    
    * @param  postMatrix the matrix that links transitions to places    
    * @param  postMarkings number of tokens in each place after the update      
    */ 
    public static void insertTokensFromTransitions(PetriMap map, Transition transition, int[][] postMatrix, int[] postMarkings){
        
        String s = "Inserted tokens: ";
        
        for(Place p : map.places){  
            int nbTokens = postMatrix[p.getIndex()][transition.getIndex()];
            
            if(nbTokens > 0 && p.capacity >= nbTokens + postMarkings[p.getIndex()]){
                postMarkings[p.getIndex()] += nbTokens;
                s += p.name;
            }
        }
        if(!s.equals("Inserted tokens: "))
            System.out.println(s);  
    }
    
    /**
    * Checks the matrices for places that go into it. 
    * 
    * @param  map object based representation of a Petri network
    * @param  transition the transition to check  
    * @param  preMatrix the matrix that links transitions to places         
    * @return true if no places go IN the transition
    */ 
    public static Boolean transitionHasNoIncommingPlaces(PetriMap map, Transition transition, int[][] preMatrix){        
        for(int i = 0; i < map.places.size(); i++){
            if(preMatrix[i][transition.getIndex()] > 0) return false;
        }        
        return true;
    }
}
