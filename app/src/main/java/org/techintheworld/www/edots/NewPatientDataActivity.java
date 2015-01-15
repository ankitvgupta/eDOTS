package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.models.Project;
import edots.tasks.GetPatientLoadTask;
import edots.tasks.NewPatientUploadTask;
import edots.utils.DatePickerFragment;
import edots.utils.InternetConnection;

/*
 * Written by Nishant
 * Reviewed by Ankit on 01/12/15
 *
 * Controller File
 *      Associated Views: activity_new_patient_data.xml
 *      Accesses Models: Patient
 *
 * Used to parse inputted data about a new patient, and submit that to the server.
 *
 * onSubmit behavior: adds Patient to db, pulls patient from db to get patientcode, and passes that Patient to GetPatientActivity via Intent
 */

public class NewPatientDataActivity extends Activity implements DatePickerFragment.TheListener {

    private Patient currentPatient;
    private ArrayList<Project> treatmentList = new ArrayList<Project>();
    EditText datePicker;
    private String date_string;
    DateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient_data);

        // get the birthdate
        datePicker = (EditText) findViewById(R.id.Birthdate);
        datePicker.setOnClickListener(new View.OnClickListener() {

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
        ListView treatmentView = (ListView) findViewById(R.id.treatments);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 * treatmentList.size(), getResources().getDisplayMetrics());
        treatmentView.getLayoutParams().height = height;


        ArrayList<String> checkboxesText = new ArrayList<String>();
        for (int i = 0; i < treatmentList.size(); i++) {
            checkboxesText.add(treatmentList.get(i).getName());
        }
        // creating adapter for ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, checkboxesText);

        // creates ListView checkboxes
        ListView listview = (ListView) findViewById(R.id.treatments);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setAdapter(adapter);

        if (!InternetConnection.checkConnection(this)){
            //TODO: dialogue
            ProgressDialog.Builder loginProgress = new ProgressDialog.Builder(this);
            loginProgress.setTitle("Login Error");
            loginProgress.setMessage("Your username or password was incorrect or invalid");
            loginProgress.show();
        }
    }

    /**
     * @param date set the text field as the selected date
     * @author lili
     */
    @Override
    public void returnDate(Date date) {
        datePicker.setText(displayDateFormat.format(date));
        date_string = dbDateFormat.format(date) + " 00:00:00.0";
        Log.i("date_string", date_string);
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

    public void AlertError(String title, String message) {
        // Alert if username and password are not entered
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // here you can add functions
            }
        });
        alertDialog.show();
    }

    public void addToDatabase(String name, String father, String mother, String docType, String nationalID, String birthDate, String sex) {

        NewPatientUploadTask uploader = new NewPatientUploadTask();
        try {
            String result = uploader.execute("http://demo.sociosensalud.org.pe", name, father, mother, docType, nationalID, birthDate, sex).get();
            Log.v("New patient: what we got was", result);
        } catch (InterruptedException e) {
            e.printStackTrace();
            AlertError("Entry Error", "The data you entered is of the wrong format, please try again");
        } catch (ExecutionException e) {
            e.printStackTrace();
            AlertError("Entry Error", "The data you entered is of the wrong format, please try again");
        }
        return;
    }

    private void saveLocally(String name, String father, String mother, String docType, String nationalID, String birthDate, String sex) {
        //Patient p = new Patient(name, birthDate, Long.valueOf(nationalID),sex,  project,  mother,  father, patientID, Integer.valueOf(docType));
    }

    public void onRadioButtonClicked(View view) {
        return;
    }

    public boolean validateNationalID() {

        EditText editor = (EditText) findViewById(R.id.National_ID);
        String nationalID = editor.getText().toString();

        boolean allDigits = true;

        for (char c : nationalID.toCharArray()) {
            if (!Character.isDigit(c)) {
                allDigits =  false;
            }
        }

        boolean properLength = true;

        if (!(nationalID.length() == 8)) {
            properLength = false;
        }

        if (properLength && allDigits) {
            return true;
        } else {
            return false;
        }
    }

    // validate that each field has some entry
    public boolean validateEmpty() {
        EditText editor = (EditText) findViewById(R.id.National_ID);
        String nationalID = editor.getText().toString();

        editor = (EditText) findViewById(R.id.Name);
        String name = editor.getText().toString();

        editor = (EditText) findViewById(R.id.Fathers_name);
        String fatherName = editor.getText().toString();

        editor = (EditText) findViewById(R.id.Mothers_name);
        String motherName = editor.getText().toString();

        RadioButton buttnMale = (RadioButton) findViewById(R.id.radio_female);
        RadioButton buttnFemale = (RadioButton) findViewById(R.id.radio_male);

        ListView treatmentListText = (ListView) findViewById(R.id.treatments);
        SparseBooleanArray checkedItems = treatmentListText.getCheckedItemPositions();
        int numTreatments = 0;
        for (int i = 0; i < treatmentListText.getAdapter().getCount(); i++) {
            if (checkedItems.get(i)) {
                numTreatments++;
            }
        }


        if (nationalID.equals("") || name.equals("") || fatherName.equals("") ||
                motherName.equals("") || !(buttnMale.isChecked() || buttnFemale.isChecked())) {
            return false;
        } else if (numTreatments == 0) {
            return false;
        }
        return true;

    }



    // switch to PatientHome activity
    public void addPatientBtn(View view) {

        if (!(validateEmpty())) {
            AlertError("Entry Error", "You have left one of the fields blank, please try again");
        } else if (!(validateNationalID())) {
            AlertError("Entry Error", "The entered NationalID is not valid");
        } else {

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
            if (buttn.isChecked()) {
                sex = "2";
            }
            buttn = (RadioButton) findViewById(R.id.radio_male);
            if (buttn.isChecked()) {
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
                // switch to NewVisitActivity
                Intent intent = new Intent(this, GetPatientActivity.class);
                intent.putExtra("Patient", currentPatient.toString());
                startActivity(intent);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch(NullPointerException e)
            {
                if (!InternetConnection.checkConnection(this)) {
                    Toast.makeText(getBaseContext(), R.string.no_internet_connection,
                            Toast.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                }
            }


        }


    }

}
