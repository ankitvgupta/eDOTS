package edots.models;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * @author JN
 *
 */

public class Schema  extends Saveable{
    private String id;
    private String name;
    private ArrayList<Drug> drugs;
// TODO: add phase
    private String visit_mode; // 1 for "clinic" or 2 for "patient home"
    private String start_date;
    private String end_date;

    // For testing only
    public Schema() {
        try {
            id = "6";
            name = "test";
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

    public ArrayList<Drug> getDrugs() {
        return drugs;
    }

    public void setDrugs(ArrayList<Drug> drugs) {
        this.drugs = drugs;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getVisit_mode() {
        return visit_mode;
    }

    public void setVisit_mode(String visit_mode) {
        this.visit_mode = visit_mode;
    }
}
