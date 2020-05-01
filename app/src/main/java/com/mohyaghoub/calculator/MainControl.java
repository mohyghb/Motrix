package com.mohyaghoub.calculator;


import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.Random;

public class MainControl {

    private FunctionCreator function;
    private LineGraphSeries series;
    private LineGraphSeries seriesPrime;
    private Calculator calculator;


    private double maxX = 10;
    private double minX = -10;
    private double accuracy = 0.12;
    private int maxDataPoints;

    private Context context;
    private Random random;

    double savedValue;

    MainControl(String functionLine,Context con)
    {
        initClass(functionLine,con);
    }

    private void initClass(String line,Context con)
    {
        this.function = new FunctionCreator(line);
        this.calculator = new Calculator();
        this.calculator.setEquation(function.getFunction());
        this.maxDataPoints = (int)((Math.abs(this.maxX-this.minX))/this.accuracy);
        this.series = new LineGraphSeries<>();
        this.seriesPrime = new LineGraphSeries<>();
        this.context = con;
        this.random = new Random();
        initSeries();
    }

    private void initSeries()
    {
        double x,y;
        x = minX-this.accuracy;
        while(x<=this.maxX)
        {
            try {
                x += accuracy;
                y = calculator.getValue(x);
                if(!Double.isNaN(y)&&!Double.isInfinite(y))
                {
                    series.appendData(new DataPoint(x, y), true, maxDataPoints);
                }
            }catch(ArithmeticException e)
            {

            }
        }

        x = minX-this.accuracy;
        while(x<=this.maxX)
        {
            try {
                x += this.accuracy;
                y = calculator.getNumericalDerivative(x);
                if(!Double.isNaN(y)&&!Double.isInfinite(y))
                {
                    seriesPrime.appendData(new DataPoint(x, y), true, maxDataPoints);
                }
            } catch (ArithmeticException arit) {

            }
        }




        series.setTitle("F(x)");
        seriesPrime.setTitle("F'(x)");
//        series.setOnDataPointTapListener(new OnDataPointTapListener() {
//            @Override
//            public void onTap(Series series, DataPointInterface dataPoint) {
//                double x = dataPoint.getX();
//                double y = dataPoint.getY();
//                cas.setPoint(x,y);
//                Toast.makeText(context,String.format("Point [x = %.2f, y = %.9f] on %s",x,y,series.getTitle().toString()),Toast.LENGTH_SHORT).show();
//            }
//        });

//        seriesPrime.setOnDataPointTapListener(new OnDataPointTapListener() {
//            @Override
//            public void onTap(Series series, DataPointInterface dataPoint) {
//                double x = dataPoint.getX();
//                double y = dataPoint.getY();
//                cas.setPoint(x,y);
//                Toast.makeText(context,String.format("Point [x = %.2f, y = %.9f] on %s",x,y,series.getTitle().toString()),Toast.LENGTH_SHORT).show();
//            }
//        });


        series.setThickness(10);
        series.setColor(Color.BLUE);

        seriesPrime.setThickness(10);
        seriesPrime.setColor(Color.RED);
    }

    public LineGraphSeries getSeries() {
        return series;
    }
    public LineGraphSeries getSeriesPrime() {
        return seriesPrime;
    }



    public boolean isAFunction()
    {
        double commonX = random.nextDouble();
        double y1 = this.getValue(commonX);
        double y2 = this.getValue(commonX-Calculator.ERRORCODE);
        return !(y1 == y2);
    }

    public String getEquation()
    {
        String equation = String.format("y = %s",this.function.getFunction());
        return equation;
    }


    public double getValue(double x)
    {
        this.savedValue = this.calculator.getValue(x);
        return this.savedValue;
    }

    public double getSavedValue()
    {
        return this.savedValue;
    }

    public String getFraction()
    {
        return this.calculator.decimalToFraction(this.savedValue);
    }








}
