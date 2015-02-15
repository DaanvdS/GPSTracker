package daandesign.gpstracker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;


public class StartActivity extends ActionBarActivity {
    private static Context context;
    public Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ToggleButton tb = (ToggleButton) findViewById(R.id.toggleButtonTrack);
        if(isMyServiceRunning(GPSService.class)){
            tb.setChecked(true);
        } else {
            tb.setChecked(false);
        }
        StartActivity.context=getApplicationContext();
        i= new Intent(this, GPSService.class);
    }

    public static Context getAppContext() {
        return StartActivity.context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToGoogleMaps(View v){
        Log.d("Tracker", "Showing Google Maps screen.");
        Intent intent = new Intent(this, Main.class);
        this.startActivity(intent);
    }

    public void goToSettings(View v){
        Log.d("Tracker", "Showing Settings screen.");
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startTracking(View v){
        Log.d("Tracker", "Toggling the tracking.");
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String address = info.getMacAddress();
        i.putExtra("mac", address);
        if(isMyServiceRunning(GPSService.class)){
            Log.d("Tracker", "Stopping the GPS service.");
            this.stopService(i);
        } else {
            Log.d("Tracker", "Starting the GPS service.");
            this.startService(i);
        }
    }
}
