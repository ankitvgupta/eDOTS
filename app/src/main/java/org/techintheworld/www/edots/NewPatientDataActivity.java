package org.techintheworld.www.edots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.models.Project;


public class NewPatientDataActivity extends Activity {

    private Patient currentPatient;
    private ArrayList<Project> treatmentList = new ArrayList<Project>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient_data);

        // list of treatment study groups
        treatmentList.add(new Project());
        treatmentList.add(new Project());
        treatmentList.add(new Project());
        treatmentList.add(new Project());

        //        {"studyProject1", "studyProject2", "studyProject3", "studyProject4"};

        // sets layout_height for ListView based on number of treatments
        ListView treatmentView = (ListView)findViewById(R.id.treatments);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50*treatmentList.size(), getResources().getDisplayMetrics());
        treatmentView.getLayoutParams().height = height;


        ArrayList<String> checkboxesText = new ArrayList<String>();
        for (int i = 0; i < treatmentList.size(); i ++){
            checkboxesText.add(treatmentList.get(i).getName());
        }
        // creating adapter for ListView
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
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // switch to CheckFingerPrint Activity
    public void switchCheckFingerPrint(View view) {

        Intent intent = new Intent(this, CheckFingerPrintActivity.class);
    }

    public void addToDatabase(String name, String father, String mother, String docType, String nationalID, String birthDate, String sex){

        NewPatientUploadTask uploader = new NewPatientUploadTask();
        try {
            String result = uploader.execute("http://demo.sociosensalud.org.pe", name, father, mother, docType, nationalID, birthDate, sex).get();
            Log.v("What we got was", result);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e){
            e.printStackTrace();
        }
        return;
    }

    public void onRadioButtonClicked(View view){
        return;
    }

    // switch to PatientHome activity
    public void addPatientBtn (View view){

        // get the national id
        EditText editor = (EditText) findViewById(R.id.National_ID);
        String nationalID = editor.getText().toString();

        // get the name
        editor = (EditText) findViewById(R.id.Name);
        String name = editor.getText().toString();

        // get the father's name
        editor = (EditText) findViewById(R.id.Fathers_name);
        String fatherName = editor.getText().toString();

        // get the mother's name
        editor = (EditText) findViewById(R.id.Mothers_name);
        String motherName = editor.getText().toString();

        // get the birthdate
        editor = (EditText) findViewById(R.id.Birthdate);
        String date = editor.getText().toString();

        // get the sex
        String sex = "";
        RadioButton buttn = (RadioButton) findViewById(R.id.radio_female);
        if (buttn.isChecked()){
            sex = "2";
        }
        buttn = (RadioButton) findViewById(R.id.radio_male);
        if (buttn.isChecked()){
            sex = "1";
        }

        //editor = (EditText) findViewById(R.id.Sex);
        //String sex = editor.getText().toString();


        // determines which treatments are checked and stores them in ArrayList of Projects
        ArrayList<Project> enrolledProjects = new ArrayList<Project>();
        ListView treatmentListText = (ListView) findViewById(R.id.treatments);
        SparseBooleanArray checkedItems = treatmentListText.getCheckedItemPositions();
        for (int i = 0; i < treatmentListText.getAdapter().getCount(); i++) {
            if (checkedItems.get(i)) {
                //String treatment = treatmentListText.getAdapter().getItem(i).toString();
                enrolledProjects.add(treatmentList.get(i));
            }
        }

        Date date2 = new Date();
        // Submit the patient data to the server.
        addToDatabase(name, fatherName, motherName, "2", nationalID, date, sex);
        // Instantiate a patient using the given details.
        currentPatient = new Patient (name, date2, Long.valueOf(nationalID), sex, enrolledProjects, motherName, fatherName, "1923745", 1);

        // switch to NewVisitActivity
        Intent intent = new Intent(this, NewVisitActivity.class);
        intent.putExtra("Patient", currentPatient.toString());
        startActivity(intent);

    }

}
