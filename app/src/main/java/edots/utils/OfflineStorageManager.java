package edots.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.techintheworld.www.edots.R;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import edots.models.Locale;
import edots.models.Patient;
import edots.models.Promoter;
import edots.tasks.GetPatientFromIDTask;
import edots.tasks.LoadPatientFromPromoterTask;

/**
 * Created by jfang on 1/7/15.
 */
public class OfflineStorageManager {


    public static String getJSONFromLocal(Context c, String fileName){
        try {
            // Opens file for reading
            FileInputStream fis = c.openFileInput(fileName);
            DataInputStream dis = new DataInputStream(fis);

            // Create buffer
            int length = dis.available();
            byte[] buf = new byte[length];

            // Read full data into buffer
            dis.readFully(buf);
            String fileContent = new String(buf);
            dis.close();
            fis.close();

            // Convert to string
            return fileContent;


        } catch (FileNotFoundException e) {
            Log.e("OfflineStorageManager: FileNotFoundException", "Cannot find file");
            e.printStackTrace();

        } catch (IOException e) {
            Log.e("OfflineStorageManager: IOException", "File error in finding patient files");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Queries service for promoter object with promoter username
     * @param promoterUsername
     * @param c
     * @return a promoter object
     */
    //TODO: promoterUsername is never used
    public static Promoter GetWebPromoterData(String promoterUsername, Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
        String user_id = prefs.getString((c.getString(R.string.key_userid)), null);
        String locale = prefs.getString((c.getString(R.string.login_locale)), null);

        Promoter p_result = new Promoter();
        LoadPatientFromPromoterTask newP = new LoadPatientFromPromoterTask();
        AsyncTask p = newP.execute("http://demo.sociosensalud.org.pe", user_id);
        try {
            ArrayList<String> patient_ids = (ArrayList<String>) p.get();
            p_result.setLocale(locale);
            p_result.setId(user_id);
            p_result.setPatientIds(patient_ids);
            SaveWebPromoterData(p_result, c);
            return p_result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return p_result;
    }

    public static void SaveWebPatientData(Promoter p, Context c) throws JSONException {
        // Save to local file for Patients
        String patients_filename = c.getString(R.string.patient_data_filename);
        boolean patient_result = c.deleteFile(c.getString(R.string.patient_data_filename));
        if (!patient_result)  {
            Log.e("OfflineStorageManager: SaveWebPatientData", "Patient delete file failed");
        }

        int num_patients = p.getPatientIds().size();
        JSONArray ja = new JSONArray();

        // Queries web service for patients with the ids associated with this promoter
        for (int i = 0; i < num_patients; i++) {
            Patient new_patient = GetWebPatientData(p.getPatientIds().get(i));
            JSONObject obj = new JSONObject(new_patient.toString());
            ja.put(obj);
        }

        // Saves patients data of this promoter to a file named under patients_filename

        String patientData = ja.toString();

        FileOutputStream p_outputStream;
        try {
            p_outputStream = c.openFileOutput(patients_filename, Context.MODE_PRIVATE);
            p_outputStream.write(patientData.getBytes());
            p_outputStream.close();
        } catch (Exception e) {
            Log.w("StorageManager: Saving Patient files error", "Cannot write to patient file");
            e.printStackTrace();
        }

        // Testing only: read from file to see that data is not appended
        String str = getJSONFromLocal(c, c.getString(R.string.patient_data_filename));
        Log.e("OfflineStorageManager: SaveWebPatientData", str);
    }


    // Gets Promoter info from web and saves as local file
    public static void SaveWebPromoterData(Promoter p, Context c) {
        // TODO: add connection to web and retrieve all info of that promoter

        // Save to local file for Projects
        String filename = c.getString(R.string.promoter_data_filename);
        boolean promoter_result = c.deleteFile(c.getString(R.string.promoter_data_filename));
        if (!promoter_result)  {
            Log.e("OfflineStorageManager: SaveWebPromoterData", "Promoter delete file failed");
        }
        String promoterData = p.toString();
        FileOutputStream outputStream;

        try {
            outputStream = c.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(promoterData.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("OfflineStorageManager: Saving Promoter files error", "Cannot write to promoter file");
            e.printStackTrace();
        }

        // Testing only: read from file to see that data is not appended
        String s = getJSONFromLocal(c, c.getString(R.string.promoter_data_filename));
        Log.e("OfflineStorageManager: SaveWebPromoterData", s);


    }

    // Gets Patient object that is with this CodigoPaciente
    public static Patient GetWebPatientData(String patient_id) {
        GetPatientFromIDTask newP = new GetPatientFromIDTask();
        // TODO: do not hard code in the url
        AsyncTask get_patient = newP.execute("http://demo.sociosensalud.org.pe", patient_id);
        Patient p;
        try {
            p = (Patient) get_patient.get();
            Log.v("OfflineStorageManager.java: The patient that we pulled from the server is", p.toString());
            return p;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void SetLastLocalUpdateTime(Context context) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = mPreferences.edit();
        Date currentTime = new Date();
        long milli_currentTime = currentTime.getTime();
        editor.putString(context.getString(R.string.date), String.valueOf(milli_currentTime));
        Log.e("Offline StorageManager", String.valueOf(milli_currentTime));

        editor.commit();
    }

    /**
     * Loads when MainMenuActivity is created and checks if need to get new info from the service
     * if last update was less than 5 hours ago, will make calls to update local files
     *
     * @param context Current context of the activity
     */
    // TODO: some kind of manual fetch method
    public static void UpdateLocalStorage(Context context) {
        boolean isConnected = InternetConnection.checkConnection(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String last_update = prefs.getString((context.getString(R.string.date)), null);
        try {
            long time_updated = Long.valueOf(last_update);

            long diff = Math.abs(time_updated - new Date().getTime());
            long threshold = 18000000; // 5 hours in milliseconds is 18000000
            Log.w("OfflineStorageManager: UpdateLocalStorage", String.valueOf(diff));
            if (isConnected && diff > threshold) {
                try {
                    SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(context);
                    String promoterId = prefs.getString((context.getString(R.string.username)), null);

                    Promoter new_promoter = OfflineStorageManager.GetWebPromoterData(promoterId, context);
                    OfflineStorageManager.SaveWebPatientData(new_promoter, context);

                    OfflineStorageManager.SetLastLocalUpdateTime(context);

                } catch (JSONException e) {
                    Log.e("OfflineStorageManager: Update Local Storage", "Error save patient data");
                }
            }
        }
        catch (NumberFormatException e){
            long time_updated = Long.valueOf("0");
            Log.i("offline storage catch",e.toString());
        }
    }

    /**
     * @author Brendan
     * @param l the array of locales that you wish to save locally
     * @param c the context
     * @throws JSONException an exception where JSON cannot be cast
     */
    public static void SaveLocaleData(Locale[] l, Context c) throws JSONException {
        // Save to local file for Locale
        String locale_filename = "locale_data";
        boolean locale_result = c.deleteFile("locale_data");
        if (!locale_result)  {
            Log.e("OfflineStorageManager: SaveWebPromoterData", "Locale delete file failed");
        }

        int num_locales = l.length;
        JSONArray ja = new JSONArray();

        // Puts all Locales into JSON form and saves them to an a JSON array
        for (int i = 0; i < num_locales; i++) {
            JSONObject obj = new JSONObject(l[i].toString());
            ja.put(obj);
        }

        // Saves locale data of this promoter to a file named under locale_filename

        String localeData = ja.toString();

        FileOutputStream l_outputStream;
        try {
            l_outputStream = c.openFileOutput(locale_filename, Context.MODE_PRIVATE);
            l_outputStream.write(localeData.getBytes());
            l_outputStream.close();
        } catch (Exception e) {
            Log.w("StorageManager: Saving Locale files error", "Cannot write to locale file");
            e.printStackTrace();
        }
    }

}
