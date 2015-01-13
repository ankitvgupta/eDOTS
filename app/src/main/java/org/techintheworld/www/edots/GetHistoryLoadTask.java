

package org.techintheworld.www.edots;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import edots.models.Patient;
import edots.models.Project;
import edots.models.Visit;

/**
 * Created by Ankit on 1/12/15.
 */
public class GetHistoryLoadTask extends AsyncTask<String,String,ArrayList<Visit>> {


    //private Patient lstGeofence;

    @Override
    protected ArrayList<Visit> doInBackground(String... params) {

        ArrayList<Visit> results = new ArrayList<Visit>();

        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "ListadoVisitas1";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("CodigoPaciente", params[1]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap = (SoapObject) envelope.getResponse();
            SoapObject resSoapTemp = null;

            int numVisits = resSoap.getPropertyCount();
            for (int i = 0; i < numVisits; i++){
                resSoapTemp  = (SoapObject) resSoap.getProperty(i);
                String SiteCode = "Locale"; // need to change the C# function to actually pull the locale
                String ProjectCode = resSoapTemp.getProperty("CodigoProyecto").toString();
                String VisitGroupCode = (String) resSoapTemp.getProperty("CodigoGrupoVisita").toString();
                String VisitCode = (String) resSoapTemp.getProperty("CodigoVisita").toString();
                String PacientCode = params[1];
                String VisitDate = (String) resSoapTemp.getProperty("FechaVisita").toString();
                String TimeVal = (String) resSoapTemp.getProperty("HoraCita").toString();
                String UserCode = "NOUSERCODEYET";
                Visit tmp = new Visit(SiteCode, ProjectCode, VisitGroupCode,
                        VisitCode, PacientCode, VisitDate, TimeVal, UserCode);
                results.add(tmp);
            }

            if (resSoap.getPropertyCount() == 0){
                Log.v("GetHistoryLoadTask: This is not a valid person or has no visits", "This is not a valid person or has no visits");
                return null;
            }

            SoapObject resSoap2 = (SoapObject) resSoap.getProperty(0);

            Log.v("GetHistoryLoadTask: The history object we got is", resSoap.toString());



        }
        catch (Exception e)
        {
            e.printStackTrace();
            //results = null;
        }

        return results;
    }

}

