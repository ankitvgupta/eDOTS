package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.concurrent.ExecutionException;

//import edots.models.Login;
import edots.models.Promoter;
import edots.models.Locale;

//TODO: remember session, auto login

public class PromoterLoginActivity extends Activity {
    private Button loginButton;
    private EditText username;
    private EditText password;
    private Spinner spnLocale;
    private TextView tvwMensaje;
//    private AsyncTask<String, String, Login> asyncTask;
    private AsyncTask<String, String, Locale[]> loadLocale;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promoter_login);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.loginButton);
        spnLocale = (Spinner) findViewById(R.id.locale_spinner);
        String myurl = "http://demo.sociosensalud.org.pe";
        loadLocaleSpinner(myurl);
        // list of sites
//        String[] sites = {"site1", "site2", "site3", "site4"};

        // sets layout_height for ListView based on number of sites
//        ListView siteView = (ListView)findViewById(R.id.sites);
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50 * sites.length, getResources().getDisplayMetrics());
//        siteView.getLayoutParams().height = height;

        // creating adapter for ListView
//        ArrayList<String> checkboxesText = new ArrayList<String>(Arrays.asList(sites));
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_checked, checkboxesText);

        // creates ListView checkboxes
//        ListView listview = (ListView) findViewById(R.id.sites);
//        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        listview.setAdapter(adapter);

//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String uname=username.getText().toString();
//                String password=password.getText().toString();
//
//                }
//            }
//
//        });
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
        boolean validLogin = checkLogin(username, password, "2"); // TODO: get locale
        if (validLogin){
            Intent intent = new Intent(this, MainMenuActivity.class);
            String fileJSON = StorageManager.GetLocalData("Promoter", username, this);
            Log.e("LOGGED IN:", username.concat(password) );
            // TODO: fix storage manager for login
            //StorageManager.GetLocalData(username, username, this);
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

    public boolean checkLogin(String username, String password, String locale) {
        if(password != null && !password.isEmpty()) {
            String message =  AccountLogin.login(username,password,locale,this);
            //String saltedHash = ProcessPassword.getSaltedHash(password);
            if(message.equals(getString(R.string.session_init_key)) || message.equals(getString(R.string.password_expired_key))){
                // Remote Server
                return true;

            }else{
                Log.i("login", "Datos incorrectos" );
                Toast.makeText(getBaseContext(), message,Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        else{
            return false;
        }
    }


    public void loadLocaleSpinner(String url){
        LocaleLoadTask localeTask = new LocaleLoadTask();

        loadLocale = localeTask.execute(url);
        Locale[] objLocale;
        String[] wee;
        try {

            objLocale = loadLocale.get();
            wee = new String[objLocale.length];

            for(int i = 0;i < objLocale.length; i++){
                wee[i]= objLocale[i].name;
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, wee);
            spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
            spnLocale.setAdapter(spinnerArrayAdapter);

        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

    }

    public Promoter getPromoterInfo(String username){

        return new Promoter("e","Name","Lima", "e", new ArrayList<Long>(Arrays.asList(new Long("12312"), new Long("12312"))));
    }

}
