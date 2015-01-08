package org.techintheworld.www.edots;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import edots.models.Patient;
import edots.models.Promoter;

/**
 * Created by jfang on 1/7/15.
 */
public class StorageManager {

    // Gets local storage file and deserializes into Patient object
    public static Patient GetLocalPatientData(String promoterUsername, Context c){
        GetWebPatientData(promoterUsername, c);
        String fileName= promoterUsername.concat("_data");
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
            JSONObject newerObject=new JSONObject(fileContent);
            String name = (String) newerObject.get("string");
            Log.e("File Read is", name);

        } catch (FileNotFoundException e) {
            Log.e("File Not Found Exception", "Patient files cannot be found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException", "File error in finding patient files");
            e.printStackTrace();
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Patient GetWebPatientData(String promoterUsername, Context c){
        Promoter p = new Promoter();
        String filename = promoterUsername.concat("_data");
        String testString = "{\n" +
                "  \"array\": [\n" +
                "    1,\n" +
                "    2,\n" +
                "    3\n" +
                "  ],\n" +
                "  \"boolean\": true,\n" +
                "  \"null\": null,\n" +
                "  \"number\": 123,\n" +
                "  \"object\": {\n" +
                "    \"a\": \"b\",\n" +
                "    \"c\": \"d\",\n" +
                "    \"e\": \"f\"\n" +
                "  },\n" +
                "  \"string\": \"Hello World\"\n" +
                "}";
        FileOutputStream outputStream;
        Log.e("File written is",testString);

        try {
            outputStream = c.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(testString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.e("Saving Patient files error", "Cannot write to patient file");
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
