package cdc.bintan.com.bintancdc;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

public class MyLocationService extends BroadcastReceiver {
    final String LOGFGBG = "CekFGBG";
    //final String LOGBG = "CekBG";

    public static final String ACTION_PROCESS_UPDATE = "cdc.bintan.com.bintancdc.UPDATE_LOCATION";
    public static String location_string, latSekarang, lngSekarang;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null){
                    Location location = result.getLastLocation();
                    /*location_string = new StringBuilder(""+location.getLatitude())
                            .append("/")
                            .append(location.getLongitude())
                            .toString();*/
                    latSekarang = String.valueOf(location.getLatitude());
                    lngSekarang = String.valueOf(location.getLongitude());
                    try {
                        //Toast.makeText(context, location_string, Toast.LENGTH_SHORT).show();
                        //NotificationService.input2 = location_string;

                        // FOREGROUND UPDATE
                        Log.d(LOGFGBG, latSekarang+" "+lngSekarang);


                        ProfilODPActivity.getInstance().updateTextView(latSekarang, lngSekarang);
                    } catch (Exception e) {
                        //Toast.makeText(context, e+"", Toast.LENGTH_SHORT).show();
                        //NotificationService.input2 = location_string;

                        // BACKGROUND UPDATE
                        Log.d(LOGFGBG, latSekarang+" "+lngSekarang);
                        ProfilODPActivity.getInstance().updateTextView(latSekarang, lngSekarang);
                    }
                }
            }
        }
    }
}
