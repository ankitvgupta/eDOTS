package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.tasks.GetPatientFromIDTask;
import edots.tasks.LoadPatientFromPromoterTask;
import edots.utils.OfflineStorageManager;
import edots.utils.SMSAlarmReceiver;

public class MainMenuActivity extends Activity {
    Button btnSendSMS;
    ArrayList<String> patients = new ArrayList<String>(); // an arraylist of the patients that need to be messaged today

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Creates listener for SMS sending button
        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            //TODO: get from service instead of hard coding
            String userid = prefs.getString("userid", null);

            String message = sendSMSForGivenPromoter(userid);
            String phoneNo = "943229757";
            //String phoneNo = "943206118";
            //String message = getString(R.string.message);
            Calendar calendar = Calendar.getInstance();
            Log.w("MainMenuActivity:scheduleAlarm  current time", calendar.toString());
            calendar.add(Calendar.MINUTE, 1);
            scheduleSMSAlarm(phoneNo, message, calendar);
            }
        });

        OfflineStorageManager.UpdateLocalStorage(this);
    }


    /**
     * Schedules an system alarm to send an SMS with input parameters
     * @author JN
     * @param phone phone number to send SMS to
     * @param msg message content of the SMS
     * @param cal Calendar object that is the time the SMS will be sent
     */
    private void scheduleSMSAlarm(String phone, String msg, Calendar cal) {
        Intent intentAlarm = new Intent(this, SMSAlarmReceiver.class);
        intentAlarm.setAction("org.techintheworld.www.edots.MainMenuActivity");
        intentAlarm.putExtra("phone_number", phone);
        intentAlarm.putExtra("message", msg);
        Log.w("MainMenuActivity: intent message to send", msg);

        PendingIntent pIntent = PendingIntent.getBroadcast(MainMenuActivity.this,
                0, intentAlarm, 0);

        setSMSDeliveryReceivers();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pIntent);
        Log.w("MainMenuActivity:scheduleAlarm scheduled for", cal.toString());

    }

    /**
     * 
     * @author Ankit 
     * Queries the server to find all of the patients that missed their appointment today 
     * 
     * TODO: For now this just pulls all of the patients of the given promoter
     * TODO: Change this to load the true missed patients 
     */
    private void loadMissedPatients(){
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String promoterID = prefs.getString("userid", null);

  
        LoadPatientFromPromoterTask loadPatients = new LoadPatientFromPromoterTask();
        AsyncTask loadPatientsTask = loadPatients.execute(getString(R.string.server_url), promoterID);
        try {
            patients = (ArrayList<String>) loadPatientsTask.get();
            Log.v("MainMenuActivity.java: The patients in the second load are", patients.toString());
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
        
        
    }


    /**
     *  
     * @author Ankit  
     * @param patientID the patient that the sms is being sent to
     * @return bool indicating success
     * 
     * TODO: For now this just returns the patientID for everyone. 
     */
    private boolean sendSMSToPatient(String patientID){
        return true;
    }

    /**
     * 
     * @author 
     * Determines if the given patient has missed a pill, and returns the patient name if so 
     * @param patientID
     * @return boolean indicating whether patient missed pill
     * 
     * 
     * TODO: For now this just returns true for everyone
     */
    private boolean missedPill (String patientID){
        return patients.contains(patientID);
    }

    /**
     *
     * @author
     * @param promoterID id of the promoter whose patients this will send sms to
     * @return String with the aggregate summary to send to the promoter
     * 
     * This function sends the delay SMS to all of the patients of the given promoter, and returns an aggregate summary 
     */
    private String sendSMSForGivenPromoter(String promoterID){

        String aggregate = "";
        LoadPatientFromPromoterTask loadPatients = new LoadPatientFromPromoterTask();
        AsyncTask loadPatientsTask = loadPatients.execute(getString(R.string.server_url), promoterID);
        try {
            ArrayList<String> patients = (ArrayList<String>) loadPatientsTask.get();
            Log.v("MainMenuActivity.java: The patients are", patients.toString());
            loadMissedPatients();
            
            for (int i = 0; i < patients.size(); i++){
                if (missedPill(patients.get(i))){
                    Log.v("MainMenuActivity.java: This patient missed a visit", patients.get(i));
                    sendSMSToPatient(patients.get(i));
                    GetPatientFromIDTask pTask = new GetPatientFromIDTask();
                    AsyncTask p = pTask.execute(getString(R.string.server_url), patients.get(i));

                    Patient pat =  (Patient) p.get();
                    String message = pat.getName();
                    
                    aggregate += (message + "\n");
                }
                else{
                    Log.v("MainMenuActivity.java: This patient did not miss a visit: ", patients.get(i));
                }
            }
            
            Log.v("MainMenuActivity.java: The following patients for this promoter did not come: ", aggregate);
            return aggregate;

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

    /**
     * @author JN
     * Sets the Broadcast Receivers that listen to SMS delivery status
     */
    public void setSMSDeliveryReceivers(){
        final String SENT = getString(R.string.sms_sent);
        final String DELIVERED = getString(R.string.sms_delivered);
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    //TODO: code the message in strings.xml
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), SENT,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), DELIVERED,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void newPatient(View view) {
        Intent intent = new Intent(this, NewPatientDataActivity.class);
        startActivity(intent);

    }

    public void oldPatient(View view) {
        Intent intent = new Intent(this, GetPatientActivity.class);
        startActivity(intent);
    }

    /**
     * Logs out promoter, clears Shared Preferences, and removes internal files
     * @author JN
     * @param view
     */
    public void logOut(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
        boolean promoter_result = this.deleteFile(this.getString(R.string.promoter_data_filename));
        boolean patient_result = this.deleteFile(this.getString(R.string.patient_data_filename));
        if ((!promoter_result) || (!patient_result)) {
            Log.i("MainMenuActivity: Logout", "Promoter result " + promoter_result + " patient result " + patient_result);
        }
        Intent intent = new Intent(this, PromoterLoginActivity.class);
        startActivity(intent);
    }

    /**
     * @author lili
     * disable back button on this activity
     */
    @Override
    public void onBackPressed() {
        return;
    }
}
