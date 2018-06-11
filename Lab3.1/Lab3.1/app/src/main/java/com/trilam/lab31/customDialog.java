package com.trilam.lab31;

import android.app.Activity;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.trilam.lab31.MainActivity;
import com.trilam.lab31.R;
import java.util.ArrayList;
import java.util.jar.Manifest;

/**
 * Created by Tri Lam on 10/26/2017.
 */

public class customDialog extends AlertDialog implements View.OnClickListener{
    Context context;
    EditText tvName;
    EditText tvEmail;
    EditText tvPhone;

    public customDialog(@NonNull Context context) {

        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);
        initViews();

    }

    //private String number =""; //= "01675566519";
    private String number = "01675566519";
    private void initViews() {
        ImageView ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(this);
        LinearLayout llSend = (LinearLayout) findViewById(R.id.btn_send);
        llSend.setOnClickListener(this);
        LinearLayout llCall = (LinearLayout) findViewById(R.id.btn_call);
        llCall.setOnClickListener(this);
       tvName = (EditText) findViewById(R.id.tv_name);
       tvName.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
           }
       });
         tvEmail = (EditText) findViewById(R.id.tv_email);
        tvPhone = (EditText) findViewById(R.id.tv_phone);
        //tvName.setText("Tang Quang Huy");
        tvEmail.setText(email);
        tvPhone.setText(number);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_close:{
                closeDialog();
                Toast.makeText(context,"Exit done",Toast.LENGTH_LONG).show();
                break;}
            case R.id.btn_send: {
                sendSMS();
                Toast.makeText(context,"Go to send Email", Toast.LENGTH_LONG).show();
                break;}
            case R.id.btn_call: {

                requestPhoneCallPermission();
                Toast.makeText(context,"Calling",Toast.LENGTH_LONG).show();
                break;}
        }
    }

    private void closeDialog() {
        this.dismiss();
    }
    String[] arrayPermission = {android.Manifest.permission.CALL_PHONE};
    private void requestPhonePermission(String phone){
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((MainActivity) context,arrayPermission,555);
        }
    }
    public static final int REQUEST_PHONECALL_PERMISSION_CODE = 1111;
    public void requestPhoneCallPermission() {

        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){
            //if not have permission -> request permission
            ActivityCompat.requestPermissions((Activity) context,new String[]{android.Manifest.permission.CALL_PHONE},REQUEST_PHONECALL_PERMISSION_CODE);
        }
        else {
            EditText tvPhone = (EditText) findViewById(R.id.tv_phone);
            number=tvPhone.getText().toString();
            //if exist permission callPhone -> handle call Phone -> start Activity CALL PHONE
            Uri call = Uri.parse("tel: "+number);
            Intent intentCallPhone = new Intent(Intent.ACTION_CALL,call);
            context.startActivity(intentCallPhone);
        }

    }

    private String email = "abc@gmail.com";
    private String[] emailArr = {email};
    private String text = "Enter the text you want to send..";
    private void sendSMS() {
        EditText et=(EditText)  findViewById(R.id.tv_email);
        email=et.getText().toString();
        emailArr[0]=email;
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL,emailArr);
        emailIntent.putExtra(Intent.EXTRA_TEXT,text);
        try{
            context.startActivity(Intent.createChooser(emailIntent,"Email Activity"));
        }catch (ActivityNotFoundException e){
            e.getMessage();
        }
    }

}
