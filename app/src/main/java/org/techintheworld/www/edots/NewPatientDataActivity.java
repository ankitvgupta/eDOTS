package org.techintheworld.www.edots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import edots.models.Patient;
import edots.models.Project;


public class NewPatientDataActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient_data);

        String TAG = "MyActivity";

//        Patient p = new Patient("Brendan");
//        ArrayList<Project> patientProjects= p.getEnrolledProjects();
//        int num_projects = patientProjects.size();
//        ArrayList<String> checkBoxesText = new ArrayList<String>();
//        for(int i = 0; i < num_projects; i++) {
//            CheckBox checkBox = new CheckBox(getApplicationContext());
//            String n = patientProjects.get(i).getName();
//            checkBoxesText.add(n);
//        }

        ListView listview = (ListView) findViewById(R.id.treatments);
        String[] treatmentList = {"studyProject1", "studyProject2", "studyProject3"};

        ArrayList<String> checkboxesText = new ArrayList<String>(Arrays.asList(treatmentList));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, checkboxesText);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

//        Log.v(TAG, "length:" + listview);

        listview.setAdapter(adapter);

//        for (int i=0; i < checkboxesText.size(); i++){
//            listview.setItemChecked(i, false);
//        }

        Log.v(TAG, "length:" + listview.getItemAtPosition(1));

        Log.v(TAG, "length:" + listview.getItemAtPosition(2));


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
    public void switchPatientHome (View view){
        Intent intent = new Intent(this, PatientHomeActivity.class);
        startActivity(intent);

    }


}
