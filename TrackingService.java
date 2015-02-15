package daandesign.gpstracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class TrackingService extends Service {
    public boolean running = false;
    public TrackingService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy(Intent intent){
        running = false;
    }

    public void onStart(Intent intent, int startId) {
        running = true;
        super.onStart(intent, startId);
        addLocationListener();
    }

    private void addLocationListener()
    {
        Thread triggerService = new Thread(new Runnable(){
            public void run(){
                try{
                    Looper.prepare();//Initialise the current thread as a looper.
                    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                    Criteria c = new Criteria();
                    c.setAccuracy(Criteria.ACCURACY_COARSE);

                    final String PROVIDER = lm.getBestProvider(c, true);

                    MyLocationListener MyLocationListener = new MyLocationListener();
                    lm.requestLocationUpdates(PROVIDER, 600000, 0, MyLocationListener);
                    Log.d("LOC_SERVICE", "Service RUNNING!");
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

            double latitude, longitude;

            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Intent filterRes = new Intent();
            filterRes.setAction("xxx.yyy.intent.action.LOCATION");
            filterRes.putExtra("latitude", latitude);
            filterRes.putExtra("longitude", longitude);
            appCtx.sendBroadcast(filterRes);
        }
    }


    class MyLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location location)
        {
            updateLocation(location);
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
