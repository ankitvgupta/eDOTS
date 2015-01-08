package edots.models;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jfang on 1/6/15.
 */
public class Project {

    // TODO: Figure out why JSONArray is needed instead of ArrayList (basically it's a parsing issue)
    private ArrayList<String> medications = new ArrayList<String>();
    private ArrayList<String> types = new ArrayList<String>();
    private String name;


    // For testing only
    public Project(){
        try {
            name = "Temp";
            medications = new ArrayList<String>();
            medications.add("Med 1");
            medications.add("Med 2");
            Log.v("The original medical thing is ", medications.toString());
            //JSONArray medications2 = new JSONArray(medications);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public Project (String JSONString){
        try {
            JSONObject n = new JSONObject(JSONString);
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

    
    public Project(String n, ArrayList<String> meds, ArrayList<String> t){
        medications = meds;
        name = n;
        types = t;

    }

    @Override
    public String toString(){
        JSONObject temp = new JSONObject();
        try {
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
