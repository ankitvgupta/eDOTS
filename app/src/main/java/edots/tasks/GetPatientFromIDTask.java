package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import edots.models.Patient;
import edots.models.Project;

/**
 * Created by jfang on 1/12/15.
 * Reviewed by Ankit on 1/13/15
 *
 * Given a PatientID, queries the server and returns a patient object corresponding to that ID
 *
 */
public class GetPatientFromIDTask extends AsyncTask<String,String,Patient> {

    protected Patient doInBackground(String... params) {
        Patient p = null;

        // setup the server parameters
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "BuscarParticipanteConCodigo";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // add the CodigoPaciente property to the requet
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
            SoapObject resSoap =(SoapObject)envelope.getResponse();

            // if nothing was returned, then this is not a valid person.
            if (resSoap.getPropertyCount() == 0){
                Log.v("This is not a valid person", "This is not a valid person");
                return null;
            }

            // grab the first person with a matching PatientID
            // TODO: This does not account for multiple return values - this should not be a problem though b/c I don't think that's even possible for patient codes
            SoapObject ic = (SoapObject) resSoap.getProperty(0);


            // parse all of the fields for that patient
            String patientID = ic.getProperty("CodigoPaciente").toString();
            String name = ic.getProperty("Nombres").toString();
            String fathersName = ic.getProperty("ApellidoPaterno").toString();
            String mothersName = ic.getProperty("ApellidoMaterno").toString();
            Long nationalID;
            try{
                nationalID = Long.valueOf(ic.getProperty("DocumentoIdentidad").toString());
            }
            catch (NumberFormatException e){
                nationalID=null;
            }
            Integer sexInt = Integer.parseInt(ic.getProperty("Sexo").toString());
            Integer docType = Integer.parseInt(ic.getProperty("CodigoTipoDocumento").toString());
            String birthday = ic.getProperty("FechaNacimiento").toString();
            SimpleDateFormat parser =new SimpleDateFormat("dd/MM/yyyy");
            Date birthDate = parser.parse(birthday);

            String sex = "null";
            if (sexInt == 2){ sex = "Female";}
            else { sex = "Male"; }
            //Project testProject = new Project();
            //Project testProject2 = new Project();
            Project enrolledProject = new Project();

            // instantiate a new patient object to be returned
            p = new Patient(name, birthDate, nationalID, sex, enrolledProject, mothersName, fathersName, patientID, docType);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            p = null;
        }

        return p;
    }
}
