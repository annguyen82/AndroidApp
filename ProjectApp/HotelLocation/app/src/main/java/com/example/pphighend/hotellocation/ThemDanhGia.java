package com.example.pphighend.hotellocation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.CircularPropagation;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class ThemDanhGia extends AppCompatActivity {
    RatingBar mratingBar;
    TextView mRatingScale;
    TextView textViewDone;
    EditText txtDanhGia;
    CircularProgressButton btnThemDanhGia;
    Point toaDo;
    danhgia danhGia;
    DatabaseReference mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_danh_gia);
        mratingBar=(RatingBar)findViewById(R.id.mratingBar);
        mRatingScale = (TextView) findViewById(R.id.tvRatingScale);
        txtDanhGia = (EditText) findViewById(R.id.txtDanhGia);
        btnThemDanhGia = (CircularProgressButton) findViewById(R.id.btnThemDanhGia);
        textViewDone = (TextView) findViewById(R.id.textViewDone);
        mData = FirebaseDatabase.getInstance().getReference();
        danhGia=new danhgia();
        toaDo=new Point();
        toaDo = (Point) getIntent().getSerializableExtra("toado");
        danhGia.setToaDo(toaDo);
        mratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        mRatingScale.setText("Rất tệ");
                        break;
                    case 2:
                        mRatingScale.setText("Cần cải thiện");
                        break;
                    case 3:
                        mRatingScale.setText("Tốt");
                        break;
                    case 4:
                        mRatingScale.setText("Tuyệt");
                        break;
                    case 5:
                        mRatingScale.setText("Tuyệt vời. Tôi thích nó");
                        break;
                    default:
                        mRatingScale.setText("");
                }

            }
        });
        btnThemDanhGia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtDanhGia.getText().toString().isEmpty()) {
                    Toast.makeText(ThemDanhGia.this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_LONG).show();
                } else {
                    danhGia.setNoiDung(txtDanhGia.getText().toString());
                    danhGia.setTenDangNhap(Main2Activity.profileFB.getTenDangNhap());
                    danhGia.setStar((int)mratingBar.getRating());
                    txtDanhGia.setText("");
                    mratingBar.setRating(0);
                    AsyncTask<String, String, String> demoDanhgia=new AsyncTask<String, String, String>() {
                        @Override
                        protected String doInBackground(String... strings) {
                            mData.child("DANHGIA").push().setValue(danhGia, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                    } else
                                        Toast.makeText(ThemDanhGia.this, "Thêm đánh giá thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return "Done";
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            if(s.equals("Done")){
                                btnThemDanhGia.doneLoadingAnimation(Color.parseColor("#ffbb00"), BitmapFactory.decodeResource(getResources(),R.drawable.ic_done_white_48dp));
                                textViewDone.setText("Cảm ơn bạn đã đánh giá !");
                            }
                        }
                    };
                    btnThemDanhGia.startAnimation();
                    demoDanhgia.execute();
                }
            }
        });
    }
}
