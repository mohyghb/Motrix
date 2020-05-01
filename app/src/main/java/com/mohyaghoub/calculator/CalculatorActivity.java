package com.mohyaghoub.calculator;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class CalculatorActivity extends AppCompatActivity {


    public static String savedEquation;

    private Button b0;
    private Button b1;
    private Button b2;
    private Button b3;
    private Button b4;
    private Button b5;
    private Button b6;
    private Button b7;
    private Button b8;
    private Button b9;
    private Button b00;

    //operations
    private Button equal;
    private Button multiply;
    private Button divide;
    private Button add;
    private Button sub;
    private Button power;
    private Button percent;

    //breackets
    private Button openBracket;
    private Button closeBracket;
    private Button dot;

    //functions
    private Button sin;
    private Button cos;
    private Button tan;
    private Button log;
    private Button ln;
    private Button x;

    //del
    private Button showSavedFunctions;
    private Button delete;
    private Button second;


    private boolean secondIsOn;

    private EditText showText;
    private String savedFunctions;
    private PopupMenu popupMenu;

    private TextView mode;

    //just a keyboard with no x
    private int KeyboardMode;

    private  Bundle extras;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        CalculatorActivity.savedEquation = "";
        extras = getIntent().getExtras();
        initEditText(extras.getString("leftOver"));
        initKeyboardMode(extras);
        initNumbers();
        initOperations();
        initBrackets();
        initFunctions();
        initDelete();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initKeyboardMode(Bundle keyboardMode)
    {
        try{
            this.KeyboardMode = Integer.parseInt(keyboardMode.getString("KEYMODE"));
        }catch(Exception e)
        {
            this.KeyboardMode = 0;
        }
    }



    private void initDelete() {
        showSavedFunctions = findViewById(R.id.showSavedFunctions);
        delete = findViewById(R.id.delete);
        second = findViewById(R.id.second);

        showSavedFunctions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showSavedFunctions.getText().equals("FN"))
                {
                    initPopMenu();
                }
                else
                {
                    initHistory();
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(showText);
            }
        });

        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showText.setText("");
                return false;
            }
        });


        secondIsOn = true;
        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondOperations();
            }
        });
    }


    private void secondOperations() {
        if (this.secondIsOn == true) {
            this.secondIsOn = false;
            this.sin.setText("asin");
            this.cos.setText("acos");
            this.tan.setText("atan");
            this.ln.setText("e");
            this.b00.setText("π");
            this.power.setText("√");

            this.multiply.setText("sec(");
            this.divide.setText("csc(");
            this.add.setText("cot(");
            this.sub.setText("abs(");

            this.dot.setText("E");
            this.showSavedFunctions.setText("HIS");
            this.second.setText("1st");

        } else if (this.secondIsOn == false) {
            this.secondIsOn = true;
            this.sin.setText("sin");
            this.cos.setText("cos");
            this.tan.setText("tan");
            this.ln.setText("ln");
            this.b00.setText("c");
            this.power.setText("^");

            this.multiply.setText("*");
            this.divide.setText("/");
            this.add.setText("+");
            this.sub.setText("-");
            this.dot.setText(".");

            this.showSavedFunctions.setText("FN");
            this.second.setText("2nd");
        }
    }

    private void initFunctions() {
        sin = findViewById(R.id.sin);
        cos = findViewById(R.id.cos);
        tan = findViewById(R.id.tan);
        log = findViewById(R.id.log);
        ln = findViewById(R.id.ln);
        x = findViewById(R.id.variable);

        sin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, (sin.getText().toString() + "("), true);
            }
        });
        cos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, (cos.getText().toString() + "("), true);
            }
        });
        tan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, (tan.getText().toString() + "("), true);
            }
        });
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, (log.getText().toString() + "("), true);
            }
        });
        ln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ln.getText().toString().equals("ln")) {
                    addText(showText, (ln.getText().toString() + "("), true);
                } else {
                    addText(showText, (ln.getText().toString()), true);
                }
            }
        });
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CalculatorActivity.this.KeyboardMode!=1)
                {
                    addText(showText, (x.getText().toString()), true);
                }
                else
                {
                    Toast.makeText(CalculatorActivity.this,"Sorry you can not enter an expression in this mode.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initBrackets() {
        openBracket = findViewById(R.id.openbracket);
        closeBracket = findViewById(R.id.closebracket);
        dot = findViewById(R.id.point);

        openBracket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, openBracket.getText().toString(), true);
            }
        });
        closeBracket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, closeBracket.getText().toString(), true);
            }
        });
        dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, dot.getText().toString(), true);
            }
        });
    }

    private void initOperations() {
        equal = findViewById(R.id.equal);
        multiply = findViewById(R.id.multiply);
        divide = findViewById(R.id.divide);
        add = findViewById(R.id.add);
        sub = findViewById(R.id.sub);
        power = findViewById(R.id.power);
        percent = findViewById(R.id.percent);


        equal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = showText.getText().toString();
                Intent data = new Intent();
                data.putExtra("classicData", text);
                data.setData(Uri.parse(text));
                setResult(RESULT_OK, data);
                LoadHistory.addHistory(text,CalculatorActivity.this);
                CalculatorActivity.savedEquation = text;
                finish();
            }
        });

        multiply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, multiply.getText().toString(), true);
            }
        });
        divide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, divide.getText().toString(), true);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, add.getText().toString(), true);
            }
        });
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, sub.getText().toString(), true);
            }
        });
        power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!secondIsOn)
                {
                    addText(showText, power.getText().toString()+"(", true);
                }
                else
                {
                    addText(showText, power.getText().toString(), true);
                }

            }
        });
        percent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    addText(showText, percent.getText().toString(), true);
            }
        });
    }

    private void initEditText(String leftOver) {
        showText = findViewById(R.id.displyEquation);
//        showText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        showText.setTextIsSelectable(true);
        showText.setLongClickable(false);
//        showText.setRawInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // API 21
            showText.setShowSoftInputOnFocus(false);
        } else { // API 11-20
            showText.setTextIsSelectable(true);
        }
        showText.setText(leftOver);


        //show mode
        this.mode = findViewById(R.id.ModeCalculatorActivity);
        mode.setText(Modes.CALCULATOR_MODES[Modes.currentMode]);

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogForModeChange();
            }
        });
    }

    private void initNumbers() {
        b00 = findViewById(R.id.doublezero);
        b0 = findViewById(R.id.zero);
        b1 = findViewById(R.id.one);
        b2 = findViewById(R.id.two);
        b3 = findViewById(R.id.three);
        b4 = findViewById(R.id.four);
        b5 = findViewById(R.id.five);
        b6 = findViewById(R.id.six);
        b7 = findViewById(R.id.seven);
        b8 = findViewById(R.id.eight);
        b9 = findViewById(R.id.nine);

        b00.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(b00.getText().toString().equals("c"))
                {
                    showText.setText("");
                }
                else
                {
                    addText(showText, b00.getText().toString(), false);
                }

            }
        });

        b0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, b0.getText().toString(), false);
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, b1.getText().toString(), false);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, b2.getText().toString(), false);

            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, b3.getText().toString(), false);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, b4.getText().toString(), false);
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, b5.getText().toString(), false);
            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, b6.getText().toString(), false);
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, b7.getText().toString(), false);
            }
        });
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, b8.getText().toString(), false);
            }
        });
        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addText(showText, b9.getText().toString(), false);
            }
        });


    }



    public void addText(EditText t, String text, boolean isSpecial) {
       // char firstChar = text.charAt(0);

        if (isSpecial) {
                Spannable WordtoSpan = new SpannableString(text);
                WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                int start = Math.max(t.getSelectionStart(), 0);
                int end = Math.max(t.getSelectionEnd(), 0);
                t.getText().replace(Math.min(start, end), Math.max(start, end),
                        WordtoSpan, 0, WordtoSpan.length());
        } else {
                int start = Math.max(t.getSelectionStart(), 0);
                int end = Math.max(t.getSelectionEnd(), 0);
                t.getText().replace(Math.min(start, end), Math.max(start, end),
                        text, 0, text.length());
            }

    }
    public boolean isChar(char c)
    {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
    }

    public void remove(EditText t) {
            int start = Math.max(t.getSelectionStart(), 0);
            int end = Math.max(t.getSelectionEnd(), 0);

            String text = t.getText().toString();

            if (Math.min(start, end) - 1 >= 0) {
                if(text.substring(start-1,end).endsWith("("))
                {
                    int newStart = start-2;

                    if(newStart>0)
                    {
                        while(isChar(text.charAt(newStart))&&newStart>0)
                        {
                            newStart--;
                        }
                    }


                    if(newStart==0)
                    {
                        start = newStart;
                    }
                    else
                    {
                        start = newStart+1;
                    }


                    t.getText().replace(Math.min(start, end) , Math.max(start, end),
                            "", 0, 0);
                }
                else {
                    t.getText().replace(Math.min(start, end) - 1, Math.max(start, end),
                            "", 0, 0);
                }
            }




//        else if(Math.min(start,end)+Math.max(start,end)==t.getText().length())
//        {
//            t.setText("");
//        }

    }

    public void initSavedFunctions(String sf) {
        if(sf.equals(""))
        {
            this.savedFunctions = LoadList.getNameOfSavedFunctions();
        }
        else
        {
            this.savedFunctions = sf;
        }
    }


    public void initPopMenu() {
        initSavedFunctions(extras.getString("savedFunctions"));
        if (savedFunctions.equals(FinalString.sorryNoFunctionInEdit)) {
            Toast.makeText(CalculatorActivity.this, FinalString.sorryNoFunctionInEdit, Toast.LENGTH_LONG).show();
        } else if (savedFunctions.length() > 0) {
            String sepFunc[] = savedFunctions.split("\\|");
            popupMenu = new PopupMenu(CalculatorActivity.this, this.showSavedFunctions);
            //  int con= 0;
            for (int i = 0; i < sepFunc.length; i++) {
                if (sepFunc[i].length() > 0) {
                    popupMenu.getMenu().add(sepFunc[i])
                            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    CalculatorActivity.this.addText(showText, item.getTitle().toString() + "(", true);
                                    return false;
                                }
                            });
                }
                //    con++;
            }
            popupMenu.show();
            //Toast.makeText(CalculatorActivity.this,con+"",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(CalculatorActivity.this, "No saved functions detected.", Toast.LENGTH_LONG).show();
        }


    }

    public void initHistory()
    {
        if(KeyboardMode!=2)
        {
            String data = LoadHistory.getHistory();
            if(!data.equals(""))
            {
                String sepHis[] = data.split("\\|");
                popupMenu = new PopupMenu(CalculatorActivity.this, this.showSavedFunctions);
                //  int con= 0;
                for (int i = sepHis.length-1; i >= 0; i--) {
                    if (sepHis[i].length() > 0) {
                        popupMenu.getMenu().add(sepHis[i])
                                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                        CalculatorActivity.this.addText(showText, item.getTitle().toString() , true);
                                        return false;
                                    }
                                });
                    }
                    //    con++;
                }
                popupMenu.show();
            } else {
                Toast.makeText(CalculatorActivity.this, FinalString.noHistoryDetected, Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(this,"Sorry you can not use history button in the edit section.",Toast.LENGTH_SHORT).show();
        }

    }


    public void createDialogForModeChange() {

        final PopupMenu popupMenu = new PopupMenu(CalculatorActivity.this, this.mode);
        //  int con= 0;
        for (int i = 0; i < Modes.CALCULATOR_MODES.length; i++) {
            final int position = i;
            popupMenu.getMenu().add(Modes.CALCULATOR_MODES[i])
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Modes.currentMode = position;
                            CalculatorActivity.this.mode.setText(Modes.CALCULATOR_MODES[Modes.currentMode]);
                            return false;
                        }
                    });
        }
        popupMenu.show();
        //Toast.makeText(CalculatorActivity.this,con+"",Toast.LENGTH_LONG).show();
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


