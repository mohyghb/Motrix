package com.mohyaghoub.calculator;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.ArrayList;
import java.util.Random;


public class InformationCollector{

    //equation
    private String equation;
    private String unParsedEquation;
    private Context mainContext;
    private boolean isAGoodEquation;
    private boolean isInCalculatorMode;
    private boolean wasInCalculatorMode;
    private boolean isAFunction;
    private boolean dontContinue;

    private boolean isInActivity;
    //domain
    private double accuracy;
    private ArrayList<Double> potentialMaxMin, xints;
    //name of the equation (in case they want to save the equation)
    private String name;
    private CAS cas;

    private String result;
    private String history;

    //graph
    private boolean findPoints;
    private int maxDataPoints;
    private LineGraphSeries<DataPoint> function,derivative;
    private GraphView graphView;
    private double graphingAccuracy;
    private ScrollView History_ScrollView,DisplayText_ScrollView;
    private Animation upToDown,opposite;


    private boolean xint,yint,max,min,value,domain,range,graph,derivativeGraph;


    InformationCollector(Context context, GraphView graphView, ScrollView History_ScrollView,ScrollView DisplayText_ScrollView)
    {
        this.mainContext = context;
        this.graphView = graphView;
        this.DisplayText_ScrollView = DisplayText_ScrollView;
        this.History_ScrollView = History_ScrollView;
        initClass();
    }

    private void initClass()
    {
        this.equation = "";
        this.unParsedEquation = "";
        this.result = "";
        this.history = "";
        this.isAGoodEquation = false;
        this.isInCalculatorMode = false;
        this.wasInCalculatorMode = false;
        this.isAFunction = false;
        this.isInActivity = false;
        this.dontContinue = false;
        this.name = "";

        this.cas = new CAS();
        this.potentialMaxMin = new ArrayList<>();
        this.xints = new ArrayList<>();
        setAccuracy();
        initDataPoints();
        initGraphView();
        upToDown = AnimationUtils.loadAnimation(this.mainContext,R.anim.uptodown);
        opposite = AnimationUtils.loadAnimation(this.mainContext,R.anim.opposite);
        initFalseBooleans();
    }

    public boolean getContinue()
    {
        return this.dontContinue;
    }

    public void setContinue(boolean b)
    {
        this.dontContinue = b;
    }


    private void initFalseBooleans()
    {
        this.xint = false;
        this.yint= false;
        this.max = false;
        this.min = false;
        this.value = false;
        this.domain = false;
        this.range = false;
    }

    public void turnTrue(String ope)
    {
        switch (ope)
        {
            case"min":
                this.min = true;
                break;
            case"max":
                this.max = true;
                break;
            case"xint":
                this.xint = true;
                break;
            case"yint":
                this.yint = true;
                break;
            case"domain":
                this.domain = true;
                break;
            case"range":
                this.range = true;
                break;
            case"value":
                this.value = true;
                break;
        }
    }

    public void update(Context context, GraphView graphView, ScrollView History_ScrollView,ScrollView DisplayText_ScrollView)
    {
        this.mainContext = context;
        this.graphView = graphView;
        this.DisplayText_ScrollView = DisplayText_ScrollView;
        this.History_ScrollView = History_ScrollView;
        this.graph = false;
        this.isInActivity = false;
        if(this.isInCalculatorMode)
        {
            this.setEquationFromCalculator(CalculatorActivity.savedEquation);
            this.isInCalculatorMode = false;
            this.wasInCalculatorMode = true;
        }
        else if(this.isAGoodEquation)
        {
            this.cas = new CAS();
            this.potentialMaxMin = new ArrayList<>();
            this.xints = new ArrayList<>();
            this.graphingAccuracy = 0.15;
            setAccuracy();
            initDataPoints();
            restart();
            initFalseBooleans();
            initGraphView();
            LoadList.calculator.setEquation(this.equation);
            this.findMaxMin();
            this.findIntercepts();
            this.findYintercept();
        }
    }





    private void appear(View v,Animation animation)
    {
        v.startAnimation(animation);
        v.setVisibility(View.VISIBLE);
    }

    private void disappear(View v,Animation animation)
    {
        v.setVisibility(View.INVISIBLE);
        v.startAnimation(animation);
    }

    private void initGraphView()
    {
        this.graph = false;
        this.derivativeGraph = false;
        graphView.setBackgroundColor(Color.WHITE);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(Ratios.maxX);
        graphView.getViewport().setMinX(Ratios.minX);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMaxY(Ratios.maxY);
        graphView.getViewport().setMinY(Ratios.minY);
        graphView.getViewport().setScrollable(false);
        graphView.getViewport().setScrollableY(false);
        graphView.setTitleTextSize(55.0f);
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    private void initDataPoints()
    {
        this.maxDataPoints = (int)((Math.abs(Ratios.maxX-Ratios.minX))/this.graphingAccuracy);
        this.function = new LineGraphSeries<>();
        this.derivative = new LineGraphSeries<>();
    }

    //adding result
    private void addResult(String title,String context)
    {
        this.result+= String.format("%s %s\n",title,context);
    }

    public String getResult()
    {
        if(result.isEmpty())
        {
            return "Sorry, I could not catch what you said.";
        }
        else
        {
            return result;
        }
    }

    public void clearResults()
    {
        this.result = "";
        initFalseBooleans();
    }

    public void addHistory(String history)
    {
        if(!history.equals(""))
        {
            this.history+= String.format("\n%s",history);
        }
    }


    public String getHistory()
    {
        return this.history;
    }





    //getters
    public String getEquation() {
        return equation;
    }



    public String getName() {
        return name;
    }


    //setters


    public void setEquation(String equation) {
        this.unParsedEquation = equation;
        SmartMathText smt = new SmartMathText(equation);
        String newEquation = SmartParser.finalCheck(smt.getText(),false);
        if(LoadList.isAGoodFunction(newEquation,mainContext))
        {
            dontContinue = false;
            this.equation = LoadList.goodEquation;
            this.isAGoodEquation = true;
            this.graph = false;
            this.derivativeGraph = false;
            if(this.equation.charAt(this.equation.length()-1)=='+')
            {
                this.equation = this.equation.substring(0,this.equation.length()-1);
            }
            LoadList.calculator.setEquation(this.equation);
            this.isAFunction = this.isAFunction();
            addResult("Equation\n", this.equation+"\n");
            restart();
            this.findMaxMin();
            this.findIntercepts();
            this.findYintercept();
        }
        else
        {
            dontContinue = true;
            clearResults();
            if(newEquation.isEmpty())
            {
                addResult("Sorry","We could not hear an equation from your speech.");
            }
            else
            {
                addResult("Sorry",String.format("Your equation '%s' is not a good expression.",newEquation));
            }
        }
    }

    public void setEquationFromCalculator(String equation)
    {
        if(LoadList.isAGoodFunction(equation,mainContext))
        {
            this.equation = equation;
            LoadList.calculator.setEquation(this.equation);
            this.isAGoodEquation = true;
            this.graph = false;
            this.derivativeGraph = false;
            this.isAFunction = this.isAFunction();

            addResult("Equation\n", "y = "+this.equation+"\n");
            restart();
            this.findMaxMin();
            this.findIntercepts();
            this.findYintercept();
        }
        else
        {
//          this.isAGoodEquation = false;
            clearResults();
            if(equation.isEmpty())
            {
                addResult("You need to enter something!","");
            }
            else
            {
                addResult("Sorry",String.format("Your equation '%s' is not a good expression.",equation));
            }
        }
    }




    private void restart()
    {
        cas = new CAS();
        this.potentialMaxMin = new ArrayList<>();
        this.xints = new ArrayList<>();
        this.function = new LineGraphSeries<>();
        this.derivative = new LineGraphSeries<>();
        this.graphView.removeAllSeries();
        this.graphView.setVisibility(View.INVISIBLE);
        initFalseBooleans();
    }




    public void setName(String name) {
        this.name = name;
    }


    public void getValue()
    {
        if(this.isAGoodEquation)
        {
            if(this.isAFunction)
            {
                this.giveAYCalc();
            }
            else if(!value)
            {
                addResult("Answer is",String.format("%.7f",LoadList.calculator.getValue(1)));
                this.value = true;
            }
        }
    }


    public void findIntercepts()
    {
        if(accuracy!=0&&isAGoodEquation)
        {
            initFindPoints();
            double x,y,contX,contY;
            x = Ratios.minX-this.accuracy;
            contX = Ratios.minX - this.graphingAccuracy;

            double prey =0;
            boolean exactZero = false;

            for(int i = 0;x<=Ratios.maxX;i++)
            {
                try {
                    x += this.accuracy;
                    y = LoadList.calculator.getValue(x);
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
                    if(contX<=Ratios.maxX)
                    {
                        contX+=this.graphingAccuracy;
                        contY = LoadList.calculator.getValue(contX);
                        function.appendData(new DataPoint(contX, contY), true, maxDataPoints);
                    }
                   // function.appendData(new DataPoint(x, y), true, maxDataPoints);
                }catch(ArithmeticException e)
                {

                }
            }
            saveXints();
        }
    }



    public void findMaxMin()
    {
        if(accuracy!=0&&isAGoodEquation)
        {
            double contX = Ratios.minX-this.graphingAccuracy;
            double x = Ratios.minX-this.accuracy;
            double prey = 0;
            double y,contY;
            for(int i= 0;x<=Ratios.maxX;i++)
            {
                try {
                    x += this.accuracy;
                    y = LoadList.calculator.getNumericalDerivative(x);
                    if (i == 0) {
                        prey = y;
                    } else {
                            if (this.signChanged(prey, y)) {
                                this.potentialMaxMin.add(x);
                            }
                            prey = y;
                    }

                    if(contX<=Ratios.maxX)
                    {
                        contX+=this.graphingAccuracy;
                        contY = LoadList.calculator.getNumericalDerivative(contX);
                        derivative.appendData(new DataPoint(contX, contY), true, maxDataPoints);
                    }

                }catch(ArithmeticException arit)
                {

                }
            }
            saveMaxMin();
        }

    }

    private void findYintercept()
    {
        cas.setYint(LoadList.calculator.getValue(0));
    }


    private void saveMaxMin()
    {
        for(Double d:this.potentialMaxMin)
        {
            this.MaxMin(d);
        }
    }

    public void saveXints()
    {
        for(Double x:this.xints)
        {
            double xint = LoadList.calculator.NM(x);
            if(hasThisXint(xint)==false&&xint!=Calculator.ERRORCODE&&(xint>=Ratios.minX&&xint<=Ratios.maxX))
            {
                if(this.intIsBetter(xint))
                {
                    double y = Math.abs(LoadList.calculator.getValue(xint));
                    double inty = Math.abs(LoadList.calculator.getValue((int)xint));
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
                    if(LoadList.calculator.getValue(intx)==0)
                    {
                        point.setDeleted(true);
                        cas.addSepMin(intx,0);
                        cas.addXint(intx,0);
                    }
                    else
                    {
                        double newXint = LoadList.calculator.NM(x);
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
                    if(LoadList.calculator.getValue(intx)==0)
                    {
                        point.setDeleted(true);
                        cas.addSepMax(intx,0);
                        cas.addXint(intx,0);
                    }
                    else
                    {
                        double newXint = LoadList.calculator.NM(x);
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

        cas.updateMinMax(LoadList.calculator);

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




    //adding results
    public void addGettingThings(String request)
    {
        if(this.isAGoodEquation)
        {
            switch (request)
            {
                case "Maximum":
                    if(!this.max)
                    {
                        this.addResult(request,"\n"+cas.getMax()+"\n");
                        this.max = true;
                    }
                    break;
                case "Minimum":
                    if(!this.min)
                    {
                        this.addResult(request, "\n" + cas.getMin()+"\n");
                        this.min = true;
                    }
                    break;
                case "x-intercepts":
                    if(!this.xint)
                    {
                        this.addResult(request,"\n"+cas.getXints()+"\n");
                        this.xint = true;
                    }
                    break;
                case "y-intercept":
                    if(!this.yint)
                    {
                        this.addResult(request,"\n"+cas.getYint()+"\n");
                        this.yint = true;
                    }
                    break;
                case "intercept":
                    if(!this.xint)
                    {
                        this.addResult("x-intercepts","\n"+cas.getXints()+"\n");
                        this.xint = true;
                    }
                    if(!this.yint)
                    {
                        this.addResult("y-intercept","\n"+cas.getYint()+"\n");
                        this.yint = true;
                    }
                    break;
                case"domain":
                    showDomain();
                    break;
            }
        }
    }


    public void openGraph()
    {
        if(this.isAGoodEquation&&!this.isInActivity)
        {
            addResult("Opening","Graph...");
            Intent intent = new Intent(mainContext,Graph.class);
            intent.putExtra("position", this.equation);
            mainContext.startActivity(intent);
            this.isInActivity = true;
        }
        else
        {
            addResult("Sorry","We could not open the graph because you have not said a valid equation.");
        }
    }

    public void openFunctions()
    {
        if(!this.isInActivity)
        {
            addResult("Opening","Functions...");
            Intent intent = new Intent(mainContext,FunctionActivity.class);
            mainContext.startActivity(intent);
            this.isInActivity = true;
        }
    }

    public void openLists()
    {
        if(!this.isInActivity)
        {
            addResult("Opening","Lists...");
            Intent intent = new Intent(mainContext,ShowListsActivity.class);
            mainContext.startActivity(intent);
            this.isInActivity = true;
        }

    }

    public void openWindowRatios()
    {
        if(!this.isInActivity)
        {
            addResult("Opening","Window ratios...");
            Intent intent = new Intent(mainContext,WindowActivity.class);
            mainContext.startActivity(intent);
            this.isInActivity = true;
        }

    }

    public void openCalculator()
    {
        if(!this.isInActivity)
        {
            Intent intent4 = new Intent(this.mainContext,CalculatorActivity.class);
            intent4.putExtra("leftOver","");
            intent4.putExtra("savedFunctions","");
            this.mainContext.startActivity(intent4);
            this.isInCalculatorMode = true;
            addResult("Opening Calculator","...");
            this.isInActivity = true;
        }
    }


    public void openTools()
    {
        if(this.isAGoodEquation)
        {
            Intent i = new Intent(this.mainContext,ToolsActivity.class);
            i.putExtra("EQUATION",this.equation);
            mainContext.startActivity(i);
        }
        else
        {
            Intent i = new Intent(this.mainContext,Tools_FunctionSelection.class);
            mainContext.startActivity(i);
        }
    }




    //showing
    public void showGraph()
    {
        if(this.isAGoodEquation)
        {
            if(accuracy!=0)
            {
                if(!graph)
                {
                    this.addResult("Here you are!","");
                    initGraphView();
                    this.graphView.removeSeries(this.function);
                    this.graphView.removeSeries(this.cas.getSeries());
                    this.graphView.setTitle("y = "+this.equation);
                    this.function.setColor(ContextCompat.getColor(this.mainContext,ColorsInJava.colors[5]));
                    this.function.setTitle("Function");
                    this.function.setThickness(13);
                    this.graphView.addSeries(function);
                    this.graphView.addSeries(cas.getSeries());
                    this.graphView.setVisibility(View.VISIBLE);
                    this.graph = true;
                }
                else
                {
                    addResult("Here you are!","");
                }
            }else
            {
                addResult("The window ratios are too small for us to show you the graph.","\n");
            }
        }
        else
        {
            addResult("Sorry we can not show you the graph at this moment because we could not find a valid equation.","\n");
        }

    }


    public void showDerivative()
    {
        if(this.isAGoodEquation)
        {
            if(accuracy!=0)
            {
                if(!this.derivativeGraph)
                {
                    this.addResult("Here you are!","");
                    initGraphView();
                    this.graphView.removeSeries(this.derivative);
                    this.graphView.removeSeries(this.cas.getSeries());
                    this.graphView.setTitle("y = "+this.equation);
                    this.derivative.setTitle("Derivative");
                    this.derivative.setColor(ContextCompat.getColor(this.mainContext,ColorsInJava.colors[0]));
                    this.derivative.setThickness(13);
                    this.graphView.addSeries(derivative);
                    this.graphView.addSeries(cas.getSeries());
                    this.graphView.setVisibility(View.VISIBLE);
                    this.derivativeGraph = true;
                }
                else
                {
                    addResult("Here you are!","");
                }
            }else
            {
                addResult("The window ratios are too small for us to show you the graph.","\n");
            }
        }
        else
        {
            addResult("Sorry we can not show you the graph at this moment because we could not find a valid equation.","\n");
        }

    }






    public void showEquation()
    {
        if(this.equation.equals(""))
        {
            this.addResult("","You have not said a valid equation yet.\n");
        }
        else
        {
            this.addResult("Equation is",this.equation);
        }
    }

    public void showHistory()
    {
        if(this.History_ScrollView.getVisibility()!=View.VISIBLE)
        {
            this.disappear(this.DisplayText_ScrollView,opposite);
            this.appear(this.History_ScrollView,upToDown);
            this.addResult("Opening","History...");
        }
        else
        {
            Toast.makeText(mainContext,"You can see the history right here on your screen!",Toast.LENGTH_SHORT).show();
        }
    }
    //hide history
    public void hideHistory()
    {
        if(this.History_ScrollView.getVisibility()==View.VISIBLE)
        {
            this.appear(this.DisplayText_ScrollView,upToDown);
            this.disappear(this.History_ScrollView,opposite);
        }
        else
        {
            Toast.makeText(mainContext,"History has been removed already!",Toast.LENGTH_SHORT).show();
        }
    }

    public void hideGraph()
    {
        if(this.graphView.getVisibility()==View.VISIBLE)
        {
            addResult("Removed the","Graph");
           this.graphView.setVisibility(View.INVISIBLE);
        }
        else
        {
            addResult("Graph is already hidden!","\n");
        }
    }


    public void showDomain()
    {
        if(!this.domain)
        {
            addResult("Domain\n",String.format("from %.4f to %.4f",Ratios.minX,Ratios.maxX));
            this.domain = true;
        }
    }

    public void showRange()
    {
        if(!this.range)
        {
            addResult("Range\n",String.format("from %.4f to %.4f",Ratios.minY,Ratios.maxY));
            this.range = true;
        }
    }

    public void showWindowRatios()
    {
        showDomain();
        showRange();
    }


    public void ChangeMode(int pos)
    {
        this.graph = false;
        Modes.currentMode = pos;
        addResult("Mode was changed to",Modes.CALCULATOR_MODES[pos]);
    }








    //function keys
    public void saveFunction()
    {
        if(isAGoodEquation)
        {
            addResult("Enter a name please","");
            LoadList.getAName(mainContext,this.equation);
        }
        else
        {
            addResult("Sorry","I have not heard a valid equation from you");
        }
    }


    public void giveAYCalc()
    {
        if(this.isAGoodEquation)
        {
            addResult("Here is a y-calculator!","\n so you can get the value from this!");
            LoadList.createDialogForYCALC(mainContext,this.equation);
        }
        else
        {
            addResult("You have not said a valid equation","so that I could give you a y-calculator.");
        }

    }


    public boolean wasInCalculatorMode()
    {
        if(this.wasInCalculatorMode)
        {
            this.wasInCalculatorMode = false;
            return true;
        }
        else
        {
            return false;
        }
    }


    public boolean isAFunction()
    {
        Random random = new Random();
        double commonX = random.nextDouble();
        double y1 = LoadList.calculator.getValue(commonX);
        double y2 = LoadList.calculator.getValue(commonX-Calculator.ERRORCODE);
        return !(y1 == y2);
    }





























    //
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


    private void setAccuracy()
    {
        double average = (Math.abs(Ratios.maxX)+Math.abs(Ratios.minX))/2;
        this.accuracy = average/1000;
        this.graphingAccuracy = average/100;
    }


    protected void MaxMin(double x)
    {
        double actualX = LoadList.calculator.NMFFD(x);

        double curry = LoadList.calculator.getValue(actualX);
        double prey = LoadList.calculator.getValue(actualX-Calculator.toleranceForSecond);
        double posty = LoadList.calculator.getValue(actualX+Calculator.toleranceForSecond);

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
                double cur = LoadList.calculator.getValue(newX);
                double pre = LoadList.calculator.getValue(newX-0.1);
                double post = LoadList.calculator.getValue(newX +0.1);

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
        double multiplicity = LoadList.calculator.getMultiplicity(1);
        return difference <= (multiplicity) * 0.01;
    }


    protected void initFindPoints()
    {
        double random;
        Random rand = new Random();
        for(int i = 0;i<100;i++)
        {
            random = rand.nextDouble();
            if(Math.abs(LoadList.calculator.getValue(random))>Calculator.LOWESTTOLERANCE)
            {
                this.findPoints = true;
                return;
            }
        }
        this.findPoints = false;
    }


    public CAS getCas()
    {
        return this.cas;
    }


}
