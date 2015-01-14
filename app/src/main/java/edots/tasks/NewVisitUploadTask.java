package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * Created by Ankit on 1/9/15.
 */
public class NewVisitUploadTask extends AsyncTask<String,String,String> {

    @Override
    protected String doInBackground(String... params) {

        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "InsertarVisitas";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("CodigoLocal", params[1]);
        request.addProperty("CodigoProyecto", params[2]);
        request.addProperty("CodigoGrupoVisita", params[3]);
        request.addProperty("CodigoVisita", params[4]);
        request.addProperty("CodigoPaciente", params[5]);
        request.addProperty("FechaVisita", params[6]);
        request.addProperty("HoraCita", params[7]);
        request.addProperty("CodigoUsuario", params[8]);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;

        String returnValue = "";

        try
        {
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

