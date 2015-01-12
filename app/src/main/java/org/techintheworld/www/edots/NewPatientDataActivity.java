package org.techintheworld.www.edots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
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
        // as you specify a parent activit  y in AndroidManifest.xml.
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

    // switch to PatientHome activity
    public void addPatientBtn (View view){

//        // get the national id
//        EditText editor = (EditText) findViewById(R.id.National_ID);
//        Long nationalID = Long.valueOf(editor.getText().toString());
//
//        // get the name
//        editor = (EditText) findViewById(R.id.Name);
//        String name = editor.getText().toString();
//
//        // get the father's name
//        editor = (EditText) findViewById(R.id.Fathers_name);
//        String fatherName = editor.getText().toString();
//
//        // get the mother's name
//        editor = (EditText) findViewById(R.id.Mothers_name);
//        String motherName = editor.getText().toString();
//
//        // TODO: Do not hardcode date and sex
//        // TODO: Change sex to be a dropdown with either male or female
//        Date date = new Date();
//        String sex = "f";
//
//
//
//        // determines which treatments are checked and stores them in ArrayList of Projects
//        ArrayList<Project> enrolledProjects = new ArrayList<Project>();
//        ListView treatmentListText = (ListView) findViewById(R.id.treatments);
//        SparseBooleanArray checkedItems = treatmentListText.getCheckedItemPositions();
//        for (int i = 0; i < treatmentListText.getAdapter().getCount(); i++) {
//            if (checkedItems.get(i)) {
//                //String treatment = treatmentListText.getAdapter().getItem(i).toString();
//                enrolledProjects.add(treatmentList.get(i));
//            }
//        }
//
//        String patientID = "1231-X21231";


        // Submit the patient data to the server.
        addToDatabase("brendan","father","mother","1", "87654321","28/01/2008", "3");
        // addToDatabase(name,fatherName,motherName,"1", Long.toString(nationalID),"28/01/2008", "1");
        // Instantiate a patient using the given details.
//        currentPatient = new Patient (name, date, nationalID, sex, enrolledProjects, motherName, fatherName, patientID, 1);

        // switch to NewVisitActivity
//        Intent intent = new Intent(this, NewVisitActivity.class);
//        intent.putExtra("Patient", currentPatient.toString());
//        startActivity(intent);

    }

}
