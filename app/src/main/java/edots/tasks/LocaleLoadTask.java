package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import edots.models.Locale;
/*
 * Written by Brendan
 * Reviewed by Ankit
 *
 * Pulls all of the locales available during login from the database.
 */
public class LocaleLoadTask extends AsyncTask<String,String,ArrayList<Locale>> {

    private ArrayList<Locale> lstLocales;

    @Override
    protected ArrayList<Locale> doInBackground(String... params) {

        ArrayList<Locale> result= null;

        // setup server parameters
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "ListadoLocales";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // send request to server
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;

        Log.e("LocaleLoadTask:do in Background", "here");


        try
        {
            transporte.call(SOAP_ACTION, envelope);

            // get the response
            SoapObject resSoap = (SoapObject) envelope.getResponse();

            // instantiate the locales list
            lstLocales = new ArrayList<Locale>();
            int num_locales = resSoap.getPropertyCount();

            // for each that are returned. instantiate a new locale, add the properties, and add it to the list to be returned
            for (int i = 0; i < num_locales; i++) {
                SoapObject ic = (SoapObject) resSoap.getProperty(i);

                Locale loc = new Locale();
                // TODO: Change the argument to getProperty from indicies to the actual values (ex: ic.getProperty("CodigoPaciente"))
                loc.id = Integer.parseInt(ic.getProperty(0).toString());
                loc.name = ic.getProperty(1).toString();
                lstLocales.add(loc);
            }
            if (resSoap.getPropertyCount() > 0) {
                result = lstLocales;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return result;
    }



}

