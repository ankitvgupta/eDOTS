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

import java.util.concurrent.ExecutionException;

import edots.models.Locale;
import edots.tasks.LocaleLoadTask;
import edots.utils.AccountLogin;
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
    private AsyncTask<String, String, Locale[]> loadLocale;


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
        String locale_num = "1";
        Locale[] objLocale = new Locale[0];
        String[] wee;

        try {
            if (loadLocale.get() == null) {
                objLocale = loadLocale.get();
            } else {
                try {
                    OfflineStorageManager sm = new OfflineStorageManager(this);
                    //TODO: there is a file not found exception for this
                    String locale_file = getString(R.string.locale_filename);
                    JSONArray object = new JSONArray(sm.getStringFromLocal(locale_file));

                    objLocale = new Locale[object.length()];
                    // look at all patients
                    for (int i = 0; i < object.length(); i++) {
                        JSONObject obj = object.getJSONObject(i);
                        objLocale[i] = new Locale(obj.toString());
                    }
                } catch (JSONException e1) {
                    Log.e("ProgramLoginActivity: switchPatientType", "JSON exception on Load");
                }
            }
            for (int i = 0; i < objLocale.length; i++) {
                if (locale_name.equals(objLocale[i].name)) {
                    locale_num = String.valueOf(objLocale[i].id);
                }

            }
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        } catch (NullPointerException e1) {
            Log.e("ProgramLoginActivity: switchPatientType", "NullPointerException on Load");
        }

        boolean validLogin = checkLogin(username, password, locale_num, locale_name);
        if (validLogin) {
            OfflineStorageManager sm = new OfflineStorageManager(this);
            if (sm.CanUpdateLocalStorage()){
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
     * Calls login web service and returns true if login is successful
     *
     * @param username input promoter username
     * @param password input promoter password
     * @param locale   input promoter locale from Spinner
     * @return true if login successful from Service, false if not successful
     * @author JN
     */
    public boolean checkLogin(String username, String password, String locale, String locale_name) {
        if (password != null && !password.isEmpty()) {

            String message = AccountLogin.login(username, password, locale, locale_name, this);
            if (message == null) {
                return false;
            }
            if (message.equals(getString(R.string.session_init_key)) || message.equals(getString(R.string.password_expired_key))) {
                return true;

            } else {
                Log.i("login", "Datos incorrectos");
            }
            return false;
        } else {
            return false;
        }

    }

    /**
     * @param url the url of the server
     *            Loads the spinner for all the locales first by pulling down from server
     *            And if that does not work, then by checking file locally
     * @author Brendan
     */
    private void loadLocaleSpinner(String url) {
        LocaleLoadTask localeTask = new LocaleLoadTask();
        // load locale from server
        loadLocale = localeTask.execute(url);
        Locale[] objLocale;
        String[] locales;
        try {
            // try server side first
            objLocale = loadLocale.get();
            locales = new String[objLocale.length];

            for (int i = 0; i < objLocale.length; i++) {
                locales[i] = objLocale[i].name;
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, locales);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnLocale.setAdapter(spinnerArrayAdapter);
            OfflineStorageManager sm = new OfflineStorageManager(this);
            sm.SaveLocaleData(objLocale);
            Log.w("PromoterLoginActivity: loadLocaleSpinner", "saving to locale data file");

        } catch (InterruptedException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity1", "Interrupted Exception");
        } catch (ExecutionException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity1", "Execution Exception");
        } catch (NullPointerException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity1", " NullPointerException");
        }

        try {
            if (loadLocale.get() == null) {
                // locale_data load
                String locale_file = getString(R.string.locale_filename);
                OfflineStorageManager sm = new OfflineStorageManager(this);
                JSONArray object = new JSONArray(sm.getStringFromLocal(locale_file));
                locales = new String[object.length()];

                // look at all locales
                for (int i = 0; i < object.length(); i++) {
                    JSONObject obj = object.getJSONObject(i);
                    Locale l = new Locale(obj.toString());
                    locales[i] = l.name;
                }
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        this, android.R.layout.simple_spinner_item, locales);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnLocale.setAdapter(spinnerArrayAdapter);
            }
        } catch (ExecutionException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity", "Execution Exception On Load");
        } catch (InterruptedException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity", "Interrupted Exception On Load");
        } catch (JSONException e1) {
            Log.e("PromoterLoginActivity: loadLocaleActivity", " JSON Exception On Load");
        }
    }
}
