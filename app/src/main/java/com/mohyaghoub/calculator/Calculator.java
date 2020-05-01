package com.mohyaghoub.calculator;

import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;



public class Calculator {

    static final double LOWESTTOLERANCE = 1.0E-15;
	static final double MIDIUMROLERANCE = 1.0E-10;
	static final double OKTOLERANCE = 0.00000001;
	static final double tolerance = 0.0000001;
    static final double toleranceForSecond = 0.0001;
    static final double ERRORCODE = -0.1234592012;

	private ArrayList<Var> function;
	private String equation;
	private double initialGuessX;
	
	
	
	Calculator()
	{
		this.function = new ArrayList<>();
		this.equation = "";
		this.initialGuessX = 0;
	}
	 
	
	private void separateAddSub(String polynomial)
	{
		int numpm = pmCount(polynomial);
		int location[] = new int[numpm];
		
		int p =0;
		int isInside = 0;
		if(numpm>0)
		{
			for(int i=0;i<polynomial.length();i++)
			{
				if(polynomial.charAt(i)=='(')
				{
					isInside++;
				}
				else if(polynomial.charAt(i)==')')
				{
					isInside--;
				}
				else if(i!=0)
				{
					if(polynomial.charAt(i)=='+'&&p<numpm&&isInside==0)
					{
						location[p] = i;
						p++;
					}
					else if(polynomial.charAt(i)=='-'&&p<numpm&&isInside==0&&isMinus(polynomial.charAt(i-1)))
					{
						location[p] = i;
						p++;
					}
				}
			}
		
			
		}
		int z = 0;
		for(int i = 0;i<=location.length;i++)
		{
			if(i!=location.length)
			{
				Var v = new Var(polynomial.substring(z, location[i]));
				z = location[i];
				function.add(v);
			}
			else {
				Var v = new Var(polynomial.substring(z));
				function.add(v);
			}
		}
		
	}

	private boolean isMinus(char c)
	{
        return !MathFunctions.negativeAndMinus.contains(c + "");
    }

	
	private int pmCount(String str)
	{
		int isInside = 0;
		int num  =0;
		for(int i = 0;i<str.length();i++)
		{
			if(str.charAt(i)=='(')
			{
				isInside++;
			}
			else if(str.charAt(i)==')')
			{
				isInside--;
			}
			else if(i!=0)
			{
				if(isInside==0&&(str.charAt(i)=='+'))
		  		{
				num++;
				}
				else if(isInside==0&&str.charAt(i)=='-'&&isMinus(str.charAt(i-1)))
				{
					num++;
				}
			}
		}
		return num;
	}
	
	public static String removeSpaces(String str)
	{
		if(str.isEmpty())
		{
			return str;
		}
		else if(str.charAt(0)==' '||str.charAt(0)=='\n')
		{
			return removeSpaces(str.substring(1));
		}
		else if(str.charAt(0)=='E')
		{
			return "*10^"+removeSpaces(str.substring(1));
		}
		return str.charAt(0)+ removeSpaces(str.substring(1));
	}



	//getters
	public double getValue(double x)
	{
		double total = 0;
		for(Var v:function)
		{
			total+=v.getValueFromBase(x);
		}
		return total;
	}
	public String getEquation()
	{
		return this.equation;
	}
	public double getNumericalDerivative(double x)
	{
		double answer = 0;
		try {
			answer = (this.getValue(x+ tolerance)-this.getValue(x- tolerance))/(2* tolerance);
		}catch(Exception e)
		{

		}
		return answer;
	}

	public double getNumericalDerivativeSecond(double x)
	{
		double answer = 0;
		try {
			answer = (((((this.getValue(x+ toleranceForSecond))) - ((2*this.getValue(x)))+ (this.getValue(x- toleranceForSecond))))/(Math.pow(toleranceForSecond, 2)));
		}catch(Exception e)
		{

		}
		return answer;
	}
	
	
	//setters
	public void setEquation(String equation)
	{
		this.function = new ArrayList<>();
		this.equation = equation;
		checkBrackets();
		this.separateAddSub(removeSpaces(this.equation));
	}


	private void checkBrackets()
	{
		int numberOfBrackets = 0;
		for(int i = 0;i<this.equation.length();i++){
			if(this.equation.charAt(i) == '('){
				numberOfBrackets++;
			}else if(this.equation.charAt(i) == ')'){
				numberOfBrackets--;
			}
		}
		if(numberOfBrackets!=0){
			while(numberOfBrackets<0){
				this.equation = "(" + this.equation;
				numberOfBrackets++;
			}
			while(numberOfBrackets>0){
				this.equation+=")";
				numberOfBrackets--;
			}
		}


	}



	//calculate
	public void calculate()
	{
		try {
			this.separateAddSub(this.equation);
		}catch(Exception e)
		{
		}
	}
	
	
	//hasVar
	
	private boolean hasVar(String equation)
	{
		if(equation.isEmpty())
		{
			return false;
		}
		else if((equation.charAt(0)>=65&&equation.charAt(0)<=90)||(equation.charAt(0)>=97&&equation.charAt(0)<=122))
		{
			return true;
		}
		return hasVar(equation.substring(1));
	}
	
	
	//function

	public double NM(double x)
	{
		this.initialGuessX = x;
		return this.NewtonMethod(x,this.getValue(x),this.getNumericalDerivative(x),0,true);
	}

	public double NMFFD(double x)
	{
		this.initialGuessX = x;
		return this.NewtonMethodForFirstD(x,this.getNumericalDerivative(x),this.getNumericalDerivativeSecond(x),0,true);
	}




	public double NewtonMethod(double x,double f, double fprime,int repeat,boolean permission) throws StackOverflowError
	{
		double multiplicity = 1;
		if(this.function.size()<=1&&permission==true)
		{
			multiplicity = this.getMultiplicity(x);
			if(multiplicity==0)
            {
                multiplicity = 1;
            }
		}
		double x1 = (x)-(multiplicity*(f/fprime));
		if((Math.abs(f)<Calculator.LOWESTTOLERANCE||this.percentageCheck(x1,x))&&repeat!=0)
		{
			return x;
		}
		else if(fprime==0)
		{
		    return ERRORCODE;
		}
		else if(multiplicity!=1&&repeat>=10)
		{
			return NewtonMethod(initialGuessX,this.getValue(initialGuessX),this.getNumericalDerivative(initialGuessX),0,false);
		}
		else if(repeat==49&&this.percentageCheckOKTOLERANCE(x1,x))
		{
			return x;
		}
		else if(repeat>=50)
		{
			return ERRORCODE;
		}
		else
		{
			return NewtonMethod(x1,this.getValue(x1),this.getNumericalDerivative(x1),repeat+1,permission);
		}
	}

	public double NNM(double x, TextView tv)
    {
        tv.setText("");
        return this.NormalNewtonMethod(x,this.getValue(x),this.getNumericalDerivative(x),0,tv);
    }

    private double NormalNewtonMethod(double x,double f,double fprime,int repeat,TextView tv)
    {
        double x1 = (x)-((f/fprime));

        String xText = String.format("x(%d) = %f",repeat+1,x);
        String fText = String.format("f = %f",f);
        String fpText = String.format("f' = %f",fprime);
        String x1Text = String.format("x1(%d) = %f",repeat+1,x1);

        String combined = String.format("\n%s\n%s\n%s\n%s\n____________________",xText,fText,fpText,x1Text);
        tv.setText(tv.getText()+combined);

        if((Math.abs(f)<Calculator.LOWESTTOLERANCE||this.percentageCheck(x1,x)))
        {
            return x;
        }
        else if(fprime==0)
        {
            return ERRORCODE;
        }
        else if(repeat>=50)
        {
            return ERRORCODE;
        }
        else
        {
            return NormalNewtonMethod(x1,this.getValue(x1),this.getNumericalDerivative(x1),repeat+1,tv);
        }
    }






    public double NewtonMethodForFirstD(double x,double f, double fprime,int repeat,boolean permission) throws StackOverflowError
    {
        double multiplicity = 1;
        if(this.function.size()<=1&&permission==true)
        {
            multiplicity = this.getMultiplicity(x)-1;
            if(multiplicity==0)
            {
                multiplicity = 1;
            }
        }
        double x1 = (x)-(multiplicity*(f/fprime));
        if((Math.abs(f)<Calculator.LOWESTTOLERANCE||this.percentageCheck(x1,x))&&repeat!=0)
        {
            return x;
        }
        else if(fprime==0)
        {
            return x;
        }
		else if(multiplicity!=1&&repeat>=10)
		{
			return NewtonMethodForFirstD(initialGuessX, this.getNumericalDerivative(initialGuessX), this.getNumericalDerivativeSecond(initialGuessX),0,false);
		}
        else if(repeat==49&&this.percentageCheckOKTOLERANCE(x1,x))
		{
			return x;
		}
        else if(repeat>=50)
        {
            return ERRORCODE;
        }
        else
        {
            return NewtonMethodForFirstD(x1, this.getNumericalDerivative(x1), this.getNumericalDerivativeSecond(x1),repeat+1,permission);
        }
    }

    public boolean percentageCheck(double x,double preX)
    {
        double percentageError = Math.abs((Math.abs(preX - x)/x));
        return percentageError < Calculator.MIDIUMROLERANCE;
    }

    public boolean differenceIsLowTolerance(double value,double anotherValue)
	{
		double difference = Math.abs(Math.max(value,anotherValue)-Math.min(value,anotherValue));
        return difference <= 0.1;
	}


	public boolean percentageCheckOKTOLERANCE(double x,double preX)
	{
		double percentageError = Math.abs((Math.abs(preX - x)/x));
        return percentageError <= Calculator.OKTOLERANCE;
	}



    //integrals
	public String simpsonRule(double lowerLimit,double upperLimit,double subintervals,boolean lowIsHigher)
	{
		double h = (upperLimit-lowerLimit)/subintervals;
		double insideBracket = 0;
		double absInsideBracket = 0;
		boolean quad = false;
		for(double i = lowerLimit;i<=upperLimit;i+=h)
		{
			if(i==lowerLimit||i==upperLimit)
			{
			    double y = this.getValue(i);
				insideBracket+=y;
                absInsideBracket+=Math.abs(y);
				quad = true;
			}
			else if(quad==true)
			{
				quad = false;
				double y = this.getValue(i)*4;
				insideBracket+=(y);
                absInsideBracket+=Math.abs(y);
			}
			else if(quad==false)
			{
				quad = true;
				double y = this.getValue(i)*2;
				insideBracket+=(y);
                absInsideBracket+=Math.abs(y);
			}
		}
		double answer = (h/3)*insideBracket;
		double averageValue = answer/(upperLimit-lowerLimit);
		double area = (h/3)*absInsideBracket;
        String simsonForAll = "";
		if(lowIsHigher==false)
        {
           simsonForAll = String.format("[%.2f,%.2f]\nIntegrals: %.3f\nAverage value: %.3f\nArea: %.3f",lowerLimit,upperLimit,answer,averageValue,area);
        }
        else{
            simsonForAll = String.format("[%.2f,%.2f]\nIntegrals: %.3f\nAverage value: %.3f\nArea: %.3f",lowerLimit,upperLimit,-answer,averageValue,area);
        }
	    return simsonForAll;
	}


	public String simpsonRuleShowWork(double leftBound,double rightBound,double subintervals,boolean lowIsHigher)
	{
		if(leftBound==rightBound)
		{
			return " = 0";
		}
		double h = (rightBound-leftBound)/subintervals;
		double insideBracket = 0;
		double absInsideBracket = 0;
		boolean quad = false;

		String insideBracketWork = "";

		for(double i = leftBound;i<=rightBound;i+=h)
		{
			if(i==leftBound||i==rightBound)
			{
				double y = this.getValue(i);
				insideBracket+=y;
				absInsideBracket+=Math.abs(y);

				//show work
				if(subintervals<100)
				{
					String eq = String.format("%.2f+",y);
					insideBracketWork+=eq;
				}


				quad = true;
			}
			else if(quad==true)
			{
				quad = false;
				double y = this.getValue(i)*4;
				insideBracket+=(y);

				if(subintervals<100)
				{
					String eq = String.format("4(%.2f)+",y/4);
					insideBracketWork+=eq;
				}

				absInsideBracket+=Math.abs(y);
			}
			else if(quad==false)
			{
				quad = true;
				double y = this.getValue(i)*2;
				insideBracket+=(y);

				if(subintervals<100)
				{
					String eq = String.format("2(%.2f)+",y/2);
					insideBracketWork+=eq;
				}

				absInsideBracket+=Math.abs(y);
			}
		}
		double answer;
		if(lowIsHigher)
		{
			answer = -(h/3)*insideBracket;
		}
		else
		{
			answer = (h/3)*insideBracket;
		}

		String wholeWork = "";
		if(subintervals<100)
		{
			wholeWork = String.format("(%.2f/3)(%s)\n = %.10f",h,insideBracketWork.substring(0,insideBracketWork.length()-1),answer);
		}
		else
		{
			wholeWork = String.format("(%.2f/3)(%.6f)\n = %.10f",h,insideBracket,answer);
		}

		return wholeWork;
	}


    public String trapezoidRuleShowWork(double leftBound,double rightBound,double subintervals,boolean lowIsHigher)
    {
        if(leftBound==rightBound)
        {
            return " = 0";
        }
        double h = (rightBound-leftBound)/subintervals;
        double insideBracket = 0;
        double absInsideBracket = 0;


        String insideBracketWork = "";

        for(double i = leftBound;i<=rightBound;i+=h)
        {
            if(i==leftBound||i==rightBound)
            {
                double y = this.getValue(i);
                insideBracket+=y;
                absInsideBracket+=Math.abs(y);

                //show work
                if(subintervals<100)
                {
                    String eq = String.format("%.2f+",y);
                    insideBracketWork+=eq;
                }
            }
            else
            {
                double y = this.getValue(i)*2;
                insideBracket+=(y);

                if(subintervals<100)
                {
                    String eq = String.format("2(%.2f)+",y/2);
                    insideBracketWork+=eq;
                }

                absInsideBracket+=Math.abs(y);
            }
        }
        double answer;
        if(lowIsHigher)
        {
            answer = -(h/2)*insideBracket;
        }
        else
        {
            answer = (h/2)*insideBracket;
        }

        String wholeWork = "";
        if(subintervals<100)
        {
            wholeWork = String.format("(%.2f/2)(%s)\n = %.10f",h,insideBracketWork.substring(0,insideBracketWork.length()-1),answer);
        }
        else
        {
            wholeWork = String.format("(%.2f/2)(%.6f)\n = %.10f",h,insideBracket,answer);
        }

        return wholeWork;
    }



    //RAM
	public double LRAM(double subintervals,double leftEnd,double rightEnd)
	{
		double difference =  Math.abs(rightEnd-leftEnd);
		double totalArea = 0;

		double width = difference/subintervals;

		for(int i = 0;i<subintervals;i++)
		{
			double currLength = getValue((leftEnd+(width*i)));
			totalArea+= (width*currLength);
		}


		return totalArea;
	}

	//RRAM
	public double RRAM(double subintervals,double leftEnd,double rightEnd)
	{
		double difference =  Math.abs(rightEnd-leftEnd);
		double totalArea = 0;

		double width = difference/subintervals;

		for(int i = 1;i<subintervals+1;i++)
		{
			double currLength = getValue((leftEnd+(width*i)));
			totalArea+= (width*currLength);
		}

		return totalArea;
	}

	//MRAM
	public double MRAM(double subintervals,double leftEnd,double rightEnd)
	{
		double difference =  Math.abs(rightEnd-leftEnd);
		double totalArea = 0;

		double width = difference/subintervals;
		double firstMP = (leftEnd+(width/2));

		for(int i = 0;i<subintervals;i++)
		{
			double currLength = getValue((firstMP+(width*i)));
			totalArea+= (width*currLength);
		}
		return totalArea;
	}

	public String getRAMS(double leftEnd,double rightEnd,double subintervals)
	{
		double LRAM = this.LRAM(subintervals,leftEnd,rightEnd);
		double MRAM = this.MRAM(subintervals, leftEnd, rightEnd);
		double RRAM = this.RRAM(subintervals, leftEnd, rightEnd);

		String rams = String.format("LRAM = %f\nMRAM = %f\nRRAM = %f",LRAM,MRAM,RRAM);
		return rams;
	}

	public String nthFibonacci(int n)
	{
		double goldenRatio = (1+Math.sqrt(5))/2;
		double nthFib = (Math.pow(goldenRatio,n)- Math.pow((-1/goldenRatio),n))/Math.sqrt(5);
		String str = String.format("= %d",(int)nthFib);
		return str;
	}


    public double getMultiplicity(double x)
    {
        for(Var v:this.function)
        {
            return v.getMultiplicity(x);
        }
        return 1;
    }




    //decimal to fraction
	public String decimalToFraction(double number)
	{
		int numberOfDecimals = this.howManyDecimals(number+"", false);
		double numerator;
		double denominator;
		numerator = (number*Math.pow(10, numberOfDecimals));
		denominator = (Math.pow(10, numberOfDecimals));
		int gcd = this.GCD((int)numerator, (int)denominator);
		numerator/=gcd;
		denominator/=gcd;
		return String.format("%.0f / %.0f",numerator,denominator);
	}

	private int howManyDecimals(String number,boolean hadPoint)
	{
		if(number.isEmpty())
		{
			return 0;
		}
		else if(number.charAt(0)=='.')
		{
			return howManyDecimals(number.substring(1),true);
		}
		else if(hadPoint==true)
		{
			return 1+howManyDecimals(number.substring(1),hadPoint);
		}
		return howManyDecimals(number.substring(1),hadPoint);
	}
	public int GCD(int a, int b)
	{
		if (b==0) return a;
		return GCD(b,a%b);
	}

}
