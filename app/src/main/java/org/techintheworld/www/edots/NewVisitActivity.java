package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.models.Project;
import edots.tasks.GetHistoryLoadTask;
import edots.tasks.NewVisitUploadTask;
import edots.utils.DatePickerFragment;
import edots.utils.TimePickerFragment;

/**
 * @author lili
 * @author ankit
 *
 * Controller for Adding a Visit
 *     Associated View: activity_new_visit.xml
 *     Associated git Models: Visit
 *
 *  Adds a new visit to the db
 */

public class NewVisitActivity extends Activity implements DatePickerFragment.TheListener, TimePickerFragment.TheListener{

    private Patient currentPatient;
    private ArrayList<Project> treatmentList = new ArrayList<Project>();
    EditText datePicker;
    EditText timePicker;
    EditText visitLocaleEditor;
    EditText visitProjectEditor;
    EditText visitGroupEditor;
    EditText visitNoEditor;
    DateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat displayTimeFormat = new SimpleDateFormat("hh:mm");
    DateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");
    Date visitDate = new Date();

    @Override
    /**
     * @author lili
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_visit);

        // if a patient was passed in, pre-load that patient
        try {
            currentPatient = new Patient(getIntent().getExtras().getString("Patient"));
        }
        catch (Exception e){
            // TODO: Don't print the stack trace, give some sort of dialog box instead
            e.printStackTrace();
        }

//        ProjectLoadTask newP = new ProjectLoadTask();
//        AsyncTask p = newP.execute("2", "19");

        GetHistoryLoadTask newV = new GetHistoryLoadTask();
        AsyncTask v = newV.execute(currentPatient.getPid(), "2","5");
        Log.i("new visit: asynctask", v.toString());

        // Get the current projects that the patient is signed up for
        Project patientProject = currentPatient.getEnrolledProject();
        //Log.v("NewVisitActivity.java: The project the patient is in is", patientProject.toString());
        int num_projects = 1;

        // visit date
        datePicker = (EditText) findViewById(R.id.visitDate);
        Date currentTime = new Date();
        // set the default date to today
        datePicker.setText(displayDateFormat.format(currentTime));
        // pop up date picker dialog when clicked
        datePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }
        });

        // visit time
        timePicker = (EditText) findViewById(R.id.visitTime);
        // set the default date to today
        timePicker.setText(displayTimeFormat.format(currentTime));
        // pop up time picker dialog when clicked
        timePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                DialogFragment picker = new TimePickerFragment();
                picker.show(getFragmentManager(), "timePicker");
            }
        });

        // visit locale
        visitLocaleEditor = (EditText) findViewById(R.id.visitLocale);
        // set visit locale default to the promoter's locale
        // TODO: should this be a dropdown menu of all locales?
        Log.i("new visit activity: oncreate pulled locale", returnLocale());
        visitLocaleEditor.setText(returnLocale());

//
//        // instantiate arraylist that will be used for the checkboxes text
//        ArrayList<String> checkBoxesText = new ArrayList<String>();
//
//        // Retrieve list of projects of this patient
//        for (int i = 0; i < 1; i++) {
//            CheckBox checkBox = new CheckBox(getApplicationContext());
//            String n = patientProject.getName();
//            checkBoxesText.add(n);
//        }
//
//        // sets layout_height for ListView based on number of treatments
//        ListView treatmentView = (ListView)findViewById(R.id.active_treatments);
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 * num_projects, getResources().getDisplayMetrics());
//        treatmentView.getLayoutParams().height = height;
//
//        // set the dropdown to have the given text
//        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, checkBoxesText);
//        ListView lv= (ListView)findViewById(R.id.active_treatments);
//        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        lv.setAdapter(adapter);
//
//        // set all of them to true
//        for (int i=0; i<checkBoxesText.size(); i++){
//            lv.setItemChecked(i, true);
//        }
//        lv.setMinimumHeight(200);

        // visit group
        visitGroupEditor = (EditText) findViewById(R.id.visitGroup);
//       visitLocaleEditor.setText(returnLocale());

    }

    /**
     * @author lili
     * set the text field as the selected date
     */
    @Override
    public void returnDate(Date date) {
        // TODO: format display text in "dd/MM/yyyy"
        datePicker.setText(displayDateFormat.format(date));
        visitDate = date;

    }

    /**
     * @author lili
     * set the text field as the selected time
     */
    public void returnTime(String time) {
        timePicker.setText(time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_new_visit, menu);

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

    /**
    * @author lili
    *
    * @return the locale of the signed-in promoter
    */
    public String returnLocale(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String locale =  prefs.getString((getString(R.string.login_locale)), null);
        Log.i("new visit activity: locale", locale);
        return locale;
    }

    /**
     *
     * @param date A string representation of the visit's date
     * @param timeString A string representatino of the visit's time of day
     */
    public void addToDatabase(String date, String timeString){
        // TODO
        NewVisitUploadTask uploader = new NewVisitUploadTask();
        //Log.v("NewVisitActivity.java: The currentPatient is", currentPatient.toString());
        try {
            String result = uploader.execute("http://demo.sociosensalud.org.pe", "2", "2", "1", "1",
                    currentPatient.getPid(), date, timeString, "19").get();
            Log.v("What we got was", result);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e){
            e.printStackTrace();
        }
    }

    // TODO: Add the actual submission to the server
    // Submits the visit to the server and switches to GetPatientActivity

    /**
     *
     * @param view The current view
     */
    public void submitVisit(View view)
    {
        String date_string = dbDateFormat.format(visitDate);
        String time_string = timePicker.getText().toString();
        time_string = time_string + ":00.0000000";
        Log.i("new visit: time", date_string+" "+time_string+":00.0");
        addToDatabase(date_string, time_string);
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }


}
