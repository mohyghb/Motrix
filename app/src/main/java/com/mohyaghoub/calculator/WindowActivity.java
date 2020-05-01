package com.mohyaghoub.calculator;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WindowActivity extends AppCompatActivity {

    private Button save;
    private Button reset;
    private Button cancel;
    private Button minX;
    private Button maxX;
    private Button minY;
    private Button maxY;
    private final int MINX_CODE = 100;
    private final int MAXX_CODE = 101;
    private final int MINY_CODE = 102;
    private final int MAXY_CODE = 103;



    double changedMinX,changedMaxX,changedMinY,changedMaxY;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window);

        initObjects();
    }

    protected void initObjects()
    {
        this.save = findViewById(R.id.SaveWindow);
        this.reset = findViewById(R.id.ResetWindow);
        this.cancel = findViewById(R.id.CancelWindow);

        this.minX = findViewById(R.id.MinXWindow);
        this.maxX = findViewById(R.id.MaxXWindow);
        this.minY = findViewById(R.id.MinYWindow);
        this.maxY = findViewById(R.id.MaxYWindow);


        changedMinX = -10;
        changedMaxX = 10;
        changedMinY = -10;
        changedMaxY = 10;

        LoadWindowRatios();

        setOnClickListenerForMinXBtn();
        setOnClickListenerForMaxXBtn();
        setOnClickListenerForMinYBtn();
        setOnClickListenerForMaxYBtn();

        setOnClickListenerForCancel();
        setOnClickListenerForReset();
        setOnClickListenerForSave();
        //animations
    }

    private void LoadWindowRatios()
    {
        String readWindowRatios = LoadList.readFile(FileNames.windowRatioFileName,this);
        if(readWindowRatios.length()>0)
        {
            String splitRatios[] = readWindowRatios.split("\\|");
            if(splitRatios.length==4)
            {
                changedMinX = Double.parseDouble(splitRatios[0]);
                changedMaxX = Double.parseDouble(splitRatios[1]);
                changedMinY = Double.parseDouble(splitRatios[2]);
                changedMaxY = Double.parseDouble(splitRatios[3]);
            }
        }
        minX.setHint(String.format("Min X:     %f",changedMinX));
        maxX.setHint(String.format("Max X:     %f",changedMaxX));
        minY.setHint(String.format("Min Y:     %f",changedMinY));
        maxY.setHint(String.format("Max Y:     %f",changedMaxY));
    }



    private void setOnClickListenerForMinXBtn()
    {
        this.minX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(MINX_CODE,parseNumber(minX.getText().toString()));
            }
        });
    }
    private void setOnClickListenerForMaxXBtn()
    {
        this.maxX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(MAXX_CODE,parseNumber(maxX.getText().toString()));
            }
        });
    }
    private void setOnClickListenerForMinYBtn()
    {
        this.minY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(MINY_CODE,parseNumber(minY.getText().toString()));
            }
        });
    }
    private void setOnClickListenerForMaxYBtn()
    {
        this.maxY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(MAXY_CODE,parseNumber(maxY.getText().toString()));
            }
        });
    }

    private String parseNumber(@NonNull String text)
    {
        if(text.length()==0)
        {
            return "";
        }
        else
        {
            String spilt[] = text.split(":");
            return spilt[1];
        }
    }


    private void setOnClickListenerForCancel()
    {
        this.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setOnClickListenerForReset()
    {
        this.reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    private void reset()
    {
        changedMinX = -10;
        changedMaxX = 10;
        changedMinY = -10;
        changedMaxY = 10;

        minX.setHint(String.format("Min X:     %f",changedMinX));
        maxX.setHint(String.format("Max X:     %f",changedMaxX));
        minY.setHint(String.format("Min Y:     %f",changedMinY));
        maxY.setHint(String.format("Max Y:     %f",changedMaxY));

        Toast.makeText(this,"Make sure to save changes before leaving.",Toast.LENGTH_LONG).show();
    }



    private void setOnClickListenerForSave()
    {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (changedMinX < changedMaxX) {
                    if (changedMinY < changedMaxY) {
                        String windowRatiosText = String.format("%f|%f|%f|%f", changedMinX, changedMaxX, changedMinY, changedMaxY);
                        LoadList.saveFile(FileNames.windowRatioFileName, windowRatiosText,WindowActivity.this);
                        Toast.makeText(WindowActivity.this,"Window ratios were successfully saved!",Toast.LENGTH_SHORT).show();
                        LoadList.loadWindowRatios(WindowActivity.this);
                        finish();
                    } else {
                        Toast.makeText(WindowActivity.this, "Min Y should be smaller than Max Y.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(WindowActivity.this, "Min X must be smaller than Max X.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void startCalculatorActivity(int code,String text)
    {
        Intent intent = new Intent(WindowActivity.this,CalculatorActivity.class);
        intent.putExtra("leftOver",text);
        intent.putExtra("savedFunctions","");
        intent.putExtra("KEYMODE","1");
        startActivityForResult(intent,code);
    }

    private void getValueOfExpression(Button btn,String equation,int code)
    {
        if(LoadList.isAGoodFunction(equation,this))
        {
            LoadList.calculator.setEquation(equation);
            double value = LoadList.calculator.getValue(1);
            String field = "";
            switch (code)
            {
                case MINX_CODE:
                    field = "Min X";
                    changedMinX = value;
                    break;
                case MAXX_CODE:
                    field = "Max X";
                    changedMaxX = value;
                    break;
                case MINY_CODE:
                    field = "Min Y";
                    changedMinY = value;
                    break;
                case MAXY_CODE:
                    field = "Max Y";
                    changedMaxY = value;
                    break;
            }
            btn.setText(String.format("%s:     %f",field,value));
        }
    }


    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {

                case MINX_CODE:
                    String text = data.getStringExtra("classicData");
                    getValueOfExpression(minX,text,MINX_CODE);
                    break;
                case MAXX_CODE:
                    String text1 = data.getStringExtra("classicData");
                    getValueOfExpression(maxX,text1,MAXX_CODE);
                    break;

                case MINY_CODE:
                    String text3 = data.getStringExtra("classicData");
                    getValueOfExpression(minY,text3,MINY_CODE);
                    break;
                case MAXY_CODE:
                    String text4 = data.getStringExtra("classicData");
                    getValueOfExpression(maxY,text4,MAXY_CODE);
                    break;




            }

        }
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(MainActivity.examMode.isAppInLockTaskMode())
        {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }


}
