
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
        request.addProperty("LunesManana", Boolean.toString(params[2] == "1"));
        request.addProperty("LunesTarde", Boolean.toString(params[3] == "1"));
        request.addProperty("MartesManana", Boolean.toString(params[4] == "1"));
        request.addProperty("MartesTarde", Boolean.toString(params[5] == "1"));
        request.addProperty("MiercolesManana", Boolean.toString(params[6] == "1"));
        request.addProperty("MiercolesTarde", Boolean.toString(params[7] == "1"));
        request.addProperty("JuevesManana", Boolean.toString(params[8] == "1"));
        request.addProperty("JuevesTarde", Boolean.toString(params[9] == "1"));
        request.addProperty("ViernesManana", Boolean.toString(params[10] == "1"));
        request.addProperty("ViernesTarde", Boolean.toString(params[11] == "1"));
        request.addProperty("SabadoManana", Boolean.toString(params[12] == "1"));
        request.addProperty("SabadoTarde", Boolean.toString(params[13] == "1"));
        request.addProperty("DomingoManana", Boolean.toString(params[14] == "1"));
        request.addProperty("DomingoTarde", Boolean.toString(params[15] == "1"));
        request.addProperty("FechaComienzo", params[16]);
        request.addProperty("FechaTermino", params[17]);
        request.addProperty("CodigoEsquema", params[18]);
        request.addProperty("TipoDeVisita", Boolean.toString(params[19] == "1"));


        String returnvalue = "";
        


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
        

        return returnvalue;
    }

}

