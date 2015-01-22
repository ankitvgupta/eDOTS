package org.techintheworld.www.edots;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edots.models.Patient;
import edots.models.Schedule;
import edots.models.Schema;
import edots.tasks.NewSchemaUploadTask;

// TODO: name and national ID fields should not be editable

public class ChangeSchemaActivity extends Activity {
    
    Patient currentPatient;
    Schema currentPatientSchema;
    

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
        currentPatientSchema = currentPatient.getEnrolledSchema();
        
        //Schedule currentPatientSchedule = currentPatient.getPatientSchedule();
        Schedule currentPatientSchedule = currentPatientSchema.getSchedule();
        Log.v("ChangeSchemaActivity: the current schedule is", currentPatientSchedule.toString());
        
        EditText startDate = (EditText) findViewById(R.id.schema_start_day);
        startDate.setText(currentPatientSchedule.getStartDate());
        
        EditText endDate = (EditText) findViewById(R.id.schema_end_day);
        endDate.setText(currentPatientSchedule.getEndDate());

        loadSchemaDayCheckboxes(currentPatientSchedule);

    }
    
    /**
     * @author lili
     */
    public void loadSchemaDayCheckboxes(Schedule s) {
        ListView schemaView = (ListView) findViewById(R.id.changeSchema_schema_days);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 700, getResources().getDisplayMetrics());
        schemaView.getLayoutParams().height = height;

        //ArrayList<String> schemaDays = new ArrayList<String>();
        //TODO: should not hardcode the strings for days
        schemaDays.add("Monday");
        schemaDays.add("MondayAfternoon");
        schemaDays.add("Tuesday");
        schemaDays.add("TuesdayAfternoon");
        schemaDays.add("Wednesday");
        schemaDays.add("WednesdayAfternoon");
        schemaDays.add("Thursday");
        schemaDays.add("ThursdayAfternoon");
        schemaDays.add("Friday");
        schemaDays.add("FridayAfternoon");
        schemaDays.add("Saturday");
        schemaDays.add("SaturdayAfternoon");
        schemaDays.add("Sunday");
        schemaDays.add("SundayAfternoon");

        // creating adapter for ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, schemaDays);

        // creates ListView checkboxes
        ListView listview = (ListView) findViewById(R.id.changeSchema_schema_days);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setAdapter(adapter);
        checkSchemaDayBoxes(s);
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
    
    // TODO: Make this actually update the local schema in currentPatient - or perhaps update by pulling from the database
    public void updateSchema(){
        
        Schema tmp = new Schema();
        currentPatient.setEnrolledSchema(tmp);
        
    }
    
    public void submitChangeSchema(View view) {
        
        EditText startDate = (EditText) findViewById(R.id.schema_start_day);
        String start = startDate.getText().toString();
        EditText endDate = (EditText) findViewById(R.id.schema_end_day);
        String end = endDate.getText().toString();
        

        ArrayList<String> visitDays = new ArrayList<String>();
        ListView daysVisited = (ListView) findViewById(R.id.changeSchema_schema_days);
        SparseBooleanArray daysPicked = daysVisited.getCheckedItemPositions();
        for (int i = 0; i < daysVisited.getAdapter().getCount(); i++) {
            if (daysPicked.get(i)) {
                visitDays.add("1");
            }
            else {
                visitDays.add("0");
            }
        }

        NewSchemaUploadTask schemaUploader = new NewSchemaUploadTask();
        
        try {
            String s = schemaUploader.execute(
                    getString(R.string.server_url),
                    currentPatient.getPid(),
                    visitDays.get(0),
                    visitDays.get(1),
                    visitDays.get(2),
                    visitDays.get(3),
                    visitDays.get(4),
                    visitDays.get(5),
                    visitDays.get(6),
                    visitDays.get(7),
                    visitDays.get(8),
                    visitDays.get(9),
                    visitDays.get(10),
                    visitDays.get(11),
                    visitDays.get(12),
                    visitDays.get(13),
                    "2014-05-20 00:00:00.0", // TODO: SHould be the real start date
                    "2015-05-28 00:00:00.0", // TODO: Should be the real end date
                    "1",
                    "1").get();
            updateSchema();
            Log.v("ChangeSchemaActivity.java: The result of the schema upload was: ", s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, GetPatientActivity.class);
        intent.putExtra("Patient", currentPatient.toString());
        startActivity(intent);
        
        return;
        
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
