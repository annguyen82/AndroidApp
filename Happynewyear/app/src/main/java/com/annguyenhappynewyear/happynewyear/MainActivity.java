package com.annguyenhappynewyear.happynewyear;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
  ConstraintLayout mhmain;
    private static final int PICK_IMAGE = 100;
    public static MediaPlayer song ;
Animation fade;
public  static ImageView hinh;
Button btnnhac, btnhinh;
    Uri imageUri;
    public void AnhXA() {
        mhmain = (ConstraintLayout)findViewById(R.id.ManHinh);
        hinh = (ImageView)findViewById(R.id.imv2018);
        btnhinh = (Button)findViewById(R.id.btnchonhinh);
        btnnhac = (Button)findViewById(R.id.btnchonnhac);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        AnhXA();
        mhmain.setBackgroundResource(R.drawable.a);
        song = MediaPlayer.create(MainActivity.this, R.raw.nhac);
        song.start();
        song.setLooping(true); // Set looping
        song.setVolume(100,100);
        hinh.clearAnimation();
        hinh.setAnimation(AnimationUtils.loadAnimation(this,R.anim.fade));
        btnnhac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                song.stop();
                Intent mhnhac = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(mhnhac);
            }
        });
        btnhinh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            hinh.setImageURI(imageUri);

        }
    }
}
