package edots.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author JN Fang
 * @author Ankit Gupta
 * @since 2015-01-13
 *
 *
 * Model for Promoters.
 *
 */
public class Promoter extends Object {
    private String username;
    private String locale;
    private ArrayList<String> patient_ids;
    //TODO: add projects they are qualified to administer

    public Promoter(){

    }

    /**
     * Constructor for Promoter
     *
     * @param u username of promoter
     * @param l locale of promoter
     * @param p NOTUSED
     * @param pt ids of the patient of that promoter
     */
    public Promoter(String u,String l, String p, ArrayList<String> pt){
        username = u;
        locale = l;
        patient_ids=pt;
    }

    /**
     * @param JSONString JSON encoding of the Promoter object
     */
    public Promoter(String JSONString) {
        try {
            JSONObject n = new JSONObject(JSONString);
            username = n.get("username").toString();
            locale = n.get("locale").toString();
            patient_ids = new ArrayList<String>();
            JSONArray arry = new JSONArray(n.get("patient_ids").toString());
            for (int i = 0; i < arry.length(); i++) {
                patient_ids.add(arry.getString(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return a JSON serialization of the Promoter object
     */
    public String toString(){
        JSONObject temp = new JSONObject();
        try {
            temp.put("username", getUsername());
            temp.put("locale", getLocale());
            temp.put("patient_ids", new JSONArray(getPatient_ids()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return temp.toString();

    }

    public ArrayList<String> getPatient_ids(){return patient_ids;}

    public String getUsername(){
        return username;
    }

    public String getLocale(){
        return locale;
    }

    public void setPatient_ids(ArrayList<String> p_ids){ patient_ids = p_ids;}

    public void setUsername(String u){
        username=u;
    }


    public void setLocale(String l){locale=l;
    }


}
