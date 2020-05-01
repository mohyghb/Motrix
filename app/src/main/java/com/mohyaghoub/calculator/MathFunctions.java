package com.mohyaghoub.calculator;

import java.util.ArrayList;

public class MathFunctions {

    static boolean INVERSE = false;
    static boolean THEREWASAVARIABLE = false;
    //add a boolean so that it transfers 'to' to '2' if there is no variable before it

    //trig
    static final String[] SIN = {"sin","sign","sine","fine","find"};
    static final String[] COS = {"cos","cosine","cosign","coast","cost","cosigner","ghost"};
    static final String[] TAN = {"tangent","tan","can"};
    static final String[] CSC = {"cosecant","cosicant","csc","cosi","cuz","casement"};
    static final String[] SEC = {"sec","secant","sicant","see"};
    static final String[] COT = {"cot","cotan","cotangent","coat","put","coach","codes","code","kot","quote","quotes","coats","colton"};
    static final String[] ABS = {"abs","absolute"};
    //logs
    static final String[] LOG = {"log","lock","locked","lucky","luck","logged","lot"};
    static final String[] LN = {"ln","lawn","loan","lan","blond","lon","lone","lana","alone","salon","long","lauren","law","full-on"};

    //variables
    static final String[] PI = {"pi","pie","hi","π","n"};
    static final String[] X = {"explicit","excel","context","extra","x","ex","axe","ax","excess","necks","ox","tax","taxi","exit","access","expose","excuses","excuse","dax","axis","effects","sex","eggs","xbox","annex"};

    //powers
    static final String[] POWER = {"power","powder","^","pair","verify"};
    static final String[] SQUARED = {"squared","squirt","scored","score","escort","card","scar","court","accessport"};
    static final String[] CUBED = {"cubed","cube","cute"};

    //basic math
    static final String[] MULTIPLICATION = {"times","multiply","*","multiplication","time","mcfly"};
    static final String[] DIVISION = {"divide","slash","/","division","dividedby","divided"};
    static final String[] ADDITION = {"add","addition","+","plus","cost","cross","class","at","plastic","blast"};
    static final String[] SUBTRACTION = {"subtract","minus","-","subtraction","negative"};

    //numbers
    static final String[] NUMBERS = {"zero","one","two","three","four","five","six","seven","eight","nine","cero","on","to","tree","for","fiv","see","soven","ate","nin"};
    static final String[] EQUAL = {"equals","equal"};
    static final String[] E = {"a","e","eay","yay","ii","i","eat"};
    static final String[] SQUAREROOT = {"√","root","rot","rut","sqrt","route","screw","screws"};
    static final String[] PERCENTS = {"percent","cent","%","person","persons"};

    //brackets
    static final String[] OPEN = {"open","bracket","upon","(","opened","opens","brock"};
    static final String[] CLOSE = {"close","closet","clause",")","clothes","cloth","closed","closes","clues","clue","cloves","calls","call","called","class","clove","coils","coil","clippers"};

    //inverse
    static final String[] INVERSEcommand = {"invoice","inverse","ingress","in","inverter","reverse","universe"};

    //functions that was created by them
    static ArrayList<FunctionCreator> savedFunctions = new ArrayList<>();

    //combinations
    static final String[] SINX = {"sinex","sinx","sonics","sonic's","snacks","sinus","cidex","sidex"};
    static final String[] COSX = {"cossacks","cosex"};
    static final String[] LNX = {"loanex","lennox","lenox"};
    static final String[] PIX = {"pyrex","piex","kayaks"};

    public static void cleanBooleans()
    {
        MathFunctions.INVERSE = false;
        MathFunctions.THEREWASAVARIABLE = false;
    }


    static final String negativeAndMinus ="+/^*-(";


}
