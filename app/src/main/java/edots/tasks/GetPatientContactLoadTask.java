package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import edots.models.Patient;
import edots.models.Project;
import edots.models.Visit;

/**
 * Created by Ankit on 1/15/15.
 * @author Ankit
 * 
 *
 */
public class GetPatientContactLoadTask extends AsyncTask<String,String,String> {

    @Override
    protected String doInBackground(String... params) {

        // instantiate results array to be returned
        //ArrayList<Visit> results = new ArrayList<Visit>();

        // setup server parameters
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "ListadoVisitas1"; // TODO: Change this to whatever the new one is
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // added CodigoPaciente for the request (used to find the patient)
        request.addProperty("CodigoPaciente", params[1]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        
        String result = "";
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            // get the response
            SoapObject resSoap = (SoapObject) envelope.getResponse();
            SoapObject resSoapTemp = null;
            
            // return null if no patient found or patient had no visits
            if (resSoap.getPropertyCount() == 0){
                Log.v("GetHistoryLoadTask: This is not a valid person or has no visits", "This is not a valid person or has no visits");
                return null;
            }

            Log.v("GetPatientContactLoadTask.java: The contact object we got is", resSoap.toString());
            
            result = (String) resSoap.getProperty(0);

            
/*
            int numVisits = resSoap.getPropertyCount();

            // loop over all of the visits that the patient made
            for (int i = 0; i < numVisits; i++){
                // for each iteration, create a visit object and add to the results array
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
*/

            
            




        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

}

