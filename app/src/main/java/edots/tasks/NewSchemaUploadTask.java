
package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


/**
 * @author ankit
 * upload new patient schedule to server and insert it into the database
 */
public class NewSchemaUploadTask extends AsyncTask<String,String,String> {

    @Override
    protected String doInBackground(String... params) {

        // setup server parameters
        // TODO: do not hard code in these parameters
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "InsertarPacienteEsquema";// TODO: Change this to a new method for schedule uploads
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // add all of the patient properties to the request
        request.addProperty("CodigoPaciente", params[1]);
        request.addProperty("Lunes", params[2]);
        request.addProperty("LunesTarde", params[3]);
        request.addProperty("Martes", params[4]);
        request.addProperty("MartesTarde", params[5]);
        request.addProperty("Miercoles", params[6]);        
        request.addProperty("MiercolesTarde", params[7]);
        request.addProperty("Jueves", params[8]);
        request.addProperty("JuevesTarde", params[9]);
        request.addProperty("Viernes", params[10]);
        request.addProperty("ViernesTarde", params[11]);
        request.addProperty("Sabado", params[12]);
        request.addProperty("SabadoTarde", params[13]);
        request.addProperty("Domingo", params[14]);
        request.addProperty("DomingoTarde", params[15]);
        
        request.addProperty("FechaComienzo", params[16]);
        request.addProperty("FechaTermino", params[17]);
        
        request.addProperty("CodigoEsquema", params[18]);
        request.addProperty("TipoDeVisito", params[19]);

        String returnvalue = "";
        
        /*

        // setup request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        //String returnvalue = "";

        try
        {
            transporte.call(SOAP_ACTION, envelope);
            // receive response (as a string)
            SoapPrimitive resSoap = (SoapPrimitive) envelope.getResponse();
            returnvalue = resSoap.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        */

        return returnvalue;
    }

}

