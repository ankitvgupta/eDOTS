package org.techintheworld.www.edots;

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
        final String METHOD_NAME = "NuevoParticipanteSimple";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        /*
        request.addProperty("Nombres", params[1]);
        request.addProperty("ApellidoP", params[2]);
        request.addProperty("ApellidoM", params[3]);
        request.addProperty("CodigoTipoDocumento", params[4]);
        Log.i("national id", params[5]);
        request.addProperty("DocumentoIdentidad", params[5]);

        SimpleDateFormat reverseParse = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sqlParse = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");

        try {
            request.addProperty("FechaNacimiento", sqlParse.format(reverseParse.parse(params[6])));
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        request.addProperty("Sexo", params[7]);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        */
        String returnValue = "";
        /*
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapPrimitive resSoap = (SoapPrimitive) envelope.getResponse();

            Log.v("The object we got is", resSoap.toString());
            returnvalue = resSoap.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
*/

        return returnValue;
    }

}
