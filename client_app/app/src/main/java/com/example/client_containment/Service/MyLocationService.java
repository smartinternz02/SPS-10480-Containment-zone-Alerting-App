package com.example.client_containment.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.client_containment.MainActivity;
import com.example.client_containment.SignUp;
import com.google.android.gms.location.LocationResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;


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
                        postDataUsingVolley(Double.toString(location.getLatitude()),Double.toString(location.getLongitude()),context);
                        Location test;
                        getDataUsingVolley(context);
                        float distanceInMeters = center.distanceTo(test);
                        boolean isWithin10km = distanceInMeters < 10000;
                    }catch (Exception ex){
                        Toast.makeText(context,loc,Toast.LENGTH_SHORT).show();
//                        postDataUsingVolley(Double.toString(location.getLatitude()),Double.toString(location.getLongitude()),context);
                    }
                }
            }
        }

    }

    private ArrayList<Location> getDataUsingVolley(Context context) {
        final RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONArray jsonArray = response;
                try {
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String albumname = jsonObject.getString("title");
                        String albumimageurl = jsonObject.getString("image");
                    }
                }
                catch (Exception w)
                {
                    Toast.makeText(context,w.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonArrayRequest);
    }


}

    private void postDataUsingVolley(String lat,String lon,Context context) {
        final RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://192.168.1.17:5000/post_user_location_data";
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_data", 0);
        int id = sharedPreferences.getInt("id",0);
        JSONObject postparams = new JSONObject();
        try {
            postparams.put("id", id);
            postparams.put("lat", lat);
            postparams.put("long",lon);
            postparams.put("timestamp", Calendar.getInstance().getTime().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, postparams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response",response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error",error.toString());
                    }
                });

        queue.add(jsonObjReq);
    }
}