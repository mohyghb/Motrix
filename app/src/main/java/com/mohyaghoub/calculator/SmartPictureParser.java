package com.mohyaghoub.calculator;

public class SmartPictureParser {

    private String token;
    private String parsedToken;

    SmartPictureParser(String token)
    {
        this.token = token;
        getEquation(token);
    }

    private void getEquation(String token)
    {
       this.parsedToken = parseToken(token);
    }



    private String parseToken(String token)
    {
        if(token.isEmpty())
        {
            return token;
        }
        else if(token.charAt(0)=='X')
        {
            return 'x' + parseToken(token.substring(1));
        }
        else if(token.charAt(0)=='A'||token.charAt(0)=='N'||token.charAt(0)=='M')
        {
            return '^' + parseToken(token.substring(1));
        }
        else if(token.charAt(0)==' ')
        {
            return parseToken(token.substring(1));
        }
//      else if(token.charAt(0)=='n')
//      {
//           return 'π' + parseToken(token.substring(1));
//      }
        return token.charAt(0) + parseToken(token.substring(1));
    }
    public String getToken()
    {
        if(this.parsedToken.equals(""))
        {
            return "";
        }
        else{
            return this.parsedToken;
        }
    }

    public boolean isVar(char c)
    {
        return (c >= 65 && c <= 90) || (c >= 97 && c <= 122);
    }



}
