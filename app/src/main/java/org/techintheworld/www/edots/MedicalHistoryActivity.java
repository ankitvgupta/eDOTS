package org.techintheworld.www.edots;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edots.models.Patient;
import edots.models.Schedule;
import edots.models.Visit;

/*
 * Written by Nishant
 * The Calendar
 */

public class MedicalHistoryActivity extends FragmentActivity {

    private Patient currentPatient;
    private Context c = this;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dayOfTheWeekFormatter = new SimpleDateFormat("EEEE");
    SimpleDateFormat visitDateFormatter = new SimpleDateFormat("EEE dd/MM/yyyy");
    Date currentDate;
    Date weekAgo;
    Date monthAgo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        try {
            currentPatient = new Patient(getIntent().getExtras().getString("Patient"));
        } catch (Exception e) {
            // TODO: Don't print the stack trace, give some sort of dialog box instead
            e.printStackTrace();
        }

        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        currentDate = cal.getTime();
        cal.add(Calendar.DATE, -7);
        weekAgo = cal.getTime();
        cal.add(Calendar.MONTH, -1);
        monthAgo = cal.getTime();
        cal.add(Calendar.MONTH, 1);

        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        caldroidFragment.setArguments(args);

        updateCalendar(caldroidFragment, cal);

        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();
    }


    /*
    * Written by Nishant
    * Adds colors and listeners to calendar
    */
    public void updateCalendar(CaldroidFragment caldroidFragment, Calendar cal) {

        Schedule patientSchedule = currentPatient.getPatientSchedule();

        String startDate = patientSchedule.getStartDate(); // day/month/year
        String endDate = patientSchedule.getEndDate(); // day/month/year
        Boolean Monday = patientSchedule.scheduledLunes();
        Boolean MondayTarde = patientSchedule.scheduledLunesTarde();
        Boolean Tuesday = patientSchedule.scheduledMartes();
        Boolean TuesdayTarde = patientSchedule.scheduledMartesTarde();
        Boolean Wednesday = patientSchedule.scheduledMiercoles();
        Boolean WednesdayTarde = patientSchedule.scheduledMiercolesTarde();
        Boolean Thursday = patientSchedule.scheduledJueves();
        Boolean ThursdayTarde = patientSchedule.scheduledJuevesTarde();
        Boolean Friday = patientSchedule.scheduledViernes();
        Boolean FridayTarde = patientSchedule.scheduledViernesTarde();
        Boolean Saturday = patientSchedule.scheduledSabado();
        Boolean SaturdayTarde = patientSchedule.scheduledSabadoTarde();
        Boolean Sunday = patientSchedule.scheduledDomingo();
        Boolean SundayTarde = patientSchedule.scheduledDomingoTarde();

        int total_missed = 0;
        int total_received = 0;
        int total_future = 0;

        int past_week_missed = 0;
        int past_week_received = 0;

        int past_month_missed = 0;
        int past_month_received = 0;

        Date startDateObj = new Date();
        Date endDateObj = new Date();
        String startDayOfTheWeek = "";
        String endDayOfTheWeek = "";

        try {
            startDateObj = dateFormatter.parse(startDate);
            startDayOfTheWeek = dayOfTheWeekFormatter.format(startDateObj);

            endDateObj = dateFormatter.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int startDayOfTheWeekInt = 0;

        if (startDayOfTheWeek.equals("Monday")) {
            startDayOfTheWeekInt = 0;
        } else if (startDayOfTheWeek.equals("Tuesday")) {
            startDayOfTheWeekInt = 1;
        } else if (startDayOfTheWeek.equals("Wednesday")) {
            startDayOfTheWeekInt = 2;
        } else if (startDayOfTheWeek.equals("Thursday")) {
            startDayOfTheWeekInt = 3;
        } else if (startDayOfTheWeek.equals("Friday")) {
            startDayOfTheWeekInt = 4;
        } else if (startDayOfTheWeek.equals("Saturday")) {
            startDayOfTheWeekInt = 5;
        } else if (startDayOfTheWeek.equals("Sunday")) {
            startDayOfTheWeekInt = 6;
        }

        int difference;
        boolean[] weekdays = {Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday};

        for (int j = 0; j < weekdays.length; j++) {
            if (weekdays[j]) {
                difference = j - startDayOfTheWeekInt;

                Calendar c = Calendar.getInstance();
                c.setTime(startDateObj);
                c.add(Calendar.DATE, difference);  // number of days to add
                Date newDate = c.getTime();

                do {
                    if (newDate.before(currentDate)) {
                        caldroidFragment.setBackgroundResourceForDate(R.color.red, newDate);
                        total_missed++;
                        if (newDate.after(weekAgo)){
                            past_week_missed++;
                        } else if (newDate.after(monthAgo)) {
                            past_month_missed++;
                        }
                    } else if (newDate.after(currentDate)) {
                        caldroidFragment.setBackgroundResourceForDate(R.color.blue_normal, newDate);
                        total_future++;
                    }
                    c.add(Calendar.DATE, 7);
                    newDate = c.getTime();
                } while (newDate.before(endDateObj));
            }
        }

        ArrayList<Visit> patientVisits = currentPatient.getPatientHistory(this);
        int numVisits = 0;
        if (patientVisits != null) {
            numVisits = patientVisits.size();
        }

        String visitDate;
        Date visitDateObj = new Date();

        for (int i = (numVisits - 1); i >= 0; i--) {
            try {
                visitDate = patientVisits.get(i).getVisitDate(); // day of the week day/month/year
                visitDateObj = visitDateFormatter.parse(visitDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            caldroidFragment.setBackgroundResourceForDate(R.color.green, visitDateObj);

            if (visitDateObj.after(weekAgo)) {
                past_week_received++;
                past_week_missed--;
            } else if (visitDateObj.after(monthAgo)) {
                past_month_received++;
                past_month_missed--;
            }

            total_missed--;
            total_received++;
        }

        CaldroidListener listener = new CaldroidListener() {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            @Override
            public void onSelectDate(Date date, View view) {
                Intent intent = new Intent(c, ShowVisitActivity.class);
                intent.putExtra("Patient", currentPatient.toString());
                intent.putExtra("Visit Date", formatter.format(date));
                startActivity(intent);
            }
        };

        caldroidFragment.setCaldroidListener(listener);
        updateTreatmentTable(total_missed, total_received, total_future, past_week_missed,
                past_week_received, past_month_missed, past_month_received);
    }

    public void updateTreatmentTable(int total_missed, int total_received, int total_future,
                                     int past_week_missed, int past_week_received,
                                     int past_month_missed, int past_month_received) {

        TextView pastWeekMissed = (TextView) findViewById(R.id.past_week_missed);
        TextView pastWeekReceived = (TextView) findViewById(R.id.past_week_received);

        pastWeekMissed.setText(Integer.toString(past_week_missed));
        pastWeekReceived.setText(Integer.toString(past_week_received));

        TextView pastMonthMissed = (TextView) findViewById(R.id.past_month_missed);
        TextView pastMonthReceived = (TextView) findViewById(R.id.past_month_received);

        pastMonthMissed.setText(Integer.toString(past_month_missed));
        pastMonthReceived.setText(Integer.toString(past_month_received));

        TextView totalMissed = (TextView) findViewById(R.id.total_missed);
        TextView totalReceived = (TextView) findViewById(R.id.total_received);
        TextView totalFuture = (TextView) findViewById(R.id.total_future);

        totalMissed.setText(Integer.toString(total_missed));
        totalReceived.setText(Integer.toString(total_received));
        totalFuture.setText(Integer.toString(total_future));
    }

    /*
     * Written by Nishant
     * Loads full medical history of patient
     */
    public void loadFullHistory (View view) {
        Intent intent = new Intent(this, ShowVisitActivity.class);
        intent.putExtra("Patient", currentPatient.toString());
        intent.putExtra("Visit Date", "");
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medical_history, menu);
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
}
