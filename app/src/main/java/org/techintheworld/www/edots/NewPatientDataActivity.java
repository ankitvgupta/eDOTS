package org.techintheworld.www.edots;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import edots.models.Patient;
import edots.models.Project;


public class NewPatientDataActivity extends Activity {

    private Patient currentPatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient_data);

//        Patient p = new Patient("Brendan");
//        ArrayList<Project> patientProjects= p.getEnrolledProjects();
//        int num_projects = patientProjects.size();
//        ArrayList<String> checkBoxesText = new ArrayList<String>();
//        for(int i = 0; i < num_projects; i++) {
//            CheckBox checkBox = new CheckBox(getApplicationContext());
//            String n = patientProjects.get(i).getName();
//            checkBoxesText.add(n);
//        }

        // list of treatment study groups
        String[] treatmentList = {"studyProject1", "studyProject2", "studyProject3", "studyProject4"};

        // sets layout_height for ListView based on number of treatments
        ListView treatmentView = (ListView)findViewById(R.id.treatments);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50*treatmentList.length, getResources().getDisplayMetrics());
        treatmentView.getLayoutParams().height = height;

        // creating adapter for ListView
        ArrayList<String> checkboxesText = new ArrayList<String>(Arrays.asList(treatmentList));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, checkboxesText);

        // creates ListView checkboxes
        ListView listview = (ListView) findViewById(R.id.treatments);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_patient_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activit  y in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // switch to CheckFingerPrint Activity
    public void switchCheckFingerPrint(View view){
        Intent intent = new Intent(this, CheckFingerPrintActivity.class);
        startActivity(intent);

    }

    // switch to PatientHome activity
    public void switchGetPatient (View view){

        // get the national id
        EditText editor = (EditText) findViewById(R.id.National_ID);
        Long nationalid = Long.valueOf(editor.getText().toString());

        // get the name
        editor = (EditText) findViewById(R.id.Name);
        String name = editor.getText().toString();

        // get the father's name
        editor = (EditText) findViewById(R.id.Fathers_name);
        String fatherName = editor.getText().toString();

        // get the mother's name
        editor = (EditText) findViewById(R.id.Mothers_name);
        String motherName = editor.getText().toString();

        // TODO: Do not hardcode date and sex
        // TODO: Change sex to be a dropdown with either male or female
        Date date = new Date();
        String sex = "f";

        // TODO: Get the projects that are selected.
        ArrayList<Project> enrolledProjects = new ArrayList<Project>();


        // Instantiate a patient using the given details.
        currentPatient = new Patient (name, date, nationalid, sex, enrolledProjects);


        // TODO: Submit the patient data to the server.


        Intent intent = new Intent(this, GetPatientActivity.class);
        intent.putExtra("Patient", currentPatient.toString());
        startActivity(intent);

    }


}
