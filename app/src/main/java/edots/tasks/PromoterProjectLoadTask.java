package edots.tasks;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import edots.models.Schema;

/**
 * @author lili
 * parameters: localeId, promoterId
 * load the projects the promoter can administer
 */
public class PromoterProjectLoadTask extends AsyncTask<String,String,Schema[]> {


    private Schema[] lstProyecto;

    @Override
    protected Schema[] doInBackground(String... params) {

        Schema[] resul= null;

        String urlserver = "http://demo.sociosensalud.org.pe";
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
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

            lstProyecto = new Schema[resSoap.getPropertyCount()];

            for (int i = 0; i < lstProyecto.length; i++)
            {
                SoapObject ic = (SoapObject)resSoap.getProperty(i);

                String Id = ic.getProperty(0).toString();
                String name = ic.getProperty(1).toString();

                Schema pro = new Schema(Id, name);
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
