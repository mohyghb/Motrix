package com.mohyaghoub.calculator;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ToolsActivity extends AppCompatActivity {

    private Button newtonMethodBtn;
    private Button simpsonMethodBtn;
    private Button trapezoidMethodBtn;
    private Button RAMBtn;
    private Button fibonacciBtn;
    private Button ycalculator;
    private Button equationBtn;
    private Button isIntegralBtn;

    private ScrollView scrollView;

    private TextView functionEquation;

    //Animations
    private Animation upToDown;
    private Animation appear;
    private Animation downToUp;
    private Animation disappear;

    private Bundle extras;
    private Calculator calculator;


    // newton layout
    private ScrollView NewtonMethodLayout;
    private EditText NewtonMethodInput;
    private TextView NewtonMethodResults;
    private Button BackNewtonMethod;
    private final int NEWTON_CALCULATOR_CODE = 2348;

    //simpson layout
    private ScrollView SimpsonLayout;
    private EditText LeftInputSimpson;
    private EditText RightInputSimpson;
    private EditText SubintervalsSimpson;
    private TextView SimpsonResults;
    private Button BackSimpson;
    private Button CalculateSimpson;
    private int focusListener;
    private int modeChanger;

    private double leftBound;
    private double rightBound;
    private int subintervals;

    private final int CALCULATOR_LEFT = 2322;
    private final int CALCULATOR_RIGHT = 23202;
    private final int CALCULATOR_SUBINTERVALS = 322;
    private final int SIMPSON_CODE = 1;
    private final int TRAPEZOID_CODE = 2;
    private final int RAM_CODE = 3;



    //fibonacci layout
    private ScrollView FibonacciLayout;
    private EditText FibonacciInput;
    private TextView FibonacciResults;
    private Button BackFibonacci;
    private final int FIBONACCI_CODE = 1123;


    //Value Layout
    private ScrollView ValueLayout;
    private Button ValueInput;
    private Button ValueBack;
    private TextView ValueResults;
    private final int VALUEINPUT_CODE = 1002;


    //Equation layout
    private ScrollView EquationLayout;
    private Button EquationInput;
    private Button EquationBack;
    private TextView EquationResults;
    private final int EQUATION_CODE = 67;

    private int CurrentEquationCode;
    private final int ISDERIVATIVE_CODE = 54;
    private final int ISINTEGRAL_CODE = 55;


    private boolean isAlreadyIn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        initClass();
    }

    private void initClass()
    {
        extras = getIntent().getExtras();
        initObjects();
        initAnimations();
        setUpFunction();
        AnimationForScrollView(this.upToDown,View.VISIBLE);
    }
    private void initObjects()
    {
        this.isAlreadyIn = false;
        this.newtonMethodBtn = findViewById(R.id.NEWTONMETHOD_TOOLS);
        this.simpsonMethodBtn = findViewById(R.id.SIMPSONMETHOD_TOOLS);
        this.RAMBtn = findViewById(R.id.RAM_TOOLS);
        this.trapezoidMethodBtn = findViewById(R.id.TRAPEOIDMETHOD_TOOLS);
        this.fibonacciBtn = findViewById(R.id.FIBONACCI_TOOLS);
        this.scrollView = findViewById(R.id.scrollViewTools);
        this.functionEquation = findViewById(R.id.functionEquationTools);
        this.ycalculator = findViewById(R.id.YCALCULATOR_TOOLS);
        this.equationBtn = findViewById(R.id.ISDERIVATIVE);
        this.isIntegralBtn = findViewById(R.id.ISINTEGRAL);


        initNewtonMethodLayout();
        initSimpsonLayout();
        initFibonacciLayout();
        initValueLayout();
        initEquationLayout();
        this.calculator = new Calculator();


        setOnClickListenerForNewtonsMethod();
        setOnClickListenerForSimpsonButton();
        setOnClickListenerForTrapezoidButton();
        setOnClickListenerForRAM();
        setOnClickListenerForFibonacci();
        setOnClickListenerForYcalculator();
        setOnClickListenerForEquation();
    }

    protected void initEquationLayout()
    {
        EquationLayout = findViewById(R.id.EquationLayout);
        EquationInput = findViewById(R.id.EquationInput);
        EquationBack = findViewById(R.id.EquationBack);
        EquationResults = findViewById(R.id.EquationResults);

        EquationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveView(EquationLayout);
                AnimationForScrollView(appear,View.VISIBLE);
                isAlreadyIn = false;
            }
        });
        EquationInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = EquationInput.getText().toString();
                Intent intent = new Intent(ToolsActivity.this,CalculatorActivity.class);
                intent.putExtra("leftOver",text);
                intent.putExtra("savedFunctions","");
                startActivityForResult(intent,EQUATION_CODE);
            }
        });

    }

    private void checkDerivative(String equation)
    {
        if(LoadList.isAGoodFunction(equation,this))
        {
            LoadList.calculator.setEquation(equation);

            double actualYPrime;
            double guessYPrime;
            boolean brokeLoop = false;
            for(int x = 0;x<100;x++)
            {
                try {
                    actualYPrime = calculator.getNumericalDerivative(x);
                    guessYPrime = LoadList.calculator.getValue(x);
                    if(!calculator.differenceIsLowTolerance(actualYPrime,guessYPrime))
                    {
                        EquationResults.setText("False");
                        brokeLoop = true;
                        break;
                    }
                }catch(Exception e)
                {

                }
            }
            if(brokeLoop == false)
            {
                EquationResults.setText("True");
            }
            EquationResults.setVisibility(View.VISIBLE);
        }
    }


    private void checkIntegral(String equation)
    {
        if(LoadList.isAGoodFunction(equation,this))
        {
            LoadList.calculator.setEquation(equation);

            double actualYPrime;
            double guessYPrime;
            boolean brokeLoop = false;
            for(int x = 0;x<100;x++)
            {
                try {
                    actualYPrime = calculator.getValue(x);
                    guessYPrime = LoadList.calculator.getNumericalDerivative(x);
                    if(!calculator.differenceIsLowTolerance(actualYPrime,guessYPrime))
                    {
                        EquationResults.setText("False");
                        brokeLoop = true;
                        break;
                    }
                }catch(Exception e)
                {

                }
            }
            if(brokeLoop == false)
            {
                EquationResults.setText("True");
            }
            EquationResults.setVisibility(View.VISIBLE);
        }
    }


    protected void initValueLayout()
    {
        ValueLayout = findViewById(R.id.ValueLayout);
        ValueInput = findViewById(R.id.ValueInput);
        ValueBack = findViewById(R.id.ValueBack);
        ValueResults = findViewById(R.id.ValueResults);

        ValueBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveView(ValueLayout);
                AnimationForScrollView(appear,View.VISIBLE);
                isAlreadyIn = false;
            }
        });

        ValueInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(VALUEINPUT_CODE,ValueInput.getText().toString());
            }
        });
    }

    protected void calculateValue(String equation)
    {
        if(LoadList.isAGoodFunction(equation,this))
        {
            LoadList.calculator.setEquation(equation);
            double valueOfX = LoadList.calculator.getValue(1);

            String values = String.format("x = %f\ny = %f\ny' = %f\ny'' = %f",valueOfX,calculator.getValue(valueOfX),calculator.getNumericalDerivative(valueOfX),calculator.getNumericalDerivativeSecond(valueOfX));
            ValueResults.setText(values);
            ValueResults.setVisibility(View.VISIBLE);
        }
    }


    protected void initFibonacciLayout()
    {
        this.FibonacciLayout = findViewById(R.id.FibonacciLayout);
        this.FibonacciInput = findViewById(R.id.FibonacciInput);
        this.FibonacciResults = findViewById(R.id.fibonacciResults);
        this.BackFibonacci = findViewById(R.id.BackFibonacci);
        this.FibonacciInput.setShowSoftInputOnFocus(false);

        this.BackFibonacci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveView(FibonacciLayout);
                AnimationForScrollView(appear,View.VISIBLE);
                isAlreadyIn = false;
            }
        });

        this.FibonacciInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(FIBONACCI_CODE,FibonacciInput.getText().toString());
            }
        });
    }

    protected void doNthFibonacci()
    {
        String equation = this.FibonacciInput.getText().toString();
        if(LoadList.isAGoodFunction(equation,this))
        {
            LoadList.calculator.setEquation(equation);
            this.FibonacciResults.setText(calculator.nthFibonacci((int)LoadList.calculator.getValue(1)));
        }
    }


    protected void initNewtonMethodLayout()
    {
        this.NewtonMethodLayout = findViewById(R.id.NewtonMethodLayout);
        this.NewtonMethodInput = findViewById(R.id.EnterAValueTools);
        this.NewtonMethodInput.setShowSoftInputOnFocus(false);
        this.NewtonMethodResults = findViewById(R.id.NewtonTextView);
        this.BackNewtonMethod = findViewById(R.id.backNewtonMethod);

        this.NewtonMethodInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(NEWTON_CALCULATOR_CODE,NewtonMethodInput.getText().toString());
            }
        });
        this.BackNewtonMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveView(NewtonMethodLayout);
                AnimationForScrollView(appear,View.VISIBLE);
                isAlreadyIn = false;
            }
        });
    }

    protected void doNewtonMethodShowWork()
    {
        String equation = this.NewtonMethodInput.getText().toString();
        if(LoadList.isAGoodFunction(equation,this))
        {
            LoadList.calculator.setEquation(equation);
            double valueOfEnteredEquation = LoadList.calculator.getValue(1);
            if(this.calculator.NM(valueOfEnteredEquation)!=Calculator.ERRORCODE)
            {
                //perform newton and show the work
                calculator.NNM(valueOfEnteredEquation,this.NewtonMethodResults);
            }else
            {
                Toast.makeText(this,"Your initial guess gives us an error. Change it and try again!",Toast.LENGTH_LONG).show();
            }
        }

    }

    protected void initSimpsonLayout()
    {
        SimpsonLayout = findViewById(R.id.SimpsonLayout);
        LeftInputSimpson = findViewById(R.id.LeftInputSimpsonTools);
        RightInputSimpson = findViewById(R.id.RightInputSimpsonTools);
        SubintervalsSimpson = findViewById(R.id.SubintervalsSimpsonTools);
        SimpsonResults = findViewById(R.id.SimpsonResults);
        BackSimpson = findViewById(R.id.BackSimpsonTools);
        CalculateSimpson = findViewById(R.id.CalculateSimpson);

        LeftInputSimpson.setShowSoftInputOnFocus(false);
        RightInputSimpson.setShowSoftInputOnFocus(false);
        SubintervalsSimpson.setShowSoftInputOnFocus(false);
        focusListener = 0;
        modeChanger = 0;


        this.BackSimpson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveView(SimpsonLayout);
                AnimationForScrollView(appear,View.VISIBLE);
                isAlreadyIn = false;
            }
        });

        this.LeftInputSimpson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(CALCULATOR_LEFT,LeftInputSimpson.getText().toString());
                focusListener++;
            }
        });

        this.LeftInputSimpson.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus&&focusListener!=0)
                {
                    startCalculatorActivity(CALCULATOR_LEFT,LeftInputSimpson.getText().toString());
                }
            }
        });

        this.RightInputSimpson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(CALCULATOR_RIGHT,RightInputSimpson.getText().toString());
            }
        });

        this.RightInputSimpson.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    startCalculatorActivity(CALCULATOR_RIGHT,RightInputSimpson.getText().toString());
                    focusListener++;
                }
            }
        });

        this.SubintervalsSimpson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(CALCULATOR_SUBINTERVALS,SubintervalsSimpson.getText().toString());
            }
        });

        this.SubintervalsSimpson.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    startCalculatorActivity(CALCULATOR_SUBINTERVALS,SubintervalsSimpson.getText().toString());
                    focusListener++;
                }
            }
        });





        this.CalculateSimpson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValueOfTheLeftBound();
                switch (ToolsActivity.this.modeChanger)
                {
                    case SIMPSON_CODE:
                        if(leftBound>rightBound)
                        {
                            SimpsonResults.setText(calculator.simpsonRuleShowWork(rightBound,leftBound,subintervals,true));
                        }
                        else
                        {
                            SimpsonResults.setText(calculator.simpsonRuleShowWork(leftBound,rightBound,subintervals,false));
                        }

                        SimpsonResults.setVisibility(View.VISIBLE);
                        break;

                    case TRAPEZOID_CODE:
                        if(leftBound>rightBound)
                        {
                            SimpsonResults.setText(calculator.trapezoidRuleShowWork(rightBound,leftBound,subintervals,true));
                        }
                        else
                        {
                            SimpsonResults.setText(calculator.trapezoidRuleShowWork(leftBound,rightBound,subintervals,false));
                        }
                        SimpsonResults.setVisibility(View.VISIBLE);
                        break;

                    case RAM_CODE:
                        if(leftBound>rightBound)
                        {
                            SimpsonResults.setText(calculator.getRAMS(rightBound,leftBound,subintervals));
                        }
                        else
                        {
                            SimpsonResults.setText(calculator.getRAMS(leftBound,rightBound,subintervals));
                        }
                        SimpsonResults.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });




    }

    protected void getValueOfTheLeftBound()
    {
        String equation = this.LeftInputSimpson.getText().toString();
        if(LoadList.isAGoodFunction(equation,this))
        {
           this.LeftInputSimpson.setTextColor(Color.WHITE);
           LoadList.calculator.setEquation(equation);
           this.leftBound = LoadList.calculator.getValue(1);
           getValueOfTheRightBound();
        }
        else
        {
            this.LeftInputSimpson.setTextColor(Color.RED);
        }
    }

    protected void getValueOfTheRightBound()
    {
        String equation = this.RightInputSimpson.getText().toString();
        if(LoadList.isAGoodFunction(equation,this))
        {
            this.RightInputSimpson.setTextColor(Color.WHITE);
            LoadList.calculator.setEquation(equation);
            this.rightBound = LoadList.calculator.getValue(1);
            getValueOfTheSubintervals();
        }
        else
        {
            this.RightInputSimpson.setTextColor(Color.RED);
        }
    }

    protected void getValueOfTheSubintervals()
    {
        String equation = this.SubintervalsSimpson.getText().toString();
        if(LoadList.isAGoodFunction(equation,this))
        {
            this.SubintervalsSimpson.setTextColor(Color.WHITE);
            LoadList.calculator.setEquation(equation);
            this.subintervals = (int)LoadList.calculator.getValue(1);
            if(subintervals>10000)
            {
                this.subintervals = 10000;
            }
        }
        else
        {
            this.SubintervalsSimpson.setTextColor(Color.RED);
        }
    }








    private void setUpFunction()
    {
        int position;
        String ext = extras.getString("EQUATION");
        try{
            position = Integer.parseInt(ext);
            String function =MathFunctions.savedFunctions.get(position).getFunction();
            functionEquation.setText(String.format("y = %s",function));
            calculator.setEquation(function);
        }catch(Exception e)
        {
            String function = ext;
            functionEquation.setText(String.format("y = %s",function));
            calculator.setEquation(function);
        }
        functionEquation.startAnimation(this.upToDown);
    }

    private void startCalculatorActivity(int code,String text)
    {
        Intent intent = new Intent(ToolsActivity.this,CalculatorActivity.class);
        intent.putExtra("leftOver",text);
        intent.putExtra("savedFunctions","");
        intent.putExtra("KEYMODE","1");
        startActivityForResult(intent,code);
    }


    private void initAnimations()
    {
        this.upToDown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        this.appear = AnimationUtils.loadAnimation(this,R.anim.appear);
        this.downToUp = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        this.disappear = AnimationUtils.loadAnimation(this,R.anim.disappear);
    }

    private void AnimationForScrollView(Animation animation, int visibility)
    {
        this.scrollView.setVisibility(visibility);
        this.scrollView.startAnimation(animation);
    }

    private void RemoveView(View view)
    {
        view.setVisibility(View.INVISIBLE);
        view.startAnimation(this.disappear);
    }

    private void AddView(View view)
    {
        view.setVisibility(View.VISIBLE);
        view.startAnimation(this.appear);
    }



    private void setOnClickListenerForNewtonsMethod()
    {
        this.newtonMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAlreadyIn)
                {
                    isAlreadyIn = true;
                    AnimationForScrollView(disappear,View.INVISIBLE);
                    AddView(NewtonMethodLayout);
                }
            }
        });

    }
    private void setOnClickListenerForSimpsonButton()
    {
        this.simpsonMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAlreadyIn)
                {
                    isAlreadyIn = true;
                    focusListener = 0;
                    modeChanger = SIMPSON_CODE;
                    SimpsonResults.setText("");
                    AnimationForScrollView(disappear,View.INVISIBLE);
                    AddView(SimpsonLayout);
                }

            }
        });
    }

    private void setOnClickListenerForTrapezoidButton()
    {
        this.trapezoidMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAlreadyIn)
                {
                    isAlreadyIn = true;
                    focusListener = 0;
                    modeChanger = TRAPEZOID_CODE;
                    SimpsonResults.setText("");
                    AnimationForScrollView(disappear,View.INVISIBLE);
                    AddView(SimpsonLayout);
                }

            }
        });
    }

    private void setOnClickListenerForRAM()
    {
        this.RAMBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAlreadyIn)
                {
                    isAlreadyIn = true;
                    focusListener = 0;
                    modeChanger = RAM_CODE;
                    SimpsonResults.setText("");
                    AnimationForScrollView(disappear,View.INVISIBLE);
                    AddView(SimpsonLayout);
                }

            }
        });
    }

    private void setOnClickListenerForFibonacci()
    {
        this.fibonacciBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAlreadyIn)
                {
                    isAlreadyIn = true;
                    AnimationForScrollView(disappear,View.INVISIBLE);
                    AddView(FibonacciLayout);
                }

            }
        });
    }

    private void setOnClickListenerForYcalculator()
    {
        this.ycalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAlreadyIn)
                {
                    isAlreadyIn = true;
                    AnimationForScrollView(disappear,View.INVISIBLE);
                    AddView(ValueLayout);
                }

            }
        });
    }
    private void setOnClickListenerForEquation()
    {
        this.equationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAlreadyIn)
                {
                    isAlreadyIn = true;
                    CurrentEquationCode = ISDERIVATIVE_CODE;
                    EquationResults.setText("");
                    AnimationForScrollView(disappear,View.INVISIBLE);
                    AddView(EquationLayout);
                }

            }
        });

        this.isIntegralBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAlreadyIn)
                {
                    isAlreadyIn = true;
                    CurrentEquationCode = ISINTEGRAL_CODE;
                    EquationResults.setText("");
                    AnimationForScrollView(disappear,View.INVISIBLE);
                    AddView(EquationLayout);
                }

            }
        });
    }







    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case NEWTON_CALCULATOR_CODE:
                    String text = data.getStringExtra("classicData");
                    this.NewtonMethodInput.setText(text);
                    doNewtonMethodShowWork();
                    this.AddView(this.NewtonMethodResults);
                    break;

                case CALCULATOR_LEFT:
                    String text1 = data.getStringExtra("classicData");
                    this.LeftInputSimpson.setText(text1);

                    break;


                case CALCULATOR_RIGHT:
                    String text2 = data.getStringExtra("classicData");
                    this.RightInputSimpson.setText(text2);
                    break;


                case CALCULATOR_SUBINTERVALS:
                    String text3 = data.getStringExtra("classicData");
                    this.SubintervalsSimpson.setText(text3);
                    break;

                case FIBONACCI_CODE:
                    String text4 = data.getStringExtra("classicData");
                    this.FibonacciInput.setText(text4);
                    doNthFibonacci();
                    this.FibonacciResults.setVisibility(View.VISIBLE);
                    break;

                case VALUEINPUT_CODE:
                    String text5 = data.getStringExtra("classicData");
                    this.ValueInput.setText(text5);
                    calculateValue(text5);
                    break;

                case EQUATION_CODE:
                    String text6 = data.getStringExtra("classicData");
                    this.EquationInput.setText(text6);
                    switch (this.CurrentEquationCode)
                    {
                        case ISDERIVATIVE_CODE:
                            checkDerivative(text6);
                            break;

                        case ISINTEGRAL_CODE:
                            checkIntegral(text6);
                            break;
                    }

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
