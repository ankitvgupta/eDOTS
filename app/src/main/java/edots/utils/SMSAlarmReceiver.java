package edots.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import org.techintheworld.www.edots.R;

import java.util.ArrayList;

/**
 * Created by jfang on 1/14/15.
 */
public class SMSAlarmReceiver extends BroadcastReceiver {
    private final String sms_action = "org.techintheworld.www.edots.MainMenuActivity";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        String phone_number = intent.getExtras().getString("phone_number");
        String message = intent.getExtras().getString("message");
        Log.w("SMSAlarmReceiver: intent extras get message", message);

        if (sms_action.equals(action)) {
            sendSMS(phone_number, message, context);
        }
    }

    /**
     * Uses SMSManager to send sms with the phone number and message
     * @author JN
     * @param phoneNumber to send text to
     * @param message content of text to send
     * @param c context of the application
     */
    public void sendSMS(String phoneNumber, String message, final Context c){
        SmsManager sms = SmsManager.getDefault();
        String SENT = c.getString(R.string.sms_sent);
        String DELIVERED = c.getString(R.string.sms_delivered);

        ArrayList<String> parts = sms.divideMessage(message);
        ArrayList<PendingIntent> sentPIs = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPIs = new ArrayList<PendingIntent>();
        for (int i = 0; i < parts.size(); i++){
            sentPIs.add(PendingIntent.getBroadcast(c, 0,
                    new Intent(SENT), 0));

            deliveredPIs.add(PendingIntent.getBroadcast(c, 0,
                    new Intent(DELIVERED), 0));
        }

        sms.sendMultipartTextMessage(phoneNumber, null, parts, sentPIs, deliveredPIs);

        Log.w("SMSAlarmReceiver: On Receive ", "SMS message sent");
        Log.v("SMSAlarmReceiver: The message being sent is", message);
    }
}
