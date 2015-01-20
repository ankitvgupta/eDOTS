package org.techintheworld.www.edots;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.models.Schedule;
import edots.models.Schema;
import edots.tasks.GetPatientSchemaLoadTask;


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
            return;
        }

        // Add the current patient's DNI
        EditText dni = (EditText) findViewById(R.id.changeSchema_National_ID);
        dni.setText(currentPatient.getNationalID());
        
        // Add the current patient's name
        EditText patientName = (EditText) findViewById(R.id.changeSchema_Name);
        patientName.setText(currentPatient.getName());
        
        // get the current patient's schema
        Schema currentPatientSchema = currentPatient.getEnrolledSchema();
        Schedule currentPatientSchedule = currentPatient.getPatientSchedule();
        
        EditText startDate = (EditText) findViewById(R.id.changeSchema_treatment_start_day);
        startDate.setText(currentPatientSchedule.getStartDate());
        
        EditText endDate = (EditText) findViewById(R.id.changeSchema_treatment_end_day);
        endDate.setText(currentPatientSchedule.getEndDate());

        loadTreatmentDayCheckboxes(currentPatientSchedule);
        

                
        
        /*
        GetSchemaLoadTask schemaLoader = new GetSchemaLoadTask();
        AsyncTask*/   
    }
    /**
     * @author lili
     */
    public void loadTreatmentDayCheckboxes(Schedule s) {
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
        ListView listview = (ListView) findViewById(R.id.changeSchema_treatment_days);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(adapter);
        
        checkSchemaDayBoxes(s);
        
    }
    
    
    
    
    public void checkSchemaDayBoxes(Schedule s){
        ListView treatmentView = (ListView) findViewById(R.id.changeSchema_treatment_days);
        
        if (s.scheduledLunes())
            treatmentView.setItemChecked(0,true);
        if (s.scheduledMartes())
            treatmentView.setItemChecked(1,true);
        if (s.scheduledMiercoles())
            treatmentView.setItemChecked(2,true);
        if (s.scheduledJueves())
            treatmentView.setItemChecked(3,true);
        if (s.scheduledViernes())
            treatmentView.setItemChecked(4,true);
        if (s.scheduledSabado())
            treatmentView.setItemChecked(5,true);
        if (s.scheduledDomingo())
            treatmentView.setItemChecked(6,true);



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
