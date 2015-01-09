package edots.models;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Login implements KvmSerializable {
    public int UserID;
    public String Message;


    public Login()
    {
        UserID = 0;
        Message = "";

    }

    public Login(int Username, String Mensaje)
    {
        this.UserID = Username;
        this.Message = Mensaje;

    }

    @Override
    public Object getProperty(int arg0) {

        switch(arg0)
        {
            case 0:
                return UserID;
            case 1:
                return Message;

        }

        return null;
    }

    @Override
    public int getPropertyCount() {
        return 2;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void getPropertyInfo(int ind, Hashtable ht, PropertyInfo info) {
        switch(ind)
        {
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "Username";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Message";
                break;
            default:break;
        }
    }

    @Override
    public void setProperty(int ind, Object val) {
        switch(ind)
        {
            case 0:
                UserID = Integer.parseInt(val.toString());
                break;
            case 1:
                Message = val.toString();
                break;
            default:
                break;
        }
    }
}
