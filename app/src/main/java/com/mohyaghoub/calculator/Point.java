package com.mohyaghoub.calculator;

public class Point {

    private double x;
    private double y;
    private boolean isDeleted;

    Point(double x,double y)
    {
        this.x = x;
        this.y = y;
        this.isDeleted = false;
    }

    public double getX()
    {
        return this.x;
    }
    public double getY()
    {
        return this.y;
    }
    public void setDeleted(boolean boo)
    {
        this.isDeleted = boo;
    }
    public boolean isDeleted()
    {
        return this.isDeleted;
    }





}
