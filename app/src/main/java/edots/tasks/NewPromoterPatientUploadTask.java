package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by brendan on 1/15/15.
 */
public class NewPromoterPatientUploadTask extends AsyncTask<String,String,String> {

    @Override
    protected String doInBackground(String... params) {

        // setup server parameters
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "RegistrarPacientesUsuarios";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // add all of the patient properties to the request
        request.addProperty("CodigoPaciente", params[1]);
        request.addProperty("CodigoUsuario", params[2]);
        request.addProperty("Estado", params[3]);

        // setup request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        String returnvalue = "";

        try
        {
            transporte.call(SOAP_ACTION, envelope);

            // receive response (as a string)
            // TODO: Determine why it says that the patient already exists even though it doesn't (It says patient exists when it doesn't and then adds it anyways - seems like the response codes are flipped)
            SoapPrimitive resSoap = (SoapPrimitive) envelope.getResponse();

            Log.v("The object we got is", resSoap.toString());
            returnvalue = resSoap.toString();
        }
        catch (Exception e)
        {
            Log.e("NewPromoterPatientUploadTask: Execute", "It could be like so many different exceptions");
        }

        return returnvalue;
    }

}

