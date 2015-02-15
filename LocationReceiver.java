package daandesign.gpstracker;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.util.Patterns;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.regex.Pattern;

/**
 * Created by Spek on 14-2-2015.
 */
public class LocationReceiver extends BroadcastReceiver {

    double latitude, longitude, altitude;
    float speed;
    String mac;


    @Override
    public void onReceive(final Context context, final Intent calledIntent){
        Log.d("Tracker", "Received new location, sending it to the server.");
        latitude = calledIntent.getDoubleExtra("latitude", -1);
        longitude = calledIntent.getDoubleExtra("longitude", -1);
        altitude = calledIntent.getDoubleExtra("altitude", -1);
        speed = calledIntent.getFloatExtra("speed", -1);
        mac = calledIntent.getStringExtra("mac");
        updateRemote(latitude, longitude, speed, mac);
    }

    private void updateRemote(final double latitude, final double longitude, final double speed, final String address){
        String temp = readWebsite("http://daan.s-nl.net/gps/send.php?mode=sendloc&mac=".concat(address).concat("&lat=").concat(Double.toString(latitude)).concat("&lon=0").concat(Double.toString(longitude)).concat("&speed=").concat(Double.toString(longitude)));
        if(temp.equals("ERR3")){

            Log.d("debug","http://daan.s-nl.net/gps/send.php?mode=login&mac=".concat(address));
            temp = readWebsite("http://daan.s-nl.net/gps/send.php?mode=login&mac=".concat(address));

            if(temp.equals("SUCCESS")){
                Log.d("Tracker", "Device not known at server, registered.");
                temp = readWebsite("http://daan.s-nl.net/gps/send.php?mode=sendloc&mac=".concat(address).concat("&lat=").concat(Double.toString(latitude)).concat("&lon=0").concat(Double.toString(longitude)).concat("&alt=").concat(Double.toString(altitude)).concat("&speed=").concat(Double.toString(longitude)));
                if(temp.equals("SUCCESS")){
                    Log.d("Tracker", "Location successfully sent.");
                }
            }
        } else {
            Log.d("Tracker", "Location successfully sent.");
        }
    }

    public String readWebsite(String url){
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response_str = "";
        try {
            response_str = client.execute(request, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response_str;
    }
}
