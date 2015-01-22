package edots.models;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techintheworld.www.edots.R;

import java.util.ArrayList;
import java.util.Arrays;

import edots.utils.OfflineStorageManager;

/**
 *
 * @author Lili
 *
 */

public class Schema  extends Saveable{
    private String id;
    private String name;

    public Schema(String id, String name, ArrayList<Drug> drugs, String phase, String visit_mode, Schedule schedule) {
        this.id = id;
        this.name = name;
        this.drugs = drugs;
        this.phase = phase;
        this.visit_mode = visit_mode;
        this.schedule = schedule;
    }

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
            drugs.add(new Drug());
            drugs.add(new Drug());
            drugs.add(new Drug());
            phase = "SamplePhase";
            visit_mode = "1";
            schedule = new Schedule();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * @param JSONString a JSON object representing the schema
     */
    public Schema(String JSONString){
        try {
            JSONObject n = new JSONObject(JSONString);
            //Log.v()
            id = n.get("id").toString();
            name = n.get("name").toString();
            drugs = new ArrayList<Drug>();
            JSONArray arry = new JSONArray(n.get("drugs").toString());
            for (int i = 0; i < arry.length(); i++){
                drugs.add(new Drug(arry.getString(i)));
            }
            phase = n.get("phase").toString();
            visit_mode = n.get("visit_mode").toString();
            schedule = new Schedule(n.get("schedule").toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    
    @Override
    /**
     * @return a JSON Serialization of the Project object
     *
     */
    public String toString(){
        JSONObject temp = new JSONObject();
        try {
            temp.put("id", getId());
            temp.put("name", getName());
            temp.put("drugs", getDrugs());
            temp.put("phase", getPhase());
            temp.put("visit_mode", getVisit_mode());
            temp.put("schedule", getSchedule());
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


    /**
     *
     * @param schemas a list of scehmas
     * @return an array with the list of names of the schemas
     */
    public static String[] ConvertSchemaObjsToStrings(ArrayList<Schema> schemas) {
        String[] schema_strings = new String[schemas.size()];

        for (int i = 0; i < schemas.size(); i++) {
            schema_strings[i] = schemas.get(i).phase + " " + schemas.get(i).name;
        }
        return schema_strings;
    }

    public String printDrugs(){
        String result = "";
        for (int i = 0; i < drugs.size(); i++){
            result += drugs.get(i).getSymbol()+"\t"+drugs.get(i).getDosage();
            if (i< drugs.size() - 1){
                result += ",\t";
            }
        }
        return result;
    }

    public static String GetSchemaNumber(Context context, String schema_name){
        OfflineStorageManager sm = new OfflineStorageManager(context);
        String schema_file = context.getString(R.string.schema_filename);
        try{
            JSONArray array = new JSONArray(sm.getStringFromLocal(schema_file));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Schema s = new Schema(obj.toString());
                if ((s.phase + " " + s.name).equals(schema_name)){
                    return String.valueOf(s.id);
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("Locale.java: GetLocaleNumber", "JSONException");
        }
        return null;
    }
}
