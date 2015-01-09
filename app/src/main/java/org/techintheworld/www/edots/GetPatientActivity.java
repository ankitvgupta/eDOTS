package org.techintheworld.www.edots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import edots.models.Patient;


public class GetPatientActivity extends Activity {

    private Patient currentPatient;

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

        GetPatientLoadTask newP = new GetPatientLoadTask();
        newP.execute("http://demo.sociosensalud.org.pe", Integer.toString(nationalid));

        Patient p = new Patient(Long.valueOf(123456));
        return p;

    }

    public void fillTable(){
        TextView patientname = (TextView) findViewById(R.id.patientname);
        TextView nationalid = (TextView) findViewById(R.id.nationalid);
        TextView dob = (TextView) findViewById(R.id.dob);
        TextView sex = (TextView) findViewById(R.id.sex);

        patientname.setText(currentPatient.getName());
        nationalid.setText(currentPatient.getNationalID().toString());
        dob.setText(currentPatient.getBirthDate().toString());
        sex.setText(currentPatient.getSex());
    }

    public void parseAndFill(View view) {

        // clear the entered text and make new hint to search for new patient

        EditText editText = (EditText) findViewById(R.id.nationalid_input);
        String message = editText.getText().toString();
        editText.setText("Search for new patient", TextView.BufferType.EDITABLE);




        int pid = Integer.parseInt(message);

        currentPatient = lookupPatient(pid);
        fillTable();

//        TextView patientname = (TextView) findViewById(R.id.patientname);
//        TextView nationalid = (TextView) findViewById(R.id.nationalid);
//        TextView dob = (TextView) findViewById(R.id.dob);
//        TextView sex = (TextView) findViewById(R.id.sex);
//
//        patientname.setText(lookedup.getName());
//        nationalid.setText(lookedup.getNationalID().toString());
//        dob.setText(lookedup.getBirthDate().toString());
//        sex.setText(lookedup.getSex());

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


}
