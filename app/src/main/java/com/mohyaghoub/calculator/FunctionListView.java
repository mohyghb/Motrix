package com.mohyaghoub.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FunctionListView extends ArrayAdapter<FunctionCreator> {

    private Context context;
    private List<FunctionCreator> functionList;


    public FunctionListView(Context context, int resource, ArrayList<FunctionCreator> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.functionList = objects;
    }

    //called when rendering the list
    public View getView(final int position, final View convertView, ViewGroup parent) {

        //get the property we are displaying
        final FunctionCreator function = functionList.get(position);
        //get the inflater and inflate the XML layout for each item

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.functionslayout, null);
            if(function.isSelected())
            {
                view.setBackgroundColor(ContextCompat.getColor(context,ColorsInJava.colors[function.getColor()]));
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(function.isSelected())
                    {
                        view.setBackgroundColor(Color.TRANSPARENT);
                        function.setSelected(false);
                    }
                    else if(!function.isSelected())
                    {
                        view.setBackgroundColor(ContextCompat.getColor(context,ColorsInJava.colors[function.getColor()]));
                        function.setSelected(true);
                    }
                }
            });

            final TextView name = view.findViewById(R.id.nameTextView);
            final TextView functionEditText = view.findViewById(R.id.functionTextView);
            Button graph = view.findViewById(R.id.Smart_GraphView);
            Button delete = view.findViewById(R.id.delete);
            Button edit = view.findViewById(R.id.EDIT);

            name.setText(String.format("Name: %s",function.getName()));
            functionEditText.setText(String.format("y = %s",function.getFunction()));

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(String.format("Are you sure you want to delete '%s' function?",function.getName()))
                            .setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    functionList.remove(position);
                                    FunctionListView.super.notifyDataSetChanged();
                                    function.delete();
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
            graph.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Loading the graph...", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(context, SplashGraph.class);
                    i.putExtra("position", position+"position");
                    context.startActivity(i);
                  }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,EditActivity.class);
                    intent.putExtra("position",position+"");
                    context.startActivity(intent);
                }
            });





        //set


        return view;
    }







}
