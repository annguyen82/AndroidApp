package com.example.pphighend.hotellocation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class addLocateActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private static final int PICK_IMAGE = 100;
    int REQUEST_CODE_IMAGE = 1;

    // Firebase - Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://hotellocation-pphighend.appspot.com");

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mClear;

    //vars
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private PlaceAutocompleteAdapter mAutocompleteAdapter;
    private GeoDataClient mGeoDataClient;
    private String locationName;
    private Hotel hotel;
    FloatingActionButton fabAdd;

    private DatabaseReference mData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_locate);
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!CheckHotellAdded()) {
                    alertSaveHotel();
                } else {
                    Toast.makeText(addLocateActivity.this, "Địa điểm này đã tồn tại\nCảm ơn bạn !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mData = FirebaseDatabase.getInstance().getReference();

        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mClear = (ImageView) findViewById(R.id.ic_clear);
        mClear.setVisibility(View.INVISIBLE);
        mSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClear.getVisibility() == View.INVISIBLE) {
                    mClear.setVisibility(View.VISIBLE);
                }
            }
        });
        getLocationPermission();
        init();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // direction
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            init();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                fabAdd.setVisibility(View.INVISIBLE);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                fabAdd.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    private void alertSaveHotel() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Thêm hình ảnh cho khách sạn")
                .setCancelable(false)
                .setPositiveButton("Thư viện", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(gallery, PICK_IMAGE);
                    }
                })
                .setNeutralButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CODE_IMAGE);
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    // Tìm Kiếm là đây
    private void init() {
        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    // execute our method for searching
                    hideSoftKeyboard();
                    mClear.setVisibility(View.INVISIBLE);
                    geoLocate();
                }
                return false;
            }
        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchText.setText("");
            }
        });

    }

    private void geoLocate() {
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(addLocateActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException ioe) {
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            if (searchString.indexOf(",") > 0) {
                locationName = searchString.substring(0, searchString.indexOf(","));
            } else {
                locationName = searchString;
            }

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, locationName, address.getAddressLine(0));
        }
    }


    private void moveCamera(final LatLng latLng, float zoom, final String title, final String address) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        hotel = new Hotel(title, "", address, new Point(latLng.latitude, latLng.longitude));
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.marker);
        final MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .snippet(address)
                .icon(icon)
                .title(title);
        mMap.addMarker(options);

    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(addLocateActivity.this);
    }

    private void getLocationPermission() {
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    private Boolean CheckHotellAdded(){
        for(Hotel hotel1: Main2Activity.arrHotel){
            if(hotel.getToaDo().getX()==hotel1.getToaDo().getX())
                if(hotel.getToaDo().getY()==hotel1.getToaDo().getY()){
                    return true;
                }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    // ---------------Google places API autocomplete suggestions---------------------------
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();
            mClear.setVisibility(View.INVISIBLE);
            geoLocate();
        }
    };

    //-------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null){
            Uri URI = data.getData();

            Bitmap pic = null;
            try {
                pic = BitmapFactory.decodeStream(getContentResolver().openInputStream(URI));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            Calendar calendar = Calendar.getInstance();
            StorageReference mountainsRef = storageRef.child("image" + calendar.getTimeInMillis() + ".png");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            pic.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] arrImg = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(arrImg);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(addLocateActivity.this, "Lỗi upload hình ảnh", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    hotel.setHinhAnh(String.valueOf(downloadUrl));
                    Main2Activity.arrHotel.add(hotel);
                    mData.child("KHACHSAN").push().setValue(hotel);
                    Toast.makeText(addLocateActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            Calendar calendar = Calendar.getInstance();
            StorageReference mountainsRef = storageRef.child("image" + calendar.getTimeInMillis() + ".png");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] arrImg = baos.toByteArray();

            UploadTask uploadTask = mountainsRef.putBytes(arrImg);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(addLocateActivity.this, "Lỗi upload hình ảnh", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    hotel.setHinhAnh(String.valueOf(downloadUrl));
                    Main2Activity.arrHotel.add(hotel);
                    mData.child("KHACHSAN").push().setValue(hotel);
                    Toast.makeText(addLocateActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                }
            });
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}