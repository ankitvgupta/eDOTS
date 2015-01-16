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
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edots.models.Patient;
import edots.models.Visit;


public class ShowVisitActivity extends Activity {
    Patient currentPatient;
    Date selectedDate;

    String siteCode;
    String visitDate;
    String timeVal;
    String projectCode;
    String userCode;
    String visitCode;
    String visitGroupCode;

    String visitDay;
    String visitMonth;
    String visitYear;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    DateFormat dateFormatter = DateFormat.getDateInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_visit);

        try {
            currentPatient = new Patient(getIntent().getExtras().getString("Patient"));
            String date = (getIntent().getExtras().getString("Visit Date"));
            if (date.equals("")) {
                loadPastVisits();
            } else {
                selectedDate = formatter.parse(date);
                loadOneVisit(selectedDate);
            }
        }
        catch (Exception e){
            // TODO: Don't print the stack trace, give some sort of dialog box instead
            e.printStackTrace();
        }

    }

    public void loadOneVisit (Date selectedDate) {
        // sets header to Past Visits for Patient Name
        String patientName = currentPatient.getName();
        TextView header = (TextView) findViewById(R.id.medical_history_for_patient);
        header.setText("Past Visits for " + patientName + " on " + dateFormatter.format(selectedDate));

        ArrayList<Visit> patientVisits = currentPatient.getPatientHistory();
        int numVisits = 0;
        if (patientVisits != null) {
            numVisits = patientVisits.size();
        }

        LinearLayout encloseScrollLayout = (LinearLayout) findViewById(R.id.medicalhistory_encloseScroll);

        boolean dateMatchFound = false;

        for (int i = (numVisits - 1); i >=0; i--) {
            siteCode = patientVisits.get(i).getLocaleCode();
            visitDate = patientVisits.get(i).getVisitDate();
            timeVal = patientVisits.get(i).getVisitTime();
            projectCode = patientVisits.get(i).getProjectCode();
            userCode = patientVisits.get(i).getPromoterId();
            visitCode = patientVisits.get(i).getVisitCode();
            visitGroupCode = patientVisits.get(i).getVisitGroupCode();

            visitDay = visitDate.substring(4,6);
            int visitDayInt = Integer.parseInt(visitDay);

            visitMonth = visitDate.substring(7,9);
            int visitMonthInt = Integer.parseInt(visitMonth);
            visitMonthInt = visitMonthInt - 1;

            visitYear = visitDate.substring(10,14);
            int visitYearInt = Integer.parseInt(visitYear);
            visitYearInt =  visitYearInt - 1900;

            Date greenDate = new Date(visitYearInt, visitMonthInt, visitDayInt);
            if (greenDate.compareTo(selectedDate) == 0) {
                dateMatchFound = true;
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

        if (!dateMatchFound) {
            TextView visitHeader = new TextView(this);
            visitHeader.setText("No Visits Logged");
            visitHeader.setTextSize(20);
            encloseScrollLayout.addView(visitHeader);
        }
    }

    public void loadPastVisits() {
        // sets header to Past Visits for Patient Name
        String patientName = currentPatient.getName();
        TextView header = (TextView) findViewById(R.id.medical_history_for_patient);
        header.setText("All Past Visits for " + patientName);


        ArrayList<Visit> patientVisits = currentPatient.getPatientHistory();
        int numVisits = 0;
        if (patientVisits != null){
            numVisits = patientVisits.size();
        }

        LinearLayout encloseScrollLayout = (LinearLayout) findViewById(R.id.medicalhistory_encloseScroll);

        for (int i = (numVisits - 1); i >= 0; i--) {
            siteCode = patientVisits.get(i).getLocaleCode();
            visitDate = patientVisits.get(i).getVisitDate();
            timeVal = patientVisits.get(i).getVisitTime();
            projectCode = patientVisits.get(i).getProjectCode();
            userCode = patientVisits.get(i).getPromoterId();
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
        getMenuInflater().inflate(R.menu.menu_show_visit, menu);
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
