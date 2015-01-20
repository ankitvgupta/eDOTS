package org.techintheworld.www.edots;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import edots.models.Patient;


public class ChangeSchemaActivity extends Activity {
    
    Patient currentPatient;
    private ArrayList<String> treatmentDays = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_schema);

        try {
            currentPatient = new Patient(getIntent().getExtras().getString("Patient"));
        } catch (Exception e) {
            // TODO: Don't print the stack trace, give some sort of dialog box instead
            e.printStackTrace();
        }

        EditText dni = (EditText) findViewById(R.id.changeSchema_National_ID);
        dni.setText(currentPatient.getNationalID());
        
        EditText patientName = (EditText) findViewById(R.id.changeSchema_Name);
        patientName.setText(currentPatient.getName());
        
        /*
        GetSchemaLoadTask schemaLoader = new GetSchemaLoadTask();
        AsyncTask*/
        
    }
    /**
     * @author lili
     */
    public void loadTreatmentDayCheckboxes() {
        ListView treatmentView = (ListView) findViewById(R.id.changeSchema_treatment_days);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics());
        treatmentView.getLayoutParams().height = height;

        //ArrayList<String> treatmentDays = new ArrayList<String>();
        treatmentDays.add("Monday");
        treatmentDays.add("Tuesday");
        treatmentDays.add("Wednesday");
        treatmentDays.add("Thursday");
        treatmentDays.add("Friday");
        treatmentDays.add("Saturday");
        treatmentDays.add("Sunday");

        // creating adapter for ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, treatmentDays);

        // creates ListView checkboxes
        ListView listview = (ListView) findViewById(R.id.treatment_days);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(adapter);
    }
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_schema, menu);
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
