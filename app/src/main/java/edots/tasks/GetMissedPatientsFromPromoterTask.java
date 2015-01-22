package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;

/**
 * Created by jfang on 1/21/15.
 */
public class GetMissedPatientsFromPromoterTask extends AsyncTask<String,String,HashMap<String, String>> {

    /**
     * @author JN
     * @param params expects 3 params: urlserver, CodigoUsuario, and Fecha
     * @return
     */
    protected HashMap<String, String> doInBackground(String... params) {
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "ListadoPacienteCelular";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        HashMap<String, String> patients_phones= new HashMap<String, String>();

        // added CodigoPaciente for the request (used to find the patient)
        request.addProperty("CodigoUsuario", params[1]);
        request.addProperty("Fecha", params[2]);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try{
            transporte.call(SOAP_ACTION, envelope);

            // get the response
            SoapObject resSoap = (SoapObject) envelope.getResponse();

            // Checks for no rows returned.
            if (resSoap.getPropertyCount() == 0) {
                Log.v("GetMissedPatientsFromPromoter - QUERY ERROR:", "There is no one missing appointments");
                return null;
            }

            int num_patients = resSoap.getPropertyCount();

            for (int i = 0; i<num_patients; i++){
                SoapObject ic = (SoapObject) resSoap.getProperty(i);
                String p_id = ic.getProperty("CodigoPaciente").toString();
                String p_phone = ic.getProperty("Celular").toString();
                if (p_phone.equals("anyType{}")){
                    p_phone = null;
                }
                patients_phones.put(p_id, p_phone);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return patients_phones;
    }
}
