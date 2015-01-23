package org.techintheworld.www.edots;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.models.Schedule;
import edots.models.Visit;
import edots.models.VisitDay;
import edots.tasks.GetVisitPerDayLoadTask;
import edots.utils.InternetConnection;

/*
 * Written by Nishant
 * Displays medical history of patient - either the full medical history or just on a particular date
 */
public class ShowVisitActivity extends Activity {
    Patient currentPatient;
    Date selectedDate;
    Date currentDate;

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

    TextView morningHeader;
    TextView afternoonHeader;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat dateFormatter = DateFormat.getDateInstance();
    SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");
    SimpleDateFormat customDateFormat = new SimpleDateFormat("dd/MM/yyyy");


    LinearLayout encloseScrollLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_visit);

        morningHeader = new TextView(this);
        afternoonHeader= new TextView(this);
        encloseScrollLayout = (LinearLayout) findViewById(R.id.medicalhistory_encloseScroll);
        Calendar cal = Calendar.getInstance();
        currentDate = cal.getTime();

        try {
            currentPatient = new Patient(getIntent().getExtras().getString("Patient"));
            String date = (getIntent().getExtras().getString("Visit Date"));
            if (date.equals("")) {
                loadPastVisits();
            } else {
                selectedDate = formatter.parse(date);
//                loadOneVisit(selectedDate);
                loadDateStats(selectedDate);
            }
        }
        catch (Exception e){
            // TODO: Don't print the stack trace, give some sort of dialog box instead
            e.printStackTrace();
        }

    }

    // display the header
    // see if morning visit was missed
    // see if afternoon visit was missed
    // display which visits were missed, if any
    public void loadDateStats (Date selectedDate) {
        Schedule patientSchedule = currentPatient.getEnrolledSchema().getSchedule();

        // gets exact schedule for the current patient
        String startDate = patientSchedule.getStartDate(); // day/month/year
        String endDate = patientSchedule.getEndDate(); // day/month/year
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

        ArrayList<VisitDay> visitDays = new ArrayList<VisitDay> ();

        String dbStartDate = "";
        String dbEndDate = "";

        try {
            Date temp = customDateFormat.parse(startDate);
            dbStartDate = dbDateFormat.format(temp);

            temp = customDateFormat.parse(endDate);
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


        int numDays = visitDays.size();
        Calendar c = Calendar.getInstance();
        int day_of_week;
        int morning;
        int afternoon;
        boolean morningVisit;
        boolean afternoonVisit;

        morningHeader.setId(6);
        afternoonHeader.setId(7);

        boolean dateMatchFound = false;
        boolean futureDate = false;

        for (int i = (numDays - 1); i >= 0; i--) {
            VisitDay visitDay = visitDays.get(i);
            Date visitDate = visitDay.getDate();

            if (visitDate.compareTo(selectedDate) == 0) {
                dateMatchFound = true;

                String patientName = currentPatient.getName();
                TextView header = (TextView) findViewById(R.id.medical_history_for_patient);
                header.setText("Visit Information for " + patientName + " on " + dateFormatter.format(selectedDate));

                morning = visitDay.getMorning();
                afternoon = visitDay.getAfternoon();

                if (morning == 0) {
                    morningVisit = false;
                } else {
                    morningVisit = true;
                }

                if (afternoon == 0) {
                    afternoonVisit = false;
                } else {
                    afternoonVisit = true;
                }

                c.setTime(visitDate);
                day_of_week = c.get(Calendar.DAY_OF_WEEK);

                if (selectedDate.after(currentDate)) {
                    Log.v("entered future date condition", "hi");
                    futureDate = true;
                }

                if (day_of_week == Calendar.MONDAY) {
                    setHeaderText(MondayMorning, MondayTarde, morningVisit, afternoonVisit, futureDate);
                } else if (day_of_week == Calendar.TUESDAY) {
                    setHeaderText(TuesdayMorning, TuesdayTarde, morningVisit, afternoonVisit, futureDate);
                } else if (day_of_week == Calendar.WEDNESDAY) {
                  setHeaderText(WednesdayMorning, WednesdayTarde, morningVisit, afternoonVisit, futureDate);
                } else if (day_of_week == Calendar.THURSDAY) {
                    setHeaderText(ThursdayMorning, ThursdayTarde, morningVisit, afternoonVisit, futureDate);
                } else if (day_of_week == Calendar.FRIDAY) {
                    setHeaderText(FridayMorning, FridayTarde, morningVisit, afternoonVisit, futureDate);
                } else if (day_of_week == Calendar.SATURDAY) {
                    setHeaderText(SaturdayMorning, SaturdayTarde, morningVisit, afternoonVisit, futureDate);
                } else if (day_of_week == Calendar.SUNDAY) {
                    setHeaderText(SundayMorning, SaturdayTarde, morningVisit, afternoonVisit, futureDate);
                }

                // add a new Relative Layout with all this data and append it in the Linear Layout
                RelativeLayout newVisit = new RelativeLayout(this);
                RelativeLayout.LayoutParams labelLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                newVisit.setLayoutParams(labelLayoutParams);

                // set position of each TextView within RelativeLayout
                RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp1.addRule(RelativeLayout.BELOW, morningHeader.getId());

                // append all of the above TextView elements to the Relative Layout
                newVisit.addView(morningHeader);
                newVisit.addView(afternoonHeader, lp1);

                // add RelativeLayout to LinearLayout
                encloseScrollLayout.addView(newVisit);
            }
        }

        if (!dateMatchFound) {
            String patientName = currentPatient.getName();
            TextView header = (TextView) findViewById(R.id.medical_history_for_patient);
            header.setText("The selected date is not within the scope of the start and end date for" +
                    " this patient's schema");
        }
    }
    /*
     * Written by Nishant
     * Sets appropriate header text for morning and afternoon visits
     */
    public void setHeaderText(boolean morningScheduled, boolean afternoonScheduled,
                              boolean morningVisit, boolean afternoonVisit, boolean futureDate) {
        Log.v("setHeaderText", "futureDateBool: " + futureDate);
        if (futureDate) {
            if (morningScheduled) {
                morningHeader.setText(Html.fromHtml("<b>" + "Future Morning Visit: " + "</b>" + "Scheduled"));
            } else {
                morningHeader.setText(Html.fromHtml("<b>" + "Future Morning Visit: " + "</b>" + "None Scheduled"));
            }

            if (afternoonScheduled) {
                afternoonHeader.setText(Html.fromHtml("<b>" + "Future Afternoon Visit: " + "</b>" + "Scheduled"));
            } else {
                afternoonHeader.setText(Html.fromHtml("<b>" + "Future Afternoon Visit: " + "</b>" + "None Scheduled"));
            }
        } else {
            if (morningScheduled) {
                if (morningVisit) {
                    morningHeader.setText(Html.fromHtml("<b>" + "Morning Visit: " + "</b>" + "Attended"));
                } else {
                    morningHeader.setText(Html.fromHtml("<b>" + "Morning Visit: " + "</b>" + "Missed"));
                }
            } else {
                morningHeader.setText(Html.fromHtml("<b>" + "Morning Visit: " + "</b>" + "None Scheduled"));
            }

            if (afternoonScheduled) {
                if (afternoonVisit) {
                    afternoonHeader.setText(Html.fromHtml("<b>" + "Afternoon Visit: " + "</b>" + "Attended"));
                } else {
                    afternoonHeader.setText(Html.fromHtml("<b>" + "Afternoon Visit: " + "</b>" + "Missed"));
                }
            } else {
                afternoonHeader.setText(Html.fromHtml("<b>" + "Afternoon Visit: " + "</b>" + "None Scheduled"));
            }
        }

        morningHeader.setTextSize(20);
        afternoonHeader.setTextSize(20);
    }

    /*
     * Written by Nishant
     * Loads the visit data for the selected date
     */
    public void loadOneVisit (Date selectedDate) {
        // sets header to Past Visits for Patient Name
        String patientName = currentPatient.getName();
        TextView header = (TextView) findViewById(R.id.medical_history_for_patient);
        header.setText("Past Visits for " + patientName + " on " + dateFormatter.format(selectedDate));

        ArrayList<Visit> patientVisits = currentPatient.getPatientHistory(this);
        int numVisits = 0;
        if (patientVisits != null) {
            numVisits = patientVisits.size();
        }

        boolean dateMatchFound = false;

        for (int i = (numVisits - 1); i >=0; i--) {
            siteCode = patientVisits.get(i).getLocaleCode();
            visitDate = patientVisits.get(i).getVisitDate();
            timeVal = patientVisits.get(i).getVisitTime();
            projectCode = patientVisits.get(i).getProjectCode();
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

    /*
     * Written by Nishant
     * Lodas the visit data for all of the past visits
     */
    public void loadPastVisits() {
        // sets header to Past Visits for Patient Name
        String patientName = currentPatient.getName();
        TextView header = (TextView) findViewById(R.id.medical_history_for_patient);
        header.setText("All Past Visits for " + patientName);


        ArrayList<Visit> patientVisits = currentPatient.getPatientHistory(this);
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
