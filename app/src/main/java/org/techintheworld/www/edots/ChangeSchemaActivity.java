package org.techintheworld.www.edots;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import edots.models.Patient;
import edots.models.Schedule;
import edots.models.Schema;

// TODO: name and national ID fields should not be editable

public class ChangeSchemaActivity extends Activity {
    
    Patient currentPatient;

    private ArrayList<String> schemaDays = new ArrayList<String>();


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
        
        EditText startDate = (EditText) findViewById(R.id.changeSchema_schema_start_day);
        startDate.setText(currentPatientSchedule.getStartDate());
        
        EditText endDate = (EditText) findViewById(R.id.changeSchema_schema_end_day);
        endDate.setText(currentPatientSchedule.getEndDate());

        loadSchemaDayCheckboxes(currentPatientSchedule);

    }
    /**
     * @author lili
     */
    public void loadSchemaDayCheckboxes(Schedule s) {
        ListView schemaView = (ListView) findViewById(R.id.changeSchema_schema_days);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics());
        schemaView.getLayoutParams().height = height;

        //ArrayList<String> schemaDays = new ArrayList<String>();
        //TODO: should not hardcode the strings for days
        schemaDays.add("Monday");
        schemaDays.add("Tuesday");
        schemaDays.add("Wednesday");
        schemaDays.add("Thursday");
        schemaDays.add("Friday");
        schemaDays.add("Saturday");
        schemaDays.add("Sunday");

        // creating adapter for ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, schemaDays);

        // creates ListView checkboxes
        ListView listview = (ListView) findViewById(R.id.changeSchema_schema_days);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(adapter);


    }


    /**
     * @author lili
     * check the checkboxes according to the current schedule
     */
    public void checkSchemaDayBoxes(Schedule s){
        ListView schemaView = (ListView) findViewById(R.id.changeSchema_schema_days);
        
        String oneHotCoding = s.toOneHotCoding();
        for (int i = 0; i < 14; i++){
            schemaView.setItemChecked(i, (oneHotCoding.charAt(i) == '1'));
        }
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
