package edots.models;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author JN
 *
 */
public class Schema {
    private String id;
    private String name;
    private ArrayList<Drug> drugs;


    // For testing only
    public Schema() {
        try {
            Random r = new Random();
            int num = r.nextInt(100);
            id = "6";
            name = Integer.toString(num);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param JSONString a JSON object representing the project
     */
    public Schema(String JSONString){
        try {
            JSONObject n = new JSONObject(JSONString);
            id = n.get("projectId").toString();
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
    public Schema(String i, String n){
        id = i;
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
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setName(String s){
       name = s;
   }

    public String getName(){
        return name;
    }




}
