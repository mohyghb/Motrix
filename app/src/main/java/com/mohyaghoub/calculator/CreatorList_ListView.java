package com.mohyaghoub.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreatorList_ListView extends ArrayAdapter<ListFragment> {
    private Context context;
    private List<ListFragment> listList;


    public CreatorList_ListView(Context context, int resource, ArrayList<ListFragment> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.listList = objects;
    }

    //called when rendering the list
    public View getView(final int position, final View convertView, final ViewGroup parent)
    {
        final ListFragment listFragment = listList.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.list_fragment_layout, null);

        if(listFragment.isSelected())
        {
            view.setBackgroundColor(ContextCompat.getColor(context,ColorsInJava.colors[5]));
        }
        else
        {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numSelected = actualSelected(position);
                if(numSelected==1)
                {
                    int positionOfTheOtherSelcted = positionOfSelected(position);
                    ListFragment switchFragment = listList.get(positionOfTheOtherSelcted);

                    String saveText = switchFragment.getText();
                    switchFragment.setText(listFragment.getText());
                    listFragment.setText(saveText);

                    switchFragment.setSelected(false);
                    listFragment.setSelected(false);
                    notifyDataSetChanged();
                }
                else
                {
                    if(listFragment.isSelected())
                    {
                        listFragment.setSelected(false);
                        view.setBackgroundColor(Color.TRANSPARENT);
                       // addAfter.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                      //  addAfter.setVisibility(View.VISIBLE);
                        listFragment.setSelected(true);
                        view.setBackgroundColor(ContextCompat.getColor(context,ColorsInJava.colors[5]));
                    }

                }
            }
        });



        final Button inputDialog = view.findViewById(R.id.InputListFragment);
        inputDialog.setText(listFragment.getText());
        inputDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog;
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.value_change_list_fragment, null);
                builder.setView(view);

                final EditText change = view.findViewById(R.id.input_change_ListFragment);
                change.setText(listFragment.getText());
                builder.setTitle("Change").setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newText = change.getText().toString();
                        if(newText.length()>15)
                        {
                            Toast.makeText(context,"Maximum number of digits exceeded.",Toast.LENGTH_SHORT).show();
                        }
                        else if(newText.length()>0&&!newText.equals("-")&&!newText.equals("."))
                        {
                            listFragment.setText(newText);
                            notifyDataSetChanged();
                            Toast.makeText(context,"Changed!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(context,"Enter a number, could not change.",Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setNegativeButton("Cancel",null);



                dialog = builder.create();
                dialog.show();
            }
        });




        TextView number = view.findViewById(R.id.PositionListFragment);
        number.setText(String.format("#%d",position+1));

        Button DeleteListFragment = view.findViewById(R.id.DeleteListFragment);
        DeleteListFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listList.remove(position);
                listFragment.setDeleted(true);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    public int actualSelected(int position)
    {
        int i = 0;
        int cur = 0;
        for(ListFragment listFragment:this.listList)
        {
            if(listFragment.isSelected()&&cur!=position)
            {
                i++;
            }
            cur++;
        }
        return i;
    }

    public int positionOfSelected(int position)
    {
        int cur = 0;
        for(ListFragment listFragment:this.listList)
        {
            if(listFragment.isSelected()&&cur!=position)
            {
                return cur;
            }
            cur++;
        }
        return cur;
    }




}
