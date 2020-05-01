package com.mohyaghoub.calculator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Tools_FunctionListView extends ArrayAdapter<FunctionCreator>  {


        private Context context;
        private List<FunctionCreator> functionList;


        public Tools_FunctionListView(Context context, int resource, ArrayList<FunctionCreator> objects)
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
            final View view = inflater.inflate(R.layout.multiplefunctions_layout, null);

            TextView name = view.findViewById(R.id.name_multipleGraph);
            TextView functionName = view.findViewById(R.id.function_MultipleGraphs);
            TextView number = view.findViewById(R.id.functionNumbers_Multiple);

            name.setText("name: "+function.getName());
            functionName.setText("y = "+function.getFunction());
            number.setText(String.format("#%d",position+1));

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //start tools activity
                    Intent intent = new Intent (context,ToolsActivity.class);
                    intent.putExtra("EQUATION",position+"");
                    context.startActivity(intent);
                }
            });


            return view;
        }

}
