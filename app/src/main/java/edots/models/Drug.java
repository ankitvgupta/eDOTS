package edots.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author lili
 */
public class Drug {
    private String id;
    private String name;
    private String symbol;
    private String dosage;
    
    public Drug(){
        this.id = "0";
        this.name = "SampleDrug";
        this.symbol = "SYM";
        this.dosage = "100 mg";
    }

    public Drug(String id, String name, String symbol) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.dosage = null;
    }

    public Drug(String id, String name, String symbol, String dosage) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.dosage = dosage;
    }

    /**
     * @param JSONString a JSON object representing the project
     */
    public Drug(String JSONString){
        try {
            JSONObject n = new JSONObject(JSONString);
            id = n.get("id").toString();
            name = n.get("name").toString();
            symbol = n.get("symbol").toString();
            dosage = n.get("dosage").toString();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    /**
     * @return a JSON Serialization of the Project object
     */
    public String toString(){
        JSONObject temp = new JSONObject();
        try {
            temp.put("id", getId());
            temp.put("name", getName());
            temp.put("symbol", getSymbol());
            temp.put("dosage", getDosage());
        } catch (JSONException e) {
            Log.v("JSON Exception", "Found a JSON Exception");
            e.printStackTrace();
        }
        return temp.toString();
    }
    
    public String getSymbol() {
        return symbol;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

}
