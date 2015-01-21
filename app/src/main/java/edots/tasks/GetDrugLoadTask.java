package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import java.util.ArrayList;
import edots.models.Drug;

/**
 * Created by brendan on 1/20/15.
 * Loads the list of drugs given a CodigoEsquema
 */
public class GetDrugLoadTask extends AsyncTask<String,String,ArrayList<Drug> > {

    @Override
    protected ArrayList<Drug> doInBackground(String... params) {
        // instantiate results array to be returned

        // setup server parameters
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "ListadoEsquemasDrogas";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // added CodigoEsquema for the request (used to specify an Esquema)
        request.addProperty("CodigoEsquema", params[1]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        ArrayList<Drug> drugs = new ArrayList<>();
        try {
            transporte.call(SOAP_ACTION, envelope);

            // get the response
            SoapObject resSoap = (SoapObject) envelope.getResponse();
            SoapObject resSoapTemp = null;

            int numDrugs = resSoap.getPropertyCount();
            Log.i("GetDrugLoadTask: The number of drugs is", Integer.toString(numDrugs));

            // loop over all of the drugs for a given scheme made
            for (int i = 0; i < numDrugs; i++) {
                // for each iteration, create a drug object and add to the results array
                resSoapTemp = (SoapObject) resSoap.getProperty(i);
                String id = resSoapTemp.getProperty("CodigoDroga").toString();
                String name = resSoapTemp.getProperty("Farmacos").toString();
                String symbol = resSoapTemp.getProperty("Siglas").toString();
                String dosage = resSoapTemp.getProperty("DosiMaxima").toString();
                // TODO: check whether the service returns NombreGroupoVisita and DescripcionVisita such as "Tamizaje" or "Enrolamiento"
                Drug tmp = new Drug(id, name, symbol, dosage);
                drugs.add(tmp);
            }

            // return null the scheme was almost certainly not a valid scheme
            if (resSoap.getPropertyCount() == 0) {
                Log.v("GetDrugLoadTask", "This scheme has no associated drugs");
                return null;
            }
            Log.v("GetDrugLoadTask: The drug object we got was", resSoap.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return drugs;
    }
}
