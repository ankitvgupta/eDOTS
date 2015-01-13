package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import edots.models.Locale;
import edots.models.Promoter;
import edots.tasks.LocaleLoadTask;

//import edots.models.Login;

//TODO: remember session, auto login
/*
 * Written by Brendan
 *
 * This is the start screen for the app when it is logged out. Allows for Promoter to login.
 *
 * onSubmit Behavior: Switches to MainMenuActivity via Intent
 *
 */
public class PromoterLoginActivity extends Activity {
    private Button loginButton;
    private EditText username;
    private EditText password;
    private Spinner spnLocale;
    private TextView tvwMensaje;
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

        String username = checkAlreadyLoggedIn();
        if (username != null){
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);

        }
    }


    private String checkAlreadyLoggedIn(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString((getString(R.string.login_username)), null);
        if (username !=null){
            return username;
        }
        return null;
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
    public  void switchPatientType (View view) throws Exception{

        EditText u= (EditText)findViewById(R.id.username);
        EditText p= (EditText)findViewById(R.id.password);
        String username = u.getText().toString();
        String password = u.getText().toString();

        String locale_name = spnLocale.getItemAtPosition(spnLocale.getSelectedItemPosition()).toString();
        String locale_num = "1";
        Locale[] objLocale;
        String[] wee;
        try {
            objLocale = loadLocale.get();
            for(int i = 0;i < objLocale.length; i++){
                if (locale_name.equals(objLocale[i].name)) {
                    locale_num = String.valueOf(objLocale[i].id);
                };
            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }


        boolean validLogin = checkLogin(username, password, locale_num);
        if (validLogin){

            Promoter new_promoter = OfflineStorageManager.GetWebPromoterData(username, this);
            OfflineStorageManager.SaveWebPatientData(new_promoter, this);
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
        }
        else{
           AlertError("Login Error","Your username or password was incorrect or invalid" );
        }

    }

    public void AlertError(String title, String message){
        // Alert if username and password are not entered
        AlertDialog.Builder loginError = new AlertDialog.Builder(this);
        loginError.setTitle(title);
        loginError.setMessage(message);
        loginError.setPositiveButton(R.string.login_try_again, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


            }
        });
        loginError.show();
    }

    // Calls login web service and returns true if login is successful
    public boolean checkLogin(String username, String password, String locale) {
        if(password != null && !password.isEmpty()) {
            String message =  AccountLogin.login(username,password,locale,this);
            if(message.equals(getString(R.string.session_init_key)) || message.equals(getString(R.string.password_expired_key))){
                OfflineStorageManager.SetLastLocalUpdateTime(this);
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


}
