package com.example.annguyen.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    RelativeLayout mh;
    Button btnhengio, btndunglai;
    TextView txtvhienthi;
    TimePicker timePicker;
    Calendar calendar;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnhXa();
        mh.setBackgroundResource(R.drawable.a);
        calendar = Calendar.getInstance();
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        final Intent intent = new Intent(MainActivity.this,AlarmReceiver.class);

        btnhengio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                int gio = timePicker.getCurrentHour();
                int phut = timePicker.getCurrentMinute();
                String string_gio = String.valueOf(gio);
                String string_phut = String.valueOf(phut);

                if(gio >12)
                {
                   string_gio = String.valueOf(gio - 12);
                }
                if(phut <10)
                {
                    string_phut = "0" +String.valueOf(phut);
                }
                intent.putExtra("extra", "on");
                pendingIntent = PendingIntent.getBroadcast(
                        MainActivity.this, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                txtvhienthi.setText("Giờ bạn đặt là : " + string_gio + ":" +string_phut);
            }
        }
        );
        btndunglai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtvhienthi.setText("Dừng lại");
                alarmManager.cancel(pendingIntent);
                intent.putExtra("extra","off");
                sendBroadcast(intent);
            }
        });
    }

    private void AnhXa() {
        btnhengio = (Button)findViewById(R.id.btnhengio);
        btndunglai = (Button)findViewById(R.id.btndunglai);
        txtvhienthi = (TextView)findViewById(R.id.txtvhienthi);
        timePicker = (TimePicker)findViewById(R.id.timePicker);
        mh = (RelativeLayout)findViewById(R.id.manHinh);
    }
}
