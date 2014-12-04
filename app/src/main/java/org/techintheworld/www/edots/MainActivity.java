package org.techintheworld.www.edots;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String filename = "myfile";
        JSONObject newObject = new JSONObject();
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
