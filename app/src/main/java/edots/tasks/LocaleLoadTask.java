package edots.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import edots.models.Locale;

public class LocaleLoadTask extends AsyncTask<String,String,Locale[]> {

    private Locale[] lstLocales;

    @Override
    protected Locale[] doInBackground(String... params) {

        Locale[] resul= null;

        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "ListadoLocales";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

//		request.addProperty("DocIdentidad", params[0]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap =(SoapObject)envelope.getResponse();

            lstLocales = new Locale[resSoap.getPropertyCount()];

            for (int i = 0; i < lstLocales.length; i++)
            {
                SoapObject ic = (SoapObject)resSoap.getProperty(i);

                Locale loc = new Locale();

                loc.id = Integer.parseInt(ic.getProperty(0).toString());
                loc.name = ic.getProperty(1).toString();
                lstLocales[i] = loc;
            }
            if (resSoap.getPropertyCount()>0){
                resul = lstLocales;
            }
        }
        catch (Exception e)
        {
            //to do hard code when not possible
            lstLocales = new Locale[1];
            lstLocales[0] = new Locale(1,"Brendan");
            resul = lstLocales;
            Log.e("error from local load task", e.toString());
        }

        return resul;
    }

}

