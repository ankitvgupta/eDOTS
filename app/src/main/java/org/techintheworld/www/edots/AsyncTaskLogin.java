package org.techintheworld.www.edots;

import java.net.SocketException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.AsyncTask;
import android.util.Log;
import edots.models.Login;

/**
 * @author jtomaylla
 *
 */
public class AsyncTaskLogin extends  AsyncTask<String,String,Login>{

    @Override
    protected Login doInBackground(String... params) {
        Login resp=null;
        int count = params.length;
        if(count==4){



//    	final String NAMESPACE = "http://demo.sociosensalud.org.pe/";
//		final String URL="http://demo.sociosensalud.org.pe/WSSEIS/WSParticipante.asmx";
//		final String METHOD_NAME = "LoginUsuario";
//		final String SOAP_ACTION = "http://demo.sociosensalud.org.pe/LoginUsuario";

            String urlserver = params[3];
            final String NAMESPACE = urlserver+"/";
            final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
            final String METHOD_NAME = "LoginUsuario1";
            final String SOAP_ACTION = NAMESPACE+METHOD_NAME;

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("login", params[0]);
            request.addProperty("pass", params[1]);
            request.addProperty("codLocal", params[2]);
//		request.addProperty("local", 2);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;

            envelope.setOutputSoapObject(request);

            HttpTransportSE httptransport = new HttpTransportSE(URL);
            httptransport.debug = true;
            Login log = new Login();
            try {
                httptransport.call(SOAP_ACTION, envelope);

//			SoapPrimitive resSoap =(SoapPrimitive)envelope.getResponse();

                SoapObject resSoap =(SoapObject)envelope.getResponse();
                SoapObject ic = (SoapObject)resSoap.getProperty(0);

                //Login log = new Login();

                log.UserID = Integer.parseInt(ic.getProperty(0).toString());;
                log.Message = ic.getProperty(1).toString();

                resp = log;
                Log.i("login", "doInBackground_resp:" + resp);
            }
            catch (SocketException ex) {
                Log.e("Error : " , "Error on soapPrimitiveData() " + ex.getMessage());
                ex.printStackTrace();
                log.Message =  "Error on soapPrimitiveData() " + ex.getMessage();
                resp = log;
            } catch (Exception e) {
                Log.e("Error : " , "Error on soapPrimitiveData() " + e.getMessage());
                e.printStackTrace();
                log.Message =  "Error on soapPrimitiveData() " + e.getMessage();
                resp = log;
            }

        }
        return resp;

    }

}