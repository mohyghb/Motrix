package com.mohyaghoub.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShowList_ListView extends ArrayAdapter<List> {

    private Context context;
    private java.util.List<List> listList;


    public ShowList_ListView(Context context, int resource, ArrayList<List> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.listList = objects;
    }

    //called when rendering the list
    public View getView(final int position, final View convertView, ViewGroup parent)
    {
        final List list = listList.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.showlist_list_view, null);

        if(list.isSelected())
        {
            view.setBackgroundColor(ContextCompat.getColor(context,ColorsInJava.colors[5]));
        }
        else
        {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        final TextView name, data, number;
        Button deleted,edit,tools;

        name = view.findViewById(R.id.ListName_ListView);
        data = view.findViewById(R.id.Data_ListView);
        number = view.findViewById(R.id.ListNumber_ListView);
        edit = view.findViewById(R.id.EDIT_LIST_SHOWLIST);
        deleted = view.findViewById(R.id.DELETE_LIST_SHOWLIST);
        tools = view.findViewById(R.id.TOOLS_LIST_SHOWLIST);

        name.setText(list.getName());
        data.setText(list.getABitOfData());
        number.setText(String.format("List #%d",position+1));

        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!list.isDataTouched())
                {
                    list.setDataTouched(true);
                    data.setText(list.getData());
                }
                else {
                    list.setDataTouched(false);
                    data.setText(list.getABitOfData());
                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,CreateList_Activity.class);
                intent.putExtra("position",position+"");
                context.startActivity(intent);
            }
        });
        deleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(String.format("Are you sure you want to delete '%s' list?",list.getName()))
                        .setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                listList.remove(position);
                                notifyDataSetChanged();
                                list.setDeleted(true);
                                LoadActualLists.saveLists(context);
                                Toast.makeText(context,String.format("'%s' was removed successfully.",list.getName()),Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No, cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                // Create the AlertDialog object and return it
                builder.create();
                builder.show();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.isSelected())
                {
                    list.setSelected(false);
                    view.setBackgroundColor(Color.TRANSPARENT);
                }
                else
                {
                    list.setSelected(true);
                    view.setBackgroundColor(ContextCompat.getColor(context,ColorsInJava.colors[5]));
                }
            }
        });
        return view;
    }

}
