package com.mohyaghoub.calculator;

import android.graphics.DashPathEffect;
import android.graphics.PathEffect;

public class ColorsInJava {

    final static Integer[] colors=  {
            R.color.Color1,
            R.color.Color2,
            R.color.Color3,
            R.color.Color4,
            R.color.Color5,
            R.color.Color6,
            R.color.Color7,
            R.color.Color8,
            R.color.Color9,
            R.color.Color10,
            R.color.Color11,
            R.color.Color12,
            R.color.Color13,
            R.color.Color14,
            R.color.Color15,
            R.color.Color16,
            R.color.Color17,
            R.color.Color18,
            R.color.Color19,
    };
    final static Integer[] systemColors = {
            R.color.operationBtn
    };

    final static PathEffect[] pathEffects = {
            null,
            new DashPathEffect(new float[]{8,5},0),
    };
    final static String[] nameOfTheEffects = {
            "Straight",
            "Dash"
    };

    final static int[] THICKNESS = {
      4,6,8,10,12,14,16,18,20
    };

    static int currentColor = 0;

}
