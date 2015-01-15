package edots.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by jfang on 1/15/15.
 */
public class InternetConnection {

    /**
     * Checks internet connection of the phone
     * @author JN
     * @param context current context that we are testing connection for
     * @return boolean if the phone is connected to internet
     */
    public static boolean checkConnection(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

}
