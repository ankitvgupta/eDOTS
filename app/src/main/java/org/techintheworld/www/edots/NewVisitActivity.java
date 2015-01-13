package org.techintheworld.www.edots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

import edots.models.Patient;
import edots.models.Project;

/*
 * Written by Ankit
 *
 * Controller for Adding a Visit
 *      Associated View: activity_new_visit.xml
 *      Associated Models: Visit
 *
 * Adds a new visit to the db
 *
 */
public class NewVisitActivity extends Activity {

    private Patient currentPatient;

    @Override
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

    // TODO: Add the actual submission to the server
    // Submits the visit to the server and switches to GetPatientActivity
    public void submitVisit()
    {
        Intent intent = new Intent(this, GetPatientActivity.class);
        intent.putExtra("Patient", currentPatient.toString());
        startActivity(intent);
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



}
