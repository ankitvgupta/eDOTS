package org.techintheworld.www.edots;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;

import edots.models.Patient;
import edots.models.Project;


public class NewVisitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_visit);

        Patient p = new Patient("Brendan");
        ArrayList<Project> patientProjects= p.getEnrolledProjects();
        int num_projects = patientProjects.size();
        ArrayList<String> checkBoxesText = new ArrayList<String>();
        for(int i = 0; i < num_projects; i++) {
            CheckBox checkBox = new CheckBox(getApplicationContext());
            String n = patientProjects.get(i).getName();
            checkBoxesText.add(n);
        }

        // sets layout_height for ListView based on number of treatments
        ListView treatmentView = (ListView)findViewById(R.id.active_treatments);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 * num_projects, getResources().getDisplayMetrics());
        treatmentView.getLayoutParams().height = height;

        ArrayAdapter<String> adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, checkBoxesText);
        ListView lv= (ListView)findViewById(R.id.active_treatments);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lv.setAdapter(adapter);
        for (int i=0; i<checkBoxesText.size(); i++){
            lv.setItemChecked(i, true);
        }
        lv.setMinimumHeight(200);


    }

    public void submitVisit()
    {
        Intent intent = new Intent(this, GetPatientActivity.class);
        startActivity(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_visit, menu);

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



}
