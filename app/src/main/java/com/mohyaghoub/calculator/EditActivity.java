package com.mohyaghoub.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {


    private EditText name;
    private Button function;

    private Button save;
    private Button color;
    private Button style;
    private Button thickness;
    private Button clear;
    private Button voice;
    private Button camera;


    private int position;

    private int colorNumberSaved;
    private int styleNumberSaved;
    private int thicknessNumberSaved;

    private FunctionCreator functionCreator;


    private final int CAMERA_CODE = 1;
    private final int REQ_CODE = 2;
    private final int CLASSIC_CODE = 3;

    private Animation animScale;
    private Animation upToDown;
    private Animation downToUp;
    private Animation disappear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Bundle extras = getIntent().getExtras();
        this.position = Integer.parseInt(extras.getString("position"));
        initObjects();
        setOnClickListeners();
    }

    private void initObjects()
    {
        this.name = findViewById(R.id.nameEditActivity);
        this.function = findViewById(R.id.functionEditActivity);
        this.save = findViewById(R.id.saveEditActivity);
        this.color = findViewById(R.id.colorEditActivity);
        this.style = findViewById(R.id.styleEditActivity);
        this.thickness = findViewById(R.id.thicknessEditActivity);
        this.clear = findViewById(R.id.clearEditActivity);
        this.voice = findViewById(R.id.voicebtnEditActivity);
        this.camera = findViewById(R.id.camerabtnEditActivity);
        this.functionCreator = MathFunctions.savedFunctions.get(position);
        name.setText(functionCreator.getName());
        function.setText(functionCreator.getFunction());
        this.colorNumberSaved = functionCreator.getColor();
        this.styleNumberSaved = functionCreator.getStyle();
        this.thicknessNumberSaved = functionCreator.getThickness();


        this.animScale= AnimationUtils.loadAnimation(this,R.anim.scale);
        this.upToDown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        this.downToUp = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        this.disappear = AnimationUtils.loadAnimation(this,R.anim.disappear);

        Animate();
    }

    protected void Animate()
    {
        this.name.startAnimation(this.upToDown);
        this.function.startAnimation(this.upToDown);
        this.clear.startAnimation(this.upToDown);
        this.voice.startAnimation(this.upToDown);
        this.camera.startAnimation(this.upToDown);
        this.color.startAnimation(this.upToDown);

        this.style.startAnimation(this.downToUp);
        this.save.startAnimation(this.downToUp);
        this.thickness.startAnimation(this.downToUp);

    }

    private void setOnClickListeners()
    {
        this.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.this.name.setText("");
                EditActivity.this.function.setText("");
            }
        });

        this.voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.this.initVoiceRecognizer();
            }
        });

        this.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.this.initCamera();
            }
        });

        this.color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.this.initColorDialog();
            }
        });

        this.style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.this.initStyleDialog();
            }
        });

        this.thickness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.this.initThicknessDialog();
            }
        });

        changeTheInputTypeForFunction();
        saveTheChanges();
    }

    private void changeTheInputTypeForFunction()
    {
        //this.function.setShowSoftInputOnFocus(false);
        this.function.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    Intent intent = new Intent(EditActivity.this,CalculatorActivity.class);
                    intent.putExtra("leftOver",function.getText().toString());
                    intent.putExtra("savedFunctions",FinalString.sorryNoFunctionInEdit);
                    intent.putExtra("KEYMODE","2");
                    startActivityForResult(intent,CLASSIC_CODE);
                }
            }
        });
        this.function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditActivity.this,CalculatorActivity.class);
                intent.putExtra("leftOver",function.getText().toString());
                intent.putExtra("savedFunctions",FinalString.sorryNoFunctionInEdit);
                intent.putExtra("KEYMODE","2");
                startActivityForResult(intent,CLASSIC_CODE);
            }
        });
    }

    private void saveTheChanges()
    {
        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = Calculator.removeSpaces(name.getText().toString());
                String newFunction = function.getText().toString();
                if(isAGoodFunction(newFunction)&&isAGoodName(newName,functionCreator.getName()))
                {
                    functionCreator.setName(newName);
                    functionCreator.setFunction(newFunction);
                    functionCreator.setColor(colorNumberSaved);
                    functionCreator.setStyle(styleNumberSaved);
                    functionCreator.setThickness(thicknessNumberSaved);
                    Toast.makeText(EditActivity.this,"Changes were saved!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }







    //onActivity for results
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
                    this.createDialogForSpeech(SmartParser.finalCheck(smt.getText(),false));
                    MathFunctions.cleanBooleans();
                }
                break;

            case CAMERA_CODE:
                if(resultCode == RESULT_OK && data!=null)
                {
                    String d = data.getStringExtra("data");
                    SmartMathText smt = new SmartMathText(d);
                    SmartMathTextPicture smtp = new SmartMathTextPicture(smt.getText());
                    this.createCameraDialog(smtp.getText());
                }

                break;

            case CLASSIC_CODE:
                if(resultCode == RESULT_OK && data!=null)
                {
                    String text = data.getStringExtra("classicData");
                    this.function.setText(text);
                }
                break;




        }
    }

    //other activities
    public void createDialogForSpeech(final String equationToSpeeach)
    {
        if(equationToSpeeach.length()>0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
            builder.setMessage(String.format("Is this the equation %s?",equationToSpeeach))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            function.setText(function.getText()+equationToSpeeach);
                        }
                    })
                    .setNegativeButton("No, try again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            initVoiceRecognizer();
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create();

            builder.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
            builder.setMessage(String.format("Sorry we could not find an equation in your speech.",equationToSpeeach))
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    })
                    .setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            initVoiceRecognizer();
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create();

            builder.show();
        }

    }

    public void createCameraDialog(final String equation)
    {
        if(equation.length()>0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
            builder.setMessage(String.format("Is this the equation %s?",equation))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            function.setText(function.getText()+equation);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
            builder.setMessage(String.format("Sorry we could not find an equation from camera."))
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

    private void initCamera()
    {
        if(!MainActivity.examMode.isACTIVATED_EXAM_MODE())
        {
            Intent i = new Intent(EditActivity.this,CameraActivity.class);
            startActivityForResult(i,CAMERA_CODE);
        }
        else
        {
            Toast.makeText(this,"Sorry, you can not use this feature during exam mode.",Toast.LENGTH_SHORT).show();
        }
    }

    private void initVoiceRecognizer() {
        if (!MainActivity.examMode.isACTIVATED_EXAM_MODE()) {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say an expression...");


            try {
                startActivityForResult(i, REQ_CODE);
            } catch (ActivityNotFoundException a) {
                Toast.makeText(this, "Sorry, your device does not support voice input", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Sorry, you can not use this feature in exam mode.", Toast.LENGTH_SHORT).show();
        }
    }



        protected void initColorDialog()
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.changecolorof_functions, null);
        builder.setView(view1);
        LinearLayout layout = view1.findViewById(R.id.linearLayoutColorChange);
        dialog = builder.create();

        Button button1 = new Button(this);
        button1.setText("Current color ");
        button1.setBackgroundColor(ContextCompat.getColor(this,ColorsInJava.colors[functionCreator.getColor()]));
        button1.setTextColor(Color.WHITE);
        button1.setGravity(Gravity.CENTER);
        button1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(button1);

        for(int i = 0;i<ColorsInJava.colors.length;i++)
        {
            final Button button = new Button(this);
            final int colorNumber = i;
            button.setBackgroundColor(ContextCompat.getColor(this,ColorsInJava.colors[i]));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveColor(colorNumber);
                    dialog.dismiss();
                }
            });
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.addView(button);
        }

        dialog.show();
    }

    protected void initStyleDialog()
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.changecolorof_functions, null);
        builder.setView(view1);
        LinearLayout layout = view1.findViewById(R.id.linearLayoutColorChange);
        dialog = builder.create();

        Button button1 = new Button(this);
        button1.setText("Current style: "+ColorsInJava.nameOfTheEffects[functionCreator.getStyle()]);
        button1.setBackgroundColor(ContextCompat.getColor(this,ColorsInJava.colors[5]));
        button1.setTextColor(Color.WHITE);
        button1.setGravity(Gravity.CENTER);
        button1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(button1);

        for(int i = 0;i<ColorsInJava.pathEffects.length;i++)
        {
            final Button button = new Button(this);
            button.setText(ColorsInJava.nameOfTheEffects[i]);
            button.setBackgroundColor(ContextCompat.getColor(this,ColorsInJava.colors[5]));
            button.setTextColor(Color.WHITE);
            button.setGravity(Gravity.CENTER);
            final int styleNumber = i;

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveStyle(styleNumber);
                    dialog.dismiss();
                }
            });
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.addView(button);
        }

        dialog.show();
    }


    protected void initThicknessDialog()
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.changecolorof_functions, null);
        builder.setView(view1);
        LinearLayout layout = view1.findViewById(R.id.linearLayoutColorChange);
        dialog = builder.create();

        Button button1 = new Button(this);
        button1.setText("Current thickness: "+functionCreator.getThickness());
        button1.setBackgroundColor(ContextCompat.getColor(this,ColorsInJava.colors[5]));
        button1.setTextColor(Color.WHITE);
        button1.setGravity(Gravity.CENTER);
        button1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(button1);

        for(int i = 0;i<ColorsInJava.THICKNESS.length;i++)
        {
            final Button button = new Button(this);
            button.setText(ColorsInJava.THICKNESS[i]+"");
            button.setBackgroundColor(ContextCompat.getColor(this,ColorsInJava.colors[5]));
            button.setTextColor(Color.WHITE);
            button.setGravity(Gravity.CENTER);
            final int thicknessNumber = ColorsInJava.THICKNESS[i];

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveThickness(thicknessNumber);
                    dialog.dismiss();
                }
            });
            button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.addView(button);
        }

        dialog.show();
    }



    private void saveColor(int cn)
    {
        this.colorNumberSaved = cn;
        Toast.makeText(this,"Make sure to save the changes.",Toast.LENGTH_SHORT).show();
    }
    private void saveStyle(int sn)
    {
        this.styleNumberSaved = sn;
        Toast.makeText(this,"Make sure to save the changes.",Toast.LENGTH_SHORT).show();
    }
    private void saveThickness(int tn)
    {
        this.thicknessNumberSaved = tn;
        Toast.makeText(this,"Make sure to save the changes.",Toast.LENGTH_SHORT).show();
    }




    //checking if everything is alright
    private boolean isAGoodFunction(String function)
    {
        if(function.length()>0)
        {
            Calculator calculator = new Calculator();
            int qualified = 0;
            try {
                calculator.setEquation(function);
                qualified++;
            }catch(Exception e)
            {
                Toast.makeText(this,"Bad expression",Toast.LENGTH_LONG).show();
                qualified--;
            }
            try{
                double x = calculator.getValue(4);
                qualified++;
            }catch(Exception e)
            {
                qualified--;
            }

            return qualified == 2;
        }
        else{
            Toast.makeText(this,"You need to enter a function.",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean isAGoodName(String name,String currName)
    {
        if(LoadList.hasNumber(name))
        {
            Toast.makeText(EditActivity.this,"Names can not have numbers in them.",Toast.LENGTH_SHORT).show();
            return false;
        }
        for(FunctionCreator fc:MathFunctions.savedFunctions)
        {
            if(name.equals(fc.getName())&&!currName.equals(fc.getName()))
            {
                Toast.makeText(this,String.format("The name '%s' has been already used",fc.getName()),Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if(name.length()==0)
        {
            Toast.makeText(this,"You need to enter a name",Toast.LENGTH_LONG).show();
            return false;
        }
        else if(name.length()>=6)
        {
            Toast.makeText(this,"Name should be less than or equal to five characters",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
