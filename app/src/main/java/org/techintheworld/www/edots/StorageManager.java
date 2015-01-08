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

import edots.models.Promoter;

/**
 * Created by jfang on 1/7/15.
 */
public class StorageManager {

    // Gets local storage file and deserializes into Promoter object
    public static Promoter GetLocalPromoterData(String promoterUsername, Context c){
        GetWebPromoterData(promoterUsername, c);
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
            JSONObject promoterJSON=new JSONObject(fileContent);
            return new Promoter(promoterJSON.toString());


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

    // Gets Promoter info from web and saves as local file
    public static Promoter GetWebPromoterData(String promoterUsername, Context c){
        // TODO: add connection to web and retrieve all info of that promoter
        Promoter p = new Promoter("username", "Brendan","Lima", "edots", new ArrayList<String>(Arrays.asList("Patient1, Patient2")));

        // Save to local file
        String filename = promoterUsername.concat("_data");
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
        return null;

    }

    // TODO: Allow client to send requests to change remote db for adding patients, edit Promoter info
    // Send deltas rather than rewriting
    public void UpdateWebService(){

    }



}
