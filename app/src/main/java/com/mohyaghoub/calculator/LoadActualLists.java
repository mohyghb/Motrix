package com.mohyaghoub.calculator;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class LoadActualLists {

    public static ArrayList<List> savedLists = new ArrayList<>();

    public static boolean isAGoodListName(String name, Context context)
    {
        if(name.length()>=6)
        {
            Toast.makeText(context,"Names should contain equal or less than 5 characters.",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(name.length()==0)
        {
            Toast.makeText(context,"You need to enter a name",Toast.LENGTH_SHORT).show();
            return false;
        }
        for(List list:savedLists)
        {
            if(name.equals(list.getName()))
            {
                Toast.makeText(context,String.format("There is another list with the name of '%s'.",name),Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    public static boolean isAGoodListNameExcept(String name, String except ,Context context)
    {
        if(name.length()>=6)
        {
            Toast.makeText(context,"Names should contain equal or less than 5 characters.",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(name.length()==0)
        {
            Toast.makeText(context,"You need to enter a name",Toast.LENGTH_SHORT).show();
            return false;
        }
        for(List list:savedLists)
        {
            if(name.equals(list.getName())&&!name.equals(except))
            {
                Toast.makeText(context,String.format("There is another list with the name of '%s'.",name),Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    public static void saveLists(Context context)
    {
        String allLists = "";
        for(List list:savedLists)
        {
            if(!list.isDeleted())
            {
                allLists+=list.listToString()+"&";
            }
        }
        LoadList.saveFile(FileNames.SAVEDLISTS,allLists,context);
    }

    public static void loadLists(Context context)
    {
        try {
            savedLists = new ArrayList<>();
            String allLists = LoadList.readFile(FileNames.SAVEDLISTS, context);
            String allListsSplit[] = allLists.split("\\&");
            for (int i = 0; i < allListsSplit.length; i++) {
                if (!allListsSplit[i].isEmpty()) {
                    List list = new List(allListsSplit[i]);
                    savedLists.add(list);
                }
            }
        }catch(Exception e)
        {
            Toast.makeText(context,"Something went wrong when loading the lists.",Toast.LENGTH_SHORT).show();
        }
    }





}
