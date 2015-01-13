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

/*
 * Written by Ankit
 *
 * Given a NationalID, queries the server and returns the patient object of the corresponding patient.
 */
public class GetPatientLoadTask extends AsyncTask<String,String,Patient> {

    // TODO: Technically this only gets the first patient that matches the DocIdentidad - if multiple match, it just returns the first.
    // TODO: Should think about if there is a way to address that, or if that is even necessary.
    @Override
    protected Patient doInBackground(String... params) {

        // instantiate patient to be returned
        Patient p = null;

        // setup server parameters
        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "BuscarParticipante";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        // add DocIdentidad to the request
		request.addProperty("DocIdentidad", params[1]);

        // create request
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            // get the response as a SoapObject
            SoapObject resSoap = (SoapObject) envelope.getResponse();

            // verify that this was a valid person by counting number of lines returned
            if (resSoap.getPropertyCount() == 0){
                Log.v("This is not a valid person", "This is not a valid person");
                return null;
            }

            // Get the first patient
            SoapObject ic = (SoapObject) resSoap.getProperty(0);

            Log.v("GetPatientLoadTask.java: The patient that we got was", ic.toString());

            // Pull the Patient properties from the object
            String patientID = ic.getProperty("CodigoPaciente").toString();
            String name = ic.getProperty("Nombres").toString();
            String fathersName = ic.getProperty("ApellidoPaterno").toString();
            String mothersName = ic.getProperty("ApellidoMaterno").toString();
            Long nationalID = Long.valueOf(ic.getProperty("DocumentoIdentidad").toString());
            Integer sexInt = Integer.parseInt(ic.getProperty("Sexo").toString());
            Integer docType = Integer.parseInt(ic.getProperty("CodigoTipoDocumento").toString());

            // parse the date correctly
            String birthday = ic.getProperty("FechaNacimiento").toString();
            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
            Date birthDate = parser.parse(birthday);

            // parse the sex correctly
            String sex = "null";
            if (sexInt == 2){ sex = "Female";}
            else { sex = "Male"; }

            // create test projects to add
            // TODO: Change these to real ones.
            Project testProject = new Project();
            Project testProject2 = new Project();
            ArrayList<Project> enrolledProjects = new ArrayList<Project>(Arrays.asList(testProject, testProject2));

            Log.v("patient data", patientID+name+fathersName+mothersName+nationalID);

            // instantiate a new patient to be returned
            p = new Patient(name, birthDate, nationalID, sex, enrolledProjects, mothersName, fathersName, patientID, docType);

            Log.v("patient object:", p.toString());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            p = null;
        }

        return p;
    }

}

