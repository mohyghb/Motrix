package com.mohyaghoub.calculator;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;

public class CreateList_Activity extends AppCompatActivity {

    private ArrayAdapter<ListFragment> listFragmentArrayAdapter;
    private ConstraintLayout createListLayout;
    private FloatingActionButton EnterValuesBtn_List;
    private EditText EnterValues_List;
    private ListView listView_Fragments;
    private ArrayList<ListFragment> ListFragmentsArray;
    private List list;
    private int position;

    private TextView emptyShowListView;

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);
        extras = getIntent().getExtras();
        initObjects();
    }

    protected void initObjects()
    {
        this.createListLayout = findViewById(R.id.createListLayout);
        this.EnterValuesBtn_List = findViewById(R.id.EnterValuesBtn_List);
//        this.floatingVoice_List = findViewById(R.id.floatingVoiceListCreator);
        this.EnterValues_List = findViewById(R.id.EnterValues_List);
        this.emptyShowListView = findViewById(R.id.emptyShowListView);
        initListFragmentArrays();
        initListView();
        addSetOnClickListeners();
    }

    protected void initListView()
    {
        this.listFragmentArrayAdapter= new CreatorList_ListView(CreateList_Activity.this, 0,ListFragmentsArray);
        listView_Fragments = findViewById(R.id.ListViewForListFragments);
        listView_Fragments.setAdapter(this.listFragmentArrayAdapter);
        listView_Fragments.setEmptyView(this.emptyShowListView);
    }

    protected void initListFragmentArrays()
    {
        try{
            this.position = Integer.parseInt(extras.getString("position"));
            this.list = LoadActualLists.savedLists.get(position);
            this.ListFragmentsArray = this.list.getFragments();
        }catch(Exception exception)
        {
            this.position = -1;
            this.ListFragmentsArray = new ArrayList<>();
            this.list = new List("",this.ListFragmentsArray);
        }
    }

    protected void addSetOnClickListeners()
    {
        this.EnterValuesBtn_List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = EnterValues_List.getText().toString();
                if(text.length()>15)
                {
                    Toast.makeText(CreateList_Activity.this,"Maximum number of digits exceeded.",Toast.LENGTH_SHORT).show();
                }
                else if(!text.isEmpty()&&!text.equals("-")&&!text.equals("."))
                {
                    ListFragment listFragment = new ListFragment(text);
                    ListFragmentsArray.add(listFragment);
                    listFragmentArrayAdapter.notifyDataSetChanged();
                    EnterValues_List.setText("");
                }
                else
                {
                    Toast.makeText(CreateList_Activity.this,"You must enter a value.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    //MENU
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.SAVE_LISTS)
        {
            if(ListFragmentsArray.size()>0)
            {
                saveThisList();
            }
            else
            {
                Toast.makeText(this,"You must enter something inside the list.",Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        else if(id == R.id.CLEAR_ALL_LIST_FRAGMENTS)
        {
            ClearAllListFragments();
            return true;
        }
        else if(id == R.id.ORDER_LISTFRAGMENTS)
        {
            OrderList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void OrderList()
    {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view1 = inflater.inflate(R.layout.changecolorof_functions, null);
        builder.setView(view1);
        LinearLayout layout = view1.findViewById(R.id.linearLayoutColorChange);

        final RadioButton[] rb = new RadioButton[5];
        final RadioGroup rg = new RadioGroup(this); //create the RadioGroup
        rg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
        String radio[] = {"Low to High","High to Low"};
        for(int i=0; i<radio.length; i++){
            rb[i]  = new RadioButton(this);
            rb[i].setText(radio[i]);
            rb[i].setId(i + 10);
            rg.addView(rb[i]);
        }
        layout.addView(rg);

        builder.setTitle("Orientation")
                .setMessage("Choose one please")
                .setPositiveButton("Order", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int radioId = rg.getCheckedRadioButtonId();
                        switch (radioId)
                        {
                            case 10:
                                order("low");
                                Toast.makeText(CreateList_Activity.this,"Low to high completed!",Toast.LENGTH_SHORT).show();
                                break;
                            case 11:
                                order("high");
                                Toast.makeText(CreateList_Activity.this,"High to low completed!",Toast.LENGTH_SHORT).show();
                                    break;
                        }

                    }
                })
                .setNegativeButton("cancel",null);

        dialog = builder.create();
        dialog.show();
    }

    protected void order(String situation)
    {
        try {
            double[] allInputs = new double[this.ListFragmentsArray.size()];
            for (int i = 0; i < allInputs.length; i++) {
                allInputs[i] = Double.parseDouble(this.ListFragmentsArray.get(i).getText());
            }
            Arrays.sort(allInputs);

            if(situation.equals("low"))
            {
                for(int i= 0 ;i<allInputs.length;i++)
                {
                    this.ListFragmentsArray.get(i).setText(allInputs[i]+"");
                }
            }
            else
            {
                int p = 0;
                for(int i= allInputs.length-1 ;i>=0;i--)
                {
                    this.ListFragmentsArray.get(p).setText(allInputs[i]+"");
                    p++;
                }
            }
            this.listFragmentArrayAdapter.notifyDataSetChanged();
        }catch(Exception e)
        {
            Toast.makeText(CreateList_Activity.this,"Sorry could not order the list. Check your numbers.",Toast.LENGTH_SHORT).show();
        }
    }



    private void ClearAllListFragments()
    {
        ListFragmentsArray = new ArrayList<>();
        initListView();
    }



    private void saveThisList()
    {
            final AlertDialog dialog;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = CreateList_Activity.this.getLayoutInflater();
            final View view1 = inflater.inflate(R.layout.save_require_name, null);
            builder.setView(view1);
            builder.setTitle("To save, enter a name please")
                    .setPositiveButton("save", null)
                    .setNegativeButton("Cancel", null);
            dialog = builder.create();
            final EditText text = view1.findViewById(R.id.enterANameSaveMain);
            text.setText(list.getName());
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialogInterface) {

                    Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // TODO Do something
                            String name = Calculator.removeSpaces(text.getText().toString());
                            if(position!=-1&&LoadActualLists.isAGoodListNameExcept(name,list.getName(),CreateList_Activity.this))
                            {
                                LoadActualLists.savedLists.get(position).setFragments(ListFragmentsArray);
                                LoadActualLists.savedLists.get(position).setName(name);
                                LoadActualLists.saveLists(CreateList_Activity.this);
                                Toast.makeText(CreateList_Activity.this,"Saved!",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
                            }
                            else if(LoadActualLists.isAGoodListName(name,CreateList_Activity.this))
                            {
                                List list = new List(name,ListFragmentsArray);
                                LoadActualLists.savedLists.add(list);
                                LoadActualLists.saveLists(CreateList_Activity.this);
                                Toast.makeText(CreateList_Activity.this,"Created!",Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
                            }
                            }
                    });
                }
            });
            dialog.show();
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(MainActivity.examMode.isAppInLockTaskMode())
        {
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }

}

