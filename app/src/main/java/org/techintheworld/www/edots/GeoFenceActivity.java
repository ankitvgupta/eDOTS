package org.techintheworld.www.edots;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import edots.models.Geofence;



public class GeoFenceActivity extends Activity {
    private static final String TAG = "GeoFenceActivity";
    private Spinner spnGeofence;
    private Button btnShow_selected_local;
    private Geofence[] objGeofence;
    String selGeofence;
    private AsyncTask<String, String, Geofence[]> loadGeofence;

    private String myurl= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_fence);

        spnGeofence = (Spinner) findViewById(R.id.spnGeofence);
        btnShow_selected_local  = (Button) findViewById(R.id.btnShow_selected_local);
        myurl = "http://demo.sociosensalud.org.pe";
        loadGeofenceSpinner(myurl);

        spnGeofence.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               android.view.View v, int position, long id) {
                        selGeofence = Integer.toString(position);
                        Log.i(TAG, "Seleccionado: pos: " + selGeofence + " valor:" + parent.getItemAtPosition(position));
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(getBaseContext(), "Seleccione un Local!!", Toast.LENGTH_SHORT).show();
                    }
                });

        btnShow_selected_local.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                if (selGeofence.equals("0")){
                    Toast.makeText(getBaseContext(), "Choose a Local!!",Toast.LENGTH_SHORT).show();
                }else {

                    Toast.makeText(getBaseContext(), "Selected Local : "+selGeofence.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadGeofenceSpinner(String url){
        GeofenceLoadTask tareaGeofence = new GeofenceLoadTask();

        loadGeofence = tareaGeofence.execute(url);
        Log.i(TAG,".loadGeofenceSpinner:"+url);
        Geofence[] objGeofence;
        String[] wee;
        try {

            objGeofence = loadGeofence.get();
            wee = new String[objGeofence.length];

            for(int i = 0;i < objGeofence.length; i++){
                wee[i]= objGeofence[i].nombre;
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, wee);
            spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            spnGeofence.setAdapter(spinnerArrayAdapter);

            Log.i(TAG,"Geofence Array");

        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

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