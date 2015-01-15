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
    private String ProjectId;
    private ArrayList<String> medications = new ArrayList<String>();
    private ArrayList<String> types = new ArrayList<String>();
    private String name;


    // For testing only
    public Project() {
        try {
            Random r = new Random();
            int num = r.nextInt(100);
            ProjectId = "5";
            name = Integer.toString(num);
            medications = new ArrayList<String>();
            medications.add("Med 1");
            medications.add("Med 2");
            //JSONArray medications2 = new JSONArray(medications);
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
            ProjectId = n.get("projectId").toString();
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
     * @param projectId
     * @param n name of the project
     * @param meds arraylist of the medications
     * @param t array list of the types
     */
    public Project(String projectId, String n, ArrayList<String> meds, ArrayList<String> t){
        ProjectId = projectId;
        medications = meds;
        name = n;
        types = t;

    }

    @Override
    /**
     * @return a JSON Serialization of the Project object
     *
     */
    public String toString(){
        JSONObject temp = new JSONObject();
        try {
            temp.put("projectId", getProjectId());
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

    public String getProjectId(){
        return ProjectId;
    }

    public void setProjectId(String projectId){
        ProjectId = projectId;
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
