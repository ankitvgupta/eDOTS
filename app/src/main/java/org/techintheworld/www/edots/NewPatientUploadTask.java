package org.techintheworld.www.edots;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * Created by Ankit on 1/9/15.
 */
public class NewPatientUploadTask extends AsyncTask<String,String,String> {

    @Override
    protected String doInBackground(String... params) {

        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "NuevoParticipanteSimple";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        Log.v("The number of params is", Integer.toString(params.length));
        request.addProperty("Nombres", params[1]);
        request.addProperty("ApellidoP", params[2]);
        request.addProperty("ApellidoM", params[3]);
        request.addProperty("CodigoTipoDocumento", Integer.valueOf(params[4]));
        request.addProperty("DocumentoIdentidad", params[5]);

        SimpleDateFormat reverseParse = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sqlParse = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");

        try {
            request.addProperty("FechaNacimiento", sqlParse.format(reverseParse.parse(params[6])));
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        request.addProperty("Sexo", Integer.valueOf(params[7]));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        String returnvalue = "";
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap = (SoapObject) envelope.getResponse();
            /*if (resSoap.getPropertyCount() == 0){
                Log.v("This is not a valid person", "This is not a valid person");
                return null;
            }*/

           // SoapObject resSoap2 = (SoapObject) resSoap.getProperty(0);

            Log.v("The object we got is", resSoap.toString());
            returnvalue = resSoap.toString();

            //SoapObject ic = (SoapObject) resSoap.getProperty(0);

            /*String patientID = ic.getProperty(0).toString();
            String name = ic.getProperty(1).toString();
            String fathersName = ic.getProperty(2).toString();
            String mothersName = ic.getProperty(3).toString();

            Long nationalID = Long.valueOf(ic.getProperty(5).toString());
            Integer sexInt = Integer.parseInt(ic.getProperty(7).toString());
            Integer docType = Integer.parseInt(ic.getProperty(4).toString());
            String birthday = ic.getProperty(6).toString();
            SimpleDateFormat parser =new SimpleDateFormat("dd/MM/yyyy");
            Date birthDate = parser.parse(birthday);

            String sex = "null";
            if (sexInt == 2){ sex = "Female";}
            else { sex = "Male"; }
            Project testProject = new Project();
            Project testProject2 = new Project();
            ArrayList<Project> enrolledProjects = new ArrayList<Project>(Arrays.asList(testProject, testProject2));

            Log.v("patient data", patientID+name+fathersName+mothersName+nationalID);

            p = new Patient(name, birthDate, nationalID, sex, enrolledProjects, mothersName, fathersName, patientID, docType);

            Log.v("patient object:", p.toString());
*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //p = null;
        }


        return returnvalue;
    }

}

