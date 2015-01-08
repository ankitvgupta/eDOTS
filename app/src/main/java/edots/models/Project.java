package edots.models;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by jfang on 1/6/15.
 */
public class Project {

    // TODO: Figure out why JSONArray is needed instead of ArrayList (basically it's a parsing issue)
    private JSONArray medications = new JSONArray();
    private int stages;
    private String name;


    // For testing only
    public Project(){
        try {
            name = "Temp";
            medications = new JSONArray();
            medications.put("Med 1");
            medications.put("Med 2");
            Log.v("The original medical thing is ", medications.toString());
            JSONArray medications2 = new JSONArray(medications.toString());
            stages = medications.length();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public Project (String JSONString){
        try {
            JSONObject n = new JSONObject(JSONString);
            name = n.get("name").toString();
            medications = new JSONArray(n.get("medications").toString());
            stages = medications.length();

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    
    public Project(String n, JSONArray meds){
        medications = meds;
        name = n;

    }

    @Override
    public String toString(){
        JSONObject temp = new JSONObject();
        try {
            temp.put("medications", getMedications());
            temp.put("stages", Integer.toString(getStages()));
            temp.put("name", getName());
        } catch (JSONException e) {
            Log.v("JSON Exception", "Found a JSON Exception");
            e.printStackTrace();
        }
        return temp.toString();
    }

    public void setMedications(JSONArray m){
        medications = m;
    }

    public void setName(String s){
       name = s;
   }

    public JSONArray getMedications(){
        return medications;
    }

    public int getStages(){
        return stages;
    }

    public String getName(){
        return name;
    }


}
