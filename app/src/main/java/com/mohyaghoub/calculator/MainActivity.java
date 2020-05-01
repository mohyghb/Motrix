package com.mohyaghoub.calculator;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button Mic;
    private Button showEquation;
    private final int REQ_CODE = 1002;
//    private TextView showActual;


    //camera
    private Button camera,save, window,tools;
//    private TextView cameraText;
    private final int CAMERA_CODE = 9001;


    //clear
    private Button ClearEquation,Calculate;


    //calculator mode
    private final int CLASSIC_CODE =  5002;
    private Animation animScale;
    private Animation upToDown;
//    private Animation appear;
    public static Animation downToUp;
//    private Animation disappear;
    private Animation opposite;

    //graph
    private GraphView graph;
    private CardView toolbar;
    private MainControl mainControl;


    //banners
    private ImageView banner1;
    private ImageView banner2;
    private ImageView banner3;
    private ImageView banner4;

    //show answer
    private TextView showAnswer;

    //constraint layout box Calculator
    private ConstraintLayout boxCalculator;
    private TextView showFunctionBox;

    private Button mainY;
    private String mainYText;
    private Button mainX;
    private String mainXText;
    private final int MAINX_CODE = 2032;
//    private final int MAINY_CODE = 2049;

    private int focusListener;

    public static ExamMode examMode;
    public static Button ExamModeConfirm;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // no status bar overlay

        createDrawerMenu();
        initObjects();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    private void initObjects()
    {
        this.focusListener = 0;
        this.mainXText = "";
        this.mainYText = "";
        this.Mic = findViewById(R.id.Mic);
        this.showEquation = findViewById(R.id.showEquation);
        this.camera = findViewById(R.id.Camera);
        this.ClearEquation = findViewById(R.id.ClearEquation);
        this.Calculate = findViewById(R.id.calculate);

        this.animScale= AnimationUtils.loadAnimation(this,R.anim.scale);
        this.upToDown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        downToUp = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        this.opposite = AnimationUtils.loadAnimation(this,R.anim.opposite);

        this.save = findViewById(R.id.saveMain);
        this.tools = findViewById(R.id.toolsMain);
        this.window = findViewById(R.id.WindowMain);
        this.showAnswer = findViewById(R.id.answerMain);
        this.graph = findViewById(R.id.graphMain);
        this.toolbar = findViewById(R.id.CardView);
        this.boxCalculator = findViewById(R.id.boxCalculator);
        this.mainX = findViewById(R.id.xMain);
//        this.mainX.setShowSoftInputOnFocus(false);
        this.mainY = findViewById(R.id.yMain);
     //   this.mainY.setShowSoftInputOnFocus(false);
        this.showFunctionBox = findViewById(R.id.formulaMain);
        ExamModeConfirm = findViewById(R.id.ExamModeButtonConfirm);



        setUpGraphLayout();

        this.Mic.startAnimation(upToDown);
        this.showEquation.startAnimation(upToDown);
        this.ClearEquation.startAnimation(upToDown);
        this.camera.startAnimation(upToDown);
        this.Calculate.startAnimation(upToDown);


        //banners
        this.banner1 = findViewById(R.id.banner1);
        this.banner2 = findViewById(R.id.banner2);
        this.banner3 = findViewById(R.id.banner3);
        this.banner4 = findViewById(R.id.banner4);


        this.banner1.startAnimation(this.upToDown);
        this.banner2.startAnimation(this.upToDown);
        this.banner3.startAnimation(downToUp);
        this.banner4.startAnimation(downToUp);


        examMode = new ExamMode(this);


        this.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                initCamera();
            }
        });

        this.Mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animScale);
                initVoiceRecognizer();
            }
        });

        this.ClearEquation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainXText = "";
                showEquation.setText("");
                backToOriginal(opposite);
            }
        });

        this.showEquation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,CalculatorActivity.class);
                i.putExtra("leftOver",showEquation.getText().toString());
                i.putExtra("savedFunctions","");
                startActivityForResult(i,CLASSIC_CODE);
            }
        });
//        this.showEquation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus&&focusListener!=0)
//                {
//                    Intent i = new Intent(MainActivity.this,CalculatorActivity.class);
//                    i.putExtra("leftOver",showEquation.getText().toString());
//                    i.putExtra("savedFunctions","");
//                    startActivityForResult(i,CLASSIC_CODE);
//                }
//                else if(focusListener==0)
//                {
//                    focusListener++;
//                }
//            }
//        });

        MainActivity.ExamModeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.ExamModeConfirm.setVisibility(View.GONE);
            }
        });

     //   this.showEquation.setShowSoftInputOnFocus(false);
        LoadHistory.LoadCurrentLauncher(this);
        LoadList.loadList(getBaseContext());
        LoadList.loadWindowRatios(this);
        LoadHistory.LoadHistory(getBaseContext());
        setOnClickListenerForCalculate();
        setOnClickListenerForMainX();
        setOnClickListenerForSave();
        setOnClickListenerForTools();
        setOnCLickListenerForWindow();
        setOnClickListenerForAnswer();
        setOnClickListenerForMainY();
        if(LoadHistory.CurrentLauncher.equals("Calculator"))
        {
            Intent intent4 = new Intent(this,CalculatorActivity.class);
            intent4.putExtra("leftOver","");
            intent4.putExtra("savedFunctions","");
            this.startActivityForResult(intent4,CLASSIC_CODE);
        }

        this.showEquation.setText("sin(x)");
        calculate();
    }

    private void initCamera()
    {
        if(!MainActivity.examMode.isACTIVATED_EXAM_MODE())
        {
            Intent i = new Intent(MainActivity.this,CameraActivity.class);
            startActivityForResult(i,CAMERA_CODE);
        }
        else
        {
            Toast.makeText(this,"Sorry, you can not use this feature during exam mode.",Toast.LENGTH_SHORT).show();
        }
    }

    private void initVoiceRecognizer()
    {
        if(!MainActivity.examMode.isACTIVATED_EXAM_MODE())
        {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say an expression...");


            try{
                startActivityForResult(i,REQ_CODE);
            }catch(ActivityNotFoundException a)
            {
                Toast.makeText(this,"Sorry, your device does not support voice input",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(this,"Sorry, you can not use this feature in exam mode.",Toast.LENGTH_SHORT).show();
        }

    }

    protected void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode)
        {
            case REQ_CODE:
                if(resultCode == RESULT_OK&&data!=null)
                {
                    ArrayList<String> voiceInText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    SmartMathText smt = new SmartMathText(voiceInText.get(0));
                    this.showEquation.setText(SmartParser.finalCheck(smt.getText(),false));
                    calculate();
                    MathFunctions.cleanBooleans();
                }
                break;

            case CAMERA_CODE:
                if(resultCode == RESULT_OK && data!=null)
                {
                    String d = data.getStringExtra("data");
                    SmartMathTextPicture smtp = new SmartMathTextPicture(d);
                    this.createCameraDialog(smtp.getText());
                }
                break;

            case CLASSIC_CODE:
                if(resultCode == RESULT_OK && data!=null)
                {
                    String text = data.getStringExtra("classicData");
                    this.showEquation.setText(text);
                    calculate();
                }
                break;

            case MAINX_CODE:
                if(resultCode == RESULT_OK && data!=null)
                {
                    String text = data.getStringExtra("classicData");
                    this.mainXText = text;
                    performCalculationsForY();
                    this.mainX.setText(cropLength(text));
                }
                break;
        }
    }

    private void performCalculationsForY()
    {
        if(LoadList.isAGoodFunction(this.mainXText,this))
        {
            LoadList.calculator.setEquation(this.mainXText);
            String newY = mainControl.getValue(LoadList.calculator.getValue(1))+"";
            mainY.setText(cropLength(newY));
            this.mainYText = newY;
        }
        else
        {
            this.mainY.setText("y");
        }
    }


    private String cropLength(String text)
    {
        if(text.length()>10)
        {
            return text.substring(0,10)+"...";
        }
        else
        {
            return text;
        }
    }





    private void createDrawerMenu()
    {
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_functions) {
            // Handle the camera action
            Intent i = new Intent(MainActivity.this,FunctionActivity.class);
            startActivity(i);
        }  else if (id == R.id.nav_tool) {
            Intent i = new Intent(MainActivity.this,Tools_FunctionSelection.class);
            startActivity(i);
        } else if (id == R.id.nav_exam_mode) {
            AudioManager am =
                    (AudioManager)getSystemService(Context.AUDIO_SERVICE);
            if(examMode.isACTIVATED_EXAM_MODE())
            {
                MainActivity.ExamModeConfirm.setVisibility(View.VISIBLE);
                MainActivity.ExamModeConfirm.startAnimation(downToUp);
            }
            else if(!am.isMusicActive())
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startLockTask();
                }
                else
                {
                    Toast.makeText(this,"Sorry your phone does not support Exam Mode.",Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(this,"sorry something is wrong at this moment. Make sure to turn off any music that is playing!",Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_lists) {
            Intent intent = new Intent(MainActivity.this,ShowListsActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_smart_room)
        {
            if(examMode.isACTIVATED_EXAM_MODE())
            {
                Toast.makeText(this,"Sorry, the assistant is not available at this moment!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent = new Intent(MainActivity.this,SmartRoom.class);
                startActivity(intent);
            }

        }
        else if(id == R.id.nav_window_ratios)
        {
            Intent intent = new Intent(MainActivity.this,WindowActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_launcher)
        {
            launcherDialog();
        }
        else if(id == R.id.clear_history)
        {
           this.createDialogToClearHistory();
        }
        else if(id == R.id.nav_report_bug)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FinalString.ReportBugLink));
            startActivity(intent);
        }
        else if(id == R.id.nav_help)
        {
            Intent intent  = new Intent(this,HelpActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_about)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FinalString.about));
            startActivity(intent);
        }
        else if(id == R.id.nav_privacypolicy)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FinalString.privacypolicy));
            startActivity(intent);
        }



        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    public void createCameraDialog(final String equation)
    {
        if(equation.length()>0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(String.format("Is this the equation %s?",equation))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            showEquation.setText(showEquation.getText()+equation);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            initCamera();
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create();

            builder.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage(String.format("Sorry we could not find an equation from the picture."))
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            initCamera();
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create();

            builder.show();
        }

    }

    private void setUpGraphLayout()
    {
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMaxX(10);
        graph.getViewport().setMinX(-10);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(10);
        graph.getViewport().setMinY(-10);
        graph.setBackgroundColor(Color.WHITE);

        graph.setTitleTextSize(55.0f);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Loading the graph...", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(MainActivity.this, SplashGraph.class);
                i.putExtra("position", showEquation.getText().toString());
                startActivity(i);
            }
        });
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            examMode.checkExamMode();
        }
        else if(examMode.isAppInLockTaskMode())
        {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

    private void setOnClickListenerForCalculate()
    {
        this.Calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });
    }

    private void calculate()
    {
        String functionText = MainActivity.this.showEquation.getText().toString();
        if (LoadList.isAGoodFunction(functionText, MainActivity.this)) {
            setUpMainControl(functionText);
            makeObjectsVisibleWithAnimation(downToUp);
            clearBoxCalculator();
        }
    }

    private void clearBoxCalculator()
    {
        this.mainX.setText("");
        this.mainY.setText("");
    }



    private void backToOriginal(Animation animation)
    {
        this.focusListener =0;
        if(this.save.getVisibility()==View.VISIBLE)
        {
            this.graph.setVisibility(View.INVISIBLE);
            this.window.setVisibility(View.INVISIBLE);
            this.save.setVisibility(View.INVISIBLE);
            this.tools.setVisibility(View.INVISIBLE);
            this.toolbar.setVisibility(View.INVISIBLE);
            this.boxCalculator.setVisibility(View.GONE);
            this.showAnswer.setVisibility(View.GONE);

            this.graph.startAnimation(animation);
            this.window.startAnimation(animation);
            this.save.startAnimation(animation);
            this.tools.startAnimation(animation);
            this.toolbar.startAnimation(animation);
            this.boxCalculator.startAnimation(animation);
            this.showAnswer.startAnimation(animation);

            this.showEquation.setText("sin(x)");
            calculate();
            this.showEquation.setText("");
        }





        //bring back the Banners
//        if(banner1.getVisibility()==View.INVISIBLE)
//        {
//            this.banner1.startAnimation(downToUp);
//            this.banner2.startAnimation(downToUp);
//            this.banner3.startAnimation(downToUp);
//            this.banner4.startAnimation(downToUp);
//
//            this.banner1.setVisibility(View.VISIBLE);
//            this.banner2.setVisibility(View.VISIBLE);
//            this.banner3.setVisibility(View.VISIBLE);
//            this.banner4.setVisibility(View.VISIBLE);
//        }


    }

    private void makeObjectsVisibleWithAnimation(Animation animation)
    {

        this.graph.setVisibility(View.VISIBLE);
        this.window.setVisibility(View.VISIBLE);
        this.save.setVisibility(View.VISIBLE);
        this.tools.setVisibility(View.VISIBLE);
        this.toolbar.setVisibility(View.VISIBLE);
        this.window.startAnimation(animation);
        this.save.startAnimation(animation);
        this.tools.startAnimation(animation);
        this.graph.startAnimation(animation);
        this.toolbar.startAnimation(animation);

        if(!mainControl.isAFunction())
        {
            double value = mainControl.getValue(1);
            LoadHistory.addHistory(value+"",MainActivity.this);
            LoadHistory.addHistory(mainControl.getFraction(),MainActivity.this);
            this.showAnswer.setText(value+"");
            this.showAnswer.setVisibility(View.VISIBLE);
            this.showAnswer.startAnimation(animation);
            this.boxCalculator.setVisibility(View.GONE);
        }
        else
        {
            this.boxCalculator.setVisibility(View.VISIBLE);
            this.boxCalculator.startAnimation(animation);
            this.showAnswer.setVisibility(View.GONE);
            this.showFunctionBox.setText(cropLength(mainControl.getEquation()));
        }

        //removeBanners
        if(banner1.getVisibility()==View.VISIBLE)
        {
            this.banner1.startAnimation(this.opposite);
            this.banner2.startAnimation(this.opposite);
            this.banner3.startAnimation(this.opposite);
            this.banner4.startAnimation(this.opposite);

            this.banner1.setVisibility(View.INVISIBLE);
            this.banner2.setVisibility(View.INVISIBLE);
            this.banner3.setVisibility(View.INVISIBLE);
            this.banner4.setVisibility(View.INVISIBLE);
        }
    }

    private void setUpMainControl(String function)
    {
        String formatFunction = String.format("Function|%s|false|false|5|0|10",function);
        this.mainControl = new MainControl(formatFunction,this);
        if(this.mainControl.isAFunction())
        {
            this.graph.getViewport().setYAxisBoundsManual(true);
            this.graph.getViewport().setMinY(-10);
            this.graph.getViewport().setMaxY(10);
            this.graph.setTitle(String.format("y = %s",cropLength(function)));
            this.graph.removeAllSeries();
            this.graph.addSeries(mainControl.getSeries());
            this.graph.addSeries(mainControl.getSeriesPrime());
            this.graph.refreshDrawableState();
            this.graph.invalidate();
        }
        else
        {
            this.graph.getViewport().setYAxisBoundsManual(false);
            this.graph.setTitle(String.format("y = %s",cropLength(function)));
            this.graph.removeAllSeries();
            this.graph.addSeries(mainControl.getSeries());
            this.graph.refreshDrawableState();
            this.graph.invalidate();
        }

    }


    private void setOnClickListenerForMainX()
    {
        this.mainX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,CalculatorActivity.class);
                i.putExtra("leftOver",mainXText);
                i.putExtra("savedFunctions","");
                i.putExtra("KEYMODE","1");
                startActivityForResult(i,MAINX_CODE);
            }
        });
//        this.mainX.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus)
//                {
//                    Intent i = new Intent(MainActivity.this,CalculatorActivity.class);
//                    i.putExtra("leftOver",mainXText);
//                    i.putExtra("savedFunctions","");
//                    i.putExtra("KEYMODE","1");
//                    startActivityForResult(i,MAINX_CODE);
//                }
//            }
//        });
    }


    private void setOnClickListenerForSave()
    {
        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showEquation.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"You need to enter an equation first",Toast.LENGTH_SHORT).show();
                }else {
                    createDialogToGetAName();
                }
            }
        });
    }

    public void createDialogToGetAName()
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View view1 = inflater.inflate(R.layout.save_require_name, null);
        builder.setView(view1);
        builder.setTitle("To save, enter a name please")
                .setPositiveButton("save", null)
                .setNegativeButton("Cancel", null);
        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        EditText text = view1.findViewById(R.id.enterANameSaveMain);
                        String name = Calculator.removeSpaces(text.getText().toString());
                        String function = showEquation.getText().toString();
                        if(LoadList.isAGoodName(name,MainActivity.this)&&LoadList.isAGoodFunction(function,MainActivity.this))
                        {
                            String text1 = String.format("%s|%s|false|false|5|0|10",name,function);
                            LoadList.saveFile(FileNames.fileName,LoadList.getList()+"&"+text1,MainActivity.this);
                            try{
                                FunctionCreator functionCreator = new FunctionCreator(text1);
                                MathFunctions.savedFunctions.add(functionCreator);
                                Function functionCal = new Function(functionCreator.getName(),functionCreator.getFunction());
                                LinkedFunctions.FUNCTIONS.add(functionCal);
                            }catch(Exception e)
                            {

                            }

                            Toast.makeText(MainActivity.this,String.format("Function '%s' was created",name),Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    public void createDialogToClearHistory()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to clear your history?")
                .setMessage("This completely clears the calculator history.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoadHistory.History.clear();
                        LoadHistory.addHistory("",MainActivity.this);
                        Toast.makeText(MainActivity.this,"History cleared!",Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No",null);
        builder.create();
        builder.show();
    }




    protected void setOnClickListenerForTools()
    {
        this.tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showEquation.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"You need to enter an equation first",Toast.LENGTH_SHORT).show();
                }else{
                    Intent i = new Intent(MainActivity.this,ToolsActivity.class);
                    i.putExtra("EQUATION",showEquation.getText().toString());
                    startActivity(i);
                }

            }
        });
    }


    protected void setOnCLickListenerForWindow()
    {
        this.window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,WindowActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void setOnClickListenerForAnswer()
    {
        this.showAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showAnswer.getText().toString().contains("/"))
                {
                    showAnswer.setText(mainControl.getSavedValue()+"");
                }
                else {
                    showAnswer.setText(mainControl.getFraction());
                }
            }
        });
    }




    private void setOnClickListenerForMainY()
    {
        this.mainY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mainY.getText().toString();
                if(text.contains("/")&&!text.isEmpty())
                {
                    mainY.setText(mainControl.getSavedValue()+"");
                }
                else if(!text.isEmpty())
                {
                    mainY.setText(mainControl.getFraction());
                }
            }
        });
        this.mainY.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    String text = mainY.getText().toString();
                    if(text.contains("/")&&!text.isEmpty())
                    {
                        mainY.setText(mainControl.getSavedValue()+"");
                    }
                    else if(!text.isEmpty())
                    {
                        mainY.setText(mainControl.getFraction());
                    }
                }
            }
        });
    }


    //LAUNCHER
    private void launcherDialog()
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(android.app.Activity.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.changecolorof_functions, null);
        builder.setView(view1);
        LinearLayout layout = view1.findViewById(R.id.linearLayoutColorChange);
        dialog = builder.create();

        Button button1 = new Button(this);
        button1.setText("Current Launcher: "+ LoadHistory.CurrentLauncher);
        button1.setBackgroundColor(ContextCompat.getColor(this,ColorsInJava.colors[5]));
        button1.setTextColor(Color.WHITE);
        button1.setGravity(Gravity.CENTER);
        button1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(button1);

        for(int i = 0;i<LoadHistory.Launchers.length;i++)
        {
            final Button button = new Button(this);
            button.setText(LoadHistory.Launchers[i]+"");
            button.setBackgroundColor(ContextCompat.getColor(this,ColorsInJava.colors[5]));
            button.setTextColor(Color.WHITE);
            button.setGravity(Gravity.CENTER);
            final int position = i;

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadHistory.saveLauncher(position,MainActivity.this);
                    dialog.dismiss();
                   Toast.makeText(MainActivity.this,"Launcher was saved. Next time the app will be opened in "+LoadHistory.CurrentLauncher,Toast.LENGTH_SHORT).show();
                }
            });
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.addView(button);
        }

        dialog.show();

    }










}
