package com.mohyaghoub.calculator;

import android.graphics.Color;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class CAS {

    private ArrayList<Point> xints;
    private double yint;
    private ArrayList<Point> Max;
    private ArrayList<Point> SepMax;
    private ArrayList<Point> SepMin;
    private ArrayList<Point> Min;
    private ArrayList<Point> VerticalASYM;
    private PointsGraphSeries series;
    private boolean pointSelectionIsOn;
    private String operationManual;



    //update


    CAS()
    {
        initObjects();
    }


    private void initObjects()
    {
        xints = new ArrayList<>();
        yint = 0;
        Max = new ArrayList<>();
        Min = new ArrayList<>();
        SepMax = new ArrayList<>();
        SepMin = new ArrayList<>();
        VerticalASYM = new ArrayList<>();
        series = new PointsGraphSeries();
        series.setTitle("Selected point");
        series.setColor(Color.BLUE);
        this.pointSelectionIsOn = false;
        this.operationManual = "";
    }





    public void addXint(double x, double y)
    {
        Point p = new Point(x,y);
        this.xints.add(p);
    }
    public void addMax(double x,double y)
    {
        Point p = new Point(x,y);
        this.Max.add(p);
    }
    public void addMin(double x,double y)
    {
        Point p = new Point(x,y);
        this.Min.add(p);
    }
    public void addSepMin(double x,double y)
    {
        Point p = new Point(x,y);
        this.SepMin.add(p);
    }
    public void addSepMax(double x,double y)
    {
        Point p = new Point(x,y);
        this.SepMax.add(p);
    }

    public void addVerticalASYM(double x,double y)
    {
        Point p = new Point(x,y);
        this.VerticalASYM.add(p);
    }

    public ArrayList<Point> getMinimum()
    {
        return this.Min;
    }
    public ArrayList<Point> getMaximum()
    {
        return this.Max;
    }


    public void setYint(double y)
    {
        this.yint = y;
    }


    public PointsGraphSeries getSeries()
    {
        return this.series;
    }

    public void setPoint(double x, double y)
    {
        DataPoint dp[] = new DataPoint[1];
        dp[0] = new DataPoint(x,y);
        series.setTitle("Selected point");
        series.resetData(dp);
    }

    public void setData(DataPoint[] dataPoint,String title)
    {
        try {
            this.series.setTitle(title);
            this.series.resetData(dataPoint);
        }catch(Exception e)
        {

        }
    }




    public String getXints()
    {
        String xints = "";
        String sorry = "Sorry, we could not find an x-intercept in this interval.";

        double listX[] = new double[this.xints.size()];
        int p = 0;

        for(Point point:this.xints)
        {
            if(!point.isDeleted())
            {
                listX[p] = point.getX();
                p++;
            }
        }
        Arrays.sort(listX);
        for(int i = 0;i<listX.length;i++)
        {
            xints+= String.format("x(%d) = %.10f\n",i+1,listX[i]);
        }

        if(xints.equals(""))
        {
            return sorry;
        }
        else{
            return xints;
        }
    }
    public ArrayList<Point> getXintList()
    {
        return this.xints;
    }


    public String getYint()
    {
        return String.format("x = 0, y = %.9f",this.yint);
    }


    public String getMax()
    {
        String max = "";
        String sorry = "Sorry, we could not find a maximum in this interval.";
        int number = 1;

        for(Point point:this.Max)
        {
            if(!point.isDeleted())
            {
                max+= String.format("%d- x = %.7f , y = %.7f\n",number,point.getX(),point.getY());
                number++;
            }
        }
        if(max.equals(""))
        {
            return sorry;
        }
        else{
            return max;
        }
    }



    public String getMin()
    {
        String min = "";
        String sorry = "Sorry, we could not find a minimum in this interval.";
        int number = 1;

        for(Point point:this.Min)
        {
            if(!point.isDeleted())
            {
                min+= String.format("%d- x = %.7f , y = %.7f\n",number,point.getX(),point.getY());
                number++;
            }
        }
        if(min.equals(""))
        {
            return sorry;
        }
        else{
            return min;
        }
    }

    public void updateMinMax(Calculator calculator)
    {
        AddSeps();
        int actualSize = this.getActualSizeOfArray(this.Min);
        double listXMin[] = new double[actualSize];
        int p = 0;
        for(Point point:this.Min)
        {
            if(!point.isDeleted())
            {
                listXMin[p] = point.getX();
                p++;
            }
        }
        Arrays.sort(listXMin);
        this.Min = new ArrayList<>();
        for(int i = 0;i<listXMin.length;i++)
        {
            double x = listXMin[i];
            double y = calculator.getValue(x);
            Point point = new Point(x,y);
            this.Min.add(point);
        }


        //maximum update
        actualSize = this.getActualSizeOfArray(this.Max);
        double listXMax[] = new double[actualSize];
        p = 0;
        for(Point point:this.Max)
        {
            if(!point.isDeleted())
            {
                listXMax[p] = point.getX();
                p++;
            }
        }
        Arrays.sort(listXMax);
        this.Max = new ArrayList<>();
        for(int i = 0;i<listXMax.length;i++)
        {
            double x = listXMax[i];
            double y = calculator.getValue(x);
            Point point = new Point(x,y);
            this.Max.add(point);
        }


    }
    private void AddSeps()
    {
        for(Point point: this.SepMin)
        {
            this.Min.add(point);
        }
        for(Point point: this.SepMax)
        {
            this.Max.add(point);
        }
    }



    public String getOperationManual()
    {
        return this.operationManual;
    }




    public void showXintsOnGraph()
    {
        int size = this.xints.size();
        DataPoint[] dataPoints = new DataPoint[size];
        double listXInOrder[] = new double[size];
        int p = 0;
        for(Point point:this.xints)
        {
            listXInOrder[p] = point.getX();
            p++;
        }
        Arrays.sort(listXInOrder);
        for(int i = 0;i<size;i++)
        {
            DataPoint dataPoint = new DataPoint(listXInOrder[i],0);
            dataPoints[i] = dataPoint;
        }
        this.setData(dataPoints,"x-intercepts");
    }

    public void showYintOnGraph()
    {
        this.setPoint(0,this.yint);
        this.series.setTitle("y-intercept");
    }


    public void showMaxOnGraph()
    {
        int actualSize = this.getActualSizeOfArray(this.Max);
        DataPoint[] dataPoints = new DataPoint[actualSize];
        int p = 0;
        for(int i = 0;i<this.Max.size();i++)
        {
            if(this.Max.get(i).isDeleted()==false)
            {
                DataPoint dataPoint = new DataPoint(this.Max.get(i).getX(),this.Max.get(i).getY());
                dataPoints[p] = dataPoint;
                p++;
            }
        }
        this.setData(dataPoints,"Maximums");
    }
    public void showMinOnGraph()
    {
        int actualSize = this.getActualSizeOfArray(this.Min);
        DataPoint[] dataPoints = new DataPoint[actualSize];
        int p = 0;
        for(int i = 0;i<this.Min.size();i++)
        {
            if(this.Min.get(i).isDeleted()==false)
            {
                DataPoint dataPoint = new DataPoint(this.Min.get(i).getX(),this.Min.get(i).getY());
                dataPoints[p] = dataPoint;
                p++;
            }
        }

        this.setData(dataPoints,"Minimums");
    }

    public void setOnPointSelection(boolean b)
    {
        this.pointSelectionIsOn = b;
    }

    public void setOperationManual(String operation)
    {
        this.operationManual = operation;
    }




    public void clear()
    {
        this.xints.clear();
        this.Max.clear();
        this.Min.clear();
        this.VerticalASYM.clear();
    }

    public boolean isPointSelectionIsOn()
    {
        return this.pointSelectionIsOn;
    }

    public int getActualSizeOfArray(ArrayList<Point> list)
    {
        int size = 0;
        for(Point point:list)
        {
            if(point.isDeleted()==false)
            {
                size++;
            }
        }
        return size;
    }

    public double[] getXintsList()
    {
        double xintsList[] = new double[this.xints.size()];
        int index = 0;
        for(Point point:this.xints)
        {
            xintsList[index] = point.getX();
            index++;
        }
        return xintsList;
    }





    //graph and find xintercepts yintercepts maximums and minimums and other things








}
