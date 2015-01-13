package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edots.models.Patient;
import edots.models.Project;

/**
 * @author lili
 * @author ankit
 * Controller for Adding a Visit
 *     Associated View: activity_new_visit.xml
 *     Associated Models: Visit
 *
 *  Adds a new visit to the db
 */

public class NewVisitActivity extends Activity implements DatePickerFragment.TheListener, TimePickerFragment.TheListener{

    private Patient currentPatient;
    private ArrayList<Project> treatmentList = new ArrayList<Project>();
    EditText datePicker;
    EditText timePicker;
    DateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat displayTimeFormat = new SimpleDateFormat("hh:mm");
    DateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date visitDate = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_visit);

        // get the visit date
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


        // get the visit time
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

        // if a patient was passed in, pre-load that patient
        try {
            currentPatient = new Patient(getIntent().getExtras().getString("Patient"));
        }
        catch (Exception e){
            // TODO: Don't print the stack trace, give some sort of dialog box instead
            e.printStackTrace();
        }

        // Get the current projects that the patient is signed up for
        ArrayList<Project> patientProjects= currentPatient.getEnrolledProjects();
        int num_projects = patientProjects.size();

        // instantiate arraylist that will be used for the checkboxes text
        ArrayList<String> checkBoxesText = new ArrayList<String>();

        // Retrieve list of projects of this patient
        for (int i = 0; i < num_projects; i++) {
            CheckBox checkBox = new CheckBox(getApplicationContext());
            String n = patientProjects.get(i).getName();
            checkBoxesText.add(n);
        }

        // sets layout_height for ListView based on number of treatments
        ListView treatmentView = (ListView)findViewById(R.id.active_treatments);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 * num_projects, getResources().getDisplayMetrics());
        treatmentView.getLayoutParams().height = height;

        // set the dropdown to have the given text
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, checkBoxesText);
        ListView lv= (ListView)findViewById(R.id.active_treatments);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(adapter);

        // set all of them to true
        for (int i=0; i<checkBoxesText.size(); i++){
            lv.setItemChecked(i, true);
        }
        lv.setMinimumHeight(200);

    }

    // set the text field as the selected date
    @Override
    public void returnDate(Date date) {
        // TODO: format display text in "dd/MM/yyyy"
        datePicker.setText(displayDateFormat.format(date));
        visitDate = date;

    }

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

    public void addToDatabase(){
        // TODO
    }

    // TODO: Add the actual submission to the server
    // Submits the visit to the server and switches to GetPatientActivity
    public void submitVisit(View view)
    {
        String date_string = dbDateFormat.format(visitDate);
        String time_string = timePicker.getText().toString();
        Log.i("new visit: time", date_string+" "+time_string+":00.0");
        addToDatabase();
//      Intent intent = new Intent(this, GetPatientActivity.class);
//      startActivity(intent);
    }


}
