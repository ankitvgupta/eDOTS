
package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;


import edots.models.VisitDay;


/**
 * @author Brendan
 * Loads Visit Days to an array list
 * Only to be used in calendar
 */
public class GetVisitPerDayLoadTask extends AsyncTask<String,String,ArrayList<VisitDay>> {

    @Override
    protected ArrayList<VisitDay> doInBackground(String... params) {

        // setup server parameters
        // TODO: do not hard code in these parameters
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "ListadoVisitaDias";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // add all of the patient properties to the request
        request.addProperty("CodigoPaciente", params[1]);
        request.addProperty("FechaComienzo", params[2]);
        request.addProperty("FechaTermino", params[3]);
        // setup request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        ArrayList<VisitDay> visitDays = new ArrayList<>();
        try {
            transporte.call(SOAP_ACTION, envelope);

            // get the response
            SoapObject resSoap = (SoapObject) envelope.getResponse();
            SoapObject resSoapTemp = null;

            int numVisitDays = resSoap.getPropertyCount();
            Log.i("GetVisitDayLoadTask: The number of VisitDays is", Integer.toString(numVisitDays));

            // loop over all of the visitdays that the patient made
            for (int i = 0; i < numVisitDays; i++) {
                // for each iteration, create a visitday object and add to the results array
                resSoapTemp = (SoapObject) resSoap.getProperty(i);
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Date day = format.parse(resSoapTemp.getProperty("CodigoDroga").toString());
                int manana = Integer.parseInt(resSoapTemp.getProperty("Manana").toString());
                int tarde = Integer.parseInt(resSoapTemp.getProperty("Tarde").toString());
                VisitDay tmp = new VisitDay(day, manana, tarde);
                visitDays.add(tmp);
            }

            // return null if the dates provided are invalid
            if (resSoap.getPropertyCount() == 0) {
                Log.v("GetVisitPerDayLoadTask: ","format of dates was invalid was invald");
                return null;
            }
            Log.v("GetVisitPerDayLoadTask: The visitday object we got is", resSoap.toString());

        } catch (Exception e) {
            e.printStackTrace();

        }

        return visitDays;
    }
}
