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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.tasks.GetPatientLoadTask;


public class GetPatientActivity extends Activity {

    private Patient currentPatient;
    private AsyncTask<String, String, Patient> patient;
    Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_patient);
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

    public Patient lookupPatient(int nationalid) {

        currentPatient = null;

        // Instantiate a loader task and load the given patient via nationalid
        GetPatientLoadTask newP = new GetPatientLoadTask();
        AsyncTask p = newP.execute("http://demo.sociosensalud.org.pe", Integer.toString(nationalid));

        // parse the result, and return itg
        try {
            currentPatient = (Patient) p.get();
            currentPatient.getPatientHistory();
            //Log.v("Patient that we got is", currentPatient.toString());
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
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

        currentPatient = lookupPatient(pid);
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


}
