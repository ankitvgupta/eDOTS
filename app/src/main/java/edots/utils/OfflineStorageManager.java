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
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.models.Promoter;
import edots.tasks.GetPatientFromIDTask;
import edots.tasks.LoadPatientFromPromoterTask;

/**
 * Created by jfang on 1/7/15.
 */
public class OfflineStorageManager {


    public static String getJSONFromLocal(Context c, String fileName) throws FileNotFoundException {
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

            // Convert to JSON
            return fileContent;


        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Patient file not found");
        } catch (IOException e) {
            Log.e("StorageManager: IOException", "File error in finding patient files");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Queries service for promoter object with promoter username
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
            p_result.setUsername(user_id);
            p_result.setPatient_ids(patient_ids);
            SaveWebPromoterData(p_result, c);
            return p_result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return p_result;
    }

    public static void SaveWebPatientData(Promoter p, Context c) throws JSONException{
        // Save to local file for Patients
        String patients_filename = "patient_data";

        int num_patients = p.getPatient_ids().size();
        //StringBuilder sb = new StringBuilder();
        JSONArray ja = new JSONArray();

        // Queries web service for patients with the ids associated with this promoter
        for (int i = 0; i < num_patients; i++) {
            Patient new_patient = GetWebPatientData(p.getPatient_ids().get(i));
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
    }

    // Gets Promoter info from web and saves as local file
    public static void SaveWebPromoterData(Promoter p, Context c) {
        // TODO: add connection to web and retrieve all info of that promoter

        // Save to local file for Projects
        String filename = "promoter".concat("_data");
        String promoterData = p.toString();
        FileOutputStream outputStream;

        try {
            outputStream = c.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(promoterData.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("StorageManager: Saving Promoter files error", "Cannot write to promoter file");
            e.printStackTrace();
        }

    }

    // Gets Patient object that is with this CodigoPaciente
    public static Patient GetWebPatientData(String patient_id) {
        GetPatientFromIDTask newP = new GetPatientFromIDTask();
        AsyncTask get_patient = newP.execute("http://demo.sociosensalud.org.pe", patient_id);
        Patient p;
        try{
            p = (Patient)get_patient.get();
            return p;
        }
        catch (Exception e){
         e.printStackTrace();
        }

       return null;

    }

    // TODO: Allow client to send requests to change remote db for adding patients, edit Promoter info
    // Send deltas rather than rewriting
    public void SendUpdatesToWeb() {

    }


}
