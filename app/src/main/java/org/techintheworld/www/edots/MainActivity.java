package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;


public class MainActivity extends Activity {



     public class PatientConfirmDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // inflate the dialog view, and then set the text values for the subviews
            View dialog = inflater.inflate(R.layout.dialog_patient_confirmation, null);

            TextView patientname = (TextView) dialog.findViewById(R.id.patientname);
            patientname.setText("Patient Name: ");

            TextView nationalid = (TextView) dialog.findViewById(R.id.nationalid);
            nationalid.setText("National Id: ");


            //TextView textView = (TextView) findViewById(R.id.patientname);
            //textView.setText("Brendannn");

            builder.setView(dialog)
                .setPositiveButton(R.string.confirm_patient, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Confirm Patient -> move to next activity
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel and return to the previous one
                    }
                });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String filename = "myfile";
        JSONObject newObject = new JSONObject();

        DialogFragment newFragment = new PatientConfirmDialogFragment();
        newFragment.show(getFragmentManager(), "missles");



        try {
            newObject.put("location", 90210);
            newObject.put("name","brendan");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(newObject.toString().getBytes());
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // This is a comment
        try{
            FileInputStream inputStream = openFileInput(filename);
            StringBuffer fileContent = new StringBuffer("");
            byte[] buffer = new byte[1024];

            int n;
            while ((n = inputStream.read(buffer)) != -1)
            {
                fileContent.append(new String(buffer, 0, n));
            }
            JSONObject newerObject;
            newerObject = new JSONObject(fileContent.toString());
            String name = (String) newerObject.get("name");
            inputStream.close();
            TextView textView = (TextView) findViewById(R.id.searchResults);
            textView.setText(name);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(View view) {
        // Do something in response to button

        EditText editText = (EditText) findViewById(R.id.Search2);
        String message = editText.getText().toString();

        TextView textView = (TextView) findViewById(R.id.searchResults);
        textView.setText(message);

    }

    public void switchCheckFingerprint(View view){
        Intent intent = new Intent(this, CheckFingerPrintActivity.class);
        startActivity(intent);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
