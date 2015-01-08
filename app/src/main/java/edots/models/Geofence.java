package edots.models;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import java.util.Hashtable;


public class Geofence implements KvmSerializable  {
    public int codigogeofence;
    public int codigolocal;
    public String nombre;
    public String latitud;
    public String longitud;
    public String radio;
    public String duracionexpiracion;
    public int tipotransicion;

    public Geofence()
    {
        codigogeofence = 0;
        codigolocal = 0;
        nombre = "";
        latitud = "";
        longitud = "";
        radio = "";
        duracionexpiracion = "";
        tipotransicion = 0;

    }

    public Geofence(int codigogeofence,
                    int codigolocal,
                    String nombre,
                    String latitud,
                    String longitud,
                    String radio,
                    String duracionexpiracion,
                    int tipotransicion)
    {
        this.codigogeofence = codigogeofence;
        this.codigolocal = codigolocal;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.radio = radio;
        this.duracionexpiracion = duracionexpiracion;
        this.tipotransicion = tipotransicion;

    }

    @Override
    public Object getProperty(int arg0) {

        switch(arg0)
        {
            case 0:
                return codigogeofence;
            case 1:
                return codigolocal;
            case 2:
                return nombre;
            case 3:
                return latitud;
            case 4:
                return longitud;
            case 5:
                return radio;
            case 6:
                return duracionexpiracion;
            case 7:
                return tipotransicion;

        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 8;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
        switch(ind)
        {
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "codigogeofence";
                break;
            case 1:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "codigolocal";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "nombre";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "latitud";
                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "longitud";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "radio";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "duracionexpiracion";
                break;
            case 7:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "tipotransicion";
                break;
            default:break;
        }
    }

    @Override
    public void setProperty(int ind, Object val) {
        switch(ind)
        {
            case 0:
                codigogeofence = Integer.parseInt(val.toString());
                break;
            case 1:
                codigolocal = Integer.parseInt(val.toString());
                break;
            case 2:
                nombre = val.toString();
                break;
            case 3:
                latitud = val.toString();
                break;
            case 4:
                longitud = val.toString();
                break;
            case 5:
                radio = val.toString();
                break;
            case 6:
                duracionexpiracion = val.toString();
                break;
            case 7:
                tipotransicion = Integer.parseInt(val.toString());
                break;
            default:
                break;
        }
    }
}
