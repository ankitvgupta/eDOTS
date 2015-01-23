package org.techintheworld.www.edots;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.models.Schedule;
import edots.models.Visit;
import edots.models.VisitDay;
import edots.tasks.GetVisitPerDayLoadTask;
import edots.utils.InternetConnection;

/*
 * Written by Nishant
 * The Calendar
 * Uses the Caldroid API
 * https://github.com/roomorama/Caldroid
 */

public class MedicalHistoryActivity extends FragmentActivity {

    private Patient currentPatient;
    private Context c = this;
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat dayOfTheWeekFormatter = new SimpleDateFormat("EEEE");
    SimpleDateFormat visitDateFormatter = new SimpleDateFormat("EEE dd/MM/yyyy");
    SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");
    Date currentDate;
    Date weekAgo;
    Date monthAgo;

    boolean MondayMorning;
    boolean MondayTarde;
    boolean TuesdayMorning;
    boolean TuesdayTarde;
    boolean WednesdayMorning;
    boolean WednesdayTarde;
    boolean ThursdayMorning;
    boolean ThursdayTarde;
    boolean FridayMorning;
    boolean FridayTarde;
    boolean SaturdayMorning;
    boolean SaturdayTarde;
    boolean SundayMorning;
    boolean SundayTarde;

    boolean Monday;
    boolean Tuesday;
    boolean Wednesday;
    boolean Thursday;
    boolean Friday;
    boolean Saturday;
    boolean Sunday;
    int difference;


    String startDate;
    String endDate;
    Date startDateObj = new Date();
    Date endDateObj = new Date();

    int total_missed = 0;
    int total_received = 0;
    int total_future = 0;

    int past_week_missed = 0;
    int past_week_received = 0;

    int past_month_missed = 0;
    int past_month_received = 0;

    int startDayOfTheWeekInt;

    CaldroidFragment caldroidFragment = new CaldroidFragment();

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

        // instantiates Caldroid and saves current date, weekAgo and monthAgo dates.
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

        updateCalendar();

        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();
    }


    /*
    * Written by Nishant
    * Adds colors and listeners to calendar
    */
    public void updateCalendar() {

        Schedule patientSchedule = currentPatient.getEnrolledSchema().getSchedule();

        // gets exact schedule for the current patient
        startDate = patientSchedule.getStartDate(); // day/month/year
        endDate = patientSchedule.getEndDate(); // day/month/year
        MondayMorning = patientSchedule.scheduledLunes();
        MondayTarde = patientSchedule.scheduledLunesTarde();
        TuesdayMorning = patientSchedule.scheduledMartes();
        TuesdayTarde = patientSchedule.scheduledMartesTarde();
        WednesdayMorning = patientSchedule.scheduledMiercoles();
        WednesdayTarde = patientSchedule.scheduledMiercolesTarde();
        ThursdayMorning = patientSchedule.scheduledJueves();
        ThursdayTarde = patientSchedule.scheduledJuevesTarde();
        FridayMorning = patientSchedule.scheduledViernes();
        FridayTarde = patientSchedule.scheduledViernesTarde();
        SaturdayMorning = patientSchedule.scheduledSabado();
        SaturdayTarde = patientSchedule.scheduledSabadoTarde();
        SundayMorning = patientSchedule.scheduledDomingo();
        SundayTarde = patientSchedule.scheduledDomingoTarde();

        // checks which days of the weeks there will be visits
        Monday = (MondayMorning || MondayTarde);
        Tuesday = (TuesdayMorning || TuesdayTarde);
        Wednesday = (WednesdayMorning || WednesdayTarde);
        Thursday = (ThursdayMorning || ThursdayTarde);
        Friday = (FridayMorning || FridayTarde);
        Saturday = (SaturdayMorning || SaturdayTarde);
        Sunday = (SundayMorning || SundayTarde);

        Log.v("MedicalHistoryActivity", "Visit on Monday?: " + Monday);
        Log.v("MedicalHistoryActivity", "Visit on Tuesday?: " + Tuesday);
        Log.v("MedicalHistoryActivity", "Visit on Wednesday?: " + Wednesday);
        Log.v("MedicalHistoryActivity", "Visit on Thursday?: " + Thursday);
        Log.v("MedicalHistoryActivity", "Visit on Friday?: " + Friday);
        Log.v("MedicalHistoryActivity", "Visit on Saturday?: " + Saturday);
        Log.v("MedicalHistoryActivity", "Visit on Sunday?: " + Sunday);

        try {
            endDateObj = dateFormatter.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        startDayOfTheWeekInt = getDayOfTheWeekInt(startDate);

        assignScheduledDays();

        assignAttendedDays();

        individualDateListeners();

//        updateSummaryParameters();

        updateTreatmentTable(total_missed, total_received, total_future, past_week_missed,
                past_week_received, past_month_missed, past_month_received);
    }

    /*
    * Written by Nishant
    * Given a date, returns the day of the week for that date in integer form
    */
    public int getDayOfTheWeekInt(String date) {

        String dayOfTheWeek = "";
        try {
            startDateObj = dateFormatter.parse(date);
            dayOfTheWeek = dayOfTheWeekFormatter.format(startDateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int dateInt = 0;

        if (dayOfTheWeek.equals("Monday")) {
            return dateInt;
        } else if (dayOfTheWeek.equals("Tuesday")) {
            dateInt = 1;
        } else if (dayOfTheWeek.equals("Wednesday")) {
            dateInt = 2;
        } else if (dayOfTheWeek.equals("Thursday")) {
            dateInt = 3;
        } else if (dayOfTheWeek.equals("Friday")) {
            dateInt = 4;
        } else if (dayOfTheWeek.equals("Saturday")) {
            dateInt = 5;
        } else if (dayOfTheWeek.equals("Sunday")) {
            dateInt = 6;
        }

        return dateInt;
    }

    /*
    * Written by Nishant
    * Colors all the days that there are scheduled visits before the current date to red
    * Colors all the days that there are scheduled visits after the current date to blue (future visits)
    * Keeps count of how many scheduled visits there are in the past month, past week, total and future
    */
    public void assignScheduledDays() {

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
//                        total_missed++;
//                        if (newDate.after(weekAgo)){
//                            past_week_missed++;
//                        } else if (newDate.after(monthAgo)) {
//                            past_month_missed++;
//                        }
                    } else if (newDate.after(currentDate)) {
                        caldroidFragment.setBackgroundResourceForDate(R.color.blue_normal, newDate);
//                        total_future++;
                    }
                    c.add(Calendar.DATE, 7);
                    newDate = c.getTime();
                } while (newDate.before(endDateObj));
            }
        }
    }

    /*
    * Written by Nishant
    * Colors the days with all successfully attended visits (both morning and afternoon) to green
    * Also updates the summary parameters
    */
    public void assignAttendedDays() {

        ArrayList<VisitDay> visitDays = new ArrayList<VisitDay> ();

        String dbStartDate = "";
        String dbEndDate = "";

        try {
            Date temp = dateFormatter.parse(startDate);
            dbStartDate = dbDateFormat.format(temp);

            temp = dateFormatter.parse(endDate);
            dbEndDate = dbDateFormat.format(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            GetVisitPerDayLoadTask getVisitDays = new GetVisitPerDayLoadTask();
            AsyncTask visit = getVisitDays.execute(getString(R.string.server_url),
                    currentPatient.getPid(), dbStartDate, dbEndDate);
            visitDays = (ArrayList<VisitDay>) visit.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch(NullPointerException e)
        {
            if (!InternetConnection.checkConnection(this)) {
                AlertError(getString(R.string.no_internet_title), getString(R.string.no_internet_connection));
            } else {
                e.printStackTrace();
            }
        }

        Log.v("MedicalHistoryActivity", "visitDays: " + visitDays);

        int numDays = visitDays.size();
        Calendar c = Calendar.getInstance();
        int day_of_week;
        int morning;
        int afternoon;
        boolean morningVisit;
        boolean afternoonVisit;
        int count_true;


        for (int i = (numDays - 1); i >= 0; i--) {
            VisitDay visitDay = visitDays.get(i);
            Date visitDate = visitDay.getDate();

            Log.v("MedicalHistoryActivity", "visitDate: " + visitDate);
            morning = visitDay.getMorning();

            Log.v("MedicalHistoryActivity", "morning: " + morning);
            afternoon = visitDay.getAfternoon();

            Log.v("MedicalHistoryActivity", "afternoon: " + afternoon);

            count_true = 0;

            if (morning == 0) {
                morningVisit = false;
            } else {
                morningVisit = true;
                count_true++;
            }

            if (afternoon == 0) {
                afternoonVisit = false;
            } else {
                afternoonVisit = true;
                count_true++;
            }

            Log.v("MedicalHistoryActivity", "morningVisitBool: " + morningVisit);

            Log.v("MedicalHistoryActivity", "afternoonVisitBool: " + afternoonVisit);

            c.setTime(visitDate);
            day_of_week = c.get(Calendar.DAY_OF_WEEK);


            // if both morningVisit and afternoonVisit match MondayMorning and MondayTarde,
            // count how many of those two are true and increase total_received by that amount.
            // then check if the visitDate is within the past month or week and update those values by that same amount

            // if only morningVisit matches MondayMorning, and morningVisit is true total_received should increase
            // then check if the visitDate is within the past month or week and update those as well
            // this also means that the afternoonVisit was missed, so total_missed should increase
            // then check if the visitDate is within the past month or week and update those as well

            // if afternoonVisit matches MondayTarde, and afternoonVisit is true total_received should increase
            // then check if the visitDate is within the past month or week and update those as well
            // this also means that the morningVisit was missed, so total_miss
            // ed should increase
            // then check if the visitDate is within the past month or week and update those as well

            // if neither morningVisit or afternoonVisit match, that means both are missed and total_missed
            // should increase by 2.
            // then check if the visitDate is within the past month or week and update those as well

            if (day_of_week == Calendar.MONDAY) {
                if (visitDate.after(currentDate)) {
                    if (MondayMorning) {
                        total_future++;
                    }
                    if (MondayTarde) {
                        total_future++;
                    }
                } else if (morningVisit == MondayMorning && afternoonVisit == MondayTarde) {
                    total_received += count_true;
                    updateReceivedWeekAndMonthCounters(visitDate, count_true);
                    colorGreen(morningVisit, afternoonVisit, visitDate);
                } else if (morningVisit == MondayMorning && morningVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else if (afternoonVisit == MondayTarde && afternoonVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else {
                    total_missed += 2;
                    updateMissedWeekAndMonthCounters(visitDate, 2);
                }
            } else if (day_of_week == Calendar.TUESDAY) {
                if (visitDate.after(currentDate)) {
                    if (TuesdayMorning) {
                        total_future++;
                    }
                    if (TuesdayTarde) {
                        total_future++;
                    }
                } else if (morningVisit == TuesdayMorning && afternoonVisit == TuesdayTarde) {
                    total_received += count_true;
                    updateReceivedWeekAndMonthCounters(visitDate, count_true);
                    colorGreen(morningVisit, afternoonVisit, visitDate);
                } else if (morningVisit == TuesdayMorning && morningVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else if (afternoonVisit == TuesdayTarde && afternoonVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else {
                    total_missed += 2;
                    updateMissedWeekAndMonthCounters(visitDate, 2);
                }
            } else if (day_of_week == Calendar.WEDNESDAY) {
                if (visitDate.after(currentDate)) {
                    if (WednesdayMorning) {
                        total_future++;
                    }
                    if (WednesdayTarde) {
                        total_future++;
                    }
                } else if (morningVisit == WednesdayMorning && afternoonVisit == WednesdayTarde) {
                    total_received += count_true;
                    updateReceivedWeekAndMonthCounters(visitDate, count_true);
                    colorGreen(morningVisit, afternoonVisit, visitDate);
                } else if (morningVisit == WednesdayMorning && morningVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else if (afternoonVisit == WednesdayTarde && afternoonVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else {
                    total_missed += 2;
                    updateMissedWeekAndMonthCounters(visitDate, 2);
                }
            } else if (day_of_week == Calendar.THURSDAY) {
                if (visitDate.after(currentDate)) {
                    if (ThursdayMorning) {
                        total_future++;
                    }
                    if (ThursdayTarde) {
                        total_future++;
                    }
                } else if (morningVisit == ThursdayMorning && afternoonVisit == ThursdayTarde) {
                    total_received += count_true;
                    updateReceivedWeekAndMonthCounters(visitDate, count_true);
                    colorGreen(morningVisit, afternoonVisit, visitDate);
                } else if (morningVisit == ThursdayMorning && morningVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else if (afternoonVisit == ThursdayTarde && afternoonVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else {
                    total_missed += 2;
                    updateMissedWeekAndMonthCounters(visitDate, 2);
                }
            } else if (day_of_week == Calendar.FRIDAY) {
                if (visitDate.after(currentDate)) {
                    if (FridayMorning) {
                        total_future++;
                    }
                    if (FridayTarde) {
                        total_future++;
                    }
                } else if (morningVisit == FridayMorning && afternoonVisit == FridayTarde) {
                    total_received += count_true;
                    updateReceivedWeekAndMonthCounters(visitDate, count_true);
                    colorGreen(morningVisit, afternoonVisit, visitDate);
                } else if (morningVisit == FridayMorning && morningVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else if (afternoonVisit == FridayTarde && afternoonVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else {
                    total_missed += 2;
                    updateMissedWeekAndMonthCounters(visitDate, 2);
                }
            } else if (day_of_week == Calendar.SATURDAY) {
                if (visitDate.after(currentDate)) {
                    if (SaturdayMorning) {
                        total_future++;
                    }
                    if (SaturdayTarde) {
                        total_future++;
                    }
                } else if (morningVisit == SaturdayMorning && afternoonVisit == SaturdayTarde) {
                    total_received += count_true;
                    updateReceivedWeekAndMonthCounters(visitDate, count_true);
                    colorGreen(morningVisit, afternoonVisit, visitDate);
                } else if (morningVisit == SaturdayMorning && morningVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else if (afternoonVisit == SaturdayTarde && afternoonVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else {
                    total_missed += 2;
                    updateMissedWeekAndMonthCounters(visitDate, 2);
                }
            } else if (day_of_week == Calendar.SUNDAY) {
                if (visitDate.after(currentDate)) {
                    if (SundayMorning) {
                        total_future++;
                    }
                    if (SundayTarde) {
                        total_future++;
                    }
                } else if (morningVisit == SundayMorning && afternoonVisit == SundayTarde) {
                    total_received += count_true;
                    updateReceivedWeekAndMonthCounters(visitDate, count_true);
                    colorGreen(morningVisit, afternoonVisit, visitDate);
                } else if (morningVisit == SundayMorning && morningVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else if (afternoonVisit == SundayTarde && afternoonVisit) {
                    total_received++;
                    total_missed++;
                    updateMissedWeekAndMonthCounters(visitDate, 1);
                    updateReceivedWeekAndMonthCounters(visitDate, 1);
                } else {
                    total_missed += 2;
                    updateMissedWeekAndMonthCounters(visitDate, 2);
                }
            }
        }
    }


    /*
    * Written by Nishant
    * If the passed in date is within the past month, it updates the past month counter by the passed in amount
    * If the passed in date is also within the past week, it updates the past week counter by the passed in amount
    */
    public void updateMissedWeekAndMonthCounters (Date visitDate, int incrementAmount) {
        if (visitDate.after(monthAgo)) {
            past_month_missed += incrementAmount;
        }
        if (visitDate.after(weekAgo)) {
            past_week_missed += incrementAmount;
        }
    }

    public void updateReceivedWeekAndMonthCounters (Date visitDate, int incrementAmount) {
        if (visitDate.after(monthAgo)) {
            past_month_received += incrementAmount;
        }
        if (visitDate.after(weekAgo)) {
            past_week_received += incrementAmount;
        }
    }

    /*
    * Written by Nishant
    * If there exists a morningVisit or afternoonVisit, then color the Date green.
    * This is given that the schedule matches, which is checked for before this function is called.
    */
    public void colorGreen (boolean morningVisit, boolean afternoonVisit, Date visitDate) {
        if (morningVisit || afternoonVisit) {
            caldroidFragment.setBackgroundResourceForDate(R.color.green, visitDate);
        }
    }

    /*
    * Written by Nishant
    * Updates the count of missed and attended visits within the past week and month and total.
    */
    public void updateSummaryParameters() {
        // gets an Array of Visits that were attended by the patient
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
    }

    /*
    * Written by Nishant
    * Attaches a listener to each date, upon click will load the medical history for that day
    */
    public void individualDateListeners() {
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
    }

    /*
    * Written by Nishant
    * Updates the summary statistics (below the calendar) for visits missed and attended in the
    * past week, month, total and future
    */
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
     * Currently inactive because the button for this is disabled in the XML
     */
    public void loadFullHistory (View view) {
        Intent intent = new Intent(this, ShowVisitActivity.class);
        intent.putExtra("Patient", currentPatient.toString());
        intent.putExtra("Visit Date", "");
        startActivity(intent);
    }

    /*
     * Written by Nishant
     * Alert Dialog in case of User Error
     */
    public void AlertError(String title, String message) {
        // Alert if username and password are not entered
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton (Dialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // here you can add functions
            }
        });
        alertDialog.show();
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
