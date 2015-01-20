package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import edots.models.Schema;

/**
 * @author lili
 * parameters: patientId, promoterId
 * load the projects for the patient's new visit with the promoter
 */
public class PatientProjectLoadTask extends AsyncTask<String,String,Schema> {
    @Override
    protected Schema doInBackground(String... params) {

        // instantiate patient to be returned
        Schema p = null;

        // setup server parameters
        // TODO: do not hard code in the url
        final String NAMESPACE = "http://demo.sociosensalud.org.pe/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "ListadoIds";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("CodigoPaciente", "D74CCD37-8DE4-447C-946E-1300E9498577");
        request.addProperty("CodigoUsuario", "19");
        Log.v("PatientProjectLoadTask patient code", params[0]);
        Log.v("PatientProjectLoadTask patient code", params[1]);

        // create request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            // get the response as a SoapObject
            SoapObject resSoap = (SoapObject) envelope.getResponse();
            Log.v("PatientProjectLoadTask.java: resSoap", resSoap.toString());

            // Get the first row
            SoapObject ic = (SoapObject) resSoap.getProperty(0);

            // instantiate a new project to be returned
            p = new Schema(ic.getProperty("CodigoProyecto").toString(),ic.getProperty("Proyecto").toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            p = null;
        }

        return p;
    }

}

