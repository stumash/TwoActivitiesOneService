package com.example.stumash.a2activities;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class GPSService extends Service
{
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
        // sleep for 10 seconds
        final long ten_seconds_in_millis = 10 * 1000;
        try { Thread.sleep(ten_seconds_in_millis); }
        catch (InterruptedException e) { e.printStackTrace(); }

        // start ActivityTwo.class
        Intent intent = new Intent(getApplicationContext(), ActivityTwo.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
