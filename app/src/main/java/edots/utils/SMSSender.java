package edots.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import org.techintheworld.www.edots.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import edots.tasks.GetMissedPatientsFromPromoterTask;

/**
 * Created by jfang on 1/21/15.
 */
public class SMSSender {
    private Context context;

    public SMSSender(Context c) {
        context = c;
        setSMSDeliveryReceivers();
    }

    public void sendSMSMessage(String phoneNumber, String message){
        SmsManager sms = SmsManager.getDefault();
        String SENT = context.getString(R.string.sms_sent);
        String DELIVERED = context.getString(R.string.sms_delivered);

        // Testing only
        //phoneNumber = context.getString(R.string.test_phone_1);
        phoneNumber = context.getString(R.string.test_phone_2);

        ArrayList<String> parts = sms.divideMessage(message);

        ArrayList<PendingIntent> sentPIs = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPIs = new ArrayList<PendingIntent>();
        for (int i = 0; i < parts.size(); i++){
            sentPIs.add(PendingIntent.getBroadcast(context, 0,
                    new Intent(SENT), 0));

            deliveredPIs.add(PendingIntent.getBroadcast(context, 0,
                    new Intent(DELIVERED), 0));
        }

        Log.i("SMSSender: sendSMSMessage: sending", "message " + message + " phone number "+ phoneNumber);

        sms.sendMultipartTextMessage(phoneNumber, null, parts, sentPIs, deliveredPIs);
    }

    /**
     * @author JN
     * Sets the Broadcast Receivers that listen to SMS delivery status
     */
    public void setSMSDeliveryReceivers(){
        final String SENT = context.getString(R.string.sms_sent);
        final String DELIVERED = context.getString(R.string.sms_delivered);
        //---when the SMS has been sent---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    //TODO: code the message in strings.xml
                    case Activity.RESULT_OK:
                        Toast.makeText(context.getApplicationContext(), SENT,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context.getApplicationContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context.getApplicationContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context.getApplicationContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context.getApplicationContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context.getApplicationContext(), DELIVERED,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context.getApplicationContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
    }

    /**
     * @author Ankit
     * Queries the server to find all of the patients that missed their appointment today
     * <p/>
     * TODO: For now this just pulls all of the patients of the given promoter
     * TODO: Change this to load the true missed patients
     */
    public HashMap<String, String> loadMissedPatients() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        GetMissedPatientsFromPromoterTask newP = new GetMissedPatientsFromPromoterTask();
        Date today = new Date();
        DateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");
        String date_to_send = dbDateFormat.format(today);
        String userid = prefs.getString("userid", null);

        AsyncTask p = newP.execute(context.getString(R.string.server_url), userid,date_to_send);

        try {
            HashMap<String, String> missed_patient_nums = (HashMap<String, String>) p.get();
            Log.e("MainMenuActivity: sendSMS", missed_patient_nums.toString());
            return missed_patient_nums;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param patientID the patient that the sms is being sent to
     * @return bool indicating success
     * <p/>
     * TODO: For now this just returns the patientID for everyone.
     * @author Ankit
     */
    public boolean sendSMSToPatient(String patientID, String phoneNumber) {
        String missed_appt = context.getString(R.string.message);
        if (phoneNumber != null){
            sendSMSMessage(phoneNumber, missed_appt);
        }
        return true;
    }

    /**
     * @param promoterID id of the promoter whose patients this will send sms to
     * @return String with the aggregate summary to send to the promoter
     * <p/>
     * This function sends the SMS to all of the patients of the given promoter, and returns an aggregate summary
     * @author
     */
    public String sendSMSForGivenPromoter(String promoterID) {

        String aggregate = "";
        try {
            HashMap<String, String> missed_patients = loadMissedPatients();
            for (String key: missed_patients.keySet()){
                sendSMSToPatient(key, missed_patients.get(key));
                aggregate +=(key + " \n" + " \n");
            }
            String test_promoter_phone_number = context.getString(R.string.test_phone_1);

            sendSMSMessage(test_promoter_phone_number,aggregate);
            Log.v("SMSSender.java: sendSMSForGivenPromoter: these patients did not come ", aggregate);
            return aggregate;

        }  catch (NullPointerException e) {
            e.printStackTrace();
        }

        return "";

    }

    /**
     * Sends an aggregate SMS to a given coordinator by calling the send function for each promoter and compiling the results
     *
     * @param coordinatorID
     * @return true on success, false on failure
     */
    /*private boolean sendSMSForGivenCoordinator(String coordinatorID){
        GetPromotersFromCoordinatorsLoadTask loadPromoters = new GetPromotersFromCoordinatorsLoadTask();
        AsyncTask loadPromotersTask = loadPromoters.execute(getString(R.string.server_url), coordinatorID);
        String aggregate = "";
        try {

            ArrayList<String> promoters = (ArrayList<String>) loadPromotersTask.get();
            for (int i = 0; i < promoters.size(); i++){
                aggregate += sendSMSForGivenPromoter(promoters.get(i));
            }

            // TODO: Actually send the SMS to the promoter
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

    }*/

    /**
     * @author Ankit
     *
     * This function is a test one to lay out the logic for determining who to send the SMS to
     */
    /*private void determineSMS(){
        GetCoordinatorsLoadTask loadCoordinators = new GetCoordinatorsLoadTask(); // This task doesn't exist yet.
        AsyncTask loadCoordinatorsTask = loadCoordinators.execute(getString(R.string.server_url));
        try {
            ArrayList<String> coordinators = (ArrayList<String>) loadCoordinatorsTask.get();
            Log.v("MainMenuActivity.java: The coordinators are", coordinators.toString());

            for (int i = 0; i < coordinators.size(); i++){ // for each coordinator get the promoters
                sendSMSForGivenCoordinator(coordinators.get(i));
            }


        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }


        return null;




    }*/
}
