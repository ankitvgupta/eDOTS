package org.techintheworld.www.edots;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
        String[] treatmentList = {"studyProject1", "studyProject2", "studyProject3", "studyProject4"};

        // sets layout_height for ListView based on number of treatments
        ListView treatmentView = (ListView)findViewById(R.id.treatments);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50*treatmentList.length, getResources().getDisplayMetrics());
        treatmentView.getLayoutParams().height = height;

        // creating adapter for ListView
        ArrayList<String> checkboxesText = new ArrayList<String>(Arrays.asList(treatmentList));
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
    public void switchCheckFingerPrint(View view){

        EditText nationalIDText = (EditText) findViewById(R.id.National_ID);
        Long nationalID = nationalIDText.getText().toString();

        EditText nameText = (EditText) findViewById(R.id.Name);
        String name = nameText.getText().toString();

        EditText fathersNameText = (EditText) findViewById(R.id.Fathers_name);
        String fathersName = fathersNameText.getText().toString();

        EditText mothersNameText = (EditText) findViewById(R.id.Mothers_name);
        String mothersName = mothersNameText.getText().toString();

        EditText birthdateText = (EditText) findViewById(R.id.Birthdate);
        String date = birthdateText.getText().toString();

        EditText sexText = (EditText) findViewById(R.id.Sex);
        String sex = sexText.getText().toString();

        // determines which treatments are checked and stores them in ArrayList of Projects
        ArrayList<Project> treatmentList = new ArrayList<Project>();
        ListView treatmentListText = (ListView) findViewById(R.id.treatments);
        SparseBooleanArray checkedItems = treatmentListText.getCheckedItemPositions();
        for (int i = 0; i < treatmentListText.getAdapter().getCount(); i++) {
            if (checkedItems.get(i)) {
                String treatment = treatmentListText.getAdapter().getItem(i).toString();
                Project proj = new Project(treatment);
                treatmentList.add(proj);
            }
        }

        Patient p = new Patient(name, date, nationalID, sex, treatmentList)
        Intent intent = new Intent(this, CheckFingerPrintActivity.class);
        startActivity(intent);

    }

}
