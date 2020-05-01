package com.mohyaghoub.calculator;

import java.util.ArrayList;

public class SmartMathText {

    private ArrayList<SmartParser> tokens;
    SmartMathText(String text)
    {
        tokens = new ArrayList<>();
        separateText(text);
    }

    private void separateText(String text)
    {
        String sep[] = text.split(" ");
        for(int i = 0;i<sep.length;i++)
        {
            SmartParser sp = new SmartParser(checkNumVar(sep[i]));
            this.tokens.add(sp);
        }
    }

    private String checkNumVar(String equation)
    {
        if(equation.length()>=5)
        {
            if(isANumber(equation.charAt(0))&&isVar(equation.charAt(equation.length()-1)))
            {
                return sepNumVar(equation);
            }
            else
            {
                return equation;
            }
        }
        else
        {
            return equation;
        }

    }

    private String sepNumVar(String equation)
    {
        int index = 0;
        while(isANumber(equation.charAt(index)))
        {
            index++;
        }
        SmartParser sp = new SmartParser(equation.substring(0,index));
        this.tokens.add(sp);
        return equation.substring(index);
    }


    public boolean isANumber(char c)
    {
        return c <= 57 && c >= 48;
    }

    public boolean isVar(char c)
    {
        return c == 'X' || c == 'x' || (c == 'π') || c == '%' || c == 'e';
    }

    public String getText()
    {
        String finalText = "";
        for(SmartParser sp:tokens)
        {
            finalText+=sp.getText();
        }
        return finalText;
    }




}
