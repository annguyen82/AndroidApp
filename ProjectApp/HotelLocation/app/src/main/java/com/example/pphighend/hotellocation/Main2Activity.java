package com.example.pphighend.hotellocation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    //Map + Facebook
    String latitude, longitude;
    Location location;
    GoogleMap map;
    SearchView btnSearch;
    private static final int REQUEST_CODE_GPS_PERMISSION = 100;

    // Firebase
    DatabaseReference mData;
    ArrayList<Hotel> arrHotel;
    ArrayList<danhgia> arrayDanhGia;
    ArrayList<Point> arrFavToaDo;
    Hotel hotelChiTiet;
    //Nav_bar Menu
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    TaiKhoan profileFB;
    View viewHeader;
    TextView txtName;
    TextView txtEmail;
    ProfilePictureView pic;
    ImageView img;
    AutoCompleteTextView searchAutoComplete;
    ImageView btnClose;
    ArrayList<String> locates;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

            // Nav_Bar Menu
            drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
            actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
            drawerLayout.addDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();
            NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Map + Facebook
        //btnSearch=(SearchView) findViewById(R.id.btnSearch);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mymap);
        mapFragment.getMapAsync(this);
            
            hotelChiTiet=new Hotel();
            // Firebase
            arrayDanhGia= new ArrayList<danhgia>();
            arrFavToaDo = new ArrayList<Point>();
            mData = FirebaseDatabase.getInstance().getReference();
            LoadData();
        // Lay HotelData
        arrHotel = (ArrayList<Hotel>) getIntent().getSerializableExtra("key");
        profileFB = (TaiKhoan) getIntent().getSerializableExtra("profile");
        Boolean check = (Boolean) getIntent().getSerializableExtra("FB");
            viewHeader = navigationView.getHeaderView(0);
            txtName = (TextView) viewHeader.findViewById(R.id.textViewName);
            txtEmail = (TextView) viewHeader.findViewById(R.id.textViewEmail);
            pic = (ProfilePictureView) viewHeader.findViewById(R.id.image);
            img = (ImageView) viewHeader.findViewById(R.id.imageView);

            txtName.setText(profileFB.getHoTen());
            txtEmail.setText(profileFB.getEmail());
        if(check){
            pic.setProfileId(profileFB.getHinhAnh());
            img.setVisibility(View.INVISIBLE);
        }else{
            String url = profileFB.getHinhAnh();
            new DownloadImage().execute(url);
            Toast.makeText(this, "Không đăng nhập FB", Toast.LENGTH_SHORT).show();
            pic.setVisibility(View.INVISIBLE);
            img.setVisibility(View.VISIBLE);
        }

        btnClose = (ImageView)findViewById(R.id.btnClose);
        btnClose.setVisibility(View.INVISIBLE);
        searchAutoComplete = (AutoCompleteTextView)findViewById(R.id.searchAutoComplete);
            locates = new ArrayList<String>();
            for (Hotel item:arrHotel) {
                locates.add(item.getTenKhachSan());
            }
            searchAutoComplete.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, locates));
            searchAutoComplete.setThreshold(1);
            searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    hideSoftKeyboard();
                    TextView tv = (TextView)view;
                    String name = tv.getText().toString();
                    Double x = 0.0, y = 0.0;
                    Toast.makeText(Main2Activity.this, name, Toast.LENGTH_LONG).show();
                    for (Hotel item: arrHotel){
                        if (item.getTenKhachSan().equals(name))
                        {
                            x = item.getToaDo().getX();
                            y = item.getToaDo().getY();
                        }
                    }
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(x, y), 17));
                    btnClose.setVisibility(View.INVISIBLE);
                }
            });
            searchAutoComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btnClose.getVisibility() == View.INVISIBLE){
                        btnClose.setVisibility(View.VISIBLE);
                    }
                }
            });
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchAutoComplete.setText("");
                    btnClose.setVisibility(View.INVISIBLE);
                }
            });
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        for (Hotel hotel: arrHotel) {
            String ten = hotel.getTenKhachSan();
            Point toaDo = hotel.getToaDo();
            String diaChi = hotel.getDiaChi();

            LatLng location = new LatLng(toaDo.getX(), toaDo.getY());
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            map.addMarker(new MarkerOptions()
                    .title(ten)
                    .snippet(diaChi)
                    .position(location));
        }
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                hotelChiTiet=getHotelChiTiet(marker.getPosition().latitude,marker.getPosition().longitude);
                if(hotelChiTiet!=null)
                {
                    Intent intent = new Intent(Main2Activity.this, ChiTietKhachSan.class);
                    byIntent(intent);
                    startActivity(intent);
                }else{
                    Toast.makeText(Main2Activity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                }

            }
        });
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(new LatLng(-10,59.35), new LatLng(30.25, 154)), 0));
        map.animateCamera(CameraUpdateFactory.zoomTo(11.5f));
        if (ContextCompat.checkSelfPermission(Main2Activity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Toast.makeText(Main2Activity.this, "hello", Toast.LENGTH_LONG).show();
                }
            };
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_GPS_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_GPS_PERMISSION:
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "sorry hay kiem tra lai", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //Nav_bar Menu

//    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id ==  R.id.nav_location){
            Toast.makeText(this, "Địa điểm yêu thích", Toast.LENGTH_SHORT).show();
            Intent intent4 = new Intent(Main2Activity.this, FavoriteActivity.class);
            byIntentByFav(intent4);
            startActivity(intent4);
        }else if(id ==  R.id.nav_sharelocation){
            location = new Location(map.getMyLocation());
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
            Intent intent = new Intent(Main2Activity.this, Share.class);
            intent.putExtra("x", latitude);
            intent.putExtra("y", longitude);
            startActivity(intent);
        }else if(id ==  R.id.nav_help){
            Intent intent2 = new Intent(Main2Activity.this, InstructionActivity.class);
            startActivity(intent2);
        }else if(id ==  R.id.nav_info){
            Intent intent = new Intent(Main2Activity.this, thongTinActivity.class);
            startActivity(intent);
        }else if(id ==  R.id.nav_logout){
            Toast.makeText(this, "Đăng xuất", Toast.LENGTH_SHORT).show();
        }else if(id ==  R.id.nav_add){
            Intent intent3 = new Intent(Main2Activity.this, addLocateActivity.class);
            startActivity(intent3);
        }
        return true;
    }

    private void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

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
            img.setImageBitmap(bitmap);
        }
    }
    private Hotel getHotelChiTiet(double x, double y){
        Hotel resultHotel=null;
        for(Hotel hotel: arrHotel){
           if(hotel.getToaDo().getX()==x)
               if(hotel.getToaDo().getY()==y)
                   resultHotel=hotel;
        }
        return resultHotel;
    }
    private void byIntent(Intent i){
        i.putExtra("hotelchitiet",hotelChiTiet);
        i.putExtra("arrdg",arrayDanhGia);
    }
    private void byIntentByFav(Intent i){
        i.putExtra("Point", arrFavToaDo);
        i.putExtra("Hotel", arrHotel);
    }
    private void LoadData(){
        //LoadData DanhGia
        mData.child("DANHGIA").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                danhgia danhgia0 = dataSnapshot.getValue(danhgia.class);
                arrayDanhGia.add(danhgia0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       mData.child("YEUTHICH").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                favorite item = dataSnapshot.getValue(favorite.class);
                if(item.getTenDangNhap().equals(profileFB.getTenDangNhap())){
                    //Toast.makeText(FavoriteActivity.this, String.valueOf(item.getToaDo().getX()), Toast.LENGTH_SHORT).show();
                    Point p = new Point(item.getToaDo().getX(), item.getToaDo().getY());
                    arrFavToaDo.add(new Point(item.getToaDo().getX(), item.getToaDo().getY()));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}