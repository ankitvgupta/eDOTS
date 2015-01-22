package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edots.utils.OfflineStorageManager;
import edots.utils.SMSAlarmReceiver;
import edots.utils.SMSSender;

public class MainMenuActivity extends Activity {
    Button btnSendSMS;
    ArrayList<String> patients = new ArrayList<String>(); // an arraylist of the patients that need to be messaged today

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }


    protected void onResume(){
        super.onResume();
        OfflineStorageManager sm = new OfflineStorageManager(this);
        String upload_success = getString(R.string.upload_visit_success);
        if (sm.UploadLocalVisit()){
            Toast.makeText(getBaseContext(),upload_success,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Linked to SMS Activity
     * @param v
     */
    public void SendSMS(View v){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String userid = prefs.getString("userid", null);
        SMSSender sender = new SMSSender(this);

        try {
            String patient_names = sender.sendSMSForGivenPromoter(userid);
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e("MainMenuActivity:SendSMS", "Error in getting missing patients");

        }
    }

    /**
     * Schedules alarm at a specific calendar time
     * To be used for automatic sending of SMS
     * @deprecated
     * @author JN
     * @param view
     */
    public void autoSendSMS(View view){
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String userid = prefs.getString("userid", null);
        SMSSender sender = new SMSSender(this);

        // test phone numbers
        String phoneNo = "943229757";
        //String phoneNo = "943206118";
        String message = getString(R.string.message);
        Calendar calendar = Calendar.getInstance();
        Log.w("MainMenuActivity:scheduleAlarm  current time", calendar.toString());
        calendar.add(Calendar.MINUTE, 1);
        scheduleSMSAlarm(phoneNo, message, calendar);

    }
    /**
     * Schedules an system alarm to send an SMS with input parameters
     * @author JN
     * @deprecated
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

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pIntent);
        Log.w("MainMenuActivity:scheduleAlarm scheduled for", cal.toString());

    }

    /**
     * Linked to the refresh button to manually allwo updating of local storage
     * @author JN
     * @param view
     */
    public void updateLocalManual(View view){
        OfflineStorageManager sm = new OfflineStorageManager(this);
        if (sm.CanUpdateLocalStorage()){
            sm.UpdateLocalStorage();
            long time = sm.GetLastLocalUpdateTime();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            String dateString = formatter.format(new Date(time));
            String to_print = getString(R.string.last_updated) + " " + dateString;
            Toast.makeText(getBaseContext(),to_print,
                    Toast.LENGTH_SHORT).show();
        }
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
