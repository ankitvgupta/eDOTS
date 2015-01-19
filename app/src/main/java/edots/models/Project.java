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
public class Project {
    // TODO: consistent capitalization of all class variables
    private String Id;
    private String name;


    // For testing only
    public Project() {
        try {
            Random r = new Random();
            int num = r.nextInt(100);
            Id = "5";
            name = Integer.toString(num);
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

    public void setName(String s){
       name = s;
   }

    public String getName(){
        return name;
    }




}
