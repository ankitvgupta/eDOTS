

package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import edots.models.Visit;

/**
 * Written by Ankit on 1/12/15.
 *
 * Given a PatientID, queries the database and returns an ArrayList of the corresponding patient's visits
 */
public class GetHistoryLoadTask extends AsyncTask<String,String,ArrayList<Visit>> {


    //private Patient lstGeofence;

    @Override
    protected ArrayList<Visit> doInBackground(String... params) {

        // instantiate results array to be returned
        ArrayList<Visit> results = new ArrayList<Visit>();

        // setup server parameters
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "ListadoVisitas1";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // added CodigoPaciente for the request (used to find the patient)
        request.addProperty("CodigoPaciente", params[1]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            // get the response
            SoapObject resSoap = (SoapObject) envelope.getResponse();
            SoapObject resSoapTemp = null;

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
                // TODO: check whether the service returns NombreGroupoVisita and DescripcionVisita such as "Tamizaje" or "Enrolamiento"
                Visit tmp = new Visit(SiteCode, ProjectCode, VisitGroupCode,"NombreGroupoVisita",
                        VisitCode, "DescripcionVisita", PacientCode, VisitDate, TimeVal, UserCode);
                results.add(tmp);
            }

            // return null if no patient found or patient had no visits
            if (resSoap.getPropertyCount() == 0){
                Log.v("GetHistoryLoadTask: This is not a valid person or has no visits", "This is not a valid person or has no visits");
                return null;
            }

            Log.v("GetHistoryLoadTask: The history object we got is", resSoap.toString());



        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return results;
    }

}

