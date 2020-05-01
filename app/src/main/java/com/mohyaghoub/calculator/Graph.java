package com.mohyaghoub.calculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

public class Graph extends AppCompatActivity {

    private GraphView graph;
    private LineGraphSeries<DataPoint> series;
    private LineGraphSeries<DataPoint> fprime;
    private int maxDataPoints;
    private CAS cas;
    private Calculator calculator;
    private FunctionCreator functionCreator;

    private ArrayList<Double> xints;
    private ArrayList<Double> potentialMaxMin;
    private boolean findPoints;


//    private double minX;
//    private double maxX;
//    private double minY;
//    private double maxY;
    private double accuracy;
    private final int YCALCULATOR_CODE = 783;
    private final int XCALCULATOR_CODE = 784;
    private final int UPPERLIMIT_CODE = 785;
    private final int LOWERLIMIT_CODE = 786;
    private final int CHANGE_WINDOW_CODE = 7231;

    private Button inputX;
    private Button inputY;
    private Button UpperLimit;
    private Button LowerLimit;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Bundle extras = getIntent().getExtras();
        initFunction(extras.getString("position"));
        setAccuracy();
        loadEverything();
    }



    private void loadEverything()
    {
        if(accuracy!=0) {
            if(LoadList.isAGoodFunction(this.functionCreator.getFunction(),this))
            {
                initObjects();
                initSeries();
            }
            else
            {
                finish();
            }
        }
    }


    private void initFunction(String position)
    {
        try{
            if(position.contains("position"))
            {
                String split[] = position.split("p");
                int pos = Integer.parseInt(split[0]);
                this.functionCreator = MathFunctions.savedFunctions.get(pos);
            }
            else
            {
                String text = String.format("%s|%s|false|false|5|0|10","function",position);
                this.functionCreator = new FunctionCreator(text);
            }
        }catch(Exception e)
        {

        }
    }


    private void initSeries()
    {
        calculator = new Calculator();
        calculator.setEquation(functionCreator.getFunction());
        initFindPoints();
        double x,y;
        x = Ratios.minX-this.accuracy;



        double prey =0;
        boolean exactZero = false;


        for(int i = 0;x<=Ratios.maxX;i++)
        {
            try {
                x += this.accuracy;
                y = calculator.getValue(x);
                if(y==0&&this.findPoints)
                {
                    cas.addXint(x,0);
                    exactZero = true;
                }
                else if(i==0)
                {
                    prey = y;
                }
                else{
                    if(this.findPoints&&!exactZero) {
                        if (this.signChanged(prey, y)) {
                            this.xints.add(x);
                        }
                        prey = y;
                        exactZero = false;
                    }
                }
                series.appendData(new DataPoint(x, y), true, maxDataPoints);
            }catch(ArithmeticException e)
            {

            }
        }

        x = Ratios.minX-this.accuracy;
        prey = 0;
        for(int i= 0;x<=Ratios.maxX;i++)
        {
            try {
                x += this.accuracy;
                y = calculator.getNumericalDerivative(x);
                if (i == 0) {
                    prey = y;
                } else {
                    if(this.findPoints)
                    {
                        if (this.signChanged(prey, y)) {
                            this.potentialMaxMin.add(x);
                        }
                        prey = y;
                    }
                }
                fprime.appendData(new DataPoint(x, y), true, maxDataPoints);
            }catch(ArithmeticException arit)
            {

            }
        }


        this.saveMaxMin();
        this.saveXints();
        this.saveYint();
        series.setTitle("Function");

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(functionCreator.getThickness());
        paint.setPathEffect(ColorsInJava.pathEffects[functionCreator.getStyle()]);
        paint.setColor(ContextCompat.getColor(this,ColorsInJava.colors[functionCreator.getColor()]));
        series.setDrawAsPath(true);
        series.setCustomPaint(paint);
        series.setColor(ContextCompat.getColor(this,ColorsInJava.colors[functionCreator.getColor()]));
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                if(cas.isPointSelectionIsOn())
                {
                    double x = dataPoint.getX();
                    double y = dataPoint.getY();
                    cas.setPoint(x,y);
                    createManualDialog(cas.getOperationManual(),x,y);
                    cas.setOnPointSelection(false);

                }else{
                    Toast.makeText(Graph.this,String.format("[x:%.3f, y:%.9f]\nOn the graph of %s",dataPoint.getX(),dataPoint.getY(),series.getTitle()),Toast.LENGTH_LONG).show();
                    cas.setPoint(dataPoint.getX(),dataPoint.getY());
                }

            }
        });
        graph.addSeries(series);
        graph.addSeries(cas.getSeries());
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        cas.updateMinMax(this.calculator);
    }
    private void initObjects()
    {
        this.graph = findViewById(R.id.graph);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setMaxX(Ratios.maxX);
        graph.getViewport().setMinX(Ratios.minX);

        graph.getViewport().setMinY(Ratios.minY);
        graph.getViewport().setMaxY(Ratios.maxY);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.setTitle(String.format("y = %s",functionCreator.getFunction()));
        graph.setTitleTextSize(55.0f);



        graph.getViewport().setOnXAxisBoundsChangedListener(new Viewport.OnXAxisBoundsChangedListener() {
            @Override
            public void onXAxisBoundsChanged(double minX, double maxX, Reason reason) {
                double average = (maxX-minX);
                if(average<0.01)
                {
                    RE_CENTER();
                    Toast.makeText(Graph.this,"Graph refreshed, you could not zoom more than that.",Toast.LENGTH_LONG).show();
                }
            }
        });

        this.series = new LineGraphSeries<>();
        this.fprime = new LineGraphSeries<>();
        this.fprime.setTitle("Derivative");
        this.fprime.setColor(Color.RED);
        this.xints = new ArrayList<>();
        this.potentialMaxMin = new ArrayList<>();
        this.maxDataPoints = (int)((Math.abs(Ratios.maxX-Ratios.minX))/this.accuracy);
        this.cas = new CAS();
//      this.savedDataPoints = new DataPoint[maxDataPoints];



        fprime.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(Graph.this,String.format("[x:%.3f, y:%.9f]\nOn the graph of %s",dataPoint.getX(),dataPoint.getY(),fprime.getTitle()),Toast.LENGTH_LONG).show();
                cas.setPoint(dataPoint.getX(),dataPoint.getY());
            }
        });
    }

    //menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.graphcalculations, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.XINTS) {
            try {
                String text = cas.getXints();
                if (text.equals("Sorry, we could not find an x-intercept in this interval.")) {
                    createSorryDialog(text, "x-intercepts");
                } else {
                    createDialog(text,"x-intercepts");
                    cas.showXintsOnGraph();
                }
            }catch(Exception e)
            {
                Toast.makeText(this,"Sorry there was an error.",Toast.LENGTH_LONG).show();
            }
            return true;
        }
        else if(id == R.id.MAX)
        {
            createDialog(cas.getMax(),"Maximums");
            cas.showMaxOnGraph();
            return true;
        }
        else if(id == R.id.MIN)
        {
            createDialog(cas.getMin(),"Minimums");
            cas.showMinOnGraph();
            return true;
        }
        else if(id==R.id.SHOWD)
        {
            MenuItem menuItem = item;
            if(menuItem.getTitle().equals("Show Derivative"))
            {
                menuItem.setTitle("Hide Derivative");
                this.graph.addSeries(this.fprime);
            }
            else if(menuItem.getTitle().equals("Hide Derivative"))
            {
                menuItem.setTitle("Show Derivative");
                this.graph.removeSeries(this.fprime);
            }
            else
            {
                Toast.makeText(this,"ERROR-402",Toast.LENGTH_LONG).show();
            }
            return true;
        }
        else if(id==R.id.YINT)
        {
            createDialog(cas.getYint(),"y-intercept");
            cas.showYintOnGraph();
            return true;
        }
        else if(id==R.id.YCALC)
        {
            createDialogForYCALC(this.functionCreator.getFunction());
            return true;
        }
        else if(id==R.id.XCALC)
        {
            this.createDialogForXCALC();
            return true;
        }
        else if(id==R.id.INTEGRAL)
        {
            getLowerAndUpperLimits();
            return true;
        }
        else if(id==R.id.Window_Change_Graph)
        {
            Intent intent = new Intent(this,WindowActivity.class);
            startActivityForResult(intent,CHANGE_WINDOW_CODE);
        }
        else if(id==R.id.RECENTER)
        {
            this.RE_CENTER();
            Toast.makeText(Graph.this,"Graph was re-centered",Toast.LENGTH_SHORT).show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void createDialog(String text, final String title)
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = Graph.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogbottom, null);
        builder.setView(view);

        TextView textView = view.findViewById(R.id.bottomText);
        textView.setText(title+"\n"+text);

        Button okay = view.findViewById(R.id.okayDialog);
        Button tryFinding = view.findViewById(R.id.TryfindingManually);



//        builder.setTitle(title)
//                .setPositiveButton("OKay!", null)
//                .setNegativeButton(String.format("Try finding %s manually",title), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        instructionDialogForManualUsage(title);
//                        cas.setOnPointSelection(true);
//                        cas.setOperationManual(title);
//                        Toast.makeText(Graph.this,"Tap on a point.",Toast.LENGTH_SHORT).show();
//                    }
//                });
        dialog = builder.create();

        tryFinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instructionDialogForManualUsage(title);
                cas.setOnPointSelection(true);
                cas.setOperationManual(title);
                dialog.dismiss();
            }
        });
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
//        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(wlp);
        dialog.show();
    }
    public void instructionDialogForManualUsage(String operation)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Graph.this);
        builder.setMessage(String.format("To find %s manually you just need to click on part of the graph which you think is closer to %s.",operation,operation))
                .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(Graph.this,"Tap on a point.",Toast.LENGTH_SHORT).show();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }


    public void createSorryDialog(String text, final String operation)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Graph.this);
        builder.setMessage(text)
                .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(String.format("Try finding %s manually", operation), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //show them how to select a point
                        instructionDialogForManualUsage(operation);
                        cas.setOnPointSelection(true);
                        cas.setOperationManual(operation);
                        Toast.makeText(Graph.this,"Tap on a point.",Toast.LENGTH_SHORT).show();
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }

    public void createManualDialog(final String type, final double xValue,double yValue)
    {
        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(Graph.this);
        LayoutInflater inflater = Graph.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.manual_xint_max_min,null);
        builder.setView(view);
        builder.setTitle("Point was found!");
        TextView message = view.findViewById(R.id.MessageManual);
        Button yup,cancel,letstryagain;
        yup = view.findViewById(R.id.YupBtnManual);
        cancel = view.findViewById(R.id.CancelBtnManual);
        letstryagain = view.findViewById(R.id.LetsTryAgainBtnManual);
        dialog = builder.create();

        String msg = String.format("Are you sure this point\n [x = %.3f, y = %.3f] is close to the %s of this graph?",xValue,yValue,type);
        message.setText(msg);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        letstryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cas.setOnPointSelection(true);
                Toast.makeText(Graph.this,"Tap on a point.",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        yup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type.equals("x-intercepts"))
                {
                    getXintManual(xValue);
                }
                else if(type.equals("Maximums"))
                {
                    getMaxManual(xValue);
                }
                else if(type.equals("Minimums"))
                {
                    getMinManual(xValue);
                }
                else if(type.equals("y-intercept"))
                {
                    createDialog(cas.getYint(),"y-intercept");
                    cas.showYintOnGraph();
                }
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public void getMinManual(double guess)
    {
        try{
            double ActualX = calculator.NMFFD(guess);
            double curry = calculator.getValue(ActualX);
            double prey = calculator.getValue(ActualX-Calculator.toleranceForSecond);
            double posty = calculator.getValue(ActualX+Calculator.toleranceForSecond);
            if(ActualX!=Calculator.ERRORCODE)
            {
                if((curry<=prey&&curry<=posty)&&(ActualX>=Ratios.minX&&ActualX<=Ratios.maxX)) {
                    String xint = String.format("This is what we found!\nx = %f, y = %f", ActualX, curry);
                    cas.setPoint(ActualX, curry);
                    createDialog(xint, "Minimums");
                }
                else
                {
                    createDialog("Sorry we still could not find a minimum!","Minimums");
                }
            }
            else
            {
                createDialog("Sorry we still could not find a minimum!","Minimums");
            }
        }catch(Exception e)
        {
            createDialog("Sorry we still could not find a minimum!","Minimums");
        }
    }

    public void getMaxManual(double guess)
    {
        try{
            double ActualX = calculator.NMFFD(guess);
            double curry = calculator.getValue(ActualX);
            double prey = calculator.getValue(ActualX-Calculator.toleranceForSecond);
            double posty = calculator.getValue(ActualX+Calculator.toleranceForSecond);
            if(ActualX!=Calculator.ERRORCODE)
            {
                if(curry>=prey&&curry>=posty&&(ActualX>=Ratios.minX&&ActualX<=Ratios.maxX))
                {
                    String xint = String.format("This is what we found!\nx = %f, y = %f",ActualX,curry);
                    cas.setPoint(ActualX,curry);
                    createDialog(xint,"Maximums");
                }
                else{
                    createDialog("Sorry we still could not find a maximum!","Maximums");
                }

            }
            else
            {
                createDialog("Sorry we still could not find a maximum!","Maximums");
            }
        }catch(Exception e)
        {
            createDialog("Sorry we still could not find a maximum!","Maximums");
        }
    }


    public void getXintManual(double leftEnd)
    {
            try{
                double newtonleft = calculator.NM(leftEnd);
                if(newtonleft!=Calculator.ERRORCODE)
                {
                    if(newtonleft>=Ratios.minX&&newtonleft<=Ratios.maxX)
                    {
                        String xint = String.format("This is what we found!\nx = %.10f",newtonleft);
                        cas.setPoint(newtonleft,0);
                        createDialog(xint,"x-intercepts");
                    }
                    else{
                        createDialog("Sorry we still could not find an x-intercept!","x-intercepts");
                    }

                }
                else{
                    createDialog("Sorry we still could not find an x-intercept!","x-intercepts");
                }}catch(Exception e)
            {
                createDialog("Sorry we still could not find an x-intercept!","x-intercepts");
            }
    }


    public boolean signChanged(double prey,double y)
    {
        return isNegative(y) != isNegative(prey) && hasNumber(prey + "") && hasNumber(y + "") && !thereIsABigDifference(prey, y);
    }

    public boolean isNegative(double number)
    {
        return number <= 0;
    }

    public boolean thereIsABigDifference(Double a,Double b)
    {
        return Math.max(a, b) - Math.min(a, b) >= 10;
    }



    public boolean hasNumber(String equation)
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

    public void saveXints()
    {
        for(Double x:this.xints)
        {
            double xint = calculator.NM(x);
            if(hasThisXint(xint)==false&&xint!=Calculator.ERRORCODE&&(xint>=Ratios.minX&&xint<=Ratios.maxX))
            {
                if(this.intIsBetter(xint))
                {
                    double y = Math.abs(calculator.getValue(xint));
                    double inty = Math.abs(calculator.getValue((int)xint));
                    if(inty<y)
                    {
                        cas.addXint((int)xint,0);
                    }
                    else{
                        cas.addXint(xint, 0);
                    }
                }
                else {
                    cas.addXint(xint, 0);
                }
            }
        }

        for(Point point:cas.getMinimum())
        {
           double y = point.getY();
           double x = point.getX();
           if(Math.abs(y)<Calculator.tolerance&&!hasThisXint(x)&&x!=Calculator.ERRORCODE)
           {
               if(y==0)
               {
                   cas.addXint(x,0);
               }else{
                   int intx = (int)x;
                   if(calculator.getValue(intx)==0)
                   {
                       point.setDeleted(true);
                       cas.addSepMin(intx,0);
                       cas.addXint(intx,0);
                   }
                   else
                   {
                       double newXint = calculator.NM(x);
                       if(hasThisXint(newXint)==false&&newXint!=Calculator.ERRORCODE&&(newXint>=Ratios.minX&&newXint<=Ratios.maxX))
                       {
                           point.setDeleted(true);
                           cas.addXint(newXint,0);
                           cas.addSepMin(newXint,0);
                       }
                   }



               }

           }

        }


        for(Point point:cas.getMaximum())
        {
            double y = point.getY();
            double x = point.getX();
            if(Math.abs(y)<Calculator.tolerance&&!hasThisXint(x)&&x!=Calculator.ERRORCODE)
            {
                if(y==0)
                {
                    cas.addXint(x,0);
                }
                else{
                        int intx = (int)x;
                        if(calculator.getValue(intx)==0)
                        {
                            point.setDeleted(true);
                            cas.addSepMax(intx,0);
                            cas.addXint(intx,0);
                        }
                        else
                        {
                            double newXint = calculator.NM(x);
                            if(hasThisXint(newXint)==false&&newXint!=Calculator.ERRORCODE&&(newXint>=Ratios.minX&&newXint<=Ratios.maxX))
                            {
                                point.setDeleted(true);
                                cas.addXint(newXint,0);
                                cas.addSepMax(newXint,0);
                            }
                        }
                }


            }

        }

    }

    public boolean hasThisXint(double x)
    {
        for(Point p:cas.getXintList())
        {
            double alx = p.getX();
            double difference = Math.abs(Math.max(x,alx)-Math.min(x,alx));
            if(difference<=Calculator.MIDIUMROLERANCE)
            {
                return true;
            }
        }
        return false;
    }

    public void saveMaxMin()
    {
        for (Double x:this.potentialMaxMin)
        {
            MaxMin(x);
        }
    }
    public void saveYint()
    {
        cas.setYint(calculator.getValue(0));
    }

    public void createDialogForYCALC(String function)
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = Graph.this.getLayoutInflater();

        View view1 = inflater.inflate(R.layout.ycalc, null);
        builder.setView(view1);

        inputX = view1.findViewById(R.id.inputX);
//        inputX.setShowSoftInputOnFocus(false);
        final TextView ycalcTextView = view1.findViewById(R.id.ycalcTextV);

        ycalcTextView.setText("y = "+function);

        inputX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(YCALCULATOR_CODE,inputX.getText().toString());
            }
        });

        builder.setTitle("y-calculator")
                .setPositiveButton("Calculate", null)
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
                        try {
                            double x = Double.parseDouble(inputX.getText().toString());
                            double y = calculator.getValue(x);
                            ycalcTextView.setText(String.format("y = %.12f", y));
                        }
                        catch(Exception e)
                        {
                            Toast.makeText(Graph.this,"Something went wrong. Make sure you have entered a number.",Toast.LENGTH_LONG).show();
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

    public void getLowerAndUpperLimits()
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = Graph.this.getLayoutInflater();

        View view1 = inflater.inflate(R.layout.integraldialog, null);
        builder.setView(view1);


        UpperLimit = view1.findViewById(R.id.UL);
        LowerLimit = view1.findViewById(R.id.LL);

//        UpperLimit.setShowSoftInputOnFocus(false);
//        LowerLimit.setShowSoftInputOnFocus(false);



        UpperLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(UPPERLIMIT_CODE,UpperLimit.getText().toString());
            }
        });

        LowerLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(LOWERLIMIT_CODE,LowerLimit.getText().toString());
            }
        });


        final TextView areaintegralaverage = view1.findViewById(R.id.areaintegralaverage);
        final TextView showIntegralEquation = view1.findViewById(R.id.showintegralEquation);

        builder.setTitle("Integrals")
                .setPositiveButton("Calculate", null)
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
                        try{
                            if(LowerLimit.getText().length()<=15&&UpperLimit.getText().length()<=15)
                            {
                                double lowerlimit = Double.parseDouble(LowerLimit.getText().toString());
                                double upperlimit = Double.parseDouble(UpperLimit.getText().toString());
                                double subintervals = 10000;
                                if(lowerlimit==upperlimit)
                                {
                                    areaintegralaverage.setText(String.format("[%.2f,%.2f]\nIntegrals: 0\nAverage value: 0\nArea: 0",lowerlimit,upperlimit));
                                    areaintegralaverage.setVisibility(View.VISIBLE);
                                }
                                else if(lowerlimit>upperlimit)
                                {
                                    areaintegralaverage.setText(calculator.simpsonRule(upperlimit, lowerlimit, subintervals,true));
                                    areaintegralaverage.setVisibility(View.VISIBLE);
                                }
                                else if(lowerlimit<upperlimit)
                                {
                                    areaintegralaverage.setText(calculator.simpsonRule(lowerlimit, upperlimit, subintervals,false));
                                    areaintegralaverage.setVisibility(View.VISIBLE);
                                }
//                                createDataForIntegralGraph(lowerlimit,upperlimit);
                            }
                            else{
                                Toast.makeText(Graph.this,"Maximum number of digits is 15.",Toast.LENGTH_LONG).show();
                            }

                        }catch(Exception e)
                        {
                            Toast.makeText(Graph.this,"Fill in lower and upper limits please.",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    public void createDialogForXCALC()
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = Graph.this.getLayoutInflater();

        View view1 = inflater.inflate(R.layout.xcalc, null);
        builder.setView(view1);

        inputY = view1.findViewById(R.id.inputY);
//        inputY.setShowSoftInputOnFocus(false);

        inputY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCalculatorActivity(XCALCULATOR_CODE,inputY.getText().toString());
            }
        });
        final TextView xcalcTextView = view1.findViewById(R.id.showALLXCALC);




        builder.setTitle("x-calculator")
                .setPositiveButton("Calculate", null)
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
                        try {
                            String y = inputY.getText().toString();
                            if (y.length() != 0) {
                                double numberY = Double.parseDouble(y);
                                ArrayList<Double> xintsOfNewGraph = new ArrayList<>();
                                xcalcTextView.setText(getXintsOfNewGraph(xintsOfNewGraph,numberY));
                                xcalcTextView.setVisibility(View.VISIBLE);
                            }
                        }catch(Exception e)
                        {
                            Toast.makeText(Graph.this,"Something went wrong.",Toast.LENGTH_LONG).show();
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


    public String getXintsOfNewGraph(ArrayList<Double> xints,double y2)
    {
        if(y2!=0)
        {
            double negatedY = -y2;
            double x = Ratios.minX-this.accuracy;
            double prey = 0;
            Calculator xcal = new Calculator();
            xcal.setEquation(calculator.getEquation() + "+" + "("+negatedY+")");

            for (int i = 0; x <= Ratios.maxX; i++) {
                try {
                    x += this.accuracy;
                    double y = calculator.getValue(x) + negatedY;
                    if (i == 0) {
                        prey = y;
                    } else {
                        if (this.signChanged(prey, y)) {
                            xints.add(x);
                        }
                        prey = y;
                    }
                } catch (ArithmeticException e) {
                }
            }
            Double[] allSatisfiedXValues = new Double[xints.size()+cas.getMinimum().size()+cas.getMaximum().size()];
            int i = 0;
            for (Double d : xints) {
                allSatisfiedXValues[i] = xcal.NM(d);
                i++;
            }
            if(i==0)
            {
                for(Point point:cas.getMaximum())
                {
                    double d = point.getX();
                    double newx = xcal.NM(d);
                    if(newx!=Calculator.ERRORCODE)
                    {
                        allSatisfiedXValues[i] = newx;
                        i++;
                    }
                }
                for(Point point:cas.getMinimum())
                {
                    double d = point.getX();
                    double newx = xcal.NM(d);
                    if(newx!=Calculator.ERRORCODE)
                    {
                        allSatisfiedXValues[i] = newx;
                        i++;
                    }
                }
            }

            if (i > 0) {
                String text = "";
                for (int p = 0; p < allSatisfiedXValues.length; p++) {
                    if(allSatisfiedXValues[p]!=null)
                    {
                        text += String.format("x(%d) = %f\n", p + 1, allSatisfiedXValues[p]);
                    }
                }
                return text;
            }
            else{
                return String.format("Sorry we could not find an x that equals to %f in this domain.", y2);
            }
        }
        else{
                return cas.getXints();
        }

    }



    //getWindowRatios


    //setting accuray based on domain
    public void setAccuracy()
    {
        double average = (Math.abs(Ratios.maxX)+Math.abs(Ratios.minX))/2;
        this.accuracy = average/1000;
        if(accuracy==0)
        {
            Toast.makeText(Graph.this,"Sorry, but window ratios are either too small or too big.",Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void RE_CENTER()
    {
        graph.getViewport().setMaxX(Ratios.maxX);
        graph.getViewport().setMinX(Ratios.minX);
        graph.getViewport().setMinY(Ratios.minY);
        graph.getViewport().setMaxY(Ratios.maxY);
        graph.invalidate();
        graph.removeAllSeries();
        graph.refreshDrawableState();
        graph.addSeries(this.series);
        graph.addSeries(this.cas.getSeries());
    }

    protected void MaxMin(double x)
    {
        double actualX = calculator.NMFFD(x);

        double curry = calculator.getValue(actualX);
        double prey = calculator.getValue(actualX-Calculator.toleranceForSecond);
        double posty = calculator.getValue(actualX+Calculator.toleranceForSecond);

        if((curry<prey&&curry<posty)&&(actualX>=Ratios.minX&&actualX<=Ratios.maxX))
        {
            cas.addMin(actualX,curry);
        }
        else if(curry>prey&&curry>posty&&(actualX>=Ratios.minX&&actualX<=Ratios.maxX))
        {
            cas.addMax(actualX,curry);
        }
        else{
            if(this.intIsBetter(actualX))
            {
                double newX = (int)actualX ;
                double cur = calculator.getValue(newX);
                double pre = calculator.getValue(newX-0.1);
                double post = calculator.getValue(newX +0.1);

                if(cur<pre&&cur<post&&(actualX>=Ratios.minX&&actualX<=Ratios.maxX))
                {
                    cas.addMin(newX,cur);
                }
                else if(cur>pre&&cur>post&&(actualX>=Ratios.minX&&actualX<=Ratios.maxX))
                {
                    cas.addMax(newX,cur);
                }
                else if((actualX>=Ratios.minX&&actualX<=Ratios.maxX))
                {
                    cas.addVerticalASYM(actualX,curry);
                }
            }
            else if((actualX>=Ratios.minX&&actualX<=Ratios.maxX)){
                cas.addVerticalASYM(actualX,curry);
            }
        }
    }


    protected boolean intIsBetter(double number)
    {
        double difference = Math.abs(number - ((int)number));
        double multiplicity = calculator.getMultiplicity(1);
        return difference <= (multiplicity) * 0.01;
    }

    protected void initFindPoints()
    {
        double random;
        Random rand = new Random();
        for(int i = 0;i<100;i++)
        {
            random = rand.nextDouble();
            if(Math.abs(calculator.getValue(random))>Calculator.LOWESTTOLERANCE)
            {
                this.findPoints = true;
                return;
            }
        }
        this.findPoints = false;
    }





    //read,save,exists file


    public boolean fileExist(String fileName){
        File file = getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }

    private String checkValueOf(String equation)
    {
        if(LoadList.isAGoodFunction(equation,this))
        {
            LoadList.calculator.setEquation(equation);
            double value = LoadList.calculator.getValue(1);
            return String.format("%f",value);
        }
        else
        {
            return equation;
        }
    }


    protected void onActivityResult(int requestCode,int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case YCALCULATOR_CODE:
                    String text = data.getStringExtra("classicData");
                    inputX.setText(checkValueOf(text));
                    break;

                case XCALCULATOR_CODE:
                    String text1 = data.getStringExtra("classicData");
                    inputY.setText(checkValueOf(text1));
                    break;
                case LOWERLIMIT_CODE:
                    String text2 = data.getStringExtra("classicData");
                    LowerLimit.setText(checkValueOf(text2));
                    break;
                case UPPERLIMIT_CODE:
                    String text3 = data.getStringExtra("classicData");
                    UpperLimit.setText(checkValueOf(text3));
                    break;


            }


        }
        else if(requestCode==CHANGE_WINDOW_CODE)
        {
                graph.invalidate();
                graph.removeAllSeries();
                setAccuracy();
                loadEverything();
                Toast.makeText(this,"Yup",Toast.LENGTH_LONG).show();
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


    private void startCalculatorActivity(int code,String text)
    {
        Intent intent = new Intent(Graph.this,CalculatorActivity.class);
        intent.putExtra("leftOver",text);
        intent.putExtra("savedFunctions","");
        intent.putExtra("KEYMODE","1");
        startActivityForResult(intent,code);
    }


}
