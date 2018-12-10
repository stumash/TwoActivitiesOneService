package com.example.stumash.a2activities;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.gson.Gson;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GPSService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // not binding, only starting
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        final boolean[] switchedActivityOnce = {false};

        // sleep for 2 seconds
        final long twoSecondsMillis = 2 * 1000;
        try { Thread.sleep(twoSecondsMillis); }
        catch (InterruptedException e) { }

        // set up a listener for gps
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (switchedActivityOnce[0]) return; // do nothing if already switched activity

                try {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    boolean inRangeOfIntersection = false;

                    // get nearest intersections
                    double radius = 100.0;
                    double lat = 45.502239; // TODO: remove test values
                    double lon = -73.577248;
                    String url =
                            "https://isasdev.cim.mcgill.ca:44343/autour/getPlaces.php"+
                            "?framed=1&times=1"+
                            "&radius="+radius+"&lat="+lat+"&lon="+lon+
                            "&condensed=0&from=oxmxing&as=json&font=9&pad=0";

                    HttpClient httpClient = new DefaultHttpClient();
                    HttpRequest request = new HttpGet(url);
                    HttpResponse response = httpClient.execute((HttpUriRequest) request);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()
                    ));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    String responseString = stringBuilder.toString();

                    Gson gson = new Gson();
                    RequestResult rr = gson.fromJson(responseString, RequestResult.class);
                    RequestResult.IntersectionData intersectionData = rr.results;
                    if (intersectionData.ll.length > 0) { // if more than 0 lat-lon pairs found
                        inRangeOfIntersection = true;
                        switchedActivityOnce[0] = true;
                    }

                    if (!inRangeOfIntersection)
                        return;

                    // start ActivityTwo.class
                    Intent intent = new Intent(getApplicationContext(), ActivityTwo.class);
                    startActivity(intent);

                } catch (Exception e) { }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            // request location updates from the gps provider every 1000 ms, even if travelled 0 distance
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, ll);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

class RequestResult {
    IntersectionData results;
    String[] footer;

    static class IntersectionData {
        String id;
        double[] ll; // lat lon
        int cat;
        String title; // intersection name in natural language (english)
        String from; // data source
        int rating; // expected quality of data, 1-10 scale
    }
}
