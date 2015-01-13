package org.techintheworld.www.edots;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.models.Visit;
import edots.tasks.GetPatientLoadTask;
import edots.utils.OfflineStorageManager;


/*
 * Written by Ankit
 *
 * Controller file
 *      Associated View: activity_get_patient.xml
 *      Accesses Models: Patient, Visit,
 *
 * Used to query the database for patients and visits, by parsing the national ID input.
 * Also renders the queried patient data.
 */

public class GetPatientActivity extends Activity {

    private Patient currentPatient;
    private AsyncTask<String, String, Patient> patient;
    private Spinner spnPatient;
    Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_patient);
        spnPatient = (Spinner) findViewById(R.id.patient_spinner);
        loadPatientSpinner();
        try {
            currentPatient = new Patient(getIntent().getExtras().getString("Patient"));
            fillTable();
        }
        catch (Exception e){
            Log.v("There is no patient already", "There is no patient already");
        }

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                hideKeyboard();
                parseAndFill(v);
            }
        });

    //TODO: disable medical history and log new visit button if no patient is loaded

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_get_patient, menu);
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

    public Patient lookupPatient(int nationalid) throws JSONException{

        currentPatient = null;
        // Check if Patient is already stored locally first
        JSONArray object;
        try {
            // load list of patients from file patient_data
            object = new JSONArray(OfflineStorageManager.getJSONFromLocal(this, "patient_data"));
            // look at all patients
            for (int i = 0; i < object.length(); i++){
                JSONObject obj = object.getJSONObject(i);
                Patient p = new Patient(obj.toString());
                // this ensures that they have a NationalId
                try {
                    if (p.getNationalID() == nationalid) {
                        currentPatient = p;
                        Log.e("GetPatientActivity", "Patient Found is" + p.toString());
                    }
                }
                catch(NullPointerException e1){
                    e1.printStackTrace();
                }
            }
        }
        catch (FileNotFoundException e1){
            e1.printStackTrace();
        }

        // Instantiate a loader task and load the given patient via nationalid
        if (currentPatient == null) {
            GetPatientLoadTask newP = new GetPatientLoadTask();
            AsyncTask p = newP.execute("http://demo.sociosensalud.org.pe", Integer.toString(nationalid));

            // parse the result, and return itg
            try {
                currentPatient = (Patient) p.get();
                ArrayList<Visit> visits = currentPatient.getPatientHistory();
                Log.v("GetPatientActivity.java: The patient visits that we got are", visits.toString());
                //Log.v("Patient that we got is", currentPatient.toString());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            }
        }
        return currentPatient;

    }


    public void fillTable(){
        // TODO: clear existing patient data when searched again
        hideKeyboard();
        if (currentPatient == null){
            return;
        }
        TextView patientname = (TextView) findViewById(R.id.patientname);
        TextView nationalid = (TextView) findViewById(R.id.nationalid);
        TextView dob = (TextView) findViewById(R.id.dob);
        TextView sex = (TextView) findViewById(R.id.sex);

        SimpleDateFormat parser =new SimpleDateFormat("dd/MM/yyyy");


        patientname.setText(currentPatient.getName());
        nationalid.setText(currentPatient.getNationalID().toString());
        dob.setText(parser.format(currentPatient.getBirthDate()));
        sex.setText(currentPatient.getSex());
    }

    public void parseAndFill(View view) {

        // clear the entered text and make new hint to search for new patient
        EditText editText = (EditText) findViewById(R.id.nationalid_input);
        String message = editText.getText().toString();
        editText.setText("", TextView.BufferType.EDITABLE);

        int pid = Integer.parseInt(message);
        try {
            currentPatient = lookupPatient(pid);
        }
        catch(JSONException e1){
            e1.printStackTrace();
        }
        // pop up error message when the national id is not found
        if (currentPatient == null){
            Toast.makeText(getBaseContext(), R.string.patient_not_found,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        fillTable();
    }

    // switch to CheckFingerPrintActivity
    public void switchCheckFingerPrint(View view) {
        if (currentPatient != null){
            Intent intent = new Intent(this, CheckFingerPrintActivity.class);
            intent.putExtra("Patient", currentPatient.toString());
            startActivity(intent);
        }
    }

    public void switchMedicalHistoryActivity(View view) {
        if (currentPatient != null){
            Intent intent = new Intent(this, MedicalHistoryActivity.class);
            intent.putExtra("Patient", currentPatient.toString());
            startActivity(intent);
        }

    }

    public void switchNewVisitActivity(View view) {
        if (currentPatient != null) {
            Intent intent = new Intent(this, NewVisitActivity.class);
            intent.putExtra("Patient", currentPatient.toString());
            startActivity(intent);
        }
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void loadPatientSpinner(){
        JSONArray object;
        try {
            // load list of patients from file patient_data
            object = new JSONArray(OfflineStorageManager.getJSONFromLocal(this, "patient_data"));
            String[] patients = new String[object.length()];
            // look at all patients
            for (int i = 0; i < object.length(); i++){
                JSONObject obj = object.getJSONObject(i);
                Patient p = new Patient(obj.toString());
                try {
                    patients[i] = p.getName() + " " + p.getFathersName() + " " + p.getMothersName();
                }
                catch(NullPointerException e1){
                    e1.printStackTrace();
                }
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, patients);
            spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            spnPatient.setAdapter(spinnerArrayAdapter);
        }
        catch(JSONException e1){
            e1.printStackTrace();
        }
        catch(FileNotFoundException e1) {
            e1.printStackTrace();
        }

    }


}
