package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import edots.models.Drug;
import edots.models.Patient;
import edots.models.Schema;
import edots.tasks.GetPatientLoadTask;
import edots.tasks.GetPatientSchemaLoadTask;
import edots.tasks.NewPatientUploadTask;
import edots.tasks.NewPromoterPatientUploadTask;
import edots.tasks.NewSchemaUploadTask;
import edots.utils.InternetConnection;
import edots.utils.OfflineStorageManager;

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
 * onSubmit behavior: adds Patient to db, pulls patient from db to get patientId, and passes that Patient to GetPatientActivity via Intent
 */

public class NewPatientDataActivity extends Activity {

    private Spinner spnSchema;
    private Patient currentPatient;
    private ArrayList<Schema> schemaList = new ArrayList<Schema>();
    private ArrayList<Drug> drugList = new ArrayList<Drug>();
    private ArrayList<String> schemaDays = new ArrayList<String>();
    DateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Calendar birthDate;
    private Calendar startDate;
    private Calendar endDate;
    private EditText activeDateDisplay;
    private Calendar activeDate;
    private EditText birthDateDisplay;
    private EditText startDateDisplay;
    private EditText endDateDisplay;

    EditText nationalID;
    EditText name;
    EditText fatherName;
    EditText motherName;
    EditText birthDateText;
    EditText phoneNumber;
    EditText schemaStartDate;
    EditText schemaEndDate;
    RadioButton femaleBtn;
    RadioButton maleBtn;
    RadioButton clinicBtn;
    RadioButton patientHomeBtn;
    ListView schemaListText;
    ListView daysVisited;
    ListView drugsList;

    static final int DATE_DIALOG_ID = 0;

    @Override
    /**
     * Also checks if connected to internet
     * @author JN
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient_data);

        nationalID = (EditText) findViewById(R.id.National_ID);
        name = (EditText) findViewById(R.id.Name);
        fatherName = (EditText) findViewById(R.id.Fathers_name);
        motherName = (EditText) findViewById(R.id.Mothers_name);
        birthDateText = (EditText) findViewById(R.id.Birthdate);
        phoneNumber = (EditText) findViewById(R.id.PhoneNumber);
        schemaStartDate = (EditText) findViewById(R.id.schema_start_day);
        schemaEndDate = (EditText)findViewById(R.id.schema_end_day);
        femaleBtn = (RadioButton) findViewById(R.id.radio_female);
        maleBtn = (RadioButton) findViewById(R.id.radio_male);
        clinicBtn = (RadioButton) findViewById(R.id.radio_clinic);
        patientHomeBtn = (RadioButton) findViewById(R.id.radio_patient_home);
//        schemaListText = (ListView) findViewById(R.id.schema);
        daysVisited = (ListView) findViewById(R.id.schema_days);
        drugsList = (ListView) findViewById(R.id.drugs);
        spnSchema = (Spinner) findViewById(R.id.schema_spinner);

        // Makes sure that the keyboard doesn't automatically rise
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        loadDatePickers();
        loadSchemaSpinner(this.getString(R.string.server_url));
        //loadSchemaCheckboxes();
        loadDrugCheckboxes();
        loadSchemaDayCheckboxes();


        // check if not connected to internet, then disable everything and show dialog
        if (!InternetConnection.checkConnection(this)){
            AlertError(getString(R.string.no_internet_title), getString(R.string.no_internet_connection));
            blockAllInput();
            TextView tview = (TextView)findViewById(R.id.internet_status);
            tview.setVisibility(View.VISIBLE);

        }
    }

    /* @author Nishant
     * Loads the datePickers for birthdate, treatment start day and treatment end day
     */
    public void loadDatePickers() {

        birthDateDisplay = (EditText) findViewById(R.id.Birthdate);
        startDateDisplay = (EditText) findViewById(R.id.schema_start_day);
        endDateDisplay = (EditText) findViewById(R.id.schema_end_day);

        /* get the current date */
        birthDate = Calendar.getInstance();

        birthDateDisplay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            showDateDialog(birthDateDisplay, birthDate);
            }
        });

        startDate = Calendar.getInstance();

        startDateDisplay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
            showDateDialog(startDateDisplay, startDate);
            }
        });

        endDate = Calendar.getInstance();

        endDateDisplay.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
            showDateDialog(endDateDisplay, endDate);
            }
        });

        updateDisplay(birthDateDisplay, birthDate);
        updateDisplay(startDateDisplay, startDate);
        updateDisplay(endDateDisplay, endDate);
    }

    /* @author Nishant
     * Updates text for DatePickers
     */
    private void updateDisplay(EditText dateDisplay, Calendar date) {
        dateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(date.get(Calendar.MONTH) + 1).append("-")
                        .append(date.get(Calendar.DAY_OF_MONTH)).append("-")
                        .append(date.get(Calendar.YEAR)).append(" "));

    }

    /* @author Nishant
     * Displays date dialog for setting the date
     */
    public void showDateDialog(EditText dateDisplay, Calendar date) {
        activeDateDisplay = dateDisplay;
        activeDate = date;
        showDialog(DATE_DIALOG_ID);
    }

    /* @author Nishant
     * Tells listener for Datepicker what to do
     */
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            activeDate.set(Calendar.YEAR, year);
            activeDate.set(Calendar.MONTH, monthOfYear);
            activeDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDisplay(activeDateDisplay, activeDate);
            unregisterDateDisplay();
        }
    };

    /* @author Nishant
     * Resets the Datepicker
     */
    private void unregisterDateDisplay() {
        activeDateDisplay = null;
        activeDate = null;
    }

    /* @author Nishant
     * Used for Datepicker
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, dateSetListener, activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    /* @author Nishant
     * Displays date dialog for setting the date
     */
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
                break;
        }
    }

    /* Written by Nishant
     * Loads Checkboxes Dynamically for Schemas
     */
//    public void loadSchemaCheckboxes() {
//        // list of treatment study groups
//        // for testing
//        schemaList.add(new Schema());
//        schemaList.add(new Schema());
//        schemaList.add(new Schema());
//        schemaList.add(new Schema());
//
//        // sets layout_height for ListView based on number of treatments
//        ListView treatmentView = (ListView) findViewById(R.id.schema);
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 * schemaList.size(), getResources().getDisplayMetrics());
//        treatmentView.getLayoutParams().height = height;
//
//
//        ArrayList<String> checkboxesText = new ArrayList<String>();
//        for (int i = 0; i < schemaList.size(); i++) {
//            checkboxesText.add(schemaList.get(i).getName());
//        }
//        // creating adapter for ListView
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_checked, checkboxesText);
//
//        // creates ListView checkboxes
//        schemaListText.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        schemaListText.setAdapter(adapter);
//    }

//    public void loadSchemaCheckboxes() {
//        // list of treatment study groups
//        // for testing
//        schemaList.add(new Schema());
//        schemaList.add(new Schema());
//        schemaList.add(new Schema());
//        schemaList.add(new Schema());
//
//        // sets layout_height for ListView based on number of treatments
//        ListView schemaView = (ListView) findViewById(R.id.schema);
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 * schemaList.size(), getResources().getDisplayMetrics());
//        schemaView.getLayoutParams().height = height;
//
//
//        ArrayList<String> checkboxesText = new ArrayList<String>();
//        for (int i = 0; i < schemaList.size(); i++) {
//            checkboxesText.add(schemaList.get(i).getName());
//        }
//        // creating adapter for ListView
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_checked, checkboxesText);
//
//        // creates ListView checkboxes
//        schemaListText.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        schemaListText.setAdapter(adapter);
//    }
//

    /**
     * @author nishant
     * Loads Checkboxes Dynamically for Drugs
     */
    // TODO: add dosage text editors
    public void loadDrugCheckboxes() {
        // list of drugs
        // for testing
        drugList.add(new Drug());
        drugList.add(new Drug());
        drugList.add(new Drug());
        drugList.add(new Drug());

        // sets layout_height for ListView based on number of drugs
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 * drugList.size(), getResources().getDisplayMetrics());
        drugsList.getLayoutParams().height = height;


        ArrayList<String> checkboxesText = new ArrayList<String>();
        for (int i = 0; i < drugList.size(); i++) {
            checkboxesText.add(drugList.get(i).getName());
        }
        
        // creating adapter for ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, checkboxesText);

        // creates ListView checkboxes
        drugsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        drugsList.setAdapter(adapter);
    }

    /**
     * @author nishant
     * updated: lili
     * loads treatment day checkboxes
     */
    public void loadSchemaDayCheckboxes() {
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics());
        daysVisited.getLayoutParams().height = height;

        schemaDays.add("Monday");
        schemaDays.add("Tuesday");
        schemaDays.add("Wednesday");
        schemaDays.add("Thursday");
        schemaDays.add("Friday");
        schemaDays.add("Saturday");
        schemaDays.add("Sunday");
        schemaDays.add("Monday Afternoon");
        schemaDays.add("Tuesday Afternoon");
        schemaDays.add("Wednesday Afternoon");
        schemaDays.add("Thursday Afternoon");
        schemaDays.add("Friday Afternoon");
        schemaDays.add("Saturday Afternoon");
        schemaDays.add("Sunday Afternoon");

        // creating adapter for ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, schemaDays);

        // creates ListView checkboxes
        daysVisited.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        daysVisited.setAdapter(adapter);
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

    /*
     * Written by Nishant
     * Alert Dialog in case of User Error
     * For example, if the user does not enter proper data into the fields
     */
    public void AlertError(String title, String message) {
        // Alert if username and password are not entered
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton (Dialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // here you can add functions
            }
        });
        alertDialog.show();
    }

    public void addToDatabase(String name, String father, String mother, String docType,
                              String nationalID, String birthDate, String phoneNumber, String sex,
                              ArrayList<String> visitDays, String schemaStartDate,
                              String schemaEndDate, String visit_mode, String schema_num,
                              ArrayList<Drug> enrolledDrugs) {

        // TODO: UPLOAD PHONE NUMBER
        // TODO: upload schema start and end date
        // TODO: upload visit mode -- home or clinic
        // TODO: upload enrolledSchemas, enrolledDrugs,
        NewPatientUploadTask uploader = new NewPatientUploadTask();
        NewSchemaUploadTask scheduleUploader = new NewSchemaUploadTask();
        try {
            String url = getString(R.string.server_url);
            String result = uploader.execute(url, name, father, mother, docType, nationalID, birthDate, sex).get();
            Log.v("NewPatientDataActivity: what we got was", result);
            GetPatientLoadTask gpl = new GetPatientLoadTask();
            Patient p = gpl.execute(url, nationalID).get();
            /*

            String result2 = scheduleUploader.execute(
                                    url,
                                    p.getPid(),
                                    visitDays.get(0),
                                    visitDays.get(1),
                                    visitDays.get(2),
                                    visitDays.get(3),
                                    visitDays.get(4),
                                    visitDays.get(5),
                                    visitDays.get(6),
                                    treatmentStartDate,
                                    treatmentEndDate,
                                    "1").get();
                                    */
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
            NewPromoterPatientUploadTask npu = new NewPromoterPatientUploadTask();
            String npuMessage = npu.execute(getString(R.string.server_url), p.getPid(), mPreferences.getString(getString(R.string.key_userid), ""), "0").get();
            Log.v("GetPatientActivity: loadPatient: PromoterPatientUploadTask", npuMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
            AlertError("Entry Error", "The data you entered is of the wrong format, please try again");
        } catch (ExecutionException e) {
            e.printStackTrace();
            AlertError("Entry Error", "The data you entered is of the wrong format, please try again");
        }
        return;
    }


    public void onRadioButtonClicked(View view) {
        return;
    }

    /*
     * Written by Nishant
     * Ensures the entered national ID is of proper format and length
     */
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

    /*
     * Written by Nishant
     * Ensures none of the fields are empty
     */
    public boolean validateEmpty() {
        String nationalIDVal = nationalID.getText().toString();
        String nameVal = name.getText().toString();
        String fatherNameVal = fatherName.getText().toString();
        String motherNameVal = motherName.getText().toString();
        String birthDateVal = birthDateText.getText().toString();
        String phoneNumberVal = phoneNumber.getText().toString();
        String schemaStartDateVal = schemaStartDate.getText().toString();
        String schemaEndDateVal = schemaEndDate.getText().toString();

//        SparseBooleanArray checkedItems = schemaListText.getCheckedItemPositions();
//        int numSchemas = 0;
//        for (int i = 0; i < schemaListText.getAdapter().getCount(); i++) {
//            if (checkedItems.get(i)) {
//                numSchemas++;
//            }
//        }

        SparseBooleanArray daysPicked = daysVisited.getCheckedItemPositions();
        int scheduledDays = 0;
        for (int i = 0; i < daysVisited.getAdapter().getCount(); i++) {
            if (daysPicked.get(i)) {
                scheduledDays++;
            }
        }

        SparseBooleanArray drugsPicked = drugsList.getCheckedItemPositions();
        int numDrugs = 0;
        for (int i = 0; i < drugsList.getAdapter().getCount(); i++) {
            if (drugsPicked.get(i)) {
                numDrugs++;
            }
        }

        if (nationalIDVal.equals("") || nameVal.equals("") || fatherNameVal.equals("") ||
                motherNameVal.equals("") || birthDateVal.equals("") || phoneNumberVal.equals("")
                || schemaStartDateVal.equals("") || schemaEndDateVal.equals("")
                || !(maleBtn.isChecked() || femaleBtn.isChecked())
                || !(clinicBtn.isChecked() || patientHomeBtn.isChecked())) {
            return false;
        } else if (scheduledDays == 0 || numDrugs == 0) {
            return false;
        }
        return true;
    }

    /**
     * @author Ankit
     *
     * Blocks all of the Buttons, EditTexts, and ListViews
     */
    private void blockAllInput(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.newPatientLayout);
        for(int i=0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof Button) {
                ((Button) v).setEnabled(false); //Or View.INVISIBLE to keep its bounds
            }
            if (v instanceof EditText) {
                ((EditText)v).setEnabled(false);
            }
            if (v instanceof RadioButton){
                ((RadioButton)v).setEnabled(false);
            }
            if (v instanceof ListView){
                ((ListView)v).setEnabled(false);
            }

        }
    }

    /*
     * Written by Nishant
     * Takes all the user-entered data and creates new patient object
     */
    public void addPatientBtn(View view) {

        if (!(validateEmpty())) {
            AlertError("Entry Error", "You have left one of the fields blank, please try again");
        } else if (!(validateNationalID())) {
            AlertError("Entry Error", "The entered NationalID is not valid");
        } else {
            String nationalIDVal = nationalID.getText().toString();
            String nameVal = name.getText().toString();
            String fatherNameVal = fatherName.getText().toString();
            String motherNameVal = motherName.getText().toString();
            String birthDateVal = birthDateText.getText().toString();
            String phoneNumberVal = phoneNumber.getText().toString();
            String schemaStartDateVal = schemaStartDate.getText().toString();
            String schemaEndDateVal = schemaEndDate.getText().toString();
            String schema_name = spnSchema.getItemAtPosition(spnSchema.getSelectedItemPosition()).toString();
            String schema_num = null;
            Schema[] objSchema = new Schema[0];
            String[] wee;


            if (schema_name != null) {
                schema_num = Schema.GetSchemaNumber(this, schema_name);
                Log.e("NewPaitentDataActivity: addPatientBtn", schema_num);
            }

            // get the sex
            String sex = "";
            if (femaleBtn.isChecked()) {
                sex = "2";
            }
            if (maleBtn.isChecked()) {
                sex = "1";
            }

            // get the visit mode
            String visit_mode = "";
            if (clinicBtn.isChecked()) {
                visit_mode = "1";
            }
            if (patientHomeBtn.isChecked()) {
                visit_mode = "2";
            }
            
            // determines which treatments are checked and stores them in ArrayList of Projects
//            ArrayList<Schema> enrolledSchemas = new ArrayList<Schema>();
//            SparseBooleanArray checkedItems = schemaListText.getCheckedItemPositions();
//            for (int i = 0; i < schemaListText.getAdapter().getCount(); i++) {
//                if (checkedItems.get(i)) {
//                    enrolledSchemas.add(schemaList.get(i));
//                }
//            }

            // determines which days are checked and stores them in ArrayList of strings
            ArrayList<String> visitDays = new ArrayList<String>();
            SparseBooleanArray daysPicked = daysVisited.getCheckedItemPositions();
            for (int i = 0; i < daysVisited.getAdapter().getCount(); i++) {
                if (daysPicked.get(i)) {
                    visitDays.add("1");
                }
                else {
                    visitDays.add("0");
                }
            }

            ArrayList<Drug> enrolledDrugs = new ArrayList<Drug>();
            SparseBooleanArray drugsPicked = drugsList.getCheckedItemPositions();
            for (int i = 0; i < drugsList.getAdapter().getCount(); i++) {
                if (drugsPicked.get(i)) {
                    enrolledDrugs.add(drugList.get(i));
                }
            }

            // Submit the patient data to the server.
            addToDatabase(nameVal, fatherNameVal, motherNameVal, "2", nationalIDVal, birthDateVal,
                    phoneNumberVal, sex, visitDays, schemaStartDateVal, schemaEndDateVal, visit_mode,
                    schema_num, enrolledDrugs);

            switchNewVisitActivity(nationalIDVal);
        }
    }

    /*
     * Written by Nishant
     * Switches to the New Visit activity and passes ni the patient object through the Intent
     */
    public void switchNewVisitActivity (String nationalID) {
        // then query the database to get the patient, including the patient code generated by server
        GetPatientLoadTask getP = new GetPatientLoadTask();
        try {
            AsyncTask p = getP.execute(getString(R.string.server_url), nationalID);
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
                AlertError(getString(R.string.no_internet_title), getString(R.string.no_internet_connection));
            } else {
                e.printStackTrace();
            }
        }
    }


    /**
     * @param url the url of the server
     *            Loads the spinner for all the locales first by pulling down from server
     *            And if that does not work, then by checking file locally
     * @author Brendan
     */
    private void loadSchemaSpinner(String url) {
        GetPatientSchemaLoadTask schemaLoadTask = new GetPatientSchemaLoadTask();
        AsyncTask loadSchema;
        ArrayList<Schema> arrSchema = null;
        String[] locales;
        try {
            // try server side first
            loadSchema = schemaLoadTask.execute(url, "D74CCD37-8DE4-447C-946E-1300E9498577");
            arrSchema = (ArrayList<Schema>) loadSchema.get();
            locales = Schema.ConvertSchemaObjsToStrings(arrSchema);

            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, locales);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnSchema.setAdapter(spinnerArrayAdapter);
            OfflineStorageManager sm = new OfflineStorageManager(this);
            sm.SaveArrayListToLocal(arrSchema, this.getString(R.string.schema_filename));
        } catch (InterruptedException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity1", "Interrupted Exception");
        } catch (ExecutionException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity1", "Execution Exception");
        } catch (NullPointerException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity1", " NullPointerException");
        }
    }
}
