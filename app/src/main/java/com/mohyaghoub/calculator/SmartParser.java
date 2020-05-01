package com.mohyaghoub.calculator;



public class SmartParser {

    private String token;
    private String parsedToken;
    private boolean hasStringNumber;

    private String finalParsed;
    private boolean hadVariable;

    SmartParser(String token)
    {
        this.token = token;
        this.parsedToken = "";
        this.finalParsed = "";
        this.hasStringNumber = false;
        this.hadVariable = false;
        parseToken(token);
        checkBoolean();
    }

    private void parseToken(String token)
    {
        this.hadVariable = MathFunctions.THEREWASAVARIABLE;

        for(FunctionCreator fc:MathFunctions.savedFunctions)
        {
            if(fc.getName().toLowerCase().equals(token.toLowerCase()))
            {
                this.parsedToken = fc.getName().toLowerCase()+"(";
                MathFunctions.INVERSE = false;
                MathFunctions.THEREWASAVARIABLE = false;
            }
        }


        for(int i = 0;i<MathFunctions.SIN.length;i++)
        {

            if(token.toLowerCase().equals(MathFunctions.SIN[i]))
            {
                if(MathFunctions.INVERSE)
                {
                    this.parsedToken = "asin(";
                }
                else {
                    this.parsedToken = "sin(";
                }
                MathFunctions.INVERSE = false;
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }

        for(int i = 0;i<MathFunctions.SINX.length;i++)
        {

            if(token.toLowerCase().equals(MathFunctions.SINX[i]))
            {
                if(MathFunctions.INVERSE)
                {
                    this.parsedToken = "asin(x";
                }
                else {
                    this.parsedToken = "sin(x";
                }
                MathFunctions.INVERSE = false;
                MathFunctions.THEREWASAVARIABLE = true;
                return;
            }
        }



        for(int i = 0;i<MathFunctions.COS.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.COS[i]))
            {
                if(MathFunctions.INVERSE)
                {
                    this.parsedToken = "acos(";
                }
                else {
                    this.parsedToken = "cos(";
                }
                MathFunctions.INVERSE = false;
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }

        for(int i = 0;i<MathFunctions.COSX.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.COSX[i]))
            {
                if(MathFunctions.INVERSE)
                {
                    this.parsedToken = "acos(x";
                }
                else {
                    this.parsedToken = "cos(x";
                }
                MathFunctions.INVERSE = false;
                MathFunctions.THEREWASAVARIABLE = true;
                return;
            }
        }

        for(int i = 0;i<MathFunctions.LNX.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.LNX[i]))
            {
                if(MathFunctions.INVERSE)
                {
                    this.parsedToken = "ex";
                }
                else {
                    this.parsedToken = "ln(x";
                }
                MathFunctions.INVERSE = false;
                MathFunctions.THEREWASAVARIABLE = true;
                return;
            }
        }



        for(int i = 0;i<MathFunctions.TAN.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.TAN[i]))
            {
                if(MathFunctions.INVERSE)
                {
                    this.parsedToken = "atan(";
                }
                else {
                    this.parsedToken = "tan(";
                }
                MathFunctions.INVERSE = false;
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }

        for(int i = 0;i<MathFunctions.CSC.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.CSC[i]))
            {
                this.parsedToken = "csc(";
                MathFunctions.INVERSE = false;
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }
        for(int i = 0;i<MathFunctions.SEC.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.SEC[i]))
            {
                this.parsedToken = "sec(";
                MathFunctions.INVERSE = false;
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }
        for(int i = 0;i<MathFunctions.COT.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.COT[i]))
            {
                this.parsedToken = "cot(";
                MathFunctions.INVERSE = false;
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }

        for(int i = 0;i<MathFunctions.ABS.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.ABS[i]))
            {
                this.parsedToken = "abs(";
                MathFunctions.INVERSE = false;
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }


        for(int i = 0;i<MathFunctions.LOG.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.LOG[i]))
            {
                if(MathFunctions.INVERSE)
                {
                    this.parsedToken = "10^(";
                    MathFunctions.THEREWASAVARIABLE = false;
                }
                else {
                    this.parsedToken = "log(";
                    MathFunctions.THEREWASAVARIABLE = false;
                }
                MathFunctions.INVERSE = false;
                return;
            }
        }
        for(int i = 0;i<MathFunctions.LN.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.LN[i]))
            {
                if(MathFunctions.INVERSE)
                {
                    this.parsedToken = "e";
                    MathFunctions.THEREWASAVARIABLE = true;
                }
                else {
                    this.parsedToken = "ln(";
                    MathFunctions.THEREWASAVARIABLE = false;
                }
                MathFunctions.INVERSE = false;
                return;
            }
        }
        for(int i = 0;i<MathFunctions.PI.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.PI[i]))
            {
                this.parsedToken = "π";
                MathFunctions.THEREWASAVARIABLE = true;
                return;
            }
        }
        for(int i = 0;i<MathFunctions.PIX.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.PIX[i]))
            {
                this.parsedToken = "πx";
                MathFunctions.THEREWASAVARIABLE = true;
                return;
            }
        }


        for(int i = 0;i<MathFunctions.X.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.X[i]))
            {
                this.parsedToken = "x";
                MathFunctions.THEREWASAVARIABLE = true;
                return;
            }
        }
        for(int i = 0;i<MathFunctions.POWER.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.POWER[i]))
            {
                this.parsedToken = "^";
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }

        for(int i = 0;i<MathFunctions.SQUARED.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.SQUARED[i]))
            {
                this.parsedToken = "^2";
                MathFunctions.INVERSE = false;
                return;
            }
        }
        for(int i = 0;i<MathFunctions.CUBED.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.CUBED[i]))
            {
                this.parsedToken = "^3";
                return;
            }
        }

        for(int i = 0;i<MathFunctions.MULTIPLICATION.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.MULTIPLICATION[i]))
            {
                this.parsedToken = "*";
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }
        for(int i = 0;i<MathFunctions.DIVISION.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.DIVISION[i]))
            {
                this.parsedToken = "/";
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }
        for(int i = 0;i<MathFunctions.ADDITION.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.ADDITION[i]))
            {
                this.parsedToken = "+";
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }
        for(int i = 0;i<MathFunctions.SUBTRACTION.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.SUBTRACTION[i]))
            {
                this.parsedToken = "-";
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }

        for(int i = 0;i<MathFunctions.EQUAL.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.EQUAL[i]))
            {
                this.parsedToken = "=";
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }
        for(int i = 0;i<MathFunctions.E.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.E[i]))
            {
                if(MathFunctions.INVERSE)
                {
                    this.parsedToken = "ln(";
                    MathFunctions.THEREWASAVARIABLE = false;
                }
                else {
                    this.parsedToken = "e";
                    MathFunctions.THEREWASAVARIABLE = true;
                }
                MathFunctions.INVERSE = false;

                return;
            }
        }


        for(int i = 0;i<MathFunctions.SQUAREROOT.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.SQUAREROOT[i]))
            {
                if(MathFunctions.INVERSE)
                {
                    this.parsedToken = "^2";
                }
                else {
                    this.parsedToken = "√(";
                }
                MathFunctions.INVERSE = false;
                return;
            }
        }


        for(int i = 0;i<MathFunctions.OPEN.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.OPEN[i]))
            {
                this.parsedToken = "(";
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }
        for(int i = 0;i<MathFunctions.CLOSE.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.CLOSE[i]))
            {
                this.parsedToken = ")";
                MathFunctions.THEREWASAVARIABLE = true;
                return;
            }
        }

        for(int i = 0;i<MathFunctions.PERCENTS.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.PERCENTS[i]))
            {
                this.parsedToken = "%";
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }

        for(int i = 0;i<MathFunctions.INVERSEcommand.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.INVERSEcommand[i]))
            {
                MathFunctions.INVERSE = true;
                MathFunctions.THEREWASAVARIABLE = false;
                return;
            }
        }




        for(int i = 0;i<MathFunctions.NUMBERS.length;i++)
        {
            if(token.toLowerCase().equals(MathFunctions.NUMBERS[i]))
            {
                this.hasStringNumber = true;
                return;
            }
        }






    }
    private void checkBoolean()
    {
        if(this.parsedToken.equals(""))
        {
            if(itIsAContinuesVariable(this.token))
            {
                this.finalParsed = this.token;
                MathFunctions.THEREWASAVARIABLE = true;
            }
            else if(hasOnlyNumbers(this.token))
            {
                this.finalParsed = this.token;
                MathFunctions.THEREWASAVARIABLE = true;
            }
            else if(this.hasStringNumber) {
                this.finalParsed = getNumber();
                MathFunctions.THEREWASAVARIABLE = true;
            }
        }
        else{
            this.finalParsed = this.parsedToken;
        }

    }


    public String getText()
    {
        return this.finalParsed;
    }

    //hasOnlyNumbers
    private boolean hasOnlyNumbers(String equation)
    {
        if(equation.isEmpty())
        {
            return true;
        }
        else if(equation.charAt(0)=='+'||equation.charAt(0)=='-')
        {
            return hasOnlyNumbers(equation.substring(1));
        }
        else if(equation.charAt(0)=='('||equation.charAt(0)==')'||isVar(equation.charAt(0)))
        {
            return false;
        }
        return hasOnlyNumbers(equation.substring(1));
    }

    public static boolean isVar(char c)
    {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 122) || (c == 'π');
    }

    private String getNumber()
    {
        int number = 0;
        for(int i = 0;i<MathFunctions.NUMBERS.length;i++)
        {
            if(this.token.toLowerCase().equals(MathFunctions.NUMBERS[i]))
            {
                if(i<10)
                {
                    return i+"";
                }
                else if(i==12&&this.hadVariable==false)
                {
                    return "2";
                }
                else if(i>=10&&i!=12)
                {
                    return number+"";
                }

            }
            else if(i>=10)
            {
                number++;
            }
        }
        return "";
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

    public boolean hasVar(String equation)
    {
        if(equation.isEmpty())
        {
            return false;
        }
        else if((equation.charAt(0)>=65&&equation.charAt(0)<=90)||(equation.charAt(0)>=97&&equation.charAt(0)<=122)||equation.charAt(0)=='π'||equation.charAt(0)=='√')
        {
            return true;
        }
        return hasVar(equation.substring(1));
    }


    public static boolean isNumber(char c)
    {
        return c >= 48 && c <= 57;
    }



    public boolean itIsAContinuesVariable(String str)
    {
        return this.hasVar(str) && this.hasNumber(str);
    }


    public static String finalCheck(String equation,boolean wasX)
    {
        char curChar = ' ';
        if(!equation.isEmpty())
        {
            curChar = equation.charAt(0);
        }
        if(equation.isEmpty())
        {
            return equation;
        }

        else if(wasX==true&&(curChar=='x'||curChar=='X'))
        {
            return '*'+finalCheck(equation.substring(1),false);
        }
        else if(curChar=='x'|| curChar=='X'||curChar==')')
        {
            return equation.charAt(0)+finalCheck(equation.substring(1),true);
        }
        return equation.charAt(0)+finalCheck(equation.substring(1),false);

//        String newOne = "";

//        int length = equation.length();
//        boolean wasVar = false;
//        boolean wasNum = false;
//        for(int i =0; i<length ;i++)
//        {
//            char curC = equation.charAt(i);
//            if(curC=='x'||curC=='X') {
//
//                boolean willVar = false;
//                boolean willNum = false;
//                if (isNumber(curC)) {
//                    willNum = true;
//                    willVar = false;
//                } else if (isVar(curC)) {
//                    willVar = true;
//                    willNum = false;
//                }
//
//
//                wasVar = willVar;
//                wasNum = willNum;
//            }
//            else
//            {
//                newOne+=curC;
//            }
//        }

//
//        return newOne;
    }






}
