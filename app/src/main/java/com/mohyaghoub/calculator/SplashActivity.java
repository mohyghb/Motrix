package com.mohyaghoub.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.splashlayout);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
//        Intent intent = new Intent(SplashActivity.this, Graph.class);
//        intent.putExtra("position",getIntent().getExtras().getString("position"));
//        startActivity(intent);
        finish();
    }
}
