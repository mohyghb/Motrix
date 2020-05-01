package com.mohyaghoub.calculator;

import java.util.ArrayList;
import java.util.Arrays;

public class List {

    private String name;
    private ArrayList<ListFragment> fragments;
    private boolean deleted,selected,dataTouched;
    private double[] data;

    List(String list)
    {
        this.selected = false;
        this.deleted = false;
        this.dataTouched = false;
        parse(list);
    }

    List(String name,ArrayList<ListFragment> fragments)
    {
        this.deleted = false;
        this.selected = false;
        this.dataTouched = false;
        this.name = name;
        this.fragments = fragments;
    }

    private void parse(String list)
    {
        String split[] = list.split("\\|");
        this.name = split[0];
        this.fragments = new ArrayList<>();
        for(int i =1;i<split.length;i++)
        {
            String text = split[i];
            if(!text.isEmpty())
            {
                ListFragment listFragment = new ListFragment(text);
                this.fragments.add(listFragment);
            }
        }

    }

    public boolean isDataTouched() {
        return dataTouched;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setDataTouched(boolean dataTouched) {
        this.dataTouched = dataTouched;
    }

    public String getName() {
        return name;
    }

    public ArrayList<ListFragment> getFragments() {
        return fragments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFragments(ArrayList<ListFragment> fragments) {
        this.fragments = fragments;
    }

    public String listToString()
    {
        String listToString = name;
        for(ListFragment listFragment:this.fragments)
        {
            listToString+= String.format("|%s",listFragment.getText());
        }
        return listToString;
    }

    public String getData()
    {
        String Data = "";
        for(ListFragment listFragment:this.fragments)
        {
            if(!listFragment.isDeleted())
            {
                Data+=String.format("%s\n",listFragment.getText());
            }
        }
        if(Data.length()>0)
        {
            return Data.substring(0,Data.length()-1);
        }
        else
        {
            return "";
        }
    }

    public String[] getListData()
    {
        String data = "";
        for(ListFragment listFragment:this.fragments)
        {
            if(!listFragment.isDeleted())
            {
                data+=String.format("%s ",listFragment.getText());
            }
        }
        String split[] = data.split(" ");
        return split;
    }


    public String getABitOfData()
    {
        String aBitOfData = "";
        for(ListFragment listFragment:this.fragments)
        {
            if(aBitOfData.length()>=10)
            {
                break;
            }
            if(!listFragment.isDeleted())
            {
                aBitOfData+=String.format("%s,",listFragment.getText());
            }
        }
        if(aBitOfData.length()>0)
        {
            aBitOfData = aBitOfData.substring(0,aBitOfData.length()-1)+"...";
            return aBitOfData;
        }
        else
        {
            return "No data";
        }
    }


    public double[] getDataDouble()
    {
        return this.data;
    }


    public void generateAndSaveData()
    {
        String listData[] = this.getListData();
        this.data = new double[listData.length];
        for(int i = 0;i<data.length;i++)
        {
            data[i] = Double.parseDouble(listData[i]);
        }
    }


}
