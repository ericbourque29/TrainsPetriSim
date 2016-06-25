/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trains;

import com.thoughtworks.xstream.XStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class handles XML related functions for loading a Perti Network
 * 
 * XML is obtained and created using the popular XStream serializer 
 * 
 * @author Eric Bourque
 */
public class Parser {
    
    /**
     * Loads a PetriMap object named PetriMap.xml from the executing directory.
     * 
     * If no map is found, a map will be created with some default values.
     * @return PetriMap loaded from XML
     */
    public static PetriMap getPetriMap(){
        XStream xstream = new XStream();
        String xml;
        PetriMap map = null;
        try{
            byte[] b;
            b = Files.readAllBytes(Paths.get("./PetriMap.xml"));
            xml = new String(b);
            map = (PetriMap)xstream.fromXML(xml); 
            System.out.println("Arcs: "+map.arcs.size());            
            System.out.println("Places: "+map.places.size());
            for(Place p : map.places){
                System.out.println("\t\t"+p.name);
            }
            System.out.println("Transitions: "+map.transitions.size());
            for(Transition t : map.transitions){
                System.out.println("\t\t"+t.name);
            }
        }catch(Exception e){
            System.out.println(e.toString());
            try{  
                map = new PetriMap();                
                xml = xstream.toXML(map);
                System.out.println(xml);                 
                Files.write(Paths.get("./PetriMap.xml"), xml.getBytes());
            }catch(Exception ex){
                System.out.println(ex.toString());
                ex.printStackTrace();
            }
        }
        return map;
    }
}
