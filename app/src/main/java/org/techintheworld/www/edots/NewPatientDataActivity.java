package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.models.Project;


public class NewPatientDataActivity extends Activity implements DatePickerFragment.TheListener{

    private Patient currentPatient;
    private ArrayList<Project> treatmentList = new ArrayList<Project>();
    EditText datePicker;
    private String date_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient_data);

        // get the birthdate
        datePicker = (EditText) findViewById(R.id.Birthdate);
        datePicker.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }

        });

        // list of treatment study groups
        treatmentList.add(new Project());
        treatmentList.add(new Project());
        treatmentList.add(new Project());
        treatmentList.add(new Project());

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

    // set the text field as the selected date
    @Override
    public void returnDate(String date) {
        // TODO: display the date in dd/MM/yyyy format
        datePicker.setText(date);
        date_string = date;
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
        // TODO: error message for invalid national ID
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

        // Submit the patient data to the server.
        addToDatabase(name, fatherName, motherName, "2", nationalID, date_string, sex);

        // then query the database to get the patient, including the patient code generated by server
        GetPatientLoadTask getP = new GetPatientLoadTask();
        try {
            AsyncTask p = getP.execute("http://demo.sociosensalud.org.pe", nationalID);
            currentPatient = (Patient) p.get();
            Log.v("What we got was", currentPatient.toString());
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e){
            e.printStackTrace();
        }

        // switch to NewVisitActivity
        Intent intent = new Intent(this, MedicalHistoryActivity.class);
        intent.putExtra("Patient", currentPatient.toString());
        startActivity(intent);

    }

}
