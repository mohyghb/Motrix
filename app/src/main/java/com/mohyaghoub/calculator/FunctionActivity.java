package com.mohyaghoub.calculator;


import android.app.AlertDialog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.speech.RecognizerIntent;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;


import java.util.ArrayList;
import java.util.Locale;


public class FunctionActivity extends AppCompatActivity {

//    private ArrayList<FunctionCreator> functions;
    private ArrayAdapter<FunctionCreator> functionCreatorArrayAdapter;
//    private final static String fileName = "savedFunctions";
//    final static String windowRatioFileName = "windowRatios";

    private ConstraintLayout newFunctionLayout;
    private Button save;
    private EditText name;
    private Button function;
    private Button clearNameAndFunction;

//    private Toolbar toolbar;
    private Button camera;
    private Button voice;
    private Button backFunctionActivity;

    private FloatingActionButton floatingADDFUNCTION_ACTIVITY;

    private ListView listView;
    private TextView emptyText;



    private final int CAMERA_CODE = 10;
    private final int REQ_CODE = 11;
    private final int CLASSIC_CODE = 12;


    private Animation upToDown;
    private Animation appear;
    private Animation downToUp;
    private Animation disappear;

    private Animation go_down;

    private int oldFirstVisibleItem = -1;
    protected int oldTop = -1;
    // you can change this value (pixel)
    private static final int MAX_SCROLL_DIFF = 3;



//    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        initObjects();
        initListView();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }





    @Override
    public void onBackPressed()
    {
        LoadList.saveFile(FileNames.fileName,LoadList.getList(),this);
        finish();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        try{
            functionCreatorArrayAdapter.notifyDataSetChanged();
        }catch(Exception e)
        {
            Toast.makeText(this,"Something went wrong with loading the list.",Toast.LENGTH_LONG).show();
        }
        LoadList.saveFile(FileNames.fileName,LoadList.getList(),this);
        closeKeyboard();
    }







    public void initObjects()
    {
        this.save = findViewById(R.id.enterinformation);
        this.name = findViewById(R.id.nameText);
        this.function = findViewById(R.id.functionText);
        this.clearNameAndFunction = findViewById(R.id.clearFunctionandName);
        this.camera = findViewById(R.id.cffa);
        this.voice = findViewById(R.id.vffa);
        this.newFunctionLayout = findViewById(R.id.newFunctionBox);
        this.backFunctionActivity = findViewById(R.id.BackFunctionActivity);

        this.floatingADDFUNCTION_ACTIVITY = findViewById(R.id.floatingADDFUNCTION_ACTIVITY);

        this.upToDown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        this.appear = AnimationUtils.loadAnimation(this,R.anim.appear);
        this.downToUp = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        this.go_down = AnimationUtils.loadAnimation(this,R.anim.go_down);
        this.disappear = AnimationUtils.loadAnimation(this,R.anim.disappear);

        Appear(floatingADDFUNCTION_ACTIVITY,downToUp);

        this.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                String nameWithoutSpace = Calculator.removeSpaces(name.getText().toString());
                if(isAGoodName(nameWithoutSpace)&&isAGoodFunction(function.getText().toString()))
                {
                    String text = String.format("%s|%s|false|false|5|0|10",nameWithoutSpace,function.getText().toString());
                    LoadList.saveFile(FileNames.fileName,LoadList.getList()+"&"+text,FunctionActivity.this);
                    try{
                        FunctionCreator functionCreator = new FunctionCreator(text);
                        functionCreatorArrayAdapter.add(functionCreator);
                        Function function = new Function(functionCreator.getName(),functionCreator.getFunction());
                        LinkedFunctions.FUNCTIONS.add(function);
                    }catch(Exception e)
                    {

                    }

                    Appear(floatingADDFUNCTION_ACTIVITY,appear);
                    showAndHide(listView,newFunctionLayout);

                    Toast.makeText(FunctionActivity.this,String.format("Function '%s' was created",name.getText()),Toast.LENGTH_LONG).show();
                    name.setText("");
                    function.setText("");
                }

            }
        });

        this.clearNameAndFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setText("");
                function.setText("");
            }
        });

        this.camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCamera();
            }
        });
        this.voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initVoiceRecognizer();
            }
        });

        this.function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this,CalculatorActivity.class);
                intent.putExtra("leftOver",function.getText().toString());
                intent.putExtra("savedFunctions","");
                startActivityForResult(intent,CLASSIC_CODE);
            }
        });


        this.backFunctionActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appear(floatingADDFUNCTION_ACTIVITY,downToUp);
                showAndHide(listView,newFunctionLayout);

            }
        });

        this.floatingADDFUNCTION_ACTIVITY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newFunctionLayout.getVisibility()!=View.VISIBLE)
                {

                    Disappear(listView);
                    DisappearCustom(v,disappear);
                    Appear(newFunctionLayout,downToUp);
                    if(emptyText.getVisibility()==View.VISIBLE)
                    {
                        Disappear(emptyText);
                    }
                }
                else
                {
                    Toast.makeText(FunctionActivity.this,"You can add a function by creating one here!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void showAndHide(View show,View hide)
    {
        hide.startAnimation(this.go_down);
        hide.setVisibility(View.INVISIBLE);
        show.startAnimation(this.upToDown);
        show.setVisibility(View.VISIBLE);
        EmptyText();
    }

    private void Appear(View view,Animation animation)
    {
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    private void Disappear(View view)
    {
        view.startAnimation(this.go_down);
        view.setVisibility(View.INVISIBLE);
    }

    private void DisappearCustom(View view,Animation animation)
    {
        view.startAnimation(animation);
        view.setVisibility(View.INVISIBLE);
    }







    private boolean isAGoodFunction(String function)
    {
        if(function.length()>40)
        {
            Toast.makeText(this,"Maximum length of function exceeded.",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(function.length()>0)
        {
            Calculator calculator = new Calculator();
            int qualified = 0;
            try {
                calculator.setEquation(function);
                qualified++;
            }catch(Exception e)
            {
                Toast.makeText(FunctionActivity.this,"Bad expression",Toast.LENGTH_LONG).show();
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
            createDialog("You need to enter a function.");
            return false;
        }
    }

    private boolean isAGoodName(String name)
    {
        String name1 = name.toLowerCase();
        if(hasNumber(name1))
        {
            Toast.makeText(FunctionActivity.this,"Names can not have numbers in them.",Toast.LENGTH_SHORT).show();
            return false;
        }
        for(FunctionCreator fc:MathFunctions.savedFunctions)
        {
            if(name1.equals(fc.getName()))
            {
                this.createDialog(String.format("The name '%s' has been already used",fc.getName()));
                return false;
            }
        }
        if(name.length()==0)
        {
            this.createDialog("You need to enter a name");
            return false;
        }
        else if(name.length()>=6)
        {
            this.createDialog("Name should be less than or equal to five characters");
            return false;
        }
        return true;
    }

    public static boolean hasNumber(String equation)
    {
        if(equation.isEmpty())
        {
            return false;
        }
        else if(((equation.charAt(0)>=48&&equation.charAt(0)<=57)||(equation.charAt(0)=='-'||equation.charAt(0)=='+')))
        {
            return true;
        }
        return hasNumber(equation.substring(1));
    }


    public void initListView()
    {
        try {
            functionCreatorArrayAdapter = new FunctionListView(FunctionActivity.this, 0,MathFunctions.savedFunctions);
            listView = findViewById(R.id.listView);
            listView.setAdapter(functionCreatorArrayAdapter);
            listView.startAnimation(this.upToDown);
            emptyText = findViewById(R.id.empty);


            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {


                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int i1, int i2) {

                    if(MathFunctions.savedFunctions.size()>0)
                    {
                        if (firstVisibleItem == oldFirstVisibleItem) {
                            int top = view.getChildAt(0).getTop();
                            // range between new top and old top must greater than MAX_SCROLL_DIFF
                            if (top > oldTop && Math.abs(top - oldTop) > MAX_SCROLL_DIFF) {
                                if(floatingADDFUNCTION_ACTIVITY.getVisibility()!= View.VISIBLE){
                                    floatingADDFUNCTION_ACTIVITY.setVisibility(View.VISIBLE);
                                    floatingADDFUNCTION_ACTIVITY.startAnimation(appear);
                                }

                                // scroll up
                            } else if (top < oldTop && Math.abs(top - oldTop) > MAX_SCROLL_DIFF) {
                                if(floatingADDFUNCTION_ACTIVITY.getVisibility()!=View.INVISIBLE){
                                    floatingADDFUNCTION_ACTIVITY.setVisibility(View.INVISIBLE);
                                    floatingADDFUNCTION_ACTIVITY.startAnimation(disappear);
                                }

                                // scroll down
                            }
                            oldTop = top;
                        } else {
                            View child = view.getChildAt(0);
                            if (child != null) {
                                oldFirstVisibleItem = firstVisibleItem;
                                oldTop = child.getTop();
                            }
                        }
                    }


                }
            });


            EmptyText();

        }catch(Exception e)
        {
            this.createDialog("Could not load the listView");
        }
    }

    public void EmptyText()
    {
        if(NumberOfFunctions()==0)
        {
            Appear(emptyText,downToUp);
        }
        else
        {
            emptyText.setVisibility(View.INVISIBLE);
        }
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


//



    public void createDialog(String text)
    {

            AlertDialog.Builder builder = new AlertDialog.Builder(FunctionActivity.this);
            builder.setMessage(text)
                    .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create();
            builder.show();
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

    public void createDialogForSpeech(final String equationToSpeeach)
    {
        if(equationToSpeeach.length()>0)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(FunctionActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(FunctionActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(FunctionActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(FunctionActivity.this);
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
            builder.create();

            builder.show();
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


        private void initCamera()
        {
            if (!MainActivity.examMode.isACTIVATED_EXAM_MODE()) {
                Intent i = new Intent(FunctionActivity.this, CameraActivity.class);
                startActivityForResult(i, CAMERA_CODE);
            } else {
                Toast.makeText(this, "Sorry, you can not use this feature during exam mode.", Toast.LENGTH_SHORT).show();
            }
        }




    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.SDelete)
        {
            if(numberOfSelectedFunctions()>0)
            {
                createDialogForSelectedDelete();
            }
            else if(MathFunctions.savedFunctions.size()>0)
            {
                try{
                    createDialogForDeleteAll();
                }catch(Exception e)
                {
                    Toast.makeText(this,"Sorry, something went wrong.",Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(FunctionActivity.this,"No function available at this moment to delete.",Toast.LENGTH_LONG).show();
            }

            return true;
        }
        else if(id==R.id.WINDOW)
        {
            Intent intent = new Intent(FunctionActivity.this,WindowActivity.class);
            startActivity(intent);
            return true;
        }
        else if(id==R.id.GraphFunctionActivityMultiple)
        {
            int NSF = numberOfSelectedFunctions();
            if(NSF>20)
            {
                Toast.makeText(this,"Sorry you can only graph up to 20 graphs.",Toast.LENGTH_SHORT).show();
            }
            else if(NSF>=2)
            {
                Toast.makeText(this,"Loading the Graph...",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(FunctionActivity.this,MultipleGraphActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(this,"You need to at least select two functions.",Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        else if(id==R.id.SELECTALL)
        {
            SelectAll();
            return true;
        }
        else if(id==R.id.DESELECTALL)
        {
            DeselectAll();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public int numberOfSelectedFunctions()
    {
        int total = 0;
        for(FunctionCreator functionCreator:MathFunctions.savedFunctions)
        {
            if(functionCreator.isSelected())
            {
                total++;
            }
        }
        return total;
    }

    protected void createDialogForDeleteAll()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(FunctionActivity.this);
        builder.setMessage("Are you sure you want to delete every function?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        for(FunctionCreator functionCreator:MathFunctions.savedFunctions)
                        {
                            functionCreator.delete();
                        }
                        functionCreatorArrayAdapter.clear();
                        functionCreatorArrayAdapter.notifyDataSetChanged();
                        deleteFile(FileNames.fileName);
                        Toast.makeText(FunctionActivity.this,"Successfully deleted all the functions!",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create();
        builder.show();
    }

    protected void createDialogForSelectedDelete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(FunctionActivity.this);
        String msg = "Are you sure you want to delete these functions?\n";
        for(FunctionCreator functionCreator:MathFunctions.savedFunctions)
        {
            if(functionCreator.isSelected())
            {
                msg+=functionCreator.getNameAndFunction();
            }
        }
        builder.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteSelectedFunctions();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create();
        builder.show();
    }

    private void deleteSelectedFunctions()
    {
        while(this.numberOfSelectedFunctions()>0)
        {
            deleteSelected();
        }
        Toast.makeText(this,"Selected functions were successfully deleted!",Toast.LENGTH_SHORT).show();
    }
    private void deleteSelected()
    {
        for(FunctionCreator functionCreator : MathFunctions.savedFunctions)
        {
            if(functionCreator.isSelected())
            {
                functionCreator.delete();
                functionCreatorArrayAdapter.remove(functionCreator);
                return;
            }
        }
    }





    private void SelectAll()
    {
        for(FunctionCreator functionCreator : MathFunctions.savedFunctions)
        {
            if(!functionCreator.isSelected())
            {
                functionCreator.setSelected(true);
            }
        }
        functionCreatorArrayAdapter.notifyDataSetChanged();
        Toast.makeText(this,"All functions were selected.",Toast.LENGTH_SHORT).show();
    }

    private void DeselectAll()
    {
        for(FunctionCreator functionCreator : MathFunctions.savedFunctions)
        {
            if(functionCreator.isSelected())
            {
                functionCreator.setSelected(false);
            }
        }
        functionCreatorArrayAdapter.notifyDataSetChanged();
        Toast.makeText(this,"All functions were deselected.",Toast.LENGTH_SHORT).show();
    }








    //functionality
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
