package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;

import edots.models.Schedule;
import edots.models.Drug;
import edots.models.Schema;

/**
 * Written by Ankit on 1/12/15.
 *
 * Given a PatientID, queries the database and returns the Patient Schedule
 */
public class GetPatientSchemaLoadTask extends AsyncTask<String,String,ArrayList<Schema>> {


    @Override
    protected ArrayList<Schema> doInBackground(String... params) {

        // instantiate results array to be returned
        Schema result = new Schema();

        // setup server parameters
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "ListadoPacienteEsquema"; 
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // added CodigoPaciente for the request (used to find the patient)
        request.addProperty("CodigoPaciente", params[1]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            // get the response
            SoapObject resSoap = (SoapObject) envelope.getResponse();
            
            
            
            String codigoPaciente = resSoap.getProperty("CodigoPaciente").toString(); // need to change the C# function to actually pull the locale
            String id = resSoap.getProperty("CodigoEsquema").toString();
            String lunesManana = resSoap.getProperty("LunesManana").toString();
            String martesManana = resSoap.getProperty("MartesManana").toString();
            String miercolesManana = resSoap.getProperty("MiercolesManana").toString();
            String juevesManana = resSoap.getProperty("JuevesManana").toString();
            String viernesManana = resSoap.getProperty("ViernesManana").toString();
            String sabadoManana = resSoap.getProperty("SabadoManana").toString();
            String domingoManana = resSoap.getProperty("DomingoManana").toString();
            String lunesTarde = resSoap.getProperty("LunesTarde").toString();
            String martesTarde = resSoap.getProperty("MartesTarde").toString();
            String miercolesTarde = resSoap.getProperty("MiercolesTarde").toString();
            String juevesTarde = resSoap.getProperty("JuevesTarde").toString();
            String viernesTarde = resSoap.getProperty("ViernesTarde").toString();
            String sabadoTarde = resSoap.getProperty("SabadoTarde").toString();
            String domingoTarde = resSoap.getProperty("DomingoTarde").toString();
            String startDate = resSoap.getProperty("FechaComienzo").toString();
            String endDate = resSoap.getProperty("FechaTermino").toString();
            String isActive = resSoap.getProperty("Activo").toString();
            String visitType = resSoap.getProperty("TipoDeVisita").toString();
            String schemaName = resSoap.getProperty("EsquemaNombre").toString();
            String schemaPhase = resSoap.getProperty("EsquemaFase").toString();

            Schedule schedule = new Schedule(lunesManana, lunesTarde, martesManana, martesTarde,
                    miercolesManana, miercolesTarde, juevesManana, juevesTarde,
                    viernesManana, viernesTarde, sabadoManana, sabadoTarde,
                    domingoManana, domingoTarde, startDate, endDate);

            Schema schema = new Schema(id,schemaName,new ArrayList<Drug>,schemaPhase,visitType,schedule);

            // return null if no patient found or patient had no visits
            if (resSoap.getPropertyCount() == 0){
                Log.v("GetPatientSchemaLoadTask: This is not a valid person or has no schedule", "This is not a valid person or has no visits");
                return null;
            }

            Log.v("GetPatientSchemaLoadTask: The object we got is", resSoap.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

}

