package com.trilam.lab31;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trilam.lab31.customDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_PHONECALL_PERMISSION_CODE = 1111;
    customDialog dialog;

    Dialog a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btDialog = (Button) findViewById(R.id.bt_dialog);
        btDialog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        dialog = new customDialog(this);
//        dialog.show();
       a = new Dialog(this);
        a.setContentView(R.layout.custom_dialog);
        a.show();
        EditText tvName = (EditText)a.findViewById(R.id.tv_name);
        EditText tvEmail = (EditText)a.findViewById(R.id.tv_email);
        EditText tvPhone = (EditText)a.findViewById(R.id.tv_phone);
        ImageView ivClose = (ImageView) a.findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.dismiss();
                Toast.makeText(MainActivity.this, "Exit done", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout llSend = (LinearLayout) a.findViewById(R.id.btn_send);
        llSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText et=(EditText)  a.findViewById(R.id.tv_email);
               String email=et.getText().toString();
                String[] emailArr = {email};
                String text = "Enter the text you want to send..";
                emailArr[0]=email;
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,emailArr);
                emailIntent.putExtra(Intent.EXTRA_TEXT,text);
                try{
                    MainActivity.this.startActivity(Intent.createChooser(emailIntent,"Email Activity"));
                }catch (ActivityNotFoundException e){
                    e.getMessage();
                }
                Toast.makeText(MainActivity.this, "Go to send Email", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout llCall = (LinearLayout) a.findViewById(R.id.btn_call);
        llCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] arrayPermission = {android.Manifest.permission.CALL_PHONE};
                if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
                    //if not have permission -> request permission
                    ActivityCompat.requestPermissions((Activity) MainActivity.this,new String[]{android.Manifest.permission.CALL_PHONE},REQUEST_PHONECALL_PERMISSION_CODE);
                }
                else {
                    EditText tvPhone = (EditText) a.findViewById(R.id.tv_phone);
                    String number=tvPhone.getText().toString();
                    //if exist permission callPhone -> handle call Phone -> start Activity CALL PHONE
                    Uri call = Uri.parse("tel: "+number);
                    Intent intentCallPhone = new Intent(Intent.ACTION_CALL,call);
                    MainActivity.this.startActivity(intentCallPhone);
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case customDialog.REQUEST_PHONECALL_PERMISSION_CODE: {
                if(grantResults.length>0 &&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    dialog.requestPhoneCallPermission();
                }
            }

        }
    }

}
