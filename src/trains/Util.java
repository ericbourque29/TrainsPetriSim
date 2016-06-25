/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trains;

/**
 * Utilities class for printing data
 * 
 * @author Eric Bourque
 */
public class Util {
    
    /**
     * Prints the contents of the pre and post Matrix
     * 
     * @param map
     * @param preMatrix
     * @param postMatrix 
     */
    public static void printMatrices(PetriMap map, int[][] preMatrix, int[][] postMatrix){
        System.out.println("Pre-Matrix: ");        
        for(int i = 0; i < map.places.size(); i++)
        {
            for(int j = 0; j < map.transitions.size(); j++){
                
                System.out.print(preMatrix[i][j]+"  ");
            }
            System.out.println("");
        }
        
        System.out.println("Post-Matrix: ");
        for(int i = 0; i < map.places.size(); i++)
        {
            for(int j = 0; j < map.transitions.size(); j++){
                
                System.out.print(postMatrix[i][j]+"  ");
            }
            System.out.println("");
        }      
    }
    
    /**
     * Prints statistical data on fired activities
     * 
     * @param counters array to count activities fired
     * @param map object based representation of a Petri network
     */
    public static void printStatistics(int[] counters, PetriMap map){
        System.out.println("--------------------");
        System.out.println("---- STATISTICS ----");
        System.out.println("--------------------");
        System.out.println("");
        
        float total = 0;
        
        for(int i = 0; i < counters.length; i++){
            System.out.println("Nb starts for " + map.getPlace(i).name + " : " + counters[i]);
            total += counters[i];
        }
        System.out.println("");
        float delta = 100/total;
        for(int i = 0; i < counters.length; i++){
            System.out.println(String.format("Percentage for " + map.getPlace(i).name + " : %1$.2f", ((float)counters[i]*delta))+"%");
            total += counters[i];
        }        
    }   
    
}
