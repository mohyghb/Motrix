package com.mohyaghoub.calculator;

public class ListFragment {

    private String text;
    private boolean deleted,selected;

    ListFragment(String text)
    {
        this.text = text;
        this.deleted = false;
        this.selected = false;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getText()
    {
        if(!this.deleted)
        {
            return this.text;
        }
        else
        {
            return "";
        }

    }
    public void setText(String text) {
        this.text = text;
    }

}
