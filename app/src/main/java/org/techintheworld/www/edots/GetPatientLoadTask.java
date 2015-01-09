package org.techintheworld.www.edots;

import edots.models.Geofence;
import edots.models.Patient;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetPatientLoadTask extends AsyncTask<String,String,Patient> {


    private Patient lstGeofence;

    @Override
    protected Patient doInBackground(String... params) {

        Patient resul= null;

        String urlserver = params[0];
        final String NAMESPACE = urlserver+"/";
        final String URL=NAMESPACE+"EdotsWS/Service1.asmx";
        final String METHOD_NAME = "BuscarParticipante";
        final String SOAP_ACTION = NAMESPACE+METHOD_NAME;
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

		request.addProperty("DocIdentidad", params[1]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);
        transporte.debug = true;
        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapObject resSoap =(SoapObject)envelope.getResponse();

            Log.v("The object we got is", resSoap.toString());


            //Geofence lstGeofence = new Geofence[resSoap.getPropertyCount()];
/*
            for (int i = 0; i < lstGeofence.length; i++)
            {
                SoapObject ic = (SoapObject)resSoap.getProperty(i);

                Geofence geo = new Geofence();

                geo.codigogeofence = Integer.parseInt(ic.getProperty(0).toString());
                geo.codigolocal = Integer.parseInt(ic.getProperty(1).toString());
                geo.nombre = ic.getProperty(2).toString();
                geo.latitud = ic.getProperty(3).toString();
                geo.longitud = ic.getProperty(4).toString();
                geo.radio = ic.getProperty(5).toString();
                geo.duracionexpiracion = ic.getProperty(6).toString();
                geo.tipotransicion = Integer.parseInt(ic.getProperty(7).toString());
                lstGeofence[i] = geo;
            }
            if (resSoap.getPropertyCount()>0){
                resul = lstGeofence;
            }
            */
        }
        catch (Exception e)
        {
            resul = null;
        }

        return resul;
    }

}

