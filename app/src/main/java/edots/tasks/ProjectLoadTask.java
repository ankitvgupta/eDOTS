package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import edots.models.Project;

/**
 * @author lili
 * @since 1/14/15
 * @deprecated
 * arguments: String codigolocal, String codigoUsuario
 * load projects associated with the promoter
 */
public class ProjectLoadTask extends AsyncTask<String,String,Project[]> {


    private Project[] lstProyecto;

    @Override
    protected Project[] doInBackground(String... params) {

        Project[] resul= null;
        // TODO: make url a constant instead of being passed in as a parameter
        String urlserver = "http://demo.sociosensalud.org.pe";
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"WSSEIS/WSParticipante.asmx";
        final String METHOD_NAME = "ListadoProyectos1";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("CodigoLocal", params[0]);
        request.addProperty("CodigoUsuario", params[1]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap =(SoapObject)envelope.getResponse();

            lstProyecto = new Project[resSoap.getPropertyCount()];

            for (int i = 0; i < lstProyecto.length; i++)
            {
                SoapObject ic = (SoapObject)resSoap.getProperty(i);

                Log.i("project upload: result(0)",ic.getProperty(0).toString());
                Log.i("project upload: result(1)",ic.getProperty(1).toString());

                Project pro = new Project();

//                pro.id = Integer.parseInt(ic.getProperty(0).toString());
//                pro.nombre = ic.getProperty(1).toString();
                lstProyecto[i] = pro;
            }
            if (resSoap.getPropertyCount()>0){
                resul = lstProyecto;
            }

        }
        catch (Exception e)
        {
            resul = null;
        }

        return resul;
    }

}
