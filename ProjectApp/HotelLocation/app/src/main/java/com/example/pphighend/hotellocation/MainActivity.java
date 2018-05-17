package com.example.pphighend.hotellocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackMacnager;
    LoginButton loginButton;
    Button btnDangNhap;
    Button btnDangKy;
    EditText txtTenDangNhap;
    EditText txtMatKhau;
    TextView textView3;
    ProfilePictureView profilePictureView;
    String email, firstname, lastname, ngaysinh, name;
    TaiKhoan profileFB;
    Dialog dialog;
    // Firebase
    DatabaseReference mData;
    ArrayList<Hotel> arrHotel;
    ArrayList<TaiKhoan> arrTaiKhoan;
    private boolean CheckButtonF=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        if(!isConnected(MainActivity.this)){
            buildDialog(MainActivity.this).show();
            return;
        }
        else {
            Toast.makeText(MainActivity.this,"Welcome", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);
        }
        profileFB=new TaiKhoan();


        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackMacnager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.btnDangNhapFace);
        txtTenDangNhap=(EditText) findViewById(R.id.txtTenDangNhap);
        txtMatKhau=(EditText) findViewById(R.id.txtMatKhau);
        btnDangNhap=(Button)findViewById(R.id.btnDangNhap);
        btnDangKy=(Button) findViewById(R.id.btnDangKy);
        textView3=(TextView)findViewById(R.id.textView3);
        //profilePictureView = (ProfilePictureView) findViewById(R.id.image) ;
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));
        setLogin_Button();

        btnDangNhap.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
          String TenDangNhap = txtTenDangNhap.getText().toString();
          String MatKhau = txtMatKhau.getText().toString();
          if(CheckButtonF){
              Intent intent=new Intent(MainActivity.this, Main2Activity.class);
              byIntent(intent);
              startActivity(intent);
              return;
          }
          if (TenDangNhap.equals("") || MatKhau.equals("")) {
                 Toast.makeText(MainActivity.this, "Tài khoản hoặc mật khẩu không được để trống.", Toast.LENGTH_SHORT).show();
          }
          else{
              TaiKhoan taikhoan=CheckDangNhap(TenDangNhap,MatKhau);
              if (taikhoan!=null) {
                  profileFB.setHoTen(taikhoan.getHoTen());
                  profileFB.setTenDangNhap(taikhoan.getTenDangNhap());
                  profileFB.setEmail(taikhoan.getEmail());
                  profileFB.setMatKhau(taikhoan.getMatKhau());
                  profileFB.setHinhAnh(taikhoan.getHinhAnh());
                  Intent intent=new Intent(MainActivity.this, Main2Activity.class);
                  byIntent(intent);
                  startActivity(intent);
              }else Toast.makeText(MainActivity.this, "Tài khoản hoặc mật khẩu không đúng. Bạn vui lòng nhập lại.", Toast.LENGTH_SHORT).show();
          }
          }

         });

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, dangkyActivity.class);
                pushQuaDK(intent);
                byIntent(intent);
                startActivity(intent);
            }
        });
        txtMatKhau.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    //btnDangNhap.performClick();
                    return true;
                }
                return false;
            }
        });
        try {
            PackageInfo info = null;
            try {
                info = getPackageManager().getPackageInfo(
                        "com.example.pphighend.hotellocation",
                        PackageManager.GET_SIGNATURES);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NoSuchAlgorithmException e) {

        }


        // Firebase
        arrHotel = new ArrayList<Hotel>();
        arrTaiKhoan= new ArrayList<TaiKhoan>();
        mData = FirebaseDatabase.getInstance().getReference();
        LoadData();
    }
    //Check InterNet trước khi Load ứng dụng
    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
        else return false;
        } else
        return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Không thể kết nối với Internet.");
        builder.setMessage("Bạn cần bật dữ liệu di động hoặc Wifi để kết nối. Nhấn Ok để thoát.");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });

        return builder;
    }
    private TaiKhoan CheckDangNhap(String TenDangNhap, String MatKhau){
        TaiKhoan resultTaiKhoan=null;
        for(TaiKhoan taikhoan: arrTaiKhoan){
            if(TenDangNhap.equals(taikhoan.getTenDangNhap())){
                if(MatKhau.equals(taikhoan.getMatKhau()))
                    resultTaiKhoan=taikhoan;
            }

        }
        return resultTaiKhoan;
    }
    private void setLogin_Button() {

        loginButton.registerCallback(callbackMacnager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                loginButton.setVisibility(View.INVISIBLE);
                result();
                CheckButtonF=true;
                txtMatKhau.setVisibility(View.INVISIBLE);
                txtTenDangNhap.setEnabled(false);
                btnDangKy.setVisibility(View.INVISIBLE);
                textView3.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Không thể kết nối.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Có lỗi xảy ra.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void result() {
        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("JSON", response.getJSONObject().toString());
                try {
                    email = object.getString("email");
                    firstname = object.getString("first_name");
                    lastname = object.getString("last_name");
                    ngaysinh = object.getString("birthday");
                    name = object.getString("name");
                    txtTenDangNhap.setText(name);
                    profileFB.setTenDangNhap(name);
                    profileFB.setHoTen(name);
                    profileFB.setEmail(email);
                    profileFB.setHinhAnh(Profile.getCurrentProfile().getId());
                    profileFB.setMatKhau("");
                   // profilePictureView.setProfileId(Profile.getCurrentProfile().getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email,birthday,first_name,last_name");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackMacnager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        LoginManager.getInstance().logOut();
        super.onStart();
    }

    private void LoadData(){
        //LoadData HotelLocation
        mData.child("KHACHSAN").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Hotel hotel = dataSnapshot.getValue(Hotel.class);
                arrHotel.add(hotel);
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
                Toast.makeText(MainActivity.this, "Không tải được dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });

        mData.child("TAIKHOAN").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                TaiKhoan taikhoan = dataSnapshot.getValue(TaiKhoan.class);
                arrTaiKhoan.add(taikhoan);
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
  /*  public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tiếp tục");
        builder.setMessage("Nhấn tiếp tục để vào Hotel Location");
        builder.setCancelable(false);
        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(MainActivity.this, Main2Activity.class);
                byIntent(intent);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }*/
    private void byIntent(Intent i){
        i.putExtra("key", arrHotel);
        i.putExtra("profile", profileFB);
        i.putExtra("FB", CheckButtonF);
    }
    private void pushQuaDK(Intent i){
        i.putExtra("tk",arrTaiKhoan);
    }
}




