package org.techintheworld.www.edots;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Ankit on 11/13/14.
 *
 * Creates a new app class, and tests if a testObject class can be made and basic key value pair saved to Parse (it works!)
 */
public class App extends Application {
    @Override public void onCreate() {

        Parse.initialize(this, "4bkQnt3kNmAOe4Uu9ut30iqhudHe5HrFr39Yszn9", "2yJTXRxSCYEz9EbS0sNfZB3KnAnZeleJt1M2Psy5");
        ParseObject testObject = new ParseObject("TestObject");

        testObject.put("foo", "bar");
        testObject.saveInBackground();

        super.onCreate();
    }
}
