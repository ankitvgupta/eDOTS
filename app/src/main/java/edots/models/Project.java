package edots.models;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author JN
 * @since 2015-01-06
 *
 *
 */
public class Project extends Saveable{
    // TODO: consistent capitalization of all class variables
    private String Id;
    private ArrayList<String> medications = new ArrayList<String>();
    private ArrayList<String> types = new ArrayList<String>();
    private String name;


    // For testing only
    public Project() {
        try {
            Random r = new Random();
            int num = r.nextInt(100);
            Id = "5";
            name = Integer.toString(num);
            medications = new ArrayList<String>();
            medications.add("Med 1");
            medications.add("Med 2");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param JSONString a JSON object represeting the project
     */
    public Project (String JSONString){
        try {
            JSONObject n = new JSONObject(JSONString);
            Id = n.get("projectId").toString();
            name = n.get("name").toString();
            JSONArray temp = new JSONArray(n.get("medications").toString());
            medications = new ArrayList<String>();
            for (int i = 0; i < temp.length(); i++){
                medications.add(temp.get(i).toString());
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param i project id
     * @param n name of the project
     */
    public Project(String i, String n){
        Id = i;
        name = n;
    }

    @Override
    /**
     * @return a JSON Serialization of the Project object
     *
     */
    public String toString(){
        JSONObject temp = new JSONObject();
        try {
            temp.put("projectId", getId());
            JSONArray version = new JSONArray(getMedications());
            temp.put("medications", version);
            JSONArray types = new JSONArray(getTypes());
            temp.put("types", types);
            temp.put("name", getName());
        } catch (JSONException e) {
            Log.v("JSON Exception", "Found a JSON Exception");
            e.printStackTrace();
        }
        return temp.toString();
    }

    public String getId(){
        return Id;
    }

    public void setId(String id){
        Id = id;
    }

    public void setMedications(ArrayList<String> m){
        medications = m;
    }

    public void setName(String s){
       name = s;
   }

    public ArrayList<String> getTypes(){return types;}
    public ArrayList<String> getMedications(){
        return medications;
    }


    public String getName(){
        return name;
    }




}
