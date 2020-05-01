package com.mohyaghoub.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
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
import java.util.Arrays;

public class MultipleGraphActivity extends AppCompatActivity {

    private GraphView graphView;
    private Calculator calculator;
    private CAS cas;
    private ArrayList<Double> xints;
    private ArrayList<Double> potentialMaxMin;
    private ArrayList<Series> allSeries;

    private int maxDataPoints;
    private double accuracy;

    private ArrayList<FunctionCreator> selectedFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_graph);
        initGraph();
        if(accuracy!=0)
        {
            drawGraph();
        }
    }


    //initGraph
    protected void initGraph()
    {
        setAccuracy();
        if(accuracy!=0)
        {
            cas = new CAS();
            allSeries = new ArrayList<>();
            this.graphView = findViewById(R.id.MultipleGraphView);
            graphView.getViewport().setYAxisBoundsManual(true);
            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getViewport().setMaxX(Ratios.maxX);
            graphView.getViewport().setMinX(Ratios.minX);
            graphView.getViewport().setMinY(Ratios.minY);
            graphView.getViewport().setMaxY(Ratios.maxY);
            graphView.getViewport().setScrollable(true);
            graphView.getViewport().setScalable(true);
            graphView.getViewport().setScrollableY(true);
            graphView.getLegendRenderer().setVisible(true);
            graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);



            //init calculator and cas
            calculator = new Calculator();

            this.potentialMaxMin = new ArrayList<>();
            this.xints = new ArrayList<>();
            this.maxDataPoints = (int)((Math.abs(Ratios.maxX-Ratios.minX))/this.accuracy);
        }

    }
    //setAccuracy
    protected void setAccuracy()
    {
        double average = (Math.abs(Ratios.maxX)+Math.abs(Ratios.minX))/2;
        this.accuracy = average/1000;
        if(accuracy==0)
        {
            Toast.makeText(MultipleGraphActivity.this,"Sorry, but window ratios are either too small or too big.",Toast.LENGTH_LONG).show();
            finish();
        }
    }



    //drawGraph
    protected void drawGraph()
    {
        selectedFunctions = new ArrayList<>();
        for(FunctionCreator functionCreator:MathFunctions.savedFunctions)
        {
           if(functionCreator.isSelected())
           {
               calculator.setEquation(functionCreator.getFunction());
               LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
               double x,y;
               x = Ratios.minX-this.accuracy;
               while(x<=Ratios.maxX)
               {
                   try {
                       x += this.accuracy;
                       y = calculator.getValue(x);

                       series.appendData(new DataPoint(x, y), true, maxDataPoints);
                   }catch(ArithmeticException e)
                   {

                   }
               }
               Paint paint = new Paint();
               paint.setStyle(Paint.Style.STROKE);
               paint.setStrokeWidth(functionCreator.getThickness());
               paint.setPathEffect(ColorsInJava.pathEffects[functionCreator.getStyle()]);
               paint.setColor(ContextCompat.getColor(this,ColorsInJava.colors[functionCreator.getColor()]));
               series.setDrawAsPath(true);
               series.setCustomPaint(paint);
               series.setColor(ContextCompat.getColor(this,ColorsInJava.colors[functionCreator.getColor()]));
               series.setTitle(functionCreator.getName());
               series.setOnDataPointTapListener(new OnDataPointTapListener() {
                   @Override
                   public void onTap(Series series, DataPointInterface dataPoint) {
                       double x = dataPoint.getX();
                       double y = dataPoint.getY();
                       String text = String.format("Point [x = %.3f , y = %.3f] was tapped on the '%s' graph.",x,y,series.getTitle().toString());
                       Toast.makeText(MultipleGraphActivity.this,text,Toast.LENGTH_LONG).show();
                       cas.setPoint(x,y);
                   }
               });
               this.allSeries.add(series);
               this.graphView.addSeries(series);
               this.selectedFunctions.add(functionCreator);
           }
        }

        graphView.addSeries(cas.getSeries());

        graphView.getViewport().setOnXAxisBoundsChangedListener(new Viewport.OnXAxisBoundsChangedListener() {
            @Override
            public void onXAxisBoundsChanged(double minX, double maxX, Reason reason) {
                double average = (maxX-minX);
                if(average<0.01)
                {
                    RE_CENTER();
                    Toast.makeText(MultipleGraphActivity.this,"Graph refreshed, you could not zoom more than that.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    //loading files



    //menu for intersection
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.multiple_graph_menu_intersection, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.INTERSECTION)
        {
            if(selectedFunctions.size()==2)
            {
                for(FunctionCreator functionCreator:this.selectedFunctions)
                {
                    functionCreator.setChecked(true);
                }
                findIntersection();
            }
            else
            {
                whichFunctionsDialog();
            }

            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    protected void whichFunctionsDialog()
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.which_intersection, null);
        builder.setView(view1);
        LinearLayout layout = view1.findViewById(R.id.linear_whichFunctions);
        builder.setMessage("Select two functions to find all the intersections inside this domain.");



        for(final FunctionCreator functionCreator:this.selectedFunctions)
        {
            functionCreator.setChecked(false);
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(functionCreator.getFunction());
            checkBox.setTextColor(ContextCompat.getColor(this,ColorsInJava.colors[functionCreator.getColor()]));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        functionCreator.setChecked(true);
                    }
                    else{
                        functionCreator.setChecked(false);
                    }
                }
            });
            checkBox.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
                layout.addView(checkBox);
            }
        Button findIntersection = new Button(this);
        findIntersection.setText("Find intersection");
        findIntersection.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        findIntersection.setTextColor(Color.WHITE);
        findIntersection.setGravity(Gravity.CENTER);
        findIntersection.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(findIntersection);


        Button cancel= new Button(this);
        cancel.setText("Cancel");
        cancel.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        cancel.setTextColor(Color.WHITE);
        cancel.setGravity(Gravity.CENTER);
        cancel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(cancel);


            dialog = builder.create();

//         dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//
//            @Override
//            public void onShow(DialogInterface dialogInterface) {
//
//                   Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
//                   button.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//                        // TODO Do something
//                        int numberOfCheckedFunctions = howManyFunctionsAreChecked();
//                        if(numberOfCheckedFunctions==2)
//                        {
//                            findIntersection();
//                            dialog.dismiss();
//                        }
//                        else{
//                            if(numberOfCheckedFunctions>2)
//                            {
//                                Toast.makeText(MultipleGraphActivity.this,"Sorry, but you can only choose 2 functions.",Toast.LENGTH_LONG).show();
//                            }
//                            else {
//                                Toast.makeText(MultipleGraphActivity.this, "You need to selected more functions.", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                      }
//                });
//             }
//        });




        findIntersection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numberOfCheckedFunctions = howManyFunctionsAreChecked();
                if(numberOfCheckedFunctions==2)
                {
                    findIntersection();
                    dialog.dismiss();
                }
                else{
                    if(numberOfCheckedFunctions>2)
                    {
                        Toast.makeText(MultipleGraphActivity.this,"Sorry, but you can only choose 2 functions.",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MultipleGraphActivity.this, "You need to selected more functions.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
         dialog.show();
    }


    protected int howManyFunctionsAreChecked()
    {
        int checkedFunctions = 0;
        for(FunctionCreator functionCreator:this.selectedFunctions)
        {
            if(functionCreator.isChecked())
            {
                checkedFunctions++;
            }
        }
        return checkedFunctions;
    }
    protected ArrayList<FunctionCreator> getCheckedFunctions()
    {
        ArrayList<FunctionCreator> checkedFunctions = new ArrayList<>();
        for(FunctionCreator functionCreator:this.selectedFunctions)
        {
            if(functionCreator.isChecked())
            {
                checkedFunctions.add(functionCreator);
            }
        }
        return checkedFunctions;
    }

    protected void findIntersection()
    {
        ArrayList<FunctionCreator> checkedFunctions = getCheckedFunctions();

        String combinedEquation = "";
        int index = 1;
        for(FunctionCreator functionCreator:checkedFunctions)
        {
            if(index==1)
            {
                combinedEquation+= functionCreator.getFunction();
            }
            else{
                combinedEquation+= String.format("-(%s)",functionCreator.getFunction());
            }
            index++;
        }
        calculator.setEquation(combinedEquation);
        getAllTheXintercepts();
        showTheIntersectionsDialog();
        showIntersectionsOnGraph(checkedFunctions.get(0));
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



    protected void getAllTheXintercepts()
    {
        double x,y,prey;
        x = Ratios.minX - this.accuracy;
        prey = 0;
        for(int i = 0;x<=Ratios.maxX;i++)
        {
            try {
                x += this.accuracy;
                y = calculator.getValue(x);
                if(i==0)
                {
                    prey = y;
                }
                else{
                    if(this.signChanged(prey,y))
                    {
                        this.xints.add(x);
                    }
                    prey = y;
                }
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
                    if (this.signChanged(prey, y)) {
                        this.potentialMaxMin.add(x);
                    }
                    prey = y;
                }
            }catch(ArithmeticException arit)
            {

            }
        }

        cas.clear();
        saveMaxMin();
        saveXints();
    }
////
////    //saving max and min and xints
////    public void saveMaxMin()
////    {
////        for (Double x:this.potentialMaxMin)
////        {
////            double actualX = calculator.NMFFD(x);
////
////            double curry = calculator.getValue(actualX);
////            double prey = calculator.getValue(actualX-0.1);
////            double posty = calculator.getValue(actualX+0.1);
////
////            if(curry<prey&&curry<posty)
////            {
////                cas.addMin(actualX,curry);
////            }
////            else if(curry>prey&&curry>posty)
////            {
////                cas.addMax(actualX,curry);
////            }
////            else{
////                cas.addVerticalASYM(actualX,curry);
////            }
////
////        }
////    }
////    public void saveXints()
////    {
////        for(Double x:this.xints)
////        {
////            double xint = calculator.NM(x);
////            if(hasThisXint(xint)==false&&xint!=Calculator.ERRORCODE)
////            {
////                if(this.intIsBetter(xint))
////                {
////                    double y = Math.abs(calculator.getValue(xint));
////                    double inty = Math.abs(calculator.getValue((int)xint));
////                    if(inty<y)
////                    {
////                        cas.addXint((int)xint,0);
////                    }
////                    else{
////                        cas.addXint(xint, 0);
////                    }
////                }
////                else {
////                    cas.addXint(xint, 0);
////                }
////            }
////        }
////        for(Point point:cas.getMinimum())
////        {
////            double y = point.getY();
////            double x = point.getX();
////            if(y<=Calculator.tolerance&&y>=-Calculator.tolerance&&!hasThisXint(x)&&x!=Calculator.ERRORCODE)
////            {
////                if(Math.abs(y)<Calculator.LOWESTTOLERANCE)
////                {
////                    cas.addXint(x,0);
////                }else{
////                    double newXint = calculator.NM(x);
////                    if(hasThisXint(newXint)==false&&newXint!=Calculator.ERRORCODE)
////                    {
////                        point.setDeleted(true);
////                        cas.addXint(newXint,0);
////                        cas.addMin(newXint,0);
////                    }
////                }
////
////            }
////        }
////        for(Point point:cas.getMaximum())
////        {
////            double y = point.getY();
////            double x = point.getX();
////            if(y<=Calculator.tolerance&&y>=-Calculator.tolerance&&!hasThisXint(x)&&x!=Calculator.ERRORCODE)
////            {
////                if(Math.abs(y)<Calculator.LOWESTTOLERANCE)
////                {
////                    cas.addXint(x,0);
////                }
////                else{
////                    double newXint = calculator.NM(x);
////                    if(hasThisXint(newXint)==false&&newXint!=Calculator.ERRORCODE)
////                    {
////                        point.setDeleted(true);
////                        cas.addXint(newXint,0);
////                        cas.addMax(newXint,0);
////                    }
////                }
////
////            }
////        }
////
////    }

    public void saveMaxMin()
    {
        for (Double x:this.potentialMaxMin)
        {
            MaxMin(x);
        }
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


    public void showIntersectionsOnGraph(FunctionCreator equation)
    {
        calculator.setEquation(equation.getFunction());
        double xints[] = cas.getXintsList();
        Arrays.sort(xints);
        DataPoint [] dataPoints = new DataPoint[xints.length];
        for(int i = 0;i<xints.length;i++)
        {
            DataPoint dataPoint = new DataPoint(xints[i],calculator.getValue(xints[i]));
            dataPoints[i] = dataPoint;
        }
        cas.setData(dataPoints,"Intersection");
        graphView.refreshDrawableState();
    }


    public boolean hasThisXint(double x)
    {
        for(Point p:cas.getXintList())
        {
            if(p.getX()==x)
            {
                return true;
            }
        }
        return false;
    }


    protected void showTheIntersectionsDialog()
    {
        String intersectionsText = "";
        double[] intersections = cas.getXintsList();
        for(int i = 0;i<intersections.length;i++)
        {
            if(i==0)
            {
                intersectionsText = "Intersection points:\n";
            }
            intersectionsText+= String.format("%d: %f\n",i+1,intersections[i]);
        }
        if(intersectionsText.equals(""))
        {
            intersectionsText = "Sorry we could not find a point of intersection inside this domain.";
        }
        createDialog(intersectionsText,"Intersection");
    }

    public void createDialog(String text, final String title)
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = MultipleGraphActivity.this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogbottom, null);
        builder.setView(view);

        TextView textView = view.findViewById(R.id.bottomText);
        textView.setText(title+"\n"+text);

        Button okay = view.findViewById(R.id.okayDialog);
        Button tryFinding = view.findViewById(R.id.TryfindingManually);
        tryFinding.setVisibility(View.GONE);
        
        dialog = builder.create();


        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(wlp);
        dialog.show();
    }





    //bundle to check if it is a potential xint
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


    //RECENTER
    public void RE_CENTER()
    {
        graphView.getViewport().setMaxX(Ratios.maxX);
        graphView.getViewport().setMinX(Ratios.minX);
        graphView.getViewport().setMinY(Ratios.minY);
        graphView.getViewport().setMaxY(Ratios.maxY);
        graphView.invalidate();
        graphView.removeAllSeries();
        graphView.refreshDrawableState();
        for(Series series:this.allSeries)
        {
            graphView.addSeries(series);
        }
        graphView.addSeries(this.cas.getSeries());
    }








}
