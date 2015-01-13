package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edots.models.Patient;
import edots.models.Project;


public class NewVisitActivity extends Activity implements DatePickerFragment.TheListener{

    private Patient currentPatient;
    private ArrayList<Project> treatmentList = new ArrayList<Project>();
    EditText datePicker;
    private String date_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_visit);

        // get the visit date
        datePicker = (EditText) findViewById(R.id.visitDate);
        Date today = new Date();
        DateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
        datePicker.setText(displayFormat.format(today));
        datePicker.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View arg0) {
                DialogFragment picker = new DatePickerFragment();
                picker.show(getFragmentManager(), "datePicker");
            }

        });

    }

    // set the text field as the selected date
    @Override
    public void returnDate(String date) {
        datePicker.setText(date);
        date_string = date+" 00:00:00.0";
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

    public void addToDatabase(){
        return;
    }


}

//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_new_visit);
//
//        try {
//            currentPatient = new Patient(getIntent().getExtras().getString("Patient"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        // get the visit date
//        datePicker = (EditText) findViewById(R.id.Birthdate);
//        datePicker.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                DialogFragment picker = new DatePickerFragment();
//                picker.show(getFragmentManager(), "datePicker");
//            }
//
//        });
//    }
////
////        //Patient p = new Patient("Brendan");
////        ArrayList<Project> patientProjects= currentPatient.getEnrolledProjects();
////        int num_projects = patientProjects.size();
////        ArrayList<String> checkBoxesText = new ArrayList<String>();
////
////        // Retrieve list of projects of this patient
////        for(int i = 0; i < num_projects; i++) {
////            CheckBox checkBox = new CheckBox(getApplicationContext());
////            String n = patientProjects.get(i).getName();
////            checkBoxesText.add(n);
////        }
////
////        // sets layout_height for ListView based on number of treatments
////        ListView treatmentView = (ListView)findViewById(R.id.active_treatments);
////        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 * num_projects, getResources().getDisplayMetrics());
////        treatmentView.getLayoutParams().height = height;
////
////        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, checkBoxesText);
////        ListView lv= (ListView)findViewById(R.id.active_treatments);
////        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
////        lv.setAdapter(adapter);
////        for (int i=0; i<checkBoxesText.size(); i++){
////            lv.setItemChecked(i, true);
////        }
////        lv.setMinimumHeight(200);
////
////
////    }
////
//    // set the text field as the selected date
//    @Override
//    public void returnDate(String date) {
//        datePicker.setText(date);
//        date_string = date+" 00:00:00.0";
//        Log.i("date_string", date_string);
//    }
////
////    public void submitVisit()
////    {
////        Intent intent = new Intent(this, GetPatientActivity.class);
////        startActivity(intent);
////    }
////
////
////
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        // Inflate the menu; this adds items to the action bar if it is present.
////        getMenuInflater().inflate(R.menu.menu_new_visit, menu);
////
////        return true;
////    }
////
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item) {
////        // Handle action bar item clicks here. The action bar will
////        // automatically handle clicks on the Home/Up button, so long
////        // as you specify a parent activity in AndroidManifest.xml.
////        int id = item.getItemId();
////
////        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
////
////        return super.onOptionsItemSelected(item);
////    }
//
//
//
//}
