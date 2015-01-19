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
 * Implemented by Nishant
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
        final String METHOD_NAME = "ListadoPacientesContactos";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // added CodigoPaciente for the request (used to find the patient)
        request.addProperty("CodigoPaciente", params[1]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        
        String phoneNumber = "";
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            // get the response
            SoapObject resSoap = (SoapObject) envelope.getResponse();
            SoapObject resSoaptemp = (SoapObject) resSoap.getProperty(0);
            phoneNumber = resSoaptemp.getProperty("Celular").toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return phoneNumber;
    }

}

