package com.example.pphighend.hotellocation;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class Share extends AppCompatActivity {
    String x, y;
    TextView textView;
    ShareDialog shareDialog;
    ShareLinkContent shareLinkContent;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        x = getIntent().getStringExtra("x");
        y = getIntent().getStringExtra("y");
        textView = (TextView)findViewById(R.id.textdiem);
        textView.setText("Tọa độ vị trí hiện tại của bạn là: "+"\n"+" x: " + x  + "\n"+" y: " + y );
        button = (Button)findViewById(R.id.btnShare);
        shareDialog = new ShareDialog(Share.this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareLinkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://www.google.com/maps/@"+ x + "," + y + ",15z"))
                        .build();
                shareDialog.show(shareLinkContent);
            }
        });
    }
}
