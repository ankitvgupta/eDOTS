package edots.models;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by jfang on 1/6/15.
 */
public class Project {
    private ArrayList<String> medications= new ArrayList<String>();
    private int stages = medications.size();
    private String name;

    public Project(){

    }

    // For testing only
    public Project(String n){
        name=n;
        medications = new ArrayList<String>(Arrays.asList("Med A", "Med B"));

    }

    public void setMedications(ArrayList<String> m){
        medications=m;
    }

   public void setName(String s){
       name=s;
   }

    public ArrayList<String> getMedications(){
        return medications;
    }

    public int getStages(){
        return stages;
    }

    public String getName(){
        return name;
    }


}
