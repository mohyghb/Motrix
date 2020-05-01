package com.mohyaghoub.calculator;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HelpActivity extends AppCompatActivity {

    private Button main,functions,smartvoice,others;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initObjects();
    }



    private void initObjects()
    {
        this.main = findViewById(R.id.Help_Main_menu);
        this.functions = findViewById(R.id.Help_Functions);
        this.smartvoice = findViewById(R.id.Help_SmartVoice);
        this.others = findViewById(R.id.Help_Others);

        this.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(FinalString.mainhelp);

            }
        });

        this.functions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(FinalString.functionhelp);
            }
        });

        this.smartvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(FinalString.smartvoicehelp);

            }
        });
        this.others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink(FinalString.othershelp);
            }
        });
    }




    private void openLink(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

}
