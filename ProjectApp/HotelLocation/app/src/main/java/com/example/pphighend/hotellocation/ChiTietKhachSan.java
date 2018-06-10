package com.example.pphighend.hotellocation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class ChiTietKhachSan extends AppCompatActivity {

    String[] tenTaiKhoan={"Phong","Nam", "Nguyen","Phi,Phong","Nam", "Nguyen","Phi,Phong","Nam", "Nguyen","Phi," +
            "Phong","Nam", "Nguyen","Phi,Phong","Nam", "Nguyen","Phi,Phong","Nam", "Nguyen","Phi,Phong","Nam", "Nguyen","Phi"};
    String[] noiDung={"Quan dep", "Hai long","Tuyet voi","Tot"};
    ArrayList<danhgia> mangdanhgia;
    // Firebase
    //ArrayList<danhgia> arrayDanhGia;
    //Hotel hotelChiTiet;
    ImageView imageHotel;
    Point toaDoKS;
    long sumstar=0;
    int numstar=0;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_khach_san);
        TextView txtnameHotel, txtaddressHotel;
        Toolbar toolbarChiTiet;
        Button btnThemDanhGia;
        TextView txtStar;
        ListView listDanhGia;
        String tenHotel;
        String diachiHotel;
        toolbarChiTiet=(Toolbar) findViewById(R.id.toolbarChiTiet);
        imageHotel=(ImageView)findViewById(R.id.imagehotel);
        txtnameHotel=(TextView) findViewById(R.id.txtnameHotel);
        txtaddressHotel=(TextView)findViewById(R.id.txtAddressHotel);
        btnThemDanhGia=(Button)findViewById(R.id.btnThemDanhGia);
        txtStar=(TextView)findViewById(R.id.txtStar);
        //Gan chuoi ten mac dinh
        tenHotel="Khách sạn ";
        diachiHotel="Quận 10";
        //Lấy dữ liệu khách sạn từ main2
        //hotelChiTiet=new Hotel();
        //hotelChiTiet = (Hotel) getIntent().getSerializableExtra("hotelchitiet");
        tenHotel=Main2Activity.hotelChiTiet.getTenKhachSan();
        diachiHotel=Main2Activity.hotelChiTiet.getDiaChi();
        String url = Main2Activity.hotelChiTiet.getHinhAnh();
        toaDoKS=Main2Activity.hotelChiTiet.getToaDo();
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
        //Mở layoout thêm đánh giá
        btnThemDanhGia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChiTietKhachSan.this, ThemDanhGia.class);
                byIntent(intent);
                startActivity(intent);
            }
        });
        //Load đánh giá của khách hàng
        // Firebase
        //arrayDanhGia = new ArrayList<danhgia>();
        //arrayDanhGia = (ArrayList<danhgia>) getIntent().getSerializableExtra("arrdg");
        //
        mangdanhgia=new ArrayList<danhgia>();
        for(danhgia danhgia0: Main2Activity.arrayDanhGia){
            if(danhgia0.getToaDo().getX()==Main2Activity.hotelChiTiet.getToaDo().getX())
                if(danhgia0.getToaDo().getY()==Main2Activity.hotelChiTiet.getToaDo().getY()){
                    mangdanhgia.add(danhgia0);
                    sumstar+=danhgia0.getStar();
                    numstar++;
                }

        }
        String star=String.format("%.1f",(Double)(sumstar*1.0/numstar));
        if(sumstar==0){
            txtStar.setText("0");
        }
        else txtStar.setText(star);
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        mangdanhgia.add(new danhgia(new Point(10,10),"Người dùng","Dữ liệu trống"));
        //
        danhgiaAdapter danhgiaadapter=new danhgiaAdapter(ChiTietKhachSan.this,R.layout.custom_layout,mangdanhgia);
        listDanhGia=(ListView) findViewById(R.id.listDanhGia);
        listDanhGia.setAdapter(danhgiaadapter);

    }
    private void byIntent(Intent i){
        i.putExtra("toado",toaDoKS);
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
