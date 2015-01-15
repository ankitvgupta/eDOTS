package edots.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by jfang on 1/14/15.
 */
public class SMSAlarmReceiver extends BroadcastReceiver {
    private final String sms_action = "org.techintheworld.www.edots.MainMenuActivity";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if (sms_action.equals(action)) {
            String phoneNumber = "943229757"; //my phone number usually entered here
            String message = "Hi you missed your appointment";
            sendSMS(phoneNumber, message);
        }


    }

    public void sendSMS(String phoneNumber, String message){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
        Log.w("SMSAlarmReceiver: On Receive ", "SMS message sent");
    }
}
