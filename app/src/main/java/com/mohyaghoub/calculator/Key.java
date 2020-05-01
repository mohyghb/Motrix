package com.mohyaghoub.calculator;

import java.util.ArrayList;

public class Key {

    private String key;
    private ArrayList<String> subKey;
    private String information;
    private String mode;
    private int posInArray;
    private String askingMemory;
    private String unparsedText;


    Key(String text,String mode,int posInArray)
    {
        this.unparsedText = text;
        setMode(mode);
        this.posInArray = posInArray;
        parseTheText(text);
    }

    private void parseTheText(String text)
    {
        String[] sepText = text.split(" ");
        this.key = sepText[0];
        findSubKey(text.substring(key.length()+1));
        checkForEquation();
        getResults();
    }



//    private void findSubKeyFunctioningKey(String text)
//    {
//        this.subKey = new ArrayList<>();
//        switch (key)
//        {
//            case "save":
//                String splitTexts[] = text.split(" ");
//                int length = splitTexts.length;
//                for(int i = 0;i<length;i++) {
//                    String word = splitTexts[i].toLowerCase();
//                    for (int j = 0; j < KeysAndSubKeys.FunctioningKeysSubKeys.length; j++) {
//                        if (word.equals(KeysAndSubKeys.FunctioningKeysSubKeys[j])) {
//                            this.subKey.add(word);
//                            break;
//                        }
//                    }
//                }
//                break;
//            case "enable":
//                String splitTexts1[] = text.split(" ");
//                int length1 = splitTexts1.length;
//                for(int i = 0;i<length1;i++) {
//                    String word = splitTexts1[i].toLowerCase();
//                    for (int j = 0; j < KeysAndSubKeys.FunctioningKeysSubKeys.length; j++) {
//                        if (word.equals(KeysAndSubKeys.FunctioningKeysSubKeys[j])) {
//                            this.subKey.add(word);
//                            break;
//                        }
//                    }
//                }
//                break;
//            default:
//                break;
//        }
//    }



    private void findSubKey(String text)
    {
        this.subKey = new ArrayList<>();
        String splitTexts[] = text.split(" ");
        int lastSubkeyPosition = 0;
        int length = splitTexts.length;
        for(int i = 0;i<length;i++)
        {
            String word = splitTexts[i].toLowerCase();
            int b =0;
            if(i+1!=length)
            {
                b = i;
            }
            if (word.length()>=2)
            {
                if((word.equals("of")||word.equals("off"))&&(usePrevSubKeys(splitTexts[b+1]))&&b==i)
                {
                    i++;
                }
                else if(usePrevSubKeys(word))
                {
                    this.subKey.add(KeysAndSubKeys.USE_PREV_SUB_KEY);
                }
                else if(word.equals("intercept")||word.equals("intercepts"))
                {
                    if(i!=0)
                    {
                        String preW = splitTexts[i-1].toLowerCase();
                        if(preW.equals("y"))
                        {
                            this.subKey.add("y-intercept");
                        }
                        else if(preW.equals("x"))
                        {
                            this.subKey.add("x-intercept");
                        }
                        else
                        {
                            this.subKey.add("intercept");
                        }
                    }
                    else
                    {
                        this.subKey.add("intercept");
                    }
                }
                else if(word.equals("calculator"))
                {
                    if(i!=0)
                    {
                        String preW = splitTexts[i-1].toLowerCase();
                        if(preW.equals("y"))
                        {
                            this.subKey.add("y-calculator");
                        }
                        else if(preW.equals("x"))
                        {
                            this.subKey.add("x-calculator");
                        }
                        else
                        {
                            this.subKey.add("calculator");
                        }
                    }
                    else
                    {
                        this.subKey.add("calculator");
                    }
                }
                else {
                    if (word.equals("of")||word.equals("off"))
                    {
                        if(i>=1)
                        {
                            String preW = splitTexts[i-1].toLowerCase();
                            if(!preW.equals("power"))
                            {
                                this.subKey.add("of");
                                lastSubkeyPosition = i;
                                break;
                            }
                        }
                    }
                    else if(word.equals("is"))
                    {
                        this.subKey.add("is");
                    }
                    else if (word.equals("equation") &&i!=length-1) {
                        if(splitTexts[i + 1].equals("to"))
                        {
                            this.subKey.add("equation");
                            lastSubkeyPosition = i + 1;
                            break;
                        }
                    }
                    for (int j = 0; j < KeysAndSubKeys.SubKeys.length; j++) {
                        if (word.contains(KeysAndSubKeys.SubKeys[j])) {
                            this.subKey.add(KeysAndSubKeys.SubKeys[j]);
                            lastSubkeyPosition = i;
                            break;
                        }
                    }
                }
            }
        }
        this.information = "";
        for(int s = lastSubkeyPosition+1;s<splitTexts.length;s++)
        {
            this.information+= splitTexts[s]+" ";
        }
    }




    private void checkForEquation()
    {
//        SmartRoom.informationCollector.setEquation(this.unparsedText);

            boolean contin = true;
            for(int i = 0; i < KeysAndSubKeys.EquationKeys.length;i++)
            {
                if (contin)
                {
                    for(String sub: this.subKey)
                    {
                        if(sub.equals(KeysAndSubKeys.EquationKeys[i]))
                        {
                            SmartRoom.informationCollector.setEquation(this.information);
                            contin = false;
                            break;
                        }
                    }
                }
            }
            if(this.subKey.size()==1&&this.subKey.get(0).equals("is"))
            {
                SmartRoom.informationCollector.setEquation(this.information);
            }
            else if(this.subKey.size()==0&&(this.key.equals("what's")||this.key.equals("whats")))
            {
                SmartRoom.informationCollector.setEquation(this.unparsedText);
            }
    }


    private void setMode(String mode)
    {
        this.mode = mode;
    }


    private boolean usePrevSubKeys(String sub)
    {
        if(sub.equals("them")||sub.equals("it")||sub.equals("this")||sub.equals("that")||sub.equals("these")||sub.equals("those"))
        {
            this.askingMemory = sub;
            return true;
        }
        else
        {
            return false;
        }
    }



    public ArrayList<String> getSubKey()
    {
        return this.subKey;
    }



    public void getResults()
    {
        switch (mode)
        {
            case KeysAndSubKeys.GETTER:
                this.GetterResults(this.subKey,true);
                break;
            case KeysAndSubKeys.SHOWING:
                this.ShowingResults(this.subKey,true);
                break;
            case KeysAndSubKeys.CHANGER:
                this.ChangerResults(this.subKey,true);
                break;
            case KeysAndSubKeys.OPENER:
                this.OpenerReuslts(this.subKey);
                break;
            case KeysAndSubKeys.ALTERNATOR:
                this.AlternatorResults(this.subKey,true);
                break;
            case KeysAndSubKeys.FUNCTIONING:
                this.FunctioningResults();
                break;
                default:
                    this.GetterResults(this.subKey,true);
                    break;
        }

    }



    private void GetterResults(ArrayList<String> subKey,boolean doItOnce)
    {
        if(this.subKey.size()==0&&(this.key.equals("what's")||this.key.equals("whats")))
        {
            SmartRoom.informationCollector.getValue();
        }
        for(String subkey:subKey)
        {
            if(subkey.contains("max"))
            {
                SmartRoom.informationCollector.addGettingThings("Maximum");
            }
            else if(subkey.contains("min"))
            {
                SmartRoom.informationCollector.addGettingThings("Minimum");
            }
            else if(subkey.equals("x-intercept"))
            {
                SmartRoom.informationCollector.addGettingThings("x-intercepts");
            }
            else if(subkey.equals("y-intercept"))
            {
                SmartRoom.informationCollector.addGettingThings("y-intercept");
            }
            else if(subkey.equals("intercept"))
            {
                SmartRoom.informationCollector.addGettingThings("intercept");
            }
            else if(subkey.contains("value"))
            {
                SmartRoom.informationCollector.getValue();
            }
            else if(subkey.equals("is")&&this.subKey.size()==1)
            {
                SmartRoom.informationCollector.getValue();
            }
            else if(subkey.contains("domain"))
            {
                SmartRoom.informationCollector.showDomain();
                SmartRoom.informationCollector.turnTrue("domain");
            }
            else if(subkey.contains("range"))
            {
                SmartRoom.informationCollector.showRange();
            }
            else if(subkey.contains("window"))
            {
                SmartRoom.informationCollector.showWindowRatios();
            }
            else if(subkey.contains("equation"))
            {
                SmartRoom.informationCollector.showEquation();
            }
            else if(subkey.contains("radian"))
            {
                SmartRoom.informationCollector.ChangeMode(1);
            }
            else if(subkey.contains("degree"))
            {
                SmartRoom.informationCollector.ChangeMode(0);
            }
            else if(subkey.contains("gradient"))
            {
                SmartRoom.informationCollector.ChangeMode(2);
            }
            else if(subkey.contains("history"))
            {
                SmartRoom.informationCollector.showHistory();
            }
            else if(subkey.contains("tool"))
            {
                SmartRoom.informationCollector.openTools();
            }
            else if(subkey.equals(KeysAndSubKeys.USE_PREV_SUB_KEY)&&doItOnce)
            {
                if(posInArray-1>=0)
                {
                    this.GetterResults(SmartRoom.keys.get(this.posInArray-1).getSubKey(),false);
                    break;
                }
            }

        }
    }

    private void OpenerReuslts(ArrayList<String> subKey)
    {
        for(String subkey:subKey)
        {
            if(subkey.contains("graph"))
            {
                SmartRoom.informationCollector.openGraph();
            }
            else if(subkey.contains("function"))
            {
                SmartRoom.informationCollector.openFunctions();
            }
            else if(subkey.contains("list"))
            {
                SmartRoom.informationCollector.openLists();
            }
            else if(subkey.contains("window"))
            {
                SmartRoom.informationCollector.openWindowRatios();
            }
            else if(subkey.contains("y-calculator"))
            {
                SmartRoom.informationCollector.giveAYCalc();
            }
            else if(subkey.contains("calculator"))
            {
                SmartRoom.informationCollector.openCalculator();
            }
            else if(subkey.contains("history"))
            {
                SmartRoom.informationCollector.showHistory();
            }
            else if(subkey.contains("tool"))
            {
                SmartRoom.informationCollector.openTools();
            }
        }
    }


    private void ShowingResults(ArrayList<String> subKey,boolean doItOnce)
    {
        for(String subkey:subKey)
        {
            if(subkey.contains("graph"))
            {
                SmartRoom.informationCollector.showGraph();
            }
            else if(subkey.contains("derivative"))
            {
                SmartRoom.informationCollector.showDerivative();
            }
            else if(subkey.equals("x-intercept"))
            {
                SmartRoom.informationCollector.addGettingThings("x-intercepts");
                SmartRoom.informationCollector.showGraph();
                SmartRoom.informationCollector.getCas().showXintsOnGraph();
                SmartRoom.informationCollector.turnTrue("xint");
            }
            else if(subkey.equals("y-intercept"))
            {
                SmartRoom.informationCollector.addGettingThings("y-intercept");
                SmartRoom.informationCollector.showGraph();
                SmartRoom.informationCollector.getCas().showYintOnGraph();
                SmartRoom.informationCollector.turnTrue("yint");
            }
            else if(subkey.equals("intercept"))
            {
                SmartRoom.informationCollector.addGettingThings("intercept");
                SmartRoom.informationCollector.showGraph();
                SmartRoom.informationCollector.getCas().showXintsOnGraph();
                SmartRoom.informationCollector.turnTrue("xint");
            }
            else if(subkey.contains("max"))
            {
                SmartRoom.informationCollector.addGettingThings("Maximum");
                SmartRoom.informationCollector.showGraph();
                SmartRoom.informationCollector.getCas().showMaxOnGraph();
                SmartRoom.informationCollector.turnTrue("max");
            }
            else if(subkey.contains("min"))
            {
                SmartRoom.informationCollector.addGettingThings("Minimum");
                SmartRoom.informationCollector.showGraph();
                SmartRoom.informationCollector.getCas().showMinOnGraph();
                SmartRoom.informationCollector.turnTrue("min");
            }
            else if(subkey.contains("value"))
            {
                SmartRoom.informationCollector.getValue();
                SmartRoom.informationCollector.turnTrue("value");
            }
            else if(subkey.contains("y-calculator"))
            {
                SmartRoom.informationCollector.giveAYCalc();
            }
            else if(subkey.contains("calculator"))
            {
                SmartRoom.informationCollector.openCalculator();
            }
            else if(subkey.contains("equation"))
            {
                SmartRoom.informationCollector.showEquation();
            }
            else if(subkey.contains("history"))
            {
                SmartRoom.informationCollector.showHistory();
            }
            else if(subkey.contains("domain"))
            {
                SmartRoom.informationCollector.showDomain();
                SmartRoom.informationCollector.turnTrue("domain");
            }
            else if(subkey.contains("range"))
            {
                SmartRoom.informationCollector.showRange();
                SmartRoom.informationCollector.turnTrue("range");
            }
            else if(subkey.contains("window"))
            {
                SmartRoom.informationCollector.showWindowRatios();
            }
            else if(subkey.contains("radian"))
            {
                SmartRoom.informationCollector.ChangeMode(1);
            }
            else if(subkey.contains("degree"))
            {
                SmartRoom.informationCollector.ChangeMode(0);
            }
            else if(subkey.contains("gradient"))
            {
                SmartRoom.informationCollector.ChangeMode(2);
            }
            else if(subkey.contains("tool"))
            {
                SmartRoom.informationCollector.openTools();
            }
            else if(subkey.equals(KeysAndSubKeys.USE_PREV_SUB_KEY))
            {
                if(posInArray-1>=0&&doItOnce)
                {
                    this.ShowingResults(SmartRoom.keys.get(this.posInArray-1).getSubKey(),false);
                    break;
                }
            }
        }
    }

    private void AlternatorResults(ArrayList<String> subKey,boolean doItOnce)
    {
        for(String sub:subKey)
        {
            if(sub.contains("graph"))
            {
                SmartRoom.informationCollector.hideGraph();
            }
            else if(sub.equals(KeysAndSubKeys.USE_PREV_SUB_KEY))
            {
                if(posInArray-1>=0&&doItOnce)
                {
                    this.AlternatorResults(SmartRoom.keys.get(this.posInArray-1).getSubKey(),false);
                    break;
                }
            }
        }

    }



    private void ChangerResults(ArrayList<String> subKey,boolean doItOnce)
    {
        for(String subkey:subKey)
        {
            if(subkey.contains("domain")||subkey.contains("range")||subkey.contains("window"))
            {
                SmartRoom.informationCollector.openWindowRatios();
            }
            else if(subkey.contains("equation"))
            {
                SmartRoom.informationCollector.setEquation(this.information);
            }
            else if(subkey.contains("function"))
            {
                SmartRoom.informationCollector.openFunctions();
            }
            else if(subkey.contains("lists"))
            {
                SmartRoom.informationCollector.openLists();
            }
            else if(subkey.contains("radian"))
            {
                SmartRoom.informationCollector.ChangeMode(1);
            }
            else if(subkey.contains("degree"))
            {
                SmartRoom.informationCollector.ChangeMode(0);
            }
            else if(subkey.contains("gradient"))
            {
                SmartRoom.informationCollector.ChangeMode(2);
            }
            else if(subkey.contains("tool"))
            {
                SmartRoom.informationCollector.openTools();
            }
            else if(subkey.equals(KeysAndSubKeys.USE_PREV_SUB_KEY))
            {
                if(posInArray-1>=0&&doItOnce)
                {
                    this.ChangerResults(SmartRoom.keys.get(this.posInArray-1).getSubKey(),false);
                    break;
                }
            }

        }
    }








    private void FunctioningResults()
    {
        switch (this.key)
        {
            case "save":
                SmartRoom.informationCollector.saveFunction();
                break;
            case"enable":
                for(String subkey:subKey)
                {
                    if(subkey.contains("radian"))
                    {
                        SmartRoom.informationCollector.ChangeMode(1);
                    }
                    else if(subkey.contains("degree"))
                    {
                        SmartRoom.informationCollector.ChangeMode(0);
                    }
                    else if(subkey.contains("gradient"))
                    {
                        SmartRoom.informationCollector.ChangeMode(2);
                    }
                    else if(subkey.contains("tool"))
                    {
                        SmartRoom.informationCollector.openTools();
                    }
                }
                break;
        }

    }














}
