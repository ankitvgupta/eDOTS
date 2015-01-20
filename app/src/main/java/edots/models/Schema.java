package edots.models;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Lili
 *
 */

public class Schema  extends Saveable{
    private String id;
    private String name;
    private ArrayList<Drug> drugs;
    private String phase;
    private String visit_mode; // 1 for "clinic" or 2 for "patient home"
    private Schedule schedule;


    // For testing only
    public Schema() {
        try {
            id = "1";
            name = "SampleSchemaName";
            drugs = new ArrayList<>(Arrays.asList(new Drug()));
            phase = "SamplePhase";
            visit_mode = "1";
            schedule = new Schedule();

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

    public String getVisit_mode() {
        return visit_mode;
    }

    public void setVisit_mode(String visit_mode) {
        this.visit_mode = visit_mode;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}