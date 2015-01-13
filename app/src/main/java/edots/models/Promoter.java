package edots.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jfang on 1/6/15.
 */
public class Promoter extends Object{
    private String username;
    private String locale;
    private ArrayList<String> patient_ids;

    public Promoter(){

    }

    public Promoter(String u,String l, String p, ArrayList<String> pt){
        username = u;
        locale = l;
        patient_ids=pt;

    }

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
