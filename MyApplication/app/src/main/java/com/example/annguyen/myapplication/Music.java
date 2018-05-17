package com.example.annguyen.myapplication;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by An Nguyen on 2/27/2018.
 */

public class Music extends Service {
    MediaPlayer mediaPlayer;
    int id;
    @Nullable
    @Override

    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String key = intent.getExtras().getString("extra");
        if (key.equals("on"))
        {
            id = 1;
        }else if (key.equals("off"))
            id=0;
        if (id==1) {
            mediaPlayer = MediaPlayer.create(this, R.raw.dt);
            mediaPlayer.start();
            id=0;
        }else if (id==0){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        return START_NOT_STICKY;
    }
}
