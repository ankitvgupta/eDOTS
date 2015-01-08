package org.techintheworld.www.edots;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import edots.models.Patient;
import edots.models.Promoter;

/**
 * Created by jfang on 1/7/15.
 */
public class DataManager {

    // Gets local storage file and deserializes into Patient object
    public Patient GetLocalPatientData(String promoterUsername, Context c){
        String fileName= promoterUsername.concat("_data");

        try {
            // Opens file for reading
            FileInputStream fos = c.openFileInput(fileName);
            fos.read();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.w("File Not Found Exception", "Patient files cannot be found");
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }

    public Patient GetWebPatientData(Promoter p, Context c){
        String filename = p.getUsername().concat("_data");
        String string = "New patient!";
        FileOutputStream outputStream;

        try {
            outputStream = c.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public String SerializePatients(Promoter p){
        return null;
    }

    public void EditPatientData(String patient_username){


    }



}
