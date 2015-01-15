package edots.tasks;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


/**
 * Created by Ankit on 1/9/15.
 * @author Ankit
 * @since 2015-01-09
 *
 * The uploader task for new visits.
 */
public class NewVisitUploadTask extends AsyncTask<String,String,String> {

    @Override
    protected String doInBackground(String... params) {

        // Set up server parameters
        final String NAMESPACE = "http://demo.sociosensalud.org.pe/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "InsertarVisitas";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // Add visit attributes
        request.addProperty("CodigoLocal", params[0]);
        request.addProperty("CodigoProyecto", params[1]);
        request.addProperty("CodigoGrupoVisita", params[2]);
        request.addProperty("CodigoVisita", params[3]);
        request.addProperty("CodigoPaciente", params[4]);
        request.addProperty("FechaVisita", params[5]);
        request.addProperty("HoraCita", params[6]);
        request.addProperty("CodigoUsuario", params[7]);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;

        String returnValue = "";

        try
        {
            // Receive response
            transporte.call(SOAP_ACTION, envelope);
            SoapPrimitive resSoap = (SoapPrimitive) envelope.getResponse();
            returnValue = resSoap.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return returnValue;
    }

}

