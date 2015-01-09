package org.techintheworld.www.edots;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import edots.models.Patient;
import edots.models.Project;
import edots.models.Promoter;

/**
 * Created by jfang on 1/7/15.
 */
public class StorageManager {

    // Gets local storage file and deserializes into request object
    public static String GetLocalData(String objectType, String promoterUsername, Context c){

        if (!(objectType.equals("Promoter") && !(objectType.equals("Patient")))){
            return null;
        }
        String fileName=null;
        if (objectType.equals("Promoter")) {
            fileName= promoterUsername.concat("_data");
        }
        else if(objectType.equals("Patient")) {
            fileName= "patient".concat("_data");

        }
        try{
            if (!(fileName==null)){
                JSONObject jsonObject = getJSONFromLocal(c, fileName);
                return jsonObject.toString();
            }

        }
        catch (FileNotFoundException e){
            GetWebPromoterData(objectType, c);
            try{
                JSONObject jsonObject = getJSONFromLocal(c, fileName);
                return jsonObject.toString();
            }
            catch (FileNotFoundException ex){
                Log.e("Saving patient file unsuccessful: ", fileName.toString().concat(" error") );
                ex.printStackTrace();
            }
        }

        return null;

    }


    private static JSONObject getJSONFromLocal(Context c, String fileName) throws FileNotFoundException{
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
            return new JSONObject(fileContent);


        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Patient file not found");
        } catch (IOException e) {
            Log.e("IOException", "File error in finding patient files");
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    // Gets Promoter info from web and saves as local file
    public static Promoter GetWebPromoterData(String promoterUsername, Context c){
        // TODO: add connection to web and retrieve all info of that promoter
        Promoter p = new Promoter("username", "Brendan","Lima", "edots", new ArrayList<Long>(Arrays.asList(new Long("1234"),new Long("5678"))));

        // Save to local file for Projects
        String filename = "patient".concat("_data");
        String promoterData = p.toString();
        FileOutputStream outputStream;

        try {
            outputStream = c.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(promoterData.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("Saving Patient files error", "Cannot write to patient file");
            e.printStackTrace();
        }


        // Save to local file for Patients
        String patients_filename = promoterUsername.concat("_patients".concat("_data"));
        int num_patients= p.getPatient_ids().size();
        StringBuilder sb = new StringBuilder();

        // Queries web service for patients with the ids associated with this promoter
        for (int i = 0; i <num_patients; i++){
            Patient new_patient = GetWebPatientData(p.getPatient_ids().get(i));
            sb.append(new_patient.toString());

        }

        // Saves patients data of this promoter to a file named under patients_filename
        String patientData = sb.toString();
        FileOutputStream p_outputStream;
        try {
            p_outputStream = c.openFileOutput(patients_filename, Context.MODE_PRIVATE);
            p_outputStream.write(patientData.getBytes());
            p_outputStream.close();
        } catch (Exception e) {
            Log.e("Saving Patient files error", "Cannot write to patient file");
            e.printStackTrace();
        }

        return p;
    }

    // TODO: Get patient info from database with this id
    public static Patient GetWebPatientData(Long patient_id){
        Project testProject = new Project();
        Project testProject2 = new Project();
        return new Patient("Sample Patient", new Date(), patient_id , "F", new ArrayList<Project>(Arrays.asList(testProject, testProject2)), "Mother","" +
                "Father");

    }

    // TODO: Allow client to send requests to change remote db for adding patients, edit Promoter info
    // Send deltas rather than rewriting
    public void SendUpdatesToWeb(){

    }



}
