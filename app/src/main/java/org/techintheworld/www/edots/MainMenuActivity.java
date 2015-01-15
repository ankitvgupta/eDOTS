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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import edots.models.Visit;
import edots.tasks.GetHistoryLoadTask;
import edots.tasks.LoadPatientFromPromoterTask;
import edots.utils.OfflineStorageManager;
import edots.utils.SMSAlarmReceiver;

public class MainMenuActivity extends Activity {
    Button btnSendSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String phoneNo = "943229757";
                String message = Integer.toString(R.string.message);
                
                
                if (phoneNo.length() > 0 && message.length() > 0) {
                    // TODO: fix this to take in phone number and message
                    //sendSMS(phoneNo, message);
                    scheduleAlarm();
                } else
                    Toast.makeText(getBaseContext(),
                            "Please enter both phone number and message.",
                            Toast.LENGTH_SHORT).show();
            }
        });

        OfflineStorageManager.UpdateLocalStorage(this);


    }


    private void scheduleAlarm() {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        Log.w("MainMenuActivity:scheduleAlarm  current time", calendar.toString());
        calendar.add(Calendar.MINUTE, 1);

        Intent intentAlarm = new Intent(this, SMSAlarmReceiver.class);
        intentAlarm.setAction("org.techintheworld.www.edots.MainMenuActivity");

        //TODO: what is 234324243
        PendingIntent pIntent = PendingIntent.getBroadcast(MainMenuActivity.this,
                0, intentAlarm, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        Log.w("MainMenuActivity:scheduleAlarm scheduled for", calendar.toString());

    }

    /**
     * 
     * @param promoterID id of the promoter whose patients this will send sms to
     * @return String with the aggregate summary to send to the promoter
     * 
     * This function sends the delay SMS to all of the patients of the given promoter, and returns an aggregate summary 
     */
    private String sendSMSForGivenPromoter(String promoterID){

        LoadPatientFromPromoterTask loadPatients = new LoadPatientFromPromoterTask();
        AsyncTask loadPatientsTask = loadPatients.execute("http://demo.sociosensalud.org.pe", promoterID);
        
        
    }

    /**
     * @author Ankit
     * 
     * This function is a test one to lay out the logic for determining who to send the SMS to 
     */
    private void determineSMS(){
        GetCoordinatorsLoadTask loadCoordinators = new GetCoordinatorsLoadTask(); // This task doesn't exist yet.
        AsyncTask loadCoordinatorsTask = loadCoordinators.execute("http://demo.sociosensalud.org.pe");
        try {
            ArrayList<String> coordinators = (ArrayList<String>) loadCoordinatorsTask.get();
            Log.v("Patient.java: The coordinators are", coordinators.toString());
            
            for (int i = 0; i < coordinators.size(); i++){ // for each coordinator get the promoters
                GetPromotersLoadTask loadPromoters = new GetPromotersLoadTask();
                AsyncTask loadPromotersTask = loadPromoters.execute("http://demo.sociosensalud.org.pe", coordinators.get(i));
                ArrayList<String> promoters = (ArrayList<String>) loadPromotersTask.get();
                
                
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




    }

    //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    //TODO: code the message in strings.xml
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
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
                        //TODO: define delivered in strings.xml instead of hardcoding
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
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
}
