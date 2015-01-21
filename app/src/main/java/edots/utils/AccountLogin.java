package edots.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.techintheworld.www.edots.R;

import java.util.concurrent.ExecutionException;

import edots.models.Login;
import edots.tasks.LoginTask;

/**
 * TODO: author, description of the file
 */

public class AccountLogin {

    // TODO: refactor dialog back into promoter login activity

    public static String login( Context c, String username, String password, String locale_id, String locale_name) {
        // TODO: check internet connection

        LoginTask runner = new LoginTask();
        AsyncTask<String, String, Login> loginAsyncTask;
        String response = "";

        // Get the server from the settings
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        SharedPreferences.Editor editor = mPreferences.edit();
        // TODO: do not hardcode
        String url = "http://demo.sociosensalud.org.pe";
        Log.i("login", "OnClick_url:" + url);

        loginAsyncTask = runner.execute(username, password, locale_id, url);

        try {
            Login login = loginAsyncTask.get();
            response = login.Message;
            editor.putString(c.getString(R.string.username), username);
            editor.putString(c.getString(R.string.key_userid), String.valueOf(login.UserID));
            editor.putString(c.getString(R.string.login_locale), locale_id);
            editor.putString(c.getApplicationContext().getString(R.string.login_locale_name), locale_name);

            Log.i("login", "OnClick_response:" + response);
            if (response.equals(c.getString(R.string.session_init_key)) || response.equals(c.getString(R.string.password_expired_key))) {
                editor.commit();
            }
        } catch (InterruptedException e1) {
            response = e1.getMessage();
        } catch (ExecutionException e2) {
            response = e2.getMessage();
        } catch (Exception e3) {
            response = e3.getMessage();
        }
        return response;
    }

    /**
     * Checks Shared Preferences if already logged in by checking if saved username is the same as the current one
     *
     * @return username of the promoter that is logged in
     * @author JN
     */
    public static String CheckAlreadyLoggedIn(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String username = prefs.getString((context.getString(R.string.username)), null);
        if (username != null) {
            return username;
        }
        return null;
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
    public static boolean CheckLogin(Context context, String username, String password, String locale, String locale_name) {
        if (password != null && !password.isEmpty()) {

            String message = login(context, username, password, locale, locale_name);
            if (message == null) {
                return false;
            }
            if (message.equals(context.getString(R.string.session_init_key)) || message.equals(context.getString(R.string.password_expired_key))) {
                return true;

            } else {
                Log.i("login", "Datos incorrectos");
            }
            return false;
        } else {
            return false;
        }

    }

}
