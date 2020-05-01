package com.mohyaghoub.calculator;


import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;

public class LoadHistory {
    static ArrayList<String> History;

    static final String[] Launchers = {"Main-menu","Functions","Lists","Smart Voice","Tools","Calculator","Window"};
    static String CurrentLauncher;

    public static void LoadCurrentLauncher(Context context)
    {
        String loadedLauncher = LoadList.readFile(FileNames.LAUNCHER,context);
        if(loadedLauncher.isEmpty())
        {
            LoadHistory.CurrentLauncher = "Main-menu";
        }
        else
        {
            LoadHistory.CurrentLauncher = LoadHistory.Launchers[Integer.parseInt(loadedLauncher)];
        }

        switch (CurrentLauncher)
        {
            case "Main-menu":

                break;
            case "Functions":
                Intent intent = new Intent(context,FunctionActivity.class);
                context.startActivity(intent);
                break;
            case"Lists":
                Intent intent1 = new Intent(context,ShowListsActivity.class);
                context.startActivity(intent1);
                break;
            case"Smart Voice":
                Intent intent2 = new Intent(context,SmartRoom.class);
                context.startActivity(intent2);
                break;
            case "Tools":
                Intent intent3 = new Intent(context,Tools_FunctionSelection.class);
                context.startActivity(intent3);
                break;

            case "Window":
                Intent intent4 = new Intent(context,WindowActivity.class);
                context.startActivity(intent4);
                break;
        }
    }

    public static void saveLauncher(int position,Context context)
    {
        CurrentLauncher = Launchers[position];
        LoadList.saveFile(FileNames.LAUNCHER,position+"",context);
    }


   public static void LoadHistory(Context context)
   {
       History = new ArrayList<>();
       String loadedHistory = LoadList.readFile(FileNames.HISTORY,context);
       String sepLoadedData[] = loadedHistory.split("\\|");
       int length = sepLoadedData.length;
       if(length>200)
       {
           for(int i = length-200;i<length;i++)
           {
               History.add(sepLoadedData[i]);
           }
       }
       else
       {
           for(int i = 0;i<length;i++)
           {
               History.add(sepLoadedData[i]);
           }
       }

   }

   public static void addHistory(String line,Context context)
   {
       String all = "";
       for(String savedHis:History)
       {
           all+=savedHis+"|";
       }
       if(!line.equals(""))
       {
           all+=line;
           LoadHistory.History.add(line);
       }
       LoadList.saveFile(FileNames.HISTORY,all,context);
   }

   public static String getHistory()
   {
       String all = "";
       for(String savedHis:History)
       {
           if(!savedHis.equals(""))
           {
               all += savedHis + "|";
           }
       }
       return all;
   }






}
