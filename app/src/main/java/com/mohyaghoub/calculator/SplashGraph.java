package com.mohyaghoub.calculator;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashGraph extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(SplashGraph.this, Graph.class);
        intent.putExtra("position", getIntent().getExtras().getString("position"));
        startActivity(intent);
        finish();
    }
}
