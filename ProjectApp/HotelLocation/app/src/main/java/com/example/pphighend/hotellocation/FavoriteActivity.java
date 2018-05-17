package com.example.pphighend.hotellocation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    ListView hotelListView;
    ArrayList<Hotel> arrHotel;
    ArrayList<Hotel> arrHotelDefault;
    DatabaseReference mData;
    TextView titleFav;

    ArrayList<Point> arrFavToaDo;
    String tendangnhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        tendangnhap = "Phong Nguyen";
        titleFav = (TextView)findViewById(R.id.titleFav);
        mData = FirebaseDatabase.getInstance().getReference();

        arrHotelDefault = new ArrayList<Hotel>();
        arrFavToaDo = new ArrayList<Point>();


        hotelListView = (ListView)findViewById(R.id.listviewFav);
        arrHotel = new ArrayList<Hotel>();

        arrHotelDefault = (ArrayList<Hotel>) getIntent().getSerializableExtra("Hotel");
        arrFavToaDo = (ArrayList<Point>) getIntent().getSerializableExtra("Point");


        titleFav.setText("ĐỊA ĐIỂM YÊU THÍCH");
        for(Hotel h: arrHotelDefault){
            for(Point p: arrFavToaDo){
                if(p.getX() == h.getToaDo().getX() && p.getY() == h.getToaDo().getY()){
                    arrHotel.add(h);
                }
            }
        }

        HotelAdapter adapter = new HotelAdapter(FavoriteActivity.this, R.layout.item_favorite, arrHotel);

        hotelListView.setAdapter(adapter);
        
    }

}
