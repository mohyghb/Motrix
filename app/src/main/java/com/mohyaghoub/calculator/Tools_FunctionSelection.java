package com.mohyaghoub.calculator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Tools_FunctionSelection extends AppCompatActivity {

    private ArrayAdapter<FunctionCreator> functionCreatorArrayAdapter;
    private Animation upToDown;
    private Button noFunctionDetected;
    private TextView chooseAFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools__function_selection);
        initClass();
    }

    private void initClass()
    {
        this.chooseAFunction = findViewById(R.id.chooseAFunction_Tools);
        this.noFunctionDetected = findViewById(R.id.NoFunctionsDetectedToolsActivity);
        this.noFunctionDetected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tools_FunctionSelection.this,FunctionActivity.class);
                startActivity(intent);
            }
        });
        initAnimations();
        initListView();
    }

    private void initAnimations()
    {
        this.upToDown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
    }

    private void initListView()
    {
        try {
            functionCreatorArrayAdapter = new Tools_FunctionListView(Tools_FunctionSelection.this, 0,MathFunctions.savedFunctions);
            ListView listView = findViewById(R.id.Tools_Selection_ListView);
            listView.setAdapter(functionCreatorArrayAdapter);
            listView.startAnimation(this.upToDown);

            if(NumberOfFunctions()==0)
            {
                appear(this.noFunctionDetected);
            }
            else
            {
                appear(this.chooseAFunction);
            }
        }catch(Exception e)
        {
            Toast.makeText(this,"Could not load the listView",Toast.LENGTH_SHORT).show();
        }
    }

    private void appear(View view)
    {
        view.startAnimation(this.upToDown);
        view.setVisibility(View.VISIBLE);
    }


    private int NumberOfFunctions()
    {
        int num = 0;
        for(FunctionCreator functionCreator:MathFunctions.savedFunctions)
        {
            if(!functionCreator.isDeleted())
            {
                num++;
            }
        }
        return num;
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
