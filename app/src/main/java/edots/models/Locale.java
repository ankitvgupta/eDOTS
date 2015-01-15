package edots.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;


public class Locale implements KvmSerializable {
    public int id;
    public String name;


    public Locale()
    {
        id = 0;
        name = "";
    }


    public Locale(int id, String name)
    {
        this.id = id;
        this.name = name;

    }

    /** Added to parse a string back into the JSON form.
     *
     *  @author Brendan Bozorgmir
     *  @param JSONString A JSON Serialization of the Locale Object
     *
     */
    public Locale(String JSONString) {
        try {
            JSONObject n = new JSONObject(JSONString);
            this.name = n.get("name").toString();
            this.id = Integer.parseInt(n.get("id").toString());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public Object getProperty(int arg0) {

        switch(arg0)
        {
            case 0:
                return id;
            case 1:
                return name;

        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 2;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
        switch(ind)
        {
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "Id";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Name";
                break;
            default:break;
        }
    }
    @Override
    public String toString() {
        JSONObject temp = new JSONObject();
        try {
            temp.put("name", this.name);
            temp.put("id",  Integer.toString(this.id));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return temp.toString();
    }

    @Override
    public void setProperty(int ind, Object val) {
        switch(ind)
        {
            case 0:
                id = Integer.parseInt(val.toString());
                break;
            case 1:
                name = val.toString();
                break;
            default:
                break;
        }
    }
}

