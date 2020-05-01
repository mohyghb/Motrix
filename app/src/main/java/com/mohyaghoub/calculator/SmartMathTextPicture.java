package com.mohyaghoub.calculator;

import java.util.ArrayList;

public class SmartMathTextPicture {

    private ArrayList<SmartPictureParser> tokens;
    private String equation;
    SmartMathTextPicture(String equation)
    {
        this.equation = equation;
        this.tokens = new ArrayList<>();
        parseEquation(equation);
    }
    private void parseEquation(String equation)
    {
        String sep [] = equation.split(" ");
        for(int i = 0;i<sep.length;i++)
        {
            SmartPictureParser spp = new SmartPictureParser(sep[i]);
            tokens.add(spp);
        }
    }

    public String getText()
    {
        String result = "";
        for(SmartPictureParser spp:tokens)
        {
            result+=spp.getToken();
        }
        return result;
    }



}
