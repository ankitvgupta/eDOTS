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
    private String name;
    private String locale;
    private String password;
    private ArrayList<String> patient_ids;

    public Promoter(){

    }

    public Promoter(String u, String n,String l, String p, ArrayList<String> pt){
        username = u;
        name=n;
        locale = l;
        password= p;
        patient_ids=pt;

    }

    public Promoter(String JSONString) {
        try {
            JSONObject n = new JSONObject(JSONString);
            name = n.get("name").toString();
            username = n.get("username").toString();
            locale = n.get("locale").toString();
            password = n.get("password").toString();
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
            temp.put("name", getName());
            temp.put("username", getUsername());
            temp.put("locale", getLocale());
            temp.put("password", getPassword());
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

    public String getName(){
        return name;
    }

    public String getLocale(){
        return locale;
    }

    public String getPassword(){
        return password;
    }

    public void setPatient_ids(ArrayList<String> p_ids){ patient_ids = p_ids;}
    public void setUsername(String u){
        username=u;
    }

    public void setName(String n){
        name=n;
    }

    public void setLocale(String l){locale=l;
    }

    public void setPassword(String p){
        password = p;
    }

}
