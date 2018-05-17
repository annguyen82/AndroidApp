package com.example.pphighend.hotellocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChiTietKhachSan extends AppCompatActivity {

    String[] tenTaiKhoan={"Phong","Nam", "Nguyen","Phi,Phong","Nam", "Nguyen","Phi,Phong","Nam", "Nguyen","Phi," +
            "Phong","Nam", "Nguyen","Phi,Phong","Nam", "Nguyen","Phi,Phong","Nam", "Nguyen","Phi,Phong","Nam", "Nguyen","Phi"};
    String[] noiDung={"Quan dep", "Hai long","Tuyet voi","Tot"};
    ArrayList<danhgia> mangdanhgia;
    // Firebase
    ArrayList<danhgia> arrayDanhGia;
    Hotel hotelChiTiet;
    ImageView imageHotel;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_khach_san);
        TextView txtnameHotel, txtaddressHotel;
        Toolbar toolbarChiTiet;

        ListView listDanhGia;
        String tenHotel;
        String diachiHotel;
        toolbarChiTiet=(Toolbar) findViewById(R.id.toolbarChiTiet);
        imageHotel=(ImageView)findViewById(R.id.imagehotel);
        txtnameHotel=(TextView) findViewById(R.id.txtnameHotel);
        txtaddressHotel=(TextView)findViewById(R.id.txtAddressHotel);
        //Gan chuoi ten mac dinh
        tenHotel="Khách sạn ";
        diachiHotel="Quận 10";
        //Lấy dữ liệu khách sạn từ main2
        hotelChiTiet=new Hotel();
        hotelChiTiet = (Hotel) getIntent().getSerializableExtra("hotelchitiet");
        tenHotel=hotelChiTiet.getTenKhachSan();
        diachiHotel=hotelChiTiet.getDiaChi();
        String url = hotelChiTiet.getHinhAnh();
        //Set ten dia chi cho toolbar
        toolbarChiTiet.setTitle(tenHotel);
        setSupportActionBar(toolbarChiTiet);
        toolbarChiTiet.setSubtitle(diachiHotel);
        //Set hinh khach san
        imageHotel.setImageResource(R.drawable.imagehotel);
        if(!url.equals(""))
            new DownloadImage2().execute(url);
        //Set ten va dia chi
        txtnameHotel.setText(tenHotel);
        txtaddressHotel.setText(diachiHotel);
        //Load đánh giá của khách hàng
        // Firebase
        arrayDanhGia = new ArrayList<danhgia>();
        arrayDanhGia = (ArrayList<danhgia>) getIntent().getSerializableExtra("arrdg");
        //
        mangdanhgia=new ArrayList<danhgia>();
        for(danhgia danhgia0: arrayDanhGia){
            if(danhgia0.getToaDo().getX()==hotelChiTiet.getToaDo().getX())
                if(danhgia0.getToaDo().getY()==hotelChiTiet.getToaDo().getY())
                    mangdanhgia.add(danhgia0);
        }
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        //
        danhgiaAdapter danhgiaadapter=new danhgiaAdapter(ChiTietKhachSan.this,R.layout.custom_layout,mangdanhgia);
        listDanhGia=(ListView) findViewById(R.id.listDanhGia);
        listDanhGia.setAdapter(danhgiaadapter);

    }
    public class DownloadImage2 extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String imageURL = strings[0];

            Bitmap bm = null;
            try{
                InputStream input = new java.net.URL(imageURL).openStream();
                bm = BitmapFactory.decodeStream(input);
            }catch (Exception e){
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageHotel.setImageBitmap(bitmap);
        }
    }
}
