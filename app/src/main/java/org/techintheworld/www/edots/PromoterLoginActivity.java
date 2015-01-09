package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
<<<<<<< HEAD
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
=======
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
>>>>>>> 81cbc9de682a62c15ee4f64fc1bd5c6a52a2b702

import edots.models.Promoter;


public class PromoterLoginActivity extends Activity {
    private Button loginButton;
    private EditText username;
    private EditText password;
    private Spinner spnLocal;
    private TextView tvwMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promoter_login);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.loginButton);
        spnLocal = (Spinner) findViewById(R.id.spnLocal);

<<<<<<< HEAD
        // list of sites
        String[] sites = {"site1", "site2", "site3", "site4"};

        // sets layout_height for ListView based on number of sites
        ListView siteView = (ListView)findViewById(R.id.sites);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 * sites.length, getResources().getDisplayMetrics());
        siteView.getLayoutParams().height = height;

        // creating adapter for ListView
        ArrayList<String> checkboxesText = new ArrayList<String>(Arrays.asList(sites));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, checkboxesText);

        // creates ListView checkboxes
        ListView listview = (ListView) findViewById(R.id.sites);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setAdapter(adapter);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("login", "OnClick_connected:" + connected);
                if (connected){
                    String userName=txtUsuario.getText().toString();
                    String password=txtPassword.getText().toString();

                    try {


                        if(response.equals(getString(R.string.session_init_key)) || response.equals(getString(R.string.password_expired_key))){

                            editor.commit();
                            // Remote Server
                            Intent intent=new Intent(MainActivity.this,Menu_principal.class);
                            startActivity(intent);

                            finish();

                        }else{
                            Log.i("login", "Datos incorrectos" );
                            dismissDialog(PROGRESS_DIALOG);
                            Toast.makeText(getBaseContext(), response, Toast.LENGTH_SHORT).show();
                        }
                    } catch (InterruptedException e1) {
                        response = e1.getMessage();
                    } catch (ExecutionException e2) {
                        response = e2.getMessage();
                    } catch (Exception e3) {
                        response = e3.getMessage();
                    }
                    tvwMensaje.setText(response);

                }
            }

        });
//        // list of sites
//        String[] sites = {"site1", "site2", "site3", "site4"};
//
//        // sets layout_height for ListView based on number of sites
//        ListView siteView = (ListView)findViewById(R.id.sites);
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 * sites.length, getResources().getDisplayMetrics());
//        siteView.getLayoutParams().height = height;
//
//        // creating adapter for ListView
//        ArrayList<String> checkboxesText = new ArrayList<String>(Arrays.asList(sites));
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_checked, checkboxesText);
//
//        // creates ListView checkboxes
//        ListView listview = (ListView) findViewById(R.id.sites);
//        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        listview.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_worker_login, menu);
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

    // switch to PatientType activity
    public void switchPatientType (View view) throws Exception{

        EditText u= (EditText)findViewById(R.id.username);
        EditText p= (EditText)findViewById(R.id.password);
        String username = u.getText().toString();
        String password = u.getText().toString();
        boolean validLogin = checkLogin(username, password);
        if (validLogin){
            Intent intent = new Intent(this, PatientTypeActivity.class);
            StorageManager.GetLocalPromoterData(username, this);
            startActivity(intent);
        }
        else{
            // Alert if username and password are not entered
            AlertDialog.Builder loginError = new AlertDialog.Builder(this);
            loginError.setTitle("Login Error");
            loginError.setMessage("Your username or password was incorrect or invalid");
            loginError.setPositiveButton(R.string.login_try_again, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    EditText us= (EditText)findViewById(R.id.username);
                    EditText pw= (EditText)findViewById(R.id.password);
                    us.clearComposingText();
                    pw.clearComposingText();

                }
            });
            loginError.show();
        }

    }

    public boolean checkLogin(String username, String password) {

        if(password != null && !password.isEmpty()) {
            //String saltedHash = ProcessPassword.getSaltedHash(password);
            Promoter p = getPromoterInfo(username);
            //ProcessPassword.check(saltedHash, p.getPassword());
            return password.equals(p.getPassword());
        }
        else{
            return false;
        }
    }

    public Promoter getPromoterInfo(String username){

        return new Promoter("edots","Name","Lima", "edots", new ArrayList<String>(Arrays.asList("Med 1", "Med 2")));
    }

}
