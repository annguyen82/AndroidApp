package com.example.annguyen.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by An Nguyen on 2/27/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String chuoi_string = intent.getExtras().getString("extra");
        Intent myintent = new Intent(context, Music.class);
        myintent.putExtra("extra", chuoi_string);
        context.startService(myintent);
    }
}
