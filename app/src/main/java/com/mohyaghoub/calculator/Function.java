package com.mohyaghoub.calculator;

import java.util.ArrayList;

public class Function {
	
	private String name;
	private String equation;
	private ArrayList<Var> function;
	
	Function(String n,String e)
	{
		this.name = n;
		this.setEquation(this.removeSpaces(e));
	}
	private void separateAddSub(String polynomial)
	{
		int numpm = pmCount(polynomial,0,0);
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
				else if((polynomial.charAt(i)=='+'||polynomial.charAt(i)=='-')&&i!=0&&p<numpm&&isInside==0)
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
				function.add(v);
			}
			else {
				Var v = new Var(polynomial.substring(z));
				function.add(v);
			}
		
		}
		
	}
	
	
	public int pmCount(String str, int isInside,int index)
	{
		if(str.length()==0)
		{
			return 0;
		}
		else if(index==0&&(str.charAt(0)=='-'||str.charAt(0)=='+'))
		{
			return pmCount(str.substring(1),isInside,index+1);
		}
		else if(str.charAt(0)=='(')
		{
			return pmCount(str.substring(1),isInside+1,index+1);
		}
		else if(str.charAt(0)==')')
		{
			return pmCount(str.substring(1),isInside-1,index+1);
		}
		else if((str.charAt(0)=='+'||str.charAt(0)=='-')&&isInside==0)
		{
			return 1 + pmCount(str.substring(1),isInside,index+1);
		}
		return pmCount(str.substring(1),isInside,index+1);
	}
	
	public String removeSpaces(String str)
	{
		if(str.isEmpty())
		{
			return str;
		}
		else if(str.charAt(0)==' ')
		{
			return removeSpaces(str.substring(1));
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
	public String getName() 
	{
		return this.name;
	}
	
	
	//setters
	public void setEquation(String equation)
	{
		this.function = new ArrayList<>();
		this.equation = equation;
		this.separateAddSub(equation);
	}
	

}
