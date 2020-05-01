package com.mohyaghoub.calculator;

import java.util.ArrayList;

public class Var {

	private ArrayList<Var> base; 
	private ArrayList<Var> multiply; 
	private ArrayList<Var> divide;  
	private ArrayList<Var> power; 
	private ArrayList<Var> trigs;
	
	private String firstOpOutSide;
	private boolean thereIsAOperation;	

	
	private String equationNoBrackets;
	private boolean parseEquation;
	
	private String value;
	private String variable;
	private String function;
	
	private boolean ERROR;
	
	Var(String equation)
	{
		this.base = new ArrayList<Var>();
		this.multiply = new ArrayList<Var>();
		this.divide = new ArrayList<Var>();
		this.power = new ArrayList<Var>();
		this.trigs = new ArrayList<Var>();
		
		//numbers and variables
		this.value = "";
		this.variable = "";
		this.function = "";
		
		this.ERROR = false;
		
		//parse
//		try {
			this.parse(equation);
//		}catch (Exception e)
//		{
//
//		}
	}
	
	private void parse(String equation)
	{
		this.setFirstOp(equation);
		this.parseOuterBrackets(equation);
		
		if(!ERROR)
		{
			this.separateAddSub(this.equationNoBrackets);
			this.parseEquation(this.equationNoBrackets);
		}
	}
	
	
	//setters
	public void setFirstOp(String equation)
	{
		try {
			char[] chars = this.firstOperation(equation, 0).toCharArray();
			this.firstOpOutSide = chars[chars.length-1]+"";
			this.thereIsAOperation = true;
		}catch(Exception e)
		{
			this.thereIsAOperation = false;
		}
	}
	
	//void
	public void parseOuterBrackets(String equation)
	{
		int numParen = this.getParen(equation,0);
		if(numParen%2!=0)
		{
//			this.ERROR = true;
		}
		else if(numParen==2&&equation.startsWith("(")&&equation.endsWith(")"))
		{
			this.equationNoBrackets = equation.substring(1, equation.length()-1);
			this.setFirstOp(equationNoBrackets);
		}
		else if(numParen>2&&!this.thereIsAOperation&&equation.charAt(0)=='('&&!this.hasThisOutside(equation,"^",0))
		{
			sepMultipltyingParen(equation);
			this.ERROR = true;
		}
		else
		{
			this.equationNoBrackets = equation;
		}
	}
	private void moveOperation(String[] sep)
	{
		{
			if(this.firstOpOutSide.equals("*"))
			{
				Var x = new Var(sep[0]);
				Var xx = new Var(sep[1]);
				
				this.multiply.add(x);
				this.base.add(xx);
			}
			else if(this.firstOpOutSide.equals("/"))
			{
				Var x = new Var(sep[0]);
				Var xx = new Var(sep[1]);
				
				this.divide.add(xx);
				this.base.add(x);
			}
		}
		
	}
	private void sepMultipltyingParen(String equation)
	{
		int inParen = 0;
		for(int i = 0;i<equation.length();i++)
		{
			if(inParen==0&&i!=0)
			{
				String multiply = equation.substring(0, i);
				String base = equation.substring(i);

				Var mul = new Var(multiply);
				Var ba = new Var(base);

				this.multiply.add(mul);
				this.base.add(ba);
				return;

			}
			else if(equation.charAt(i)=='(')
			{
				inParen++;
			}
			else if(equation.charAt(i)==')')
			{
				inParen--;
			}

		}
	}
	
	
	
	
	//Strings
	public String firstOperation(String equation,int isInside)
	{
		if(equation.isEmpty())
		{
			return equation;
		}
		else if(equation.charAt(0)=='(')
		{
			return firstOperation(equation.substring(1),isInside+1);
		}
		else if(equation.charAt(0)==')')
		{
			return firstOperation(equation.substring(1),isInside-1);
		}
		else if(isInside==0&&this.isOperator(equation.charAt(0)))
		{
			return equation.charAt(0)+firstOperation(equation.substring(1),isInside);
		}
		return firstOperation(equation.substring(1),isInside);
	}
	private String firstFunction(String equation)
	{
		
		if(equation.isEmpty())
		{
			return "";
		}
		else if(equation.charAt(0)=='(')
		{
			return "";
		}
		return equation.charAt(0)+firstFunction(equation.substring(1));
	}
	
	private String getInsideOfFunction(String equation,String function,boolean start)
	{
		if(equation.isEmpty())
		{
			return equation;
		}
		else if(!start&&equation.substring(0, function.length()).equals(function))
		{
			return getInsideOfFunction(equation.substring(function.length()),function,true);
		}
		else if(start==true)
		{
			return equation.charAt(0)+getInsideOfFunction(equation.substring(1),function,true);
		}
		else {
			return getInsideOfFunction(equation.substring(function.length()),function,start);
		}
	}
	
	//ArrayString
	public String[] splitFirstOut(String str, char s)
	{
		String[] sep = new String[2];
		int isInside = 0;
		
		for(int i =0;i<str.length();i++)
		{
			if(str.charAt(i)=='(')
			{
				isInside++;
			}
			else if(str.charAt(i)==')')
			{
				isInside--;
			}
			else if(str.charAt(i)==s&&isInside==0)
			{
				sep[0] = str.substring(0, i);
				sep[1] = str.substring(i+1,str.length());
				return sep;
			}
		}
		return sep;
	}
	
	private String[] splitNumVar(String equation)
	{
		String sep[] = new String[2];
		
		for(int i = 0;i<equation.length();i++)
		{
			if((this.hasVar(equation.charAt(i)+"")||equation.charAt(i)=='(')&&i!=0)
			{
				
				sep[0] = equation.substring(0, i);
				sep[1] = equation.substring(i);
				return sep;
			}
			else if(i==0&&this.hasVar(equation.charAt(i)+""))
			{
				sep = this.splitNumVarParen(equation);
			}
		}
		return sep;
	}
	private String[] splitNumVarParen(String equation)
	{
		String sep[] = new String[2];
		for(int i = 0;i<equation.length();i++)
		{
			if(equation.charAt(i)=='(')
			{
				sep[0] = equation.substring(0, i);
				sep[1] = equation.substring(i);
			}
		}
		return sep;
	}
	
	public String[] splitLastOut(String str, char s)
	{
		String[] sep = new String[2];
		int isInside = 0;
		
		for(int i =str.length()-1;i>=0;i--)
		{
			if(str.charAt(i)=='(')
			{
				isInside++;
			}
			else if(str.charAt(i)==')')
			{
				isInside--;
			}
			else if(str.charAt(i)==s&&isInside==0)
			{
				sep[0] = str.substring(0, i);
				sep[1] = str.substring(i+1,str.length());
				return sep;
			}
		}
		return sep;
	}
	
	private String[] parsePower(String equation)
	{
		String sep[] = new String[2];
		int isInside = 0;
		
		if(this.isVar(equation.charAt(0)))
		{
			sep[0] = equation.substring(0, 1);
			sep[1] = equation.substring(1);
			return sep;
		}
		else
		{
			for(int i = 0;i<equation.length();i++)
			{
				if(i==0&&equation.charAt(0)=='(')
				{
					isInside++;
				}
				else if(isInside>0&&equation.charAt(i)=='(')
				{
					isInside++;
				}
				else if(isInside>1&&equation.charAt(i)==')')
				{
					isInside--;
				}
				else if(isInside==1&&equation.charAt(i)==')')
				{
					//sep
					sep[0] = equation.substring(0,i+1);
					sep[1] = equation.substring(i+1);
					return sep;
				}
				else if((this.isChar(equation.charAt(i))||equation.charAt(i)=='('||equation.charAt(i)==')')&&isInside==0)
				{
					sep[0] = equation.substring(0,i);
					sep[1] = equation.substring(i);
					return sep;
				}
			}
		}
		
		sep[0] = equation;
		sep[1] = "";
		return sep;
	}
	
	
	
	
	//booleans
	public boolean isOperator(char c)
	{
        return c == '/' || c == '*';
	}
	
	//returns true if the character is a number 0-9;
	public boolean isANumber(char c)
	{
		return c <= 57 && c >= 48;
	}
	
	public boolean hasNumber(String equation,int isInside)
	{
		if(equation==null)
		{
			return false;
		}
		if(equation.isEmpty())
		{
			return false;
		}
//		else if((equation.charAt(0)>=65&&equation.charAt(0)<=90)||(equation.charAt(0)>=97&&equation.charAt(0)<=122))
//		{
//			return false;
//		}
		else if(equation.charAt(0)=='(')
		{
			return hasNumber(equation.substring(1),isInside+1);
		}
		else if(equation.charAt(0)==')')
		{
			return hasNumber(equation.substring(1),isInside-1);
		}
		else if(((equation.charAt(0)>=48&&equation.charAt(0)<=57)||(equation.charAt(0)=='-'||equation.charAt(0)=='+'))&&isInside==0)
		{
			return true;
		}
		return hasNumber(equation.substring(1),isInside);
	}
	
	public boolean isVar(char c)
	{
        return c == 'X' || c == 'x' || (c == 'π') || c == '%' || c == 'e';
	}

	public boolean isChar(char c)
	{
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
	}
	
	
	//returns true if it has a character
	public boolean hasVar(String equation)
	{
		if(equation==null)
		{
			return false;
		}
		if(equation.isEmpty())
		{
			return false;
		}
		else if((equation.charAt(0)>=65&&equation.charAt(0)<=90)||(equation.charAt(0)>=97&&equation.charAt(0)<=122)||equation.charAt(0)=='π'||equation.charAt(0)=='√'||equation.charAt(0)=='%')
		{
			return true;
		}
		return hasVar(equation.substring(1));
	}
	
	public boolean hasVarAndNum(String equation)
	{
        return this.hasVar(equation) && this.hasNumber(equation, 0);
	}
	
	//returns true if sub str is found outside of a bracket
	private boolean hasThisOutside(String str, String sub,int isInside)
	{
		if(str==null||sub==null)
		{
			return false;
		}
		if(str.length()<sub.length())
		{
			return false;
		}
		else if(str.charAt(0)=='(')
		{
			return hasThisOutside(str.substring(1),sub,isInside+1);
		}
		else if(str.charAt(0)==')')
		{
			return hasThisOutside(str.substring(1),sub,isInside-1);
		}
		else if(str.substring(0, sub.length()).equals(sub)&&isInside==0)
		{
			return true;
		}
		return hasThisOutside(str.substring(1),sub,isInside);
	}
	
	//returns true if it has only numbers or '+' or '-'
	private boolean hasOnlyNumbers(String equation)
	{
		if(equation==null)
		{
			return true;
		}
		if(equation.isEmpty())
		{
			return true;
		}
		else if(equation.charAt(0)=='+'||equation.charAt(0)=='-')
		{
			return hasOnlyNumbers(equation.substring(1));
		}
		else if(equation.charAt(0)=='('||equation.charAt(0)==')')
		{
			return false;
		}
		return hasOnlyNumbers(equation.substring(1));
	}
	
	
	//checks to see if there is only function in the equation 
	private boolean hasOnlyFunction(String function)
	{
        return hasTrig(function) || hasLog(function) || hasSquareRoot(function) || HASFUNCTION(function);
	}
	
	private boolean hasTrig(String function)
	{
		for(int i = 0;i<VarLib.TRIGFUNCTION.length;i++)
		{
			if(function.equals(VarLib.TRIGFUNCTION[i]))
			{
				return true;
			}
		}
		return false;
	}
	private boolean hasLog(String function)
	{
		for(int i = 0;i<VarLib.LOGFUNCTION.length;i++)
		{
			if(function.equals(VarLib.LOGFUNCTION[i]))
			{
				return true;
			}
		}
		return false;
	}
	private boolean hasSquareRoot(String function)
	{
		for(int i = 0;i<VarLib.SQUAREROOT.length;i++)
		{
			if(function.equals(VarLib.SQUAREROOT[i]))
			{
				return true;
			}
		}
		return false;
	}
	
	//integers
	public  int pmCount(String str)
	{
		if(str==null)
		{
			return 0;
		}
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
	//returns the number of parenthesis
	public int getParen(String str, int isInside)
	{
		if(str==null)
		{
			return 0;
		}
		if(str.isEmpty())
		{
			return 0;
		}
		else if(str.charAt(0)=='('&&isInside == 0)
		{
			return 1+getParen(str.substring(1),isInside+1);
		}
		else if(str.charAt(0)==')'&&isInside==1)
		{
			return 1+getParen(str.substring(1),isInside-1);
		}
		else if(str.charAt(0)=='(')
		{
			return getParen(str.substring(1),isInside+1);
		}
		else if(str.charAt(0)==')')
		{
			return getParen(str.substring(1),isInside-1);
		}
		return getParen(str.substring(1),isInside);
	}
	
	
	
	//parsers
	public void separateAddSub(String polynomial)
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


				int z = 0;
				for(int i = 0;i<=location.length;i++)
				{
					if(i!=location.length)
					{
						Var v = new Var(polynomial.substring(z, location[i]));
						z = location[i];
						base.add(v);
					}
					else {
						Var v = new Var(polynomial.substring(z));
						base.add(v);
					}

				}
			}
        this.parseEquation = numpm <= 0;
	}

	private boolean isMinus(char c)
	{
        return !MathFunctions.negativeAndMinus.contains(c + "");
    }
	
	private String[] separateVarAndFunction(String equation)
	{
		if(equation==null)
		{
			return new String[0];
		}
		String sep [] = new String[2];
		int inside = 0;
		
		for(int i = 0;i<equation.length();i++)
		{
			if(equation.charAt(i)=='(')
			{
				inside++;
			}
			else if(equation.charAt(i)==')')
			{
				inside--;
			}
			else if(this.isVar(equation.charAt(i))&&inside==0)
			{
				if(i==0)
				{
					sep[0] = equation.substring(0, i+1);
					sep[1] = equation.substring(i+1);
				}
				else {
				sep[0] = equation.substring(0, i);
				sep[1] = equation.substring(i);
				}
				return sep;
			}
		}
		
		return sep;
	}
	
	//parse
	private void parseEquation(String equation)
	{
		if(equation!=null&&!equation.isEmpty()&&this.parseEquation&&equation.charAt(0)=='-'&&equation.length()>1){
		    String neg = "-";
		    String rest = equation.substring(1);
            Var x = new Var(neg);
            Var xx = new Var(rest);

            this.multiply.add(x);
            this.base.add(xx);


        }else{
            if(this.parseEquation&&this.thereIsAOperation)
            {
                String sep[] = this.splitLastOut(equation, this.firstOpOutSide.charAt(0));
                moveOperation(sep);
            }
            else if(this.parseEquation)
            {
                autoParser(equation);
            }
        }

	}
	
	private void autoParser(String equation)
	{
		
		if(this.hasThisOutside(equation,"^",0))
		{
			String sep[] = this.splitFirstOut(equation, '^');


			this.parseNumVar(sep[0]);
			//parsePower
			String sepPower[] = parsePower(sep[1]);
			Var power = new Var(sepPower[0]);
			this.power.add(power);
			
			if(!sepPower[1].equals(""))
			{
				Var multiply = new Var(sepPower[1]);
				this.multiply.add(multiply);
			}
			
			
			

		}
		else if(this.hasVarAndNum(equation))
		{
			this.parseNumVar(equation);
		}
		
		else
		{
			whatIsThis(equation);
		}
	}
	private void parseNumVar(String equation)
	{
		if(this.hasVarAndNum(equation))
		{
			String sep[] = splitNumVar(equation);
			
			if(!sep[0].equals(""))
			{
				Var x = new Var(sep[0]);
				this.multiply.add(x);
			}
			
			
			if(!sep[1].equals(""))
			{
				Var xx = new Var(sep[1]);
				this.base.add(xx);
			}
			
			
		}
		else {
			whatIsThis(equation);
		}
	}
	
	private void whatIsThis(String equation)
	{
		if(equation==null)
		{

		}
		else if(equation.startsWith("(")&&equation.endsWith(")"))
		{
			String base = equation.substring(1, equation.length()-1);
			Var v = new Var(base);
			this.base.add(v);
		}
		else if(this.hasNumber(equation,0)&&!this.hasVar(equation))
		{
			if(this.hasOnlyNumbers(equation))
			{
			    this.value = equation;
			}
			else {
				parseNumberEquation(equation);
			}
			
		}
		else if(this.hasVar(equation))
		{
			if(equation.length()==1)
			{
				this.variable = equation;
			}
			else 
			{
				parseTheTrig(equation);
			}
		}
	}
	private void parseNumberEquation(String equation)
	{
		String sep[] = splitNumVar(equation);
		
		Var x = new Var(sep[0]);
		Var xx = new Var(sep[1]);
		
		this.multiply.add(x);
		this.base.add(xx);
	}
	private void parseTheTrig(String equation)
	{
		String firstFunction = this.firstFunction(equation);
		
		if(hasOnlyFunction(firstFunction))
		{
			this.function = firstFunction;
			String parsedInside = getInsideOfFunction(equation,function,false);
			
			String sep[] = checkInside(parsedInside);
			
			Var v = new Var(sep[0]);
			Var v1 = new Var(sep[1]);
			this.trigs.add(v);
			this.multiply.add(v1);
		}
		else
		{
			String sep[] = separateVarAndFunction(equation);
			
			Var v = new Var(sep[0]);
			Var vv = new Var(sep[1]);
			
			this.multiply.add(v);
			this.base.add(vv);
		}
	}
	
	
	private String[] checkInside(String parsedInside)
	{
		String sep[] = new String [2];
		int inside = 0;
		for(int i = 0;i<parsedInside.length();i++)
		{
			if(parsedInside.charAt(i)=='(')
			{
				inside++;
			}
			else if(parsedInside.charAt(i)==')'&&inside==1)
			{
				sep[0] = parsedInside.substring(0, i+1);
				sep[1] = parsedInside.substring(i+1);
				return sep;
			}
			else if(parsedInside.charAt(i)==')')
			{
				inside--;
			}
		}
		sep[0] = parsedInside;
		sep[1] = "";
		return sep;
	}
	
			
	
	
	//getters
	public double getPower(double x)
	{
		double total = 0;
		boolean didLoop = false;
		for(Var v:this.power)
		{
			total+=v.getValueFromBase(x);
			didLoop = true;
		}
		if(didLoop==false) 
		{
			return 1;
		}
		return total;
	}
	public double getMultiply(double x)
	{
		double total = 1;
		boolean didLoop = false;
		for(Var v:this.multiply)
		{
			total*=v.getValueFromBase(x);
			didLoop = true;
		}
		if(didLoop==false) 
		{
			return 1;
		}
		return total;
	}
	public double getDivide(double x)
	{
		double total = 0;
		boolean didLoop = false;
		int i =0;
		for(Var v:this.divide)
		{
			if(i==0)
			{
				total+=v.getValueFromBase(x);
			}
			else {
				total/=v.getValueFromBase(x);
			}
			i++;
			didLoop = true;
		}
		if(didLoop==false) 
		{
			return 1;
		}
		return total;
	}
	
	public double getBase(double x)
	{
		double total = 0;
		boolean didLoop = false;
		for(Var v:this.base)
		{
			total+=v.getValueFromBase(x);
			didLoop = true;
		}
		if(didLoop==false) 
		{
			return 1;
		}
		return total;
	}
	public double getFunction(double x)
	{
		double total = 0;
		boolean didLoop = false;
		for(Var v:this.trigs)
		{
			total+=v.getValueFromBase(x);
			didLoop = true;
		}
		if(didLoop==false) 
		{
			return 1;
		}
		return total;
	}
	
	
	
	
	public double getValue(double x)
	{
		if(!this.value.equals(""))
		{
			if(value.equals("-"))
			{
				return -1;
			}
			else if(value.equals("+"))
			{
				return 1;
			}
			return Double.parseDouble(this.value);
		}
		else if(!this.variable.equals(""))
		{
			if(this.variable.contains("e"))
			{
				return Math.E;
			}
			else if(this.variable.contains("π"))
			{
				return Math.PI;
			}
			else if(this.variable.contains("%"))
			{
				return 0.01;
			}
			else {
				return x;
			}
		}
		else if(!this.function.equals(""))
		{
			return getValueOfFunction(this.getFunction(x));
		}
		return -0.0123678923;
	}
	
	
	public double getValueFromBase(double x)
	{
		double powers = this.getPower(x);
		double multiplies = this.getMultiply(x);
		double divides = this.getDivide(x);
		double bases = this.getBase(x);
		
		if(this.getValue(x)!=-0.0123678923)
		{
			return (Math.pow(this.getValue(x),powers)*multiplies)/divides;
		}
		else
		{
			return ((Math.pow(bases,powers))*multiplies)/divides;
		}
	}
	
	public double getValueOfFunction(double x)
	{
		if(this.function.toLowerCase().equals(VarLib.TRIGFUNCTION[0]))
		{
			return Math.sin(getProperModeValue(x));
		}
		else if(this.function.toLowerCase().equals(VarLib.TRIGFUNCTION[1]))
		{
			return Math.cos(getProperModeValue(x));
		}
		else if(this.function.toLowerCase().equals(VarLib.TRIGFUNCTION[2]))
		{
			return Math.tan(getProperModeValue(x));
		}
		else if(this.function.toLowerCase().equals(VarLib.TRIGFUNCTION[3]))
		{
			return (1/Math.tan(getProperModeValue(x)));
		}
		else if(this.function.toLowerCase().equals(VarLib.TRIGFUNCTION[4]))
		{
			return (1/Math.sin(getProperModeValue(x)));
		}
		else if(this.function.toLowerCase().equals(VarLib.TRIGFUNCTION[5]))
		{
			return (1/Math.cos(getProperModeValue(x)));
		}
		else if(this.function.toLowerCase().equals(VarLib.LOGFUNCTION[0]))
		{
			return Math.log10(x);
		}
		else if(this.function.toLowerCase().equals(VarLib.LOGFUNCTION[1]))
		{
			return Math.log(x);
		}
		else if(this.function.toLowerCase().equals(VarLib.SQUAREROOT[0])||this.function.toLowerCase().equals(VarLib.SQUAREROOT[1]))
		{
			return Math.sqrt(x);
		}
		else if(this.function.toLowerCase().equals(VarLib.SQUAREROOT[2]))
		{
			return Math.abs(x);
		}
		else if(this.function.toLowerCase().equals(VarLib.TRIGFUNCTION[6]))
		{
			return Math.asin(getProperModeValue(x));
		}
		else if(this.function.toLowerCase().equals(VarLib.TRIGFUNCTION[7]))
		{
			return Math.acos(getProperModeValue(x));
		}
		else if(this.function.toLowerCase().equals(VarLib.TRIGFUNCTION[8]))
		{
			return Math.atan(getProperModeValue(x));
		}
		else if(HASFUNCTION(this.function))
		{
			return getValueOfAnotherFunction(x);
		}
		else {
			return 0;
		}
	}

	private double getProperModeValue(double radians)
	{
		if(Modes.currentMode==0)
		{
			return (radians/180)*Math.PI;
		}
		if(Modes.currentMode==1)
		{
			return radians;
		}
		else
		{
			return (radians/200)*Math.PI;
		}
	}

	
	
	//getters for Strings
	public String getVariable()
	{
		return this.variable;
	}
	public String getNumber()
	{
		return this.value;
	}
	public String getFunction()
	{
		return this.function;
	}
	
	
	
	//function ability
	public boolean HASFUNCTION(String function)
	{
		for(Function f:LinkedFunctions.FUNCTIONS)
		{
			if(function.equals(f.getName()))
			{
				return true;
			}
		}
		return false;
	}
	public double getValueOfAnotherFunction(double x)
	{
		for(Function f:LinkedFunctions.FUNCTIONS)
		{
			if(this.function.equals(f.getName()))
			{
				return f.getValue(x);
			}
		}
		return 0;
	}


	public double getMultiplicity(double x)
	{
		double combinedPowers = 1;
		ArrayList<Double> powers = new ArrayList<>();
		for(Var v:this.multiply)
		{
			powers.add(v.getMultiplicity(x));
		}
		for(Var v:this.base)
		{
			powers.add(v.getMultiplicity(x));
		}
		for(Var v:this.power)
		{
			powers.add(v.getValue(1));
		}
		for(Var v:this.divide)
		{
			powers.add(-v.getMultiplicity(x));
		}
		for(Var v:this.trigs)
		{
			powers.add(v.getMultiplicity(x));
		}
		if(powers.size()>0)
		{
			combinedPowers = 0;
			for(Double d:powers)
			{
				if(d!=1)
				{
					combinedPowers+=d;
				}
			}
		}
		if(combinedPowers!=0)
		{
			return combinedPowers;
		}
		else{
			return 1;
		}
	}
	
	
	
	//getting fprime
//	public String getFPRIMEmultiplies()
//	{
//		String equation = "";
//		for(Var v:this.multiply)
//		{
//			equation+=v.getFPRIMEfromBase();
//		}
//		return equation;	
//	}
//	public String getFPRIMEdivides()
//	{
//		String equation = "";
//		for(Var v:this.divide)
//		{
//			equation+=v.getFPRIMEfromBase();
//		}
//		return equation;	
//	}
//	public String getFPRIMEpowers()
//	{
//		String equation = "";
//		for(Var v:this.power)
//		{
//			equation+=v.getFPRIMEfromBase();
//		}
//		return equation;	
//	}
//	public String getFPRIMEtrigs()
//	{
//		String equation = "";
//		for(Var v:this.trigs)
//		{
//			equation+=v.getFPRIMEfromBase();
//		}
//		return equation;	
//	}
//	public String getFPRIMEbases()
//	{
//		String equation = "";
//		for(Var v:this.base)
//		{
//			equation+=v.getFPRIMEfromBase();
//		}
//		return equation;	
//	}
//	
//	public String getFPRIME()
//	{
//		if(!this.value.equals(""))
//		{
//			if(value.equals("-"))
//			{
//				return "-1";
//			}
//			else if(value.equals("+"))
//			{
//				return "1";
//			}
//			return this.value;
//		}
//		else if(!this.variable.equals(""))
//		{ 
//			return this.variable;
//		}
//		else if(!this.function.equals(""))
//		{
//			return function;
//		}
//		return "null";
//	}
//	public String getFPRIMEfromBase()
//	{
//		String trig = this.getFPRIMEtrigs();
//		String multiplies = this.getFPRIMEmultiplies();
//		String divides = this.getFPRIMEdivides();
//		String powers = this.getFPRIMEpowers();
//		String bases = this.getFPRIMEbases();
//		
//		String muldiv = this.getMULDIV(multiplies, divides);
//		String pow = this.getPow(powers);
//		
//		
//		
//		
//		
//		if(!this.getFPRIME().equals("null"))
//		{
//			return this.getFPRIME();
//		}
//		else if(powers.equals(""))
//		{
//			if(muldiv.equals("1"))
//			{
//				return "";
//			}
//			else {
//			return muldiv;
//			}
//		}
//		else 
//		{
//			return this.getEQUATION(muldiv,pow,bases);
//		}
//		
//		
//		
//	}
//		
//	
//	public String getMULDIV(String multiplies,String divides)
//	{
//		if(!multiplies.equals("")&&!divides.equals(""))
//		{
//			return (Double.parseDouble(multiplies)/Double.parseDouble(divides))+"";
//		}
//		else if(multiplies.equals("")&&divides.equals(""))
//		{
//			return "1";
//		}
//		else if(!multiplies.equals(""))
//		{
//			return multiplies;
//		}
//		else {
//			return divides;
//		}
//	}
//	public String getPow(String powers)
//	{
//		if(!powers.equals(""))
//		{
//			return powers;
//		}
//		else
//		{
//			return "1";
//		}
//		
//	}
//	public String getEQUATION(String muldiv,String pow,String base)
//	{
//		if(this.hasNumber(base, 0)||!this.value.equals(""))
//		{
//			return "0";
//		}
//		else if(!muldiv.equals("1")&&!pow.equals("1"))
//		{
//			double number = Double.parseDouble(muldiv);
//			double power = Double.parseDouble(pow);
//			double newNum = power*number;
//			power--;
//			return String.format("%.2f%s^%.2f", newNum,base,power);
//		}
//		else if(muldiv.equals("1")&&pow.equals("1"))
//		{
//			return "1";
//		}
//		else if(!pow.equals("1"))
//		{
//			double number = Double.parseDouble(pow);
//			double power = Double.parseDouble(pow);
//			power--;
//			return String.format("%.2f%s^%.2f",number, base,power);
//		}
//		else if(pow.equals("1"))
//		{
//			return "";
//		}
//		else if(!muldiv.equals("1"))
//		{
//			
//			return String.format("(%s)", muldiv);
//		}
//		
//		return "";
//	}
			
			
	
	
	
	
	

}
