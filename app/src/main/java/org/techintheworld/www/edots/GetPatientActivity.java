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

    public Patient lookupPatient(int pid) {
        Patient p = new Patient("Brendan");
        currentPatient = p;
        return p;

    }

    public void fillTable(View view) {

        EditText editText = (EditText) findViewById(R.id.nationalid_input);
        String message = editText.getText().toString();
        int pid = Integer.parseInt(message);

        Patient lookedup = lookupPatient(pid);

        TextView patientname = (TextView) findViewById(R.id.patientname);
        TextView nationalid = (TextView) findViewById(R.id.nationalid);
        TextView dob = (TextView) findViewById(R.id.dob);
        TextView sex = (TextView) findViewById(R.id.sex);

        patientname.setText(lookedup.getName());
        nationalid.setText(lookedup.getNationalID().toString());
        dob.setText(lookedup.getBirthDate().toString());
        sex.setText(lookedup.getSex());

    }
//
//    // switch to PatientHomeActivity
//    public void switchPatientHome(View view){
//        Intent intent = new Intent(this, PatientHomeActivity.class);
//        startActivity(intent);
//    }

    // switch to CheckFingerPrintActivity
    public void switchCheckFingerPrint(View view) {
        Intent intent = new Intent(this, CheckFingerPrintActivity.class);
        intent.putExtra("Patient", currentPatient.toString());
        startActivity(intent);
    }

    public void switchMedicalHistoryActivity(View view) {
        Intent intent = new Intent(this, MedicalHistoryActivity.class);
        Log.v("The patient string is: ", currentPatient.toString());
        intent.putExtra("Patient", currentPatient.toString());
        startActivity(intent);
    }

    public void switchNewVisitActivity(View view) {
        Intent intent = new Intent(this, NewVisitActivity.class);
        //intent.putExtra("Patient", currentPatient.toString());
        startActivity(intent);
    }


}
