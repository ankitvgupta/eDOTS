package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edots.models.Locale;
import edots.tasks.LocaleLoadTask;
import edots.utils.AccountLogin;
import edots.utils.InternetConnection;
import edots.utils.OfflineStorageManager;


/*
 * Written by Brendan
 *
 * This is the start screen for the app when it is logged out. Allows for Promoter to login.
 *
 * onSubmit Behavior: Switches to MainMenuActivity via Intent
 *
 */
public class PromoterLoginActivity extends Activity {
    private Spinner spnLocale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promoter_login);
        spnLocale = (Spinner) findViewById(R.id.locale_spinner);
        String username = AccountLogin.CheckAlreadyLoggedIn(this);

        if (username != null) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
        } else {
            String myurl = getString(R.string.server_url);
            loadLocaleSpinner(myurl);
        }
        // Progress bar set to gone on page load
        ProgressBar p_d = (ProgressBar) findViewById(R.id.marker_progress);
        p_d.setVisibility(View.GONE);

        spnLocale.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return false;
            }
        });
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

    /**
     * Check if login is successful and save promoter and patient files locally if successful
     * show alert if login not successful
     *
     * @param view
     * @throws Exception
     * @author JN
     */
    public void switchPatientType(View view) {

        // Progress Dialog starts
        final ProgressBar p_d = (ProgressBar) findViewById(R.id.marker_progress);
        p_d.getHandler().post(new Runnable() {
            @Override
            public void run() {
                p_d.setVisibility(View.VISIBLE);
            }
        });

        EditText u = (EditText) findViewById(R.id.username);
        EditText p = (EditText) findViewById(R.id.password);
        String username = u.getText().toString();
        String password = p.getText().toString();

        String locale_name = spnLocale.getItemAtPosition(spnLocale.getSelectedItemPosition()).toString();
        String locale_num = null;
        Locale[] objLocale = new Locale[0];
        String[] wee;


        if (locale_name != null) {
            locale_num = Locale.GetLocaleNumber(this, locale_name);
            Log.e("PromoterLoginActivity: switchPatientType", locale_num);
        }

        boolean validLogin = AccountLogin.CheckLogin(this, username, password, locale_num, locale_name);
        if (validLogin) {
            OfflineStorageManager sm = new OfflineStorageManager(this);
            if (sm.CanUpdateLocalStorage()) {
                sm.UpdateLocalStorage();
            }
            Intent intent = new Intent(this, MainMenuActivity.class);
            p_d.getHandler().post(new Runnable() {
                public void run() {
                    p_d.setVisibility(View.GONE);
                }
            });
            startActivity(intent);
        } else {
            AlertError("Login Error", "Your username or password was incorrect or invalid");
            p_d.getHandler().post(new Runnable() {
                public void run() {
                    p_d.setVisibility(View.GONE);
                }
            });
        }

    }

    /**
     * Shows dialog with parameters if there is a login error
     *
     * @param title   title of dialogue
     * @param message message of dialogue
     * @author JN
     */
    public void AlertError(String title, String message) {
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


    /**
     * @param url the url of the server
     *            Loads the spinner for all the locales first by pulling down from server
     *            And if that does not work, then by checking file locally
     * @author Brendan
     */
    private void loadLocaleSpinner(String url) {
        LocaleLoadTask localeTask = new LocaleLoadTask();
        AsyncTask loadLocale;
        ArrayList<Locale> arrLocale = null;
        String[] locales;
        boolean connected = InternetConnection.checkConnection(this);
        if (connected){
            try {
                // try server side first
                loadLocale = localeTask.execute(url);
                arrLocale = (ArrayList<Locale>) loadLocale.get();
                locales = Locale.ConvertLocalObjsToStrings(arrLocale);

                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, locales);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnLocale.setAdapter(spinnerArrayAdapter);
                OfflineStorageManager sm = new OfflineStorageManager(this);
                String locale_file = getString(R.string.locale_filename);
                sm.SaveArrayListToLocal(arrLocale, locale_file);

            } catch (InterruptedException e1) {
                Log.e("PromoterLoginActivity: loadLocaleActivity1", "Interrupted Exception");
            } catch (ExecutionException e1) {
                Log.e("PromoterLoginActivity: loadLocaleActivity1", "Execution Exception");
            } catch (NullPointerException e1) {
                Log.e("PromoterLoginActivity: loadLocaleActivity1", " NullPointerException");
            }
        }

        // Load data locally
        try {
            if (arrLocale == null) {
                // locale_data load
                String locale_file = getString(R.string.locale_filename);
                OfflineStorageManager sm = new OfflineStorageManager(this);
                JSONArray array = new JSONArray(sm.getStringFromLocal(locale_file));
                locales = new String[array.length()];

                // look at all locales
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Locale l = new Locale(obj.toString());
                    locales[i] = l.name;
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, locales);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnLocale.setAdapter(spinnerArrayAdapter);
            }
        } catch (JSONException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity", " JSON Exception On Load");
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
