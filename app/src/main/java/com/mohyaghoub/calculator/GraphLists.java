package com.mohyaghoub.calculator;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.util.Arrays;
import java.util.HashMap;

public class GraphLists extends AppCompatActivity {

    private GraphView graph;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private List list1,list2;
    private HashMap<Double,Double> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_lists);
        setWindowRatios();
        initGraph();
        initHashMap();
        generateAndSaveData();
        CreateSeries();
    }

    private void initHashMap()
    {
        hashMap = new HashMap<>();
    }


    protected void generateAndSaveData()
    {
        int number = 0;
        for(List list:LoadActualLists.savedLists)
        {
            if(list.isSelected())
            {
                if(number==0)
                {
                    list1 = list;
                    list1.generateAndSaveData();
                }
                else
                {
                    list2 = list;
                    list2.generateAndSaveData();
                    break;
                }
                number++;
            }
        }
    }


    protected void CreateSeries()
    {
        double list1Values[] = list1.getDataDouble();
        double list2Values[] = list2.getDataDouble();
        int lowestSize = Math.min(list1Values.length,list2Values.length);
        double XArray[] = new double[lowestSize];
        for(int i =0;i<lowestSize;i++)
        {
            this.hashMap.put(list1Values[i],list2Values[i]);
            XArray[i] = list1Values[i];
        }

        Arrays.sort(XArray);

        LineGraphSeries lineGraphSeries = new LineGraphSeries();
        DataPoint[] dataPoints = new DataPoint[lowestSize];
        for(int i = 0;i<lowestSize;i++)
        {
            double x = XArray[i];
            DataPoint dataPoint = new DataPoint(x,hashMap.get(x));
            dataPoints[i] = dataPoint;


        }
        lineGraphSeries.resetData(dataPoints);
        lineGraphSeries.setColor(Color.BLUE);
        lineGraphSeries.setTitle(String.format("%s vs %s",list2.getName(),list1.getName()));
        lineGraphSeries.setDrawDataPoints(true);
        lineGraphSeries.setDataPointsRadius(20);
        this.graph.addSeries(lineGraphSeries);
        CreateSecondSeries();
    }


    protected void CreateSecondSeries()
    {
        this.hashMap.clear();
        double list1Values[] = list1.getDataDouble();
        double list2Values[] = list2.getDataDouble();
        int lowestSize = Math.min(list1Values.length,list2Values.length);
        double XArray[] = new double[lowestSize];
        for(int i =0;i<lowestSize;i++)
        {
            this.hashMap.put(list2Values[i],list1Values[i]);
            XArray[i] = list2Values[i];
        }

        Arrays.sort(XArray);

        LineGraphSeries lineGraphSeries = new LineGraphSeries();
        DataPoint[] dataPoints = new DataPoint[lowestSize];
        for(int i = 0;i<lowestSize;i++)
        {
            double x = XArray[i];
            DataPoint dataPoint = new DataPoint(x,hashMap.get(x));
            dataPoints[i] = dataPoint;


        }
        lineGraphSeries.resetData(dataPoints);
        lineGraphSeries.setColor(Color.RED);
        lineGraphSeries.setTitle(String.format("%s vs %s",list1.getName(),list2.getName()));
        lineGraphSeries.setDrawDataPoints(true);
        lineGraphSeries.setDataPointsRadius(20);
        this.graph.addSeries(lineGraphSeries);
    }


    protected void initGraph()
    {
        this.graph = findViewById(R.id.GraphView_Lists);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getViewport().setMaxX(this.maxX);
        graph.getViewport().setMinX(this.minX);

        graph.getViewport().setMinY(this.minY);
        graph.getViewport().setMaxY(this.maxY);

        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
//        graph.setTitle(String.format("%s vs %s graph",list1.getName(),list2.getName()));
        graph.setTitleTextSize(55.0f);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

    }



    protected void setWindowRatios()
    {
        String windowRatios = LoadList.readFile(FileNames.windowRatioFileName,this);
        if(windowRatios.length()>0)
        {
            String splitRatios[] = windowRatios.split("\\|");
            if(splitRatios.length==4)
            {
                minX = Double.parseDouble(splitRatios[0]);
                maxX = Double.parseDouble(splitRatios[1]);
                minY = Double.parseDouble(splitRatios[2]);
                maxY = Double.parseDouble(splitRatios[3]);
            }
            else{
                minX = -10;
                maxX = 10;
                minY = -10;
                maxY = 10;
            }
        } else {
            minX = -10;
            maxX = 10;
            minY = -10;
            maxY = 10;
        }
    }

}
