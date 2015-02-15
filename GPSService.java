package daandesign.gpstracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GPSService extends Service {
    public boolean running = true;
    public Location loc;
    public boolean locAvail = false;
    public String macaddress;
    public Thread triggerService;
    public LocationManager lm;
    public MyLocationListener MyLocationListener;
    public GPSService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy(){
        Log.d("Tracker", "GPS Service stopped.");
        running = false;
        locAvail = false;
        lm.removeUpdates(MyLocationListener);
        lm = null;

    }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        macaddress = intent.getStringExtra("mac");
        addLocationListener();
        final Handler mHandler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(running) {
                                    if (locAvail) {
                                        updateLocation(loc);
                                        Log.d("Tracker", "Sending out location.");
                                        locAvail = false;
                                    }
                                }
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();
    }

    private void addLocationListener()
    {
        triggerService = new Thread(new Runnable(){
            public void run(){
                try{
                    Looper.prepare();//Initialise the current thread as a looper.
                    lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                    Criteria c = new Criteria();
                    c.setAccuracy(Criteria.ACCURACY_COARSE);

                    final String PROVIDER = lm.getBestProvider(c, true);

                    MyLocationListener = new MyLocationListener();
                    lm.requestLocationUpdates(PROVIDER, 600000, 0, MyLocationListener);
                    Log.d("Tracker", "GPS Service running.");
                    Looper.loop();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }, "LocationThread");
        triggerService.start();
    }

    public void updateLocation(Location location)
    {
        if(running) {
            Context appCtx = StartActivity.getAppContext();

            double latitude, longitude, altitude;
            float speed;

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            speed = location.getSpeed();
            altitude = location.getAltitude();

            Intent filterRes = new Intent();
            filterRes.setAction("xxx.yyy.intent.action.LOCATION");
            filterRes.putExtra("latitude", latitude);
            filterRes.putExtra("longitude", longitude);
            filterRes.putExtra("altitude", altitude);
            filterRes.putExtra("speed", speed);
            filterRes.putExtra("mac", macaddress);
            appCtx.sendBroadcast(filterRes);
        }
    }


    class MyLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location)
        {
            loc = location;
            locAvail = true;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }


}