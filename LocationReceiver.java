package daandesign.gpstracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by Spek on 14-2-2015.
 */
public class LocationReceiver extends BroadcastReceiver {

    double latitude, longitude;


    @Override
    public void onReceive(final Context context, final Intent calledIntent)
    {
        Log.d("LOC_RECEIVER", "Location RECEIVED!");

        latitude = calledIntent.getDoubleExtra("latitude", -1);
        longitude = calledIntent.getDoubleExtra("longitude", -1);

        updateRemote(latitude, longitude);

    }

    private void updateRemote(final double latitude, final double longitude )
    {

        String address = "CC:FE:3C:64:06:DD";
        String temp = readWebsite("http://daan.s-nl.net/gps/send.php?mode=sendloc&mac=".concat(address).concat("&lat=").concat(Double.toString(latitude)).concat("&lon=0").concat(Double.toString(longitude)).concat("&speed=0"));
        if(temp.equals("ERR3")){
            temp = readWebsite("http://daan.s-nl.net/gps/send.php?mode=login&mac=".concat(address));
            if(temp.equals("SUCCESS")){
                temp = readWebsite("http://daan.s-nl.net/gps/send.php?mode=sendloc&mac=".concat(address).concat("&lat=").concat(Double.toString(latitude)).concat("&lon=0").concat(Double.toString(longitude)).concat("&speed=0"));
            }
        }
    }

    public String readWebsite(String url){
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        // Get the response
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response_str = null;
        try {
            response_str = client.execute(request, responseHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response_str;
    }
}
