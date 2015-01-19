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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.models.Visit;
import edots.tasks.NewVisitLoadTask;
import edots.tasks.NewVisitUploadTask;
import edots.utils.DatePickerFragment;
import edots.utils.TimePickerFragment;

/**
 * @author lili
 * @author ankit
 *
 * Controller for Adding a Visit
 *     Associated View: activity_new_visit.xml
 *     Associated Models: Visit
 *
 *  Adds a new visit to the db
 */

public class NewVisitActivity extends Activity implements DatePickerFragment.TheListener, TimePickerFragment.TheListener{

    private Patient currentPatient;
    private Visit currentVisit;
    EditText datePicker;
    EditText timePicker;
    EditText visitLocaleEditor;
    EditText visitProjectEditor;
    EditText visitGroupEditor;
    EditText visitNoEditor;
    DateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat displayTimeFormat = new SimpleDateFormat("HH:mm");
    DateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");
    DateFormat dbTimeFormat = new SimpleDateFormat("HH:mm:00.0000000");
    Date visitDate = new Date();
    Date visitTime = new Date();

    @Override
    /**
     * @author lili
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_visit);

        // get localeCode, localeName and promoterId from sharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String localeName = prefs.getString((getString(R.string.login_locale_name)), null);
        String localeCode = prefs.getString((getString(R.string.login_locale)), null);
        String promoterId = prefs.getString((getString(R.string.key_userid)), null);

        // if a patient was passed in, pre-load that patient
        try {
            currentPatient = new Patient(getIntent().getExtras().getString("Patient"));
        }
        catch (Exception e){
            // TODO: Don't print the stack trace, give some sort of dialog box instead
            e.printStackTrace();
        }
        Log.v("NewVisit: currentpatient", currentPatient.toString());

        // load the visit group number and visit number
        // TODO: put this into a function
        NewVisitLoadTask newV = new NewVisitLoadTask();
        AsyncTask v = newV.execute(currentPatient.getPid(), localeCode, currentPatient.getEnrolledProject().getId());
        // parse the result, and return it
        try {
            currentVisit = (Visit) v.get();
            Log.v("NewVisitActivity.java: The patient visit that we got is", currentVisit.toString());
        } catch (InterruptedException e1) {
            //TODO: do something when it cannot fetch a new visit (error message, break and return to main menu)
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        } catch (NullPointerException e1){
            Log.e("NewVisit: get visit","");
        }

        currentVisit.setLocaleCode(localeCode);
        currentVisit.setPromoterId(promoterId);

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
        visitLocaleEditor.setText(localeName);


        // visit project
        visitProjectEditor = (EditText) findViewById(R.id.visitProject);
        // TODO: display project name
        visitProjectEditor.setText(currentPatient.getEnrolledProject().getId()+"-"+
                                   currentPatient.getEnrolledProject().getName());

        // visit group
        visitGroupEditor = (EditText) findViewById(R.id.visitGroup);
        visitGroupEditor.setText(currentVisit.getVisitGroupCode()+"-"+currentVisit.getNombreGrupoVisita());

        // visit number
        visitNoEditor = (EditText) findViewById(R.id.visitNo);
        visitNoEditor.setText(currentVisit.getVisitCode()+"-"+currentVisit.getDescripcionVisita());

    }

    /**
     * @author lili
     * set the text field as the selected date
     */
    @Override
    public void returnDate(Date date) {
        datePicker.setText(displayDateFormat.format(date));
        visitDate = date;
        currentVisit.setVisitDate(dbDateFormat.format(date));
    }

    /**
     * @author lili
     * set the text field as the selected time
     */
    public void returnTime(Date time) {
        timePicker.setText(displayTimeFormat.format(time));
        visitTime = time;
        currentVisit.setVisitTime(dbTimeFormat.format(time));
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
     * insert new visit into the database
     * @return -1 for error, 1 or 2 for success
     */
    public String addToDatabase(){
        NewVisitUploadTask uploader = new NewVisitUploadTask();
        String result = "-1";
        try {
            Log.v("new visit: currentVisit", currentVisit.toString());
            result = uploader.execute(getString(R.string.server_url),
                                      currentVisit.getLocaleCode(),
                                      currentVisit.getProjectCode(),
                                      currentVisit.getVisitGroupCode(),
                                      currentVisit.getVisitCode(),
                                      currentPatient.getPid(),
                                      currentVisit.getVisitDate(),
                                      currentVisit.getVisitTime(),
                                      currentVisit.getPromoterId()).get();

            Log.v("What we got was", result);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e){
            e.printStackTrace();
        }
        return result;
    }

    // Submits the visit to the server and switches to GetPatientActivity

    /**
     * @param view The current view
     */
    public void submitVisit(View view)
    {
        currentVisit.setVisitDate(dbDateFormat.format(visitDate));
        currentVisit.setVisitTime(dbTimeFormat.format(visitTime));
        String result = addToDatabase();

        //TODO: code the message in strings.xml
        if (result.equals("-1")){
            Log.v("New Visit: result", result);
            Toast.makeText(getBaseContext(),"Failed to submit. Please try again",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getBaseContext(), "Successfully submitted", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
        }
    }


}
