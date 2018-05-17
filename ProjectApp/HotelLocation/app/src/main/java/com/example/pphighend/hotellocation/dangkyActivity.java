package com.example.pphighend.hotellocation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class dangkyActivity extends AppCompatActivity {
    EditText txtHoTen, txtTenDangNhap,txtEmail, txtMatKhau, txtXacNhanMK;
    Button btnDangKy;
    ImageView imgSelected;
    TaiKhoan profileFB;
    // Firebase
    ArrayList<TaiKhoan> arrTaiKhoan;
    ArrayList<Hotel> arrHotel;
    DatabaseReference mData;
    // Firebase - Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://hotellocation-pphighend.appspot.com");

    //Code open camera
    int REQUEST_CODE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangky);
        txtHoTen=(EditText) findViewById(R.id.txtHoTen);
        txtEmail=(EditText) findViewById(R.id.txtEmail);
        txtTenDangNhap=(EditText) findViewById(R.id.txtTenDangNhap);
        txtMatKhau=(EditText) findViewById(R.id.txtMatKhau);
        txtXacNhanMK=(EditText)findViewById(R.id.txtXacNhanMK);
        btnDangKy=(Button) findViewById(R.id.btnDangKy);
        imgSelected = (ImageView)findViewById(R.id.imgSelected);
        mData = FirebaseDatabase.getInstance().getReference();
        profileFB=new TaiKhoan();
        //Lay HotelData
        arrTaiKhoan = (ArrayList<TaiKhoan>) getIntent().getSerializableExtra("tk");
        arrHotel = (ArrayList<Hotel>) getIntent().getSerializableExtra("key");
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get the data from an ImageView as bytes
                Calendar calendar = Calendar.getInstance();
                StorageReference mountainsRef = storageRef.child("image" + calendar.getTimeInMillis() + ".png");

                imgSelected.setDrawingCacheEnabled(true);
                imgSelected.buildDrawingCache();
                Bitmap bitmap = imgSelected.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(dangkyActivity.this, "Lỗi upload hình ảnh", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        //Toast.makeText(dangkyActivity.this, "Upload hình ảnh thành công", Toast.LENGTH_SHORT).show();
                        Log.d("UPLOADIMG", downloadUrl + "");

                        String hoten=txtHoTen.getText().toString();
                        String tendangnhap=txtTenDangNhap.getText().toString();
                        String email=txtEmail.getText().toString();
                        String matkhau=txtMatKhau.getText().toString();
                        String xacnhanmk=txtXacNhanMK.getText().toString();
                        final TaiKhoan taikhoan=DangKyTaiKhoan(hoten,email,tendangnhap,matkhau,xacnhanmk, String.valueOf(downloadUrl));
                        if(taikhoan!=null){
                            mData.child("TAIKHOAN").push().setValue(taikhoan, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if(databaseError == null){
                                        //Toast.makeText(dangkyActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                                        profileFB.setHoTen(taikhoan.getHoTen());
                                        profileFB.setTenDangNhap(taikhoan.getTenDangNhap());
                                        profileFB.setEmail(taikhoan.getEmail());
                                        profileFB.setMatKhau(taikhoan.getMatKhau());
                                        profileFB.setHinhAnh(taikhoan.getHinhAnh());
                                        Intent intent=new Intent(dangkyActivity.this, Main2Activity.class);
                                        byIntent(intent);
                                        startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(dangkyActivity.this, "Tạo tài khoản thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });

            }
        });
        txtXacNhanMK.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    if(!CheckXacNhanMK(txtMatKhau.getText().toString(),txtXacNhanMK.getText().toString())){
                        Toast.makeText(dangkyActivity.this, "Xác nhận mật khẩu không đúng !", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        //Open Camera
        imgSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });
    }
    private Boolean CheckXacNhanMK(String MatKhau, String XacNhanMK){
        if(MatKhau.equals(XacNhanMK)) return true;
        return false;
    }
    private Boolean CheckTenDangNhap(String tenDangNhap){
        for(TaiKhoan taikhoan:arrTaiKhoan){
           if(tenDangNhap.equals(taikhoan.getTenDangNhap())) return false;
        }
        return true;
    }
    private TaiKhoan DangKyTaiKhoan(String hoten, String email, String tendangnhap, String matkhau, String xacnhanmk, String hinhanh){
        TaiKhoan taikhoanresult=null;
        if(hoten.equals("")||tendangnhap.equals("")||email.equals("")||matkhau.equals("")||xacnhanmk.equals("")){
            Toast.makeText(dangkyActivity.this, "Không được để trống. Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
        }
        else {
            if(CheckTenDangNhap(tendangnhap)){
                if(!CheckXacNhanMK(matkhau,xacnhanmk)){
                    Toast.makeText(dangkyActivity.this, "Xác nhận mật khẩu không đúng !", Toast.LENGTH_SHORT).show();
                }
                else{
                    taikhoanresult=new TaiKhoan();
                    taikhoanresult.setHoTen(hoten);
                    taikhoanresult.setTenDangNhap(tendangnhap);
                    taikhoanresult.setHinhAnh(hinhanh);
                    taikhoanresult.setMatKhau(matkhau);
                    taikhoanresult.setEmail(email);
                    //mData.child("TAIKHOAN").push().setValue(taikhoanresult);
                }
            }else  Toast.makeText(dangkyActivity.this, "Tên đăng nhập đã tồn tại !", Toast.LENGTH_SHORT).show();

        }
        return taikhoanresult;
    }
    private void byIntent(Intent i){
        i.putExtra("key", arrHotel);
        i.putExtra("profile", profileFB);
        i.putExtra("FB", false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgSelected.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
