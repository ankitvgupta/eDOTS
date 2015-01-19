

package edots.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.techintheworld.www.edots.R;

import java.util.ArrayList;

import edots.models.Visit;
import edots.utils.InternetConnection;
import edots.utils.OfflineStorageManager;

/**
 * Written by Ankit on 1/12/15.
 *
 * Given a PatientID, queries the database and returns an ArrayList of the corresponding patient's visits
 */
public class GetHistoryLoadTask extends AsyncTask<String,String,ArrayList<Visit>> {

    private Context context;
    public GetHistoryLoadTask(Context c){
        context= c;
    }

    // TODO: fix this and figure out how best to store visits
    //private Patient lstGeofence;
    public ArrayList<Visit> getVisitsFromLocal(String patient_id){
        String fileName= context.getString(R.string.visits_data_filename);
        try{
            JSONArray visits_array = new JSONArray(OfflineStorageManager.getStringFromLocal(context, fileName));
            for (int i = 0; i < visits_array.length(); i++){
                JSONObject obj = visits_array.getJSONObject(i);
                Visit p = new Visit(obj.toString());
                // this ensures that they have a NationalId

            }
        }
        catch (JSONException exception){
            exception.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void saveVisitsToLocal(ArrayList<Visit> visits){
        OfflineStorageManager storage = new OfflineStorageManager();
        String fileName = context.getString(R.string.visits_data_filename);
        storage.SaveArrayListToLocal(visits, fileName, context);

    }

    @Override
    protected ArrayList<Visit> doInBackground(String... params) {

        // instantiate results array to be returned
        ArrayList<Visit> results = new ArrayList<Visit>();
        String patient_code = params[1];

        boolean is_connected = InternetConnection.checkConnection(context);
        if (is_connected) {

            // setup server parameters
            String urlserver = params[0];
            final String NAMESPACE = urlserver + "/";
            final String URL = NAMESPACE + "EdotsWS/Service1.asmx";
            final String METHOD_NAME = "ListadoVisitas1";
            final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            // added CodigoPaciente for the request (used to find the patient)
            request.addProperty("CodigoPaciente", params[1]);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            transporte.debug = true;
            try {
                transporte.call(SOAP_ACTION, envelope);

                // get the response
                SoapObject resSoap = (SoapObject) envelope.getResponse();
                SoapObject resSoapTemp = null;

                int numVisits = resSoap.getPropertyCount();

                // loop over all of the visits that the patient made
                for (int i = 0; i < numVisits; i++) {
                    // for each iteration, create a visit object and add to the results array
                    resSoapTemp = (SoapObject) resSoap.getProperty(i);
                    String SiteCode = "Locale"; // need to change the C# function to actually pull the locale
                    String ProjectCode = resSoapTemp.getProperty("CodigoProyecto").toString();
                    String VisitGroupCode = (String) resSoapTemp.getProperty("CodigoGrupoVisita").toString();
                    String VisitCode = (String) resSoapTemp.getProperty("CodigoVisita").toString();
                    String PacientCode = params[1];
                    String VisitDate = (String) resSoapTemp.getProperty("FechaVisita").toString();
                    String TimeVal = (String) resSoapTemp.getProperty("HoraCita").toString();
                    String PromoterId = null; // TODO: get promoter thing
                    // TODO: check whether the service returns NombreGroupoVisita and DescripcionVisita such as "Tamizaje" or "Enrolamiento"
                    Visit tmp = new Visit(SiteCode, ProjectCode, VisitGroupCode, "NombreGroupoVisita",
                            VisitCode, "DescripcionVisita", PacientCode, VisitDate, TimeVal, PromoterId);
                    results.add(tmp);
                }

                // return null if no patient found or patient had no visits
                if (resSoap.getPropertyCount() == 0) {
                    Log.v("GetHistoryLoadTask: This is not a valid person or has no visits", "This is not a valid person or has no visits");
                    return null;
                }
                Log.v("GetHistoryLoadTask: The history object we got is", resSoap.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }

            saveVisitsToLocal(results);

            return results;
        }
        else{
            getVisitsFromLocal(patient_code);
            // TODO: what if there is no local either

        }
        return null;
    }
}

