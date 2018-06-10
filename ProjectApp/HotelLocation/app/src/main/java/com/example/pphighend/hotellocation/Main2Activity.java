package com.example.pphighend.hotellocation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    //Map + Facebook
    String latitude, longitude;
    Location location;
    GoogleMap map;
    ShareDialog shareDialog;
    ShareLinkContent shareLinkContent;
    private static final int REQUEST_CODE_GPS_PERMISSION = 100;
    Marker markerClick;
    Polyline[] polyline = new Polyline[1];

    // Firebase
    DatabaseReference mData;
    public static ArrayList<Hotel> arrHotel;
    public static ArrayList<danhgia> arrayDanhGia;
    ArrayList<Point> arrFavToaDo;
    public static Hotel hotelChiTiet;
    //Nav_bar Menu
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public static TaiKhoan profileFB;
    View viewHeader;
    TextView txtName;
    TextView txtEmail;
    ProfilePictureView pic;
    ImageView img;
    AutoCompleteTextView searchAutoComplete;
    ImageView btnClose;
    ArrayList<String> locates;
    LatLng locationFavor;

    FloatingActionButton fab, fab_address, fab_favorite, fab_type;
    boolean checkshow = false;
    Animation moveDuoi, moveGiua, moveTren;
    Animation backDuoi, backGiua, backTren;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Nav_Bar Menu
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Map + Facebook
        //btnSearch=(SearchView) findViewById(R.id.btnSearch);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mymap);
        mapFragment.getMapAsync(this);

        hotelChiTiet = new Hotel();
        // Firebase
        arrayDanhGia = new ArrayList<danhgia>();
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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab_address = (FloatingActionButton) findViewById(R.id.fab_address);
        fab_favorite = (FloatingActionButton) findViewById(R.id.fab_favorite);
        fab_type = (FloatingActionButton) findViewById(R.id.fab_type);

        FAB_Visibility(0.8f);
        FAB_Enabled(false);

        moveDuoi = AnimationUtils.loadAnimation(this, R.anim.move_duoi);
        moveGiua = AnimationUtils.loadAnimation(this, R.anim.move_giua);
        moveTren = AnimationUtils.loadAnimation(this, R.anim.move_tren);
        backDuoi = AnimationUtils.loadAnimation(this, R.anim.back_duoi);
        backGiua = AnimationUtils.loadAnimation(this, R.anim.back_giua);
        backTren = AnimationUtils.loadAnimation(this, R.anim.back_tren);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkshow) {
                    FAB_Visibility(0.8f);
                    MoveAnimation();
                    checkshow = true;
                } else {
                    BackAnimation();
                    checkshow = false;
                }
            }
        });
        fab_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean gps_enabled = false;
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch (Exception ex) {
                }

                if (polyline[0] != null)
                    polyline[0].remove();

                if (!gps_enabled) {
                    showDialogNotLocation();
                } else {
                    String a = String.valueOf(markerClick.getPosition().latitude);
                    String b = String.valueOf(markerClick.getPosition().longitude);
                    location = new Location(map.getMyLocation());
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                    String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + latitude + "," + longitude + "&destination=" + a + "," + b + "&key=AIzaSyAHfU6EW9KxjyJlyh_ADLUP8MpZ3cQTeeU";

                    new AsyncTask<String, Void, String>() {
                        @Override
                        protected String doInBackground(String... strings) {
                            HttpURLConnection httpURLConnection = null;
                            BufferedReader reader = null;
                            StringBuffer buffer = new StringBuffer();
                            try {
                                URL Url = new URL(strings[0]);
                                httpURLConnection = (HttpURLConnection) Url.openConnection();
                                httpURLConnection.connect();
                                InputStream stream = httpURLConnection.getInputStream();
                                reader = new BufferedReader(new InputStreamReader(stream));

                                String line = "";
                                while ((line = reader.readLine()) != null) {
                                    buffer.append(line);
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (httpURLConnection != null) {
                                    httpURLConnection.disconnect();
                                }
                                try {
                                    if (reader != null) {
                                        reader.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            return buffer.toString();
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            polyline[0] = drawPath(s);

                        }
                    }.execute(url);
                }
            }
        });

        fab_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean gps_enabled = false;
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch (Exception ex) {
                }

                if (!gps_enabled) {
                    showDialogNotLocation();
                } else {
                    Location loca = new Location(map.getMyLocation());

                    double x0 = loca.getLatitude();
                    double y0 = loca.getLongitude();

                    ArrayList<Hotel> arrNearHotel = new ArrayList<Hotel>();
                    for (Hotel h : arrHotel) {
                        double x = h.getToaDo().getX();
                        double y = h.getToaDo().getY();
                        double kc = Math.sqrt((x0 - x) * (x0 - x) + (y0 - y) * (y0 - y));

                        if (kc <= 0.015) {
                            arrNearHotel.add(h);
                        }
                    }

                    Intent intent = new Intent(Main2Activity.this, NearHotelActivity.class);
                    intent.putExtra("NearHotel", arrNearHotel);
                    startActivity(intent);
                }
            }
        });

        fab_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = false;
                Point point = new Point(locationFavor.latitude, locationFavor.longitude);
                for (int i = 0; i < arrFavToaDo.size(); i++) {
                    if (point.getX() == arrFavToaDo.get(i).getX() && point.getY() == arrFavToaDo.get(i).getY()) {
                        check = true;
                        break;
                    }
                }
                if (check) {
                    Toast.makeText(Main2Activity.this, "Khách sạn này đã có trong danh sách yêu thích.", Toast.LENGTH_SHORT).show();
                } else {
                    favorite favorite = new favorite(point, profileFB.getTenDangNhap());
                    mData.child("YEUTHICH").push().setValue(favorite);
                    Toast.makeText(Main2Activity.this, "Một khách sạn được thêm vào danh sách yêu thích của bạn!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtName.setText(profileFB.getHoTen());
        txtEmail.setText(profileFB.getEmail());
        if (check) {
            pic.setProfileId(profileFB.getHinhAnh());
            img.setVisibility(View.INVISIBLE);
        } else {
            String url = profileFB.getHinhAnh();
            new DownloadImage().execute(url);
            pic.setVisibility(View.INVISIBLE);
            img.setVisibility(View.VISIBLE);
        }

        btnClose = (ImageView) findViewById(R.id.btnClose);
        btnClose.setVisibility(View.INVISIBLE);
        searchAutoComplete = (AutoCompleteTextView) findViewById(R.id.searchAutoComplete);
        locates = new ArrayList<String>();
        for (Hotel item : arrHotel) {
            locates.add(item.getTenKhachSan());
        }
        searchAutoComplete.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, locates));
        searchAutoComplete.setThreshold(1);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideSoftKeyboard();
                TextView tv = (TextView) view;
                String name = tv.getText().toString();
                Double x = 0.0, y = 0.0;
                for (Hotel item : arrHotel) {
                    if (item.getTenKhachSan().equals(name)) {
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
                if (btnClose.getVisibility() == View.INVISIBLE) {
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

    private void showDialogNotLocation(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Vui lòng bật vị trí để thực hiện tính năng này nhé!")
                .setCancelable(false)
                .setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void FAB_Visibility(float vis) {
        fab_address.setAlpha(vis);
        fab_favorite.setAlpha(vis);
        fab_type.setAlpha(vis);
    }

    private void FAB_Enabled(boolean ena) {
        fab_favorite.setEnabled(ena);
        //fab_type.setEnabled(ena);
        fab_address.setEnabled(ena);
    }

    private void MoveAnimation() {
        FrameLayout.LayoutParams paramsDuoi = (FrameLayout.LayoutParams) fab_address.getLayoutParams();
        paramsDuoi.rightMargin = fab_address.getWidth() * 2;
        fab_address.setLayoutParams(paramsDuoi);
        fab_address.startAnimation(moveDuoi);

        FrameLayout.LayoutParams paramsGiua = (FrameLayout.LayoutParams) fab_favorite.getLayoutParams();
        paramsGiua.bottomMargin = (int) (fab_favorite.getWidth() * 1.5);
        paramsGiua.rightMargin = (int) (fab_favorite.getWidth() * 1.5);
        fab_favorite.setLayoutParams(paramsGiua);
        fab_favorite.startAnimation(moveGiua);

        FrameLayout.LayoutParams paramsTren = (FrameLayout.LayoutParams) fab_type.getLayoutParams();
        paramsTren.bottomMargin = fab_type.getWidth() * 2;
        fab_type.setLayoutParams(paramsTren);
        fab_type.startAnimation(moveTren);
    }

    private void BackAnimation() {
        FrameLayout.LayoutParams paramsDuoi = (FrameLayout.LayoutParams) fab_address.getLayoutParams();
        paramsDuoi.rightMargin -= fab_address.getWidth() * 2;
        fab_address.setLayoutParams(paramsDuoi);
        fab_address.startAnimation(backDuoi);

        FrameLayout.LayoutParams paramsGiua = (FrameLayout.LayoutParams) fab_favorite.getLayoutParams();
        paramsGiua.bottomMargin -= (int) (fab_favorite.getWidth() * 1.5);
        paramsGiua.rightMargin -= (int) (fab_favorite.getWidth() * 1.5);
        fab_favorite.setLayoutParams(paramsGiua);
        fab_favorite.startAnimation(backGiua);

        FrameLayout.LayoutParams paramsTren = (FrameLayout.LayoutParams) fab_type.getLayoutParams();
        paramsTren.bottomMargin -= fab_type.getWidth() * 2;
        fab_type.setLayoutParams(paramsTren);
        fab_type.startAnimation(backTren);
    }

    private Polyline drawPath(String abc) {
        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(abc);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            Polyline line = map.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(10)
                    .color(Color.parseColor("#05b1fb"))//Google maps blue color
                    .geodesic(true)
            );
            return line;

        } catch (JSONException e) {
            return null;
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;

    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // direction
        map.getUiSettings().setMapToolbarEnabled(false);
        for (Hotel hotel : arrHotel) {
            String ten = hotel.getTenKhachSan();
            Point toaDo = hotel.getToaDo();
            String diaChi = hotel.getDiaChi();

            LatLng location = new LatLng(toaDo.getX(), toaDo.getY());
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);
            map.addMarker(new MarkerOptions()
                    .title(ten)
                    .snippet(diaChi)
                    .icon(icon)
                    .position(location));
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                markerClick = marker;
                locationFavor = marker.getPosition();
                FAB_Enabled(true);
                return false;
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                FAB_Enabled(false);
            }
        });

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                hotelChiTiet = getHotelChiTiet(marker.getPosition().latitude, marker.getPosition().longitude);
                if (hotelChiTiet != null) {
                    Intent intent = new Intent(Main2Activity.this, ChiTietKhachSan.class);
                    //byIntent(intent);
                    startActivity(intent);
                } else {
                    Toast.makeText(Main2Activity.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                }
            }
        });
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(new LatLng(-10, 59.35), new LatLng(30.25, 154)), 0));
        map.animateCamera(CameraUpdateFactory.zoomTo(11.5f));
        if (ContextCompat.checkSelfPermission(Main2Activity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
//                    Toast.makeText(Main2Activity.this, "hello", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(this, "Sorry hãy kiểm tra lại", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //Nav_bar Menu

    //    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_location) {

            Intent intent4 = new Intent(Main2Activity.this, FavoriteActivity.class);
            byIntentByFav(intent4);
            startActivity(intent4);
        } else if (id == R.id.nav_sharelocation) {
            boolean gps_enabled = false;
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }

            if (!gps_enabled) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Vui lòng bật vị trí để thực hiện tính năng này nhé!")
                        .setCancelable(false)
                        .setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                location = new Location(map.getMyLocation());
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                shareDialog = new ShareDialog(this);
                shareLinkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://www.google.com/maps/@" + latitude + "," + longitude + ",17z"))
                        .build();
                shareDialog.show(shareLinkContent);
            }

        } else if (id == R.id.nav_help) {
            Intent intent2 = new Intent(Main2Activity.this, InstructionActivity.class);
            startActivity(intent2);
        } else if (id == R.id.nav_info) {
            Intent intent = new Intent(Main2Activity.this, thongTinActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(Main2Activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_add) {
            Intent intent3 = new Intent(Main2Activity.this, addLocateActivity.class);
            startActivity(intent3);
        }
        return true;
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            String imageURL = strings[0];

            Bitmap bm = null;
            try {
                InputStream input = new java.net.URL(imageURL).openStream();
                bm = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            img.setImageBitmap(bitmap);
        }
    }

    private Hotel getHotelChiTiet(double x, double y) {
        Hotel resultHotel = null;
        for (Hotel hotel : arrHotel) {
            if (hotel.getToaDo().getX() == x)
                if (hotel.getToaDo().getY() == y)
                    resultHotel = hotel;
        }
        return resultHotel;
    }

    private void byIntent(Intent i) {
        i.putExtra("hotelchitiet", hotelChiTiet);
        i.putExtra("arrdg", arrayDanhGia);
    }

    private void byIntentByFav(Intent i) {
        i.putExtra("Point", arrFavToaDo);
        i.putExtra("Hotel", arrHotel);
    }

    private void LoadData() {
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
                if (item.getTenDangNhap().equals(profileFB.getTenDangNhap())) {
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