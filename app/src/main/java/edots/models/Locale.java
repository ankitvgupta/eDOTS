package edots.models;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techintheworld.www.edots.R;

import java.util.ArrayList;

import edots.utils.OfflineStorageManager;


public class Locale extends Saveable {
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

    /**
     * Parses out just the locale name from a list of Locale objects and returns as an array of strings
     * @author JN
     * @param locales ArrayList of Locale objects
     * @return array of strings that contain only the 'name' of the locales
     */
    public static String[] ConvertLocalObjsToStrings(ArrayList<Locale> locales) {
        String[] locale_strings = new String[locales.size()];

        for (int i = 0; i < locales.size(); i++) {
            locale_strings[i] = locales.get(i).name;
        }
        return locale_strings;
    }

    public static String GetLocaleNumber(Context context, String locale_name){
        OfflineStorageManager sm = new OfflineStorageManager(context);
        String locale_file = context.getString(R.string.locale_filename);
        try{
            JSONArray array = new JSONArray(sm.getStringFromLocal(locale_file));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Locale l = new Locale(obj.toString());
                if (l.name.equals(locale_name)){
                    return String.valueOf(l.id);
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

