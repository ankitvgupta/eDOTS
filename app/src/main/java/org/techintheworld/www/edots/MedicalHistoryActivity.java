package org.techintheworld.www.edots;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
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

import org.json.JSONObject;

import java.util.ArrayList;

import edots.models.Patient;
import edots.models.Visit;


public class MedicalHistoryActivity extends Activity {
    CalendarView calendar;
    Patient currentPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_history);


        //initializes the calendarview
        initializeCalendar();

//        loadPastVisits();
    }

    public void initializeCalendar() {
        calendar = (CalendarView) findViewById(R.id.calendar);

        // sets whether to show the week number.
        calendar.setShowWeekNumber(false);

        // sets the first day of week according to Calendar.
        // here we set Monday as the first day of the Calendar
        calendar.setFirstDayOfWeek(2);

        //The background color for the selected week.

        calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.green));

        //sets the color for the dates of an unfocused month.

        calendar.setUnfocusedMonthDateColor(getResources().getColor(R.color.transparent));

        //sets the color for the separator line between weeks.
        calendar.setWeekSeparatorLineColor(getResources().getColor(R.color.transparent));

        //sets the color for the vertical bar shown at the beginning and at the end of the selected date.

        calendar.setSelectedDateVerticalBar(R.color.darkgreen);
        //sets the listener to be notified upon selected date change.
        calendar.setOnDateChangeListener(new OnDateChangeListener() {
                    //show the selected date as a toast
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                Toast.makeText(getApplicationContext(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadPastVisits() {
        // sets header to Past Visits for Patient Name
        String patientName = currentPatient.getName();
        TextView header = (TextView) findViewById(R.id.medical_history_for_patient);
        header.setText("Past Visits for " + patientName);


        ArrayList<Visit> patientVisits = currentPatient.getPatientHistory();
        int numVisits = 0;
        if (patientVisits != null){
            numVisits = patientVisits.size();
        }

        String siteCode;
        String visitDate;
        String timeVal;
        String projectCode;
        String userCode;
        String visitCode;
        String visitGroupCode;

        LinearLayout encloseScrollLayout = (LinearLayout) findViewById(R.id.medicalhistory_encloseScroll);

        for (int i = (numVisits - 1); i >= 0; i--) {
            siteCode = patientVisits.get(i).getSiteCode();
            visitDate = patientVisits.get(i).getVisitDate();
            timeVal = patientVisits.get(i).getTimeVal();
            projectCode = patientVisits.get(i).getProjectCode();
            userCode = patientVisits.get(i).getUserCode();
            visitCode = patientVisits.get(i).getVisitCode();
            visitGroupCode = patientVisits.get(i).getVisitGroupCode();

            // add a new Relative Layout with all this data and append it in the Linear Layout
            RelativeLayout newVisit = new RelativeLayout(this);
            RelativeLayout.LayoutParams labelLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            newVisit.setLayoutParams(labelLayoutParams);

            // instantiating TextViews
            TextView visitHeader = new TextView(this);
            TextView project = new TextView(this);
            TextView promoter = new TextView(this);
            TextView visit = new TextView(this);
            TextView visitGroup = new TextView(this);

            // assign ID to each TextView
            visitHeader.setId(1);
            project.setId(2);
            promoter.setId(3);
            visit.setId(4);
            visitGroup.setId(5);

            // assign text to each TextView
            visitHeader.setText(siteCode + " - " + visitDate + " - " + timeVal);

            project.setText(Html.fromHtml("<b>" + "Project: " + "</b>" + projectCode));
            promoter.setText(Html.fromHtml("<b>" + "Promoter: " + "</b>" + userCode));
            visit.setText(Html.fromHtml("<b>" + "Visit: " + "</b>" + visitCode));
            visitGroup.setText(Html.fromHtml("<b>" + "Visit Group: " + "</b>" + visitGroupCode));

            // sets text size for each TextView
            visitHeader.setTextSize(20);
            project.setTextSize(20);
            promoter.setTextSize(20);
            visit.setTextSize(20);
            visitGroup.setTextSize(20);

            // set position of each TextView within RelativeLayout
            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.BELOW, visitHeader.getId());

            RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.addRule(RelativeLayout.BELOW, project.getId());

            RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp3.addRule(RelativeLayout.BELOW, promoter.getId());

            RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp4.addRule(RelativeLayout.BELOW, visit.getId());


            // append all of the above TextView elements to the Relative Layout
            newVisit.addView(visitHeader);
            newVisit.addView(project, lp1);
            newVisit.addView(promoter, lp2);
            newVisit.addView(visit, lp3);
            newVisit.addView(visitGroup, lp4);

            // add RelativeLayout to LinearLayout
            encloseScrollLayout.addView(newVisit);

            // adds horizontal divider
            View v = new View(this);
            v.setLayoutParams(new LinearLayout.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    5
            ));
            v.setBackgroundColor(Color.parseColor("#B3B3B3"));
            encloseScrollLayout.addView(v);
        }
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
