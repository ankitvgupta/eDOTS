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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import edots.models.Patient;
import edots.models.Visit;

public class MedicalHistoryActivity extends FragmentActivity {
    Patient currentPatient;
    Context c = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);

        // if a patient was passed in, pre-load that patient
        try {
            currentPatient = new Patient(getIntent().getExtras().getString("Patient"));
        } catch (Exception e) {
            // TODO: Don't print the stack trace, give some sort of dialog box instead
            e.printStackTrace();
        }

        CaldroidFragment caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();

//        cal.add(Calendar.DATE, -21);
//        Date threeWeeksPrior = cal.getTime();
//
//        cal.add(Calendar.DATE, 28);
//        Date oneWeekAhead = cal.getTime();
//
//        cal.add(Calendar.DATE, -7); // reset time
//
//        caldroidFragment.setSelectedDates(threeWeeksPrior, oneWeekAhead);

        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        // args.putBoolean(CaldroidFragment.ENABLE_CLICK_ON_DISABLED_DATES, true);
        caldroidFragment.setArguments(args);

        updateCalendar(caldroidFragment, cal);

        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();
    }


    // adds colors and listeners
    public void updateCalendar(CaldroidFragment caldroidFragment, Calendar cal) {
        ArrayList<Visit> patientVisits = currentPatient.getPatientHistory();
        int numVisits = 0;
        if (patientVisits != null) {
            numVisits = patientVisits.size();
        }

        String visitDate;
        String visitDay;
        String visitMonth;
        String visitYear;

//        Date greenDate = new Date();

        for (int i = (numVisits - 1); i >= 0; i--) {
            visitDate = patientVisits.get(i).getVisitDate(); // Fri 05/09/2014; day/month/year ID: 12345671
            Log.v("MedicalHistoryActivity: ", "Visit Date: " + visitDate);

            visitDay = visitDate.substring(4,6);
            int visitDayInt = Integer.parseInt(visitDay);

            visitMonth = visitDate.substring(7,9);
            int visitMonthInt = Integer.parseInt(visitMonth);
            visitMonthInt = visitMonthInt - 1;

            visitYear = visitDate.substring(10,14);
            int visitYearInt = Integer.parseInt(visitYear);
            visitYearInt =  visitYearInt - 1900;

            Date greenDate = new Date(visitYearInt, visitMonthInt, visitDayInt); // January 16, 1963
            caldroidFragment.setBackgroundResourceForDate(R.color.green, greenDate);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            Log.v("MedicalHistoryActivity: ", "greenDate: " + formatter.format(greenDate));


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
