package edots.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.techintheworld.www.edots.R;

import edots.models.Visit;
import edots.utils.OfflineStorageManager;


/**
 * Created by Ankit on 1/9/15.
 * @author Ankit
 * @since 2015-01-09
 *
 * The uploader task for new visits.
 */
public class NewVisitUploadTask extends AsyncTask<String,String,String> {

    private Context context;

    public NewVisitUploadTask(Context c){
        context = c;
    }
    public boolean saveVisitLocally(Visit v){
        String filename = context.getString(R.string.new_visit_filename);
        OfflineStorageManager sm = new OfflineStorageManager(context);
        boolean save_success = sm.SaveSaveableToLocal(v,filename);
        Log.e("NewVisitUploadTask saveVisitLocally",v.toString() );
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(context.getString(R.string.new_visit_filename), "true");
        editor.commit();
        return save_success;
    }
    @Override
    /**
     * @params: params[0] is the url to query for
     * params[1]  to params[8] can be seen on line 57
     */
    protected String doInBackground(String... params) {

        Log.e("NewVisitUploadTask", params[0] + " " +params[1] + " " +params[2] + " " +
                params[3] + " " +params[4] + " " +params[5] + " " +params[6] + " " +params[7] + " " +params[8]);
        // Set up server parameters
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "InsertarVisitas";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // Add visit attributes
        request.addProperty("CodigoLocal", params[1]);
        request.addProperty("CodigoProyecto", params[2]);
        request.addProperty("CodigoGrupoVisita", params[3]);
        request.addProperty("CodigoVisita", params[4]);  // null atm
        request.addProperty("CodigoPaciente", params[5]);
        request.addProperty("FechaVisita", params[6]);
        request.addProperty("HoraCita", params[7]);
        request.addProperty("CodigoUsuario", params[8]);  // null atm

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

