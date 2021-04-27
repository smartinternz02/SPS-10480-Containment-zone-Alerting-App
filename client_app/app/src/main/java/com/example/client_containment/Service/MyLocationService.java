package com.example.client_containment.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import com.example.client_containment.MainActivity;
import com.google.android.gms.location.LocationResult;

public class MyLocationService extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE = "com.example.client_containment.Service.UPDATE_LOCATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_PROCESS_UPDATE.equals(action)){
                LocationResult result = LocationResult.extractResult(intent);
                if(result != null){
                    Location location = result.getLastLocation();
                    String loc = new StringBuilder(String.valueOf(location.getLatitude()))
                            .append("/")
                            .append(location.getLongitude())
                            .toString();

                    try {
                        MainActivity.getInstance().updateTextView(loc);
                    }catch (Exception ex){
                        Toast.makeText(context,loc,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }
}