package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import edots.models.Schedule;
import edots.models.Schema;
import edots.models.Visit;

/**
 * Written by Ankit on 1/12/15.
 *
 * Given a PatientID, queries the database and returns the Patient Schedule
 */
public class GetPatientSchemaLoadTask extends AsyncTask<String,String,Schema> {


    @Override
    protected Schema doInBackground(String... params) {

        // instantiate results array to be returned
        Schema result = new Schema();

        // setup server parameters
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "ListadoVisitas1"; // TODO: Change to the method for getting a schedule
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
            
            String codigoPaciente = resSoap.getProperty("CodigoPaciente").toString(); // need to change the C# function to actually pull the locale
            String lunes = resSoap.getProperty("lunes").toString();
            String martes = resSoap.getProperty("martes").toString();
            String miercoles = resSoap.getProperty("miercoles").toString();
            String jueves = resSoap.getProperty("jueves").toString();
            String viernes = resSoap.getProperty("viernes").toString();
            String sabado = resSoap.getProperty("sabado").toString();
            String domingo = resSoap.getProperty("domingo").toString();
            String startDate =resSoap.getProperty("startDate").toString();
            String endDate = resSoap.getProperty("endDate").toString();
            
            //TODO: Change this to actually make a schema
//            Schedule result2 = new Schedule(codigoPaciente, lunes, martes, miercoles, jueves, viernes, sabado, domingo, startDate, endDate);

            // return null if no patient found or patient had no visits
            if (resSoap.getPropertyCount() == 0){
                Log.v("GetPatientSchemaLoadTask: This is not a valid person or has no schedule", "This is not a valid person or has no visits");
                return null;
            }

            Log.v("GetPatientSchemaLoadTask: The object we got is", resSoap.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

}

