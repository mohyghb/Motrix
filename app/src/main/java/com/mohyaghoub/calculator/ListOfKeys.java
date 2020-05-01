package com.mohyaghoub.calculator;

public class ListOfKeys {

    private String name;
    private String[] keys;

    ListOfKeys(String name,String[] keys)
    {
        this.name = name;
        this.keys = keys;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }
}
