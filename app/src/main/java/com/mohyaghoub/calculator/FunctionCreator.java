package com.mohyaghoub.calculator;

public class FunctionCreator {

    private String name;
    private String function;
    private String deleted;
    private boolean isSelected;
    private boolean isChecked;
    private int color;
    private int style;
    private int thickness;
    private boolean colorChanged;

    FunctionCreator(String line)
    {
        readLine(line);
//        initClass();
    }

//    private void initClass()
//    {
//        this.isSelected = false;
//        this.colorChanged = false;
//        this.isChecked = false;
//        this.color = 5;
//        this.thickness = 5;
//        this.style = 0;
//    }
    private void readLine(String line)
    {
        String sep[] = line.split("\\|");
        this.name = sep[0].toLowerCase();
        this.function = sep[1];
        this.deleted = sep[2];
        initSelected(sep[3]);
        this.color = Integer.parseInt(sep[4]);
        this.style = Integer.parseInt(sep[5]);
        this.thickness = Integer.parseInt(sep[6]);
    }
    private void initSelected(String s)
    {
        if(s.equals("false"))
        {
            this.setSelected(false);
        }
        else{
            this.setSelected(true);
        }
    }


    public String getName()
    {
        return this.name;
    }
    public String getFunction()
    {
        return this.function;
    }
    public String getDeleted()
    {
        return this.deleted;
    }
    public void delete()
    {
        this.deleted = "true";
    }
    public String getNameAndFunction()
    {
        return String.format("%s: %s\n",this.name,this.function);
    }
    public boolean isDeleted()
    {
        return this.deleted.equals("true");
    }
    public boolean isSelected()
    {
        return this.isSelected;
    }
    public boolean isChecked()
    {
        return this.isChecked;
    }
    public boolean isColorChanged() {
        return colorChanged;
    }
    public int getColor()
    {
        return this.color;
    }
    public int getStyle()
    {
        return this.style;
    }
    public int getThickness()
    {
        return this.thickness;
    }
    public String getSelected()
    {
        if(this.isSelected)
        {
            return "true";
        }
        else{
            return "false";
        }
    }



    public void setName(String name)
    {
        this.name = name;
    }
    public void setFunction(String function)
    {
        this.function = function;
    }
    public void setSelected(boolean isSelected)
    {
        this.isSelected = isSelected;
    }
    public void setColor(int cn)
    {
        this.color = cn;
    }
    public void setStyle(int s)
    {
        this.style = s;
    }
    public void setThickness(int th)
    {
        this.thickness = th;
    }


    public void setColorChanged(boolean bool)
    {
        this.colorChanged = bool;
    }
    public void setChecked(boolean tof)
    {
        this.isChecked = tof;
    }










}
