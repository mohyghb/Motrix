package com.mohyaghoub.calculator;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class LoadList {

    static Calculator calculator = new Calculator();

    static String goodEquation = "";


    public static void loadList(Context context)
    {
        try {
            MathFunctions.savedFunctions = new ArrayList<>();
            LinkedFunctions.FUNCTIONS = new ArrayList<>();
            String loadStr = LoadList.readFile(FileNames.fileName,context);
            String sepFunctions[] = loadStr.split("\\&");
            for (int i = 0; i < sepFunctions.length; i++) {
                String sep[] = sepFunctions[i].split("\\|");
                if(sep.length==7)
                {
                    if(sep[2].equals("false"))
                    {
                        FunctionCreator fc = new FunctionCreator(sepFunctions[i]);
                        MathFunctions.savedFunctions.add(fc);
                        Function function = new Function(fc.getName(),fc.getFunction());
                        LinkedFunctions.FUNCTIONS.add(function);
                    }
                }
            }
        }catch(Exception e)
        {

        }
    }

    public static String getNameOfSavedFunctions()
    {
        String all = "";
        for(FunctionCreator functionCreator:MathFunctions.savedFunctions)
        {
            all+=functionCreator.getName()+"|";
        }
        return all;
    }

    public static String getList()
    {
        String all ="";
        int size =0;

        try {
            for(FunctionCreator fc :MathFunctions.savedFunctions)
            {
                if(!fc.isDeleted())
                {
//                    all+=fc.getName()+"|"+fc.getFunction()+"|"+fc.getDeleted();
                    all+= String.format("%s|%s|%s|%s|%d|%d|%d",fc.getName(),fc.getFunction(),fc.getDeleted(),fc.getSelected(),fc.getColor(),fc.getStyle(),fc.getThickness());
                    if(size!=MathFunctions.savedFunctions.size())
                    {
                        all+="&";
                    }
                }

                size++;
            }
        }catch(Exception e)
        {

        }
        return all;
    }


    public static void saveFile(String file,String text,Context context)
    {

        try{
            FileOutputStream fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
        }catch(Exception e)
        {

        }
    }



    public static String readFile(String filename,Context context)
    {
        String text = "";

        try{
            if(fileExist(filename,context)) {
                FileInputStream fis = context.openFileInput(filename);
                int size = fis.available();
                byte[] buffer = new byte[size];
                fis.read(buffer);
                fis.close();
                text = new String(buffer);
            }
        }catch(Exception e)
        {
            Toast.makeText(context,"There was an ERROR in loading the functions",Toast.LENGTH_LONG).show();
        }
        return text;
    }

    public static boolean fileExist(String fileName,Context context){
        File file = context.getFileStreamPath(fileName);
        return file.exists();
    }


    public static String getAllFunctionsInOne()
    {
        String all = "";
        for(FunctionCreator functionCreator:MathFunctions.savedFunctions)
        {
            all+=functionCreator.getName()+"|";
        }
        return all;
    }


    //check to see if the function is good
    public static boolean isAGoodFunction(String function,Context context)
    {
        if(function.length()>100)
        {
            Toast.makeText(context,"Maximum length of function exceeded.",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context,"Bad expression",Toast.LENGTH_LONG).show();
                qualified--;
            }
            try{
                double x = calculator.getValue(4);
                qualified++;
            }catch(Exception e)
            {
                qualified--;
            }
            if(qualified==2){
                goodEquation = calculator.getEquation();
            }
            return qualified == 2;
        }
        else{
            Toast.makeText(context,"You need to enter something!",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean isAGoodName(String name,Context context)
    {
        String name1 = name.toLowerCase();
        if(hasNumber(name1))
        {
            Toast.makeText(context,"Names can not have numbers in them.",Toast.LENGTH_SHORT).show();
            return false;
        }
        for(FunctionCreator fc:MathFunctions.savedFunctions)
        {
            if(name1.equals(fc.getName()))
            {
                Toast.makeText(context,String.format("The name '%s' has been already used",fc.getName()),Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(name.length()==0)
        {
            Toast.makeText(context,"You need to enter a name",Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(name.length()>=6)
        {
            Toast.makeText(context,"Name should be less than or equal to five characters",Toast.LENGTH_SHORT).show();
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


    public static void loadWindowRatios(Context context)
    {
        String readWindowRatios = LoadList.readFile(FileNames.windowRatioFileName,context);
        if(readWindowRatios.length()>0)
        {
            String splitRatios[] = readWindowRatios.split("\\|");
            if(splitRatios.length==4)
            {
                Ratios.minX = Double.parseDouble(splitRatios[0]);
                Ratios.maxX = Double.parseDouble(splitRatios[1]);
                Ratios.minY = Double.parseDouble(splitRatios[2]);
                Ratios.maxY = Double.parseDouble(splitRatios[3]);
            }
            else
            {
                Ratios.minX = -10;
                Ratios.maxX = 10;
                Ratios.minY = -10;
                Ratios.maxY = 10;
            }
        }
        else
        {
            Ratios.minX = -10;
            Ratios.maxX = 10;
            Ratios.minY = -10;
            Ratios.maxY = 10;
        }
    }




    public static void getAName(final Context context, final String equation)
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                        String function = equation;
                        if(LoadList.isAGoodName(name,context)&&LoadList.isAGoodFunction(function,context))
                        {
                            String text1 = String.format("%s|%s|false|false|5|0|10",name,function);
                            LoadList.saveFile(FileNames.fileName,LoadList.getList()+"&"+text1,context);
                            try{
                                FunctionCreator functionCreator = new FunctionCreator(text1);
                                MathFunctions.savedFunctions.add(functionCreator);
                                Function functionCal = new Function(functionCreator.getName(),functionCreator.getFunction());
                                LinkedFunctions.FUNCTIONS.add(functionCal);
                            }catch(Exception e)
                            {

                            }
                            Toast.makeText(context,String.format("'%s = %s' was saved!",name,function),Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
        dialog.show();
    }



    public static void createDialogForYCALC(final Context context, String equation)
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view1 = inflater.inflate(R.layout.ycalc, null);
        builder.setView(view1);

        Button inx = view1.findViewById(R.id.inputX);
        inx.setVisibility(View.GONE);

        final EditText inputX = view1.findViewById(R.id.inputX_EditText);
        inputX.setVisibility(View.VISIBLE);

        final TextView ycalcTextView = view1.findViewById(R.id.ycalcTextV);

        ycalcTextView.setText("y = "+equation);



        builder.setTitle("y-calculator")
                .setPositiveButton("Calculate", null)
                .setNegativeButton("Cancel", null);
        dialog = builder.create();

        calculator.setEquation(equation);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        try {
                            double x = Double.parseDouble(inputX.getText().toString());
                            double y = calculator.getValue(x);
                            ycalcTextView.setText(String.format("y = %.12f", y));
                        }
                        catch(Exception e)
                        {
                            Toast.makeText(context,"Something went wrong. Make sure you have entered a number.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(wlp);

        dialog.show();
    }



}
