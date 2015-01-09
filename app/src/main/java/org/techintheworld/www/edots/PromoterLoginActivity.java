package org.techintheworld.www.edots;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;

import edots.models.Promoter;


public class PromoterLoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promoter_login);

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
            Intent intent = new Intent(this, MainMenuActivity.class);
            StorageManager.GetLocalData(username,username, this);
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

        return new Promoter("e","Name","Lima", "e", new ArrayList<String>(Arrays.asList("Med 1", "Med 2")));
    }

}
