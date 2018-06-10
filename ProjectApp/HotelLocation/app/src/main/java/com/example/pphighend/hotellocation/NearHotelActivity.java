package com.example.pphighend.hotellocation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class NearHotelActivity extends AppCompatActivity {

    ListView hotelListView;
    ArrayList<Hotel> arrHotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_hotel);

        arrHotel = new ArrayList<Hotel>();
        hotelListView = (ListView)findViewById(R.id.listviewNear);

        arrHotel = (ArrayList<Hotel>) getIntent().getSerializableExtra("NearHotel");


        HotelAdapter adapter = new HotelAdapter(NearHotelActivity.this, R.layout.item_favorite, arrHotel);

        hotelListView.setAdapter(adapter);

    }
}
