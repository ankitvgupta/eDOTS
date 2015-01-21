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
import edots.models.Saveable;
import edots.models.Visit;
import edots.tasks.GetPatientFromIDTask;
import edots.tasks.LoadPatientFromPromoterTask;
import edots.tasks.NewVisitUploadTask;

/**
 * Created by jfang on 1/7/15.
 */
public class OfflineStorageManager {

    private Context context;


    public OfflineStorageManager(Context c) {
        context = c;
    }

    /**
     * Reads the filename from local storage and returns the string
     *
     * @param fileName Name of the file to read from
     * @return String of the file contents that are read
     * @author JN
     */
    public String getStringFromLocal(String fileName) {
        try {
            // Opens file for reading
            FileInputStream fis = context.openFileInput(fileName);
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
     *
     * @param promoterUsername
     * @return a promoter object
     */
    //TODO: promoterUsername is never used
    public Promoter GetWebPromoterData(String promoterUsername) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String user_id = prefs.getString((context.getString(R.string.key_userid)), null);
        String locale = prefs.getString((context.getString(R.string.login_locale)), null);

        Promoter p_result = new Promoter();
        LoadPatientFromPromoterTask newP = new LoadPatientFromPromoterTask();
        AsyncTask p = newP.execute(context.getString(R.string.server_url), user_id);
        try {
            ArrayList<String> patient_ids = (ArrayList<String>) p.get();
            p_result.setLocale(locale);
            p_result.setId(user_id);
            p_result.setPatientIds(patient_ids);
            return p_result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return p_result;
    }

    /**
     * Queries web and returns the Patient objects of the Promoter in the parameter
     *
     * @param p Gets all the Patient objects for that Promoter
     * @return ArrayList of Patient objects
     * @author JN
     */
    public ArrayList<Patient> GetPatientsFromPromoter(Promoter p) {
        int num_patients = p.getPatientIds().size();
        ArrayList<Patient> patients = new ArrayList<Patient>();

        // Queries web service for patients with the ids associated with this promoter
        for (int i = 0; i < num_patients; i++) {
            Patient new_patient = GetWebPatientData(p.getPatientIds().get(i));
            patients.add(new_patient);
        }
        return patients;
    }

    /**
     * Gets Patient object that is with this CodigoPaciente
     *
     * @param patient_id
     * @return Patient object with that patient id from the server
     * @author JN
     */
    public Patient GetWebPatientData(String patient_id) {
        GetPatientFromIDTask newP = new GetPatientFromIDTask();
        AsyncTask get_patient = newP.execute(context.getString(R.string.server_url), patient_id);
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


    public <T extends Saveable> void SaveArrayListToLocal(ArrayList<T> a, String filename) {
        boolean file_deleting_result = context.deleteFile(filename);
        if (!file_deleting_result) {
            Log.e("OfflineStorageManager: SaveArrayListToLocal", "Delete file failed");
        }
        int num_objects = a.size();
        JSONArray ja = new JSONArray();
        try {
            // Queries web service for patients with the ids associated with this promoter
            for (int i = 0; i < num_objects; i++) {
                T o = a.get(i);
                JSONObject obj = new JSONObject(o.toString());
                ja.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Saves patients data of this promoter to a file named under patients_filename
        String data_to_write = ja.toString();

        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data_to_write.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("OfflineStorageManager: SaveArrayListToLocal", "Cannot write to file");
            e.printStackTrace();
        }

        // Testing only: read from file to see that data is not appended
        String s = getStringFromLocal(filename);
        Log.i("OfflineStorageManager: SaveArrayListToLocal", s);
    }


    public boolean SaveSaveableToLocal(Saveable o, String filename) {
        // Save to local file for the object passed in
        boolean file_deleting_result = context.deleteFile(filename);
        if (!file_deleting_result) {
            Log.e("OfflineStorageManager: SaveSaveableToLocal", "Delete file failed");
        }
        String data_to_write = o.toString();
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data_to_write.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("OfflineStorageManager: SaveSaveableToLocal", "Cannot write to file");
            e.printStackTrace();
            return false;
        }


        // Testing only: read from file to see that data is not appended
        String s = getStringFromLocal(filename);
        Log.i("OfflineStorageManager: SaveSaveableToLocal", s);


        return true;
    }



    /**
     * Sets in Shared Preferences the time of the update aka when this function is called
     * @author JN
     */
    public void SetLastLocalUpdateTime() {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = mPreferences.edit();
        Date currentTime = new Date();
        long milli_currentTime = currentTime.getTime();
        editor.putString(context.getString(R.string.date), String.valueOf(milli_currentTime));
        Log.i("OfflineStorageManager:SetLastLocalUpdateTime", String.valueOf(milli_currentTime));
        editor.commit();
    }

    /**
     * Checks SharedPreferences and returns the last update time
     * @return a primitive long of the miliseconds since last update
     * @author JN
     */
    public long GetLastLocalUpdateTime(){
        SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(context);
        String last_update = sprefs.getString((context.getString(R.string.date)), null);
        long time_updated;
        try{
            time_updated = Long.valueOf(last_update);
        }
        catch (NumberFormatException e){
            time_updated = Long.valueOf("0");
            Log.i("offline storage catch", e.toString());

        }
        return time_updated;
    }

    /**
     * Called when internet is connected and there is a visit to upload
     *
     * @return
     * @author JN
     */
    private boolean uploadLocalVisit() {
        String new_visit_file = context.getString(R.string.new_visit_filename);
        String new_visit = getStringFromLocal(new_visit_file);
        Log.e("OfflineStorageManager: uploadLocal", new_visit);
        Visit currentVisit = new Visit(new_visit);
        Log.e("OfflineStorageManager: uploadLocal new current visit", currentVisit.toString());
        NewVisitUploadTask upload_visit = new NewVisitUploadTask(context);

        try {
            String result = upload_visit.execute(context.getString(R.string.server_url),
                    currentVisit.getLocaleCode(),
                    currentVisit.getProjectCode(),
                    currentVisit.getVisitGroupCode(),
                    currentVisit.getVisitCode(),
                    currentVisit.getPacientCode(),
                    currentVisit.getVisitDate(),
                    currentVisit.getVisitTime(),
                    currentVisit.getPromoterId()).get();
            Log.e("OfflineStorageManager: uploadLocalVisit result", result);
            boolean deletion = context.deleteFile(context.getString(R.string.new_visit_filename));
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.remove(context.getString(R.string.new_visit_filename));
            editor.commit();
            Log.e("OfflineStorageManager: uploadLocalVisit file deletion", String.valueOf(deletion));
            return true;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads when MainMenuActivity is created and checks if need to get new info from the service
     * if last update was less than 5 hours ago, will make calls to update local files
     */
    // TODO: some kind of manual fetch method and in general just clean this up
    // TODO: check that promoter is logged in/sharedpref exist, getlastupdated
    public boolean CanUpdateLocalStorage() {
        boolean isConnected = InternetConnection.checkConnection(context);
        String logged_in = AccountLogin.CheckAlreadyLoggedIn(context);
        if (!isConnected || (logged_in==null) ){
            return false;
        }

        // TODO: upload visit
        //        String result = prefs.getString(context.getString(R.string.new_visit_filename), null);
        // Log.e("OfflineStorageManager: UpdateLocalStorage", "going to upload local visit");
        // boolean visit_upload = uploadLocalVisit();


        return true;
    }

    /**
     * Updates all local files: Promoter, Patient, new Visit
     * Assumes internet connection
     * @author JN
     *
     */
    public void UpdateLocalStorage() {
        SharedPreferences sprefs = PreferenceManager.getDefaultSharedPreferences(context);

        String promoterId = sprefs.getString((context.getString(R.string.username)), null);
        Promoter new_promoter = GetWebPromoterData(promoterId);
        String promoter_file = context.getString(R.string.promoter_data_filename);
        SaveSaveableToLocal(new_promoter, promoter_file);

        ArrayList<Patient> patients = GetPatientsFromPromoter(new_promoter);
        String patients_file = context.getString(R.string.patient_data_filename);
        SaveArrayListToLocal(patients, patients_file);

        SetLastLocalUpdateTime();

    }


    /**
     * @param l the array of locales that you wish to save locally
     * @throws JSONException an exception where JSON cannot be cast
     * @author Brendan
     * @deprecated
     */
    public void SaveLocaleData(Locale[] l) {
        // Save to local file for Locale
        String locale_filename = context.getString(R.string.locale_filename);
        boolean locale_result = context.deleteFile(locale_filename);
        if (!locale_result) {
            Log.e("OfflineStorageManager: SaveWebPromoterData", "Locale delete file failed");
        }

        int num_locales = l.length;
        JSONArray ja = new JSONArray();
        try {
            // Puts all Locales into JSON form and saves them to an a JSON array
            for (int i = 0; i < num_locales; i++) {
                JSONObject obj = new JSONObject(l[i].toString());
                ja.put(obj);
            }
        } catch (JSONException e) {
            Log.e("OfflineStorageManager: SaveLocaleData", "Error saving locale");
        }

        // Saves locale data of this promoter to a file named under locale_filename

        String localeData = ja.toString();

        FileOutputStream l_outputStream;
        try {
            l_outputStream = context.openFileOutput(locale_filename, Context.MODE_PRIVATE);
            l_outputStream.write(localeData.getBytes());
            l_outputStream.close();
        } catch (Exception e) {
            Log.w("StorageManager: Saving Locale files error", "Cannot write to locale file");
            e.printStackTrace();
        }
    }

}
