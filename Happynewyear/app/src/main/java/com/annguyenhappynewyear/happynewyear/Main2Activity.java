package com.annguyenhappynewyear.happynewyear;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    Button btnChonNhac, btnback;
    private static final int PICK_AUDIO =1;
    Uri audio;
    MainActivity main=new MainActivity();
    TextView txtvloinhan;
    ListView lvbaihat;
    ConstraintLayout mh;
    Animation anim;
    MediaPlayer a;
    ArrayList<String>  mangtenbh;
    ArrayList<Integer> mangbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        TaoMang();
        btnback = (Button)findViewById(R.id.btnback);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.stop();
                Intent mhmain = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mhmain);
            }
        });
        a = MediaPlayer.create(Main2Activity.this, R.raw.nhac);
        a.start();
        txtvloinhan = (TextView)findViewById(R.id.textViewloinhan);
        txtvloinhan.setAnimation(AnimationUtils.loadAnimation(this,R.anim.animabc));
        lvbaihat = (ListView)findViewById(R.id.listViewnhac);
        mh = (ConstraintLayout)findViewById(R.id.manhinh2);
        mh.setBackgroundResource(R.drawable.bg);
        ArrayAdapter adapter = new ArrayAdapter(
                getApplicationContext(), android.R.layout.simple_list_item_1, mangtenbh
        );

        lvbaihat.setAdapter(adapter);
        lvbaihat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                a.stop();
                a = MediaPlayer.create(getApplicationContext(), mangbh.get(i));
                a.start();
                a.setVolume(100,100);
            }
        });
        txtvloinhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.stop();
                Intent mhmain = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(mhmain);
            }
        });
        btnChonNhac=(Button)findViewById(R.id.btnchonnhac);
        btnChonNhac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*"); // specify "audio/mp3" to filter only mp3 files
                startActivityForResult(intent,PICK_AUDIO);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    /*check whether you're working on correct request using requestCode , In this case 1*/
        if(main.song!=null){
            main.song.release();}

        if(requestCode == PICK_AUDIO && resultCode == Activity.RESULT_OK){
            audio = data.getData(); //declared above Uri audio;
            main.song= MediaPlayer.create(Main2Activity.this, audio);
            main.song.start();
            Toast.makeText(Main2Activity.this,"Đã chọn bài hát",Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void TaoMang(){
        mangbh = new ArrayList<>();
        mangtenbh = new ArrayList<>();
        mangbh.add(R.raw.buoncuaanh);
        mangtenbh.add("Buồn của anh");
        mangbh.add(R.raw.coduockhongem);
        mangtenbh.add("Có được không em");
        mangbh.add(R.raw.detmonguyenuong);
        mangtenbh.add("Dệt mộng uyên ương");
        mangbh.add(R.raw.loianhmuonnoi);
        mangtenbh.add("Lời anh muốn nói");
        mangbh.add(R.raw.neulaanh);
        mangtenbh.add("Nếu là anh");
        mangbh.add(R.raw.thegioiaotinhyeuthat);
        mangtenbh.add("Thế giới ảo tình yêu thật");
        mangbh.add(R.raw.toithuongnguoitalam);
        mangtenbh.add("Tôi thương người ta lắm");
        mangbh.add(R.raw.chamkhetimanhmotchutthoi);
        mangtenbh.add("Chạm khẽ tim anh một chút thôi");
        mangbh.add(R.raw.matanhemcobuon);
        mangtenbh.add("Mất anh em có buồn");
        mangbh.add(R.raw.anhnangcuaanh);
        mangtenbh.add("Ánh nắng của anh");
        mangbh.add(R.raw.anhsomatem);
        mangtenbh.add("Anh sợ mất em");
    }
}
