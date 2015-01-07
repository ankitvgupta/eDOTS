package org.techintheworld.www.edots;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;

import edots.models.Patient;
import edots.models.Promoter;

/**
 * Created by jfang on 1/7/15.
 */
public class DataManager {

    // Gets local storage file and deserializes into Patient object
    public Patient GetLocalPatientData(Promoter p, Context c){
        String fileName= p.getUsername().concat("_data");
        String toWrite = "hello";

        try {
            // Opens file for reading
            FileInputStream fos = c.openFileInput(fileName);
            fos.write(toWrite.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
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

    }

    public String SerializePatients(Promoter p){

    }

    public void EditPatientData(String patient_username){


    }



}
