package com.mohyaghoub.calculator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowListsActivity extends AppCompatActivity {

    private ListView ShowLists_ListView;
    private ArrayAdapter<List> listsArrayAdapter;
    private FloatingActionButton AddLists;
    private TextView emptyView;



    private Animation upToDown;
    private Animation appear;
    private Animation downToUp;
    private Animation disappear;


    private int oldFirstVisibleItem = -1;
    protected int oldTop = -1;
    // you can change this value (pixel)
    private static final int MAX_SCROLL_DIFF = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_lists);

        initAnimations();
        initListView();
    }

    private void initAnimations()
    {
        this.upToDown = AnimationUtils.loadAnimation(this,R.anim.uptodown);
        this.appear = AnimationUtils.loadAnimation(this,R.anim.appear);
        this.downToUp = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        this.disappear = AnimationUtils.loadAnimation(this,R.anim.disappear);
    }

    private void initListView()
    {
        LoadActualLists.loadLists(this);
        this.emptyView = findViewById(R.id.emptyShowListView);
        this.ShowLists_ListView = findViewById(R.id.ShowLists_ListView);
        this.listsArrayAdapter = new ShowList_ListView(this,0,LoadActualLists.savedLists);
        this.ShowLists_ListView.setAdapter(this.listsArrayAdapter);
        this.ShowLists_ListView.setEmptyView(emptyView);
        this.AddLists = findViewById(R.id.ADD_LISTS);



        this.AddLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowListsActivity.this,CreateList_Activity.class);
                startActivity(intent);
            }
        });

        ShowLists_ListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int i1, int i2) {

                if (firstVisibleItem == oldFirstVisibleItem) {
                    int top = view.getChildAt(0).getTop();
                    // range between new top and old top must greater than MAX_SCROLL_DIFF
                    if (top > oldTop && Math.abs(top - oldTop) > MAX_SCROLL_DIFF) {
                        AddLists.setVisibility(View.VISIBLE);
                        AddLists.startAnimation(appear);
                        // scroll up
                    } else if (top < oldTop && Math.abs(top - oldTop) > MAX_SCROLL_DIFF) {
                        AddLists.setVisibility(View.INVISIBLE);
                        AddLists.startAnimation(disappear);
                        // scroll down
                    }
                    oldTop = top;
                } else {
                    View child = view.getChildAt(0);
                    if (child != null) {
                        oldFirstVisibleItem = firstVisibleItem;
                        oldTop = child.getTop();
                    }
                }

            }
        });
    }

    @Override
    public void onResume()
    {
        this.ShowLists_ListView.startAnimation(appear);
        this.AddLists.startAnimation(appear);
        listsArrayAdapter.notifyDataSetChanged();
        super.onResume();
    }





    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.SDelete)
        {
            if(numberOfSelectedLists()>0)
            {
                createDialogForSelectedDelete();
            }
            else if(LoadActualLists.savedLists.size()>0)
            {
                try{
                    createDialogForDeleteAll();
                }catch(Exception e)
                {
                    Toast.makeText(this,"Sorry, something went wrong.",Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(ShowListsActivity.this,"No function available at this moment to delete.",Toast.LENGTH_LONG).show();
            }

            return true;
        }
        else if(id==R.id.WINDOW)
        {
            Intent intent = new Intent(ShowListsActivity.this,WindowActivity.class);
            startActivity(intent);
//            createDialogForWindow();
            return true;
        }
        else if(id==R.id.GraphFunctionActivityMultiple)
        {
            int NSF = numberOfSelectedLists();
            if(NSF==2)
            {
                Toast.makeText(this,"Loading the Graph...",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ShowListsActivity.this,GraphLists.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(this,"You need to choose two lists to graph.",Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        else if(id==R.id.SELECTALL)
        {
            SelectAll();
            return true;
        }
        else if(id==R.id.DESELECTALL)
        {
            DeselectAll();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }




    public int numberOfSelectedLists()
    {
        int total = 0;
        for(List list:LoadActualLists.savedLists)
        {
            if(list.isSelected())
            {
                total++;
            }
        }
        return total;
    }

    protected void createDialogForDeleteAll()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowListsActivity.this);
        builder.setMessage("Are you sure you want to delete every list?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        for(List list:LoadActualLists.savedLists)
                        {
                            list.setDeleted(true);
                        }
                        listsArrayAdapter.clear();
                        listsArrayAdapter.notifyDataSetChanged();
                        deleteFile(FileNames.fileName);
                        Toast.makeText(ShowListsActivity.this,"Successfully deleted all the lists!",Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create();
        builder.show();
    }

    protected void createDialogForSelectedDelete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowListsActivity.this);
        String msg = "Are you sure you want to delete these lists?\n";
        for(List list:LoadActualLists.savedLists)
        {
            if(list.isSelected())
            {
                msg+=list.getName()+"\n";
            }
        }
        builder.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteSelectedFunctions();
                        LoadActualLists.saveLists(ShowListsActivity.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create();
        builder.show();
    }

    private void deleteSelectedFunctions()
    {
        while(this.numberOfSelectedLists()>0)
        {
            deleteSelected();
        }
        Toast.makeText(this,"Selected lists were successfully deleted!",Toast.LENGTH_SHORT).show();
    }
    private void deleteSelected()
    {
        for(List list: LoadActualLists.savedLists)
        {
            if(list.isSelected())
            {
                list.setDeleted(true);
                listsArrayAdapter.remove(list);
                return;
            }
        }
    }





    private void SelectAll()
    {
        for(List list: LoadActualLists.savedLists)
        {
            if(!list.isSelected())
            {
                list.setSelected(true);
            }
        }
        listsArrayAdapter.notifyDataSetChanged();
        Toast.makeText(this,"All lists were selected.",Toast.LENGTH_SHORT).show();
    }

    private void DeselectAll()
    {
        for(List list: LoadActualLists.savedLists)
        {
            if(list.isSelected())
            {
                list.setSelected(false);
            }
        }
        listsArrayAdapter.notifyDataSetChanged();
        Toast.makeText(this,"All lists were deselected.",Toast.LENGTH_SHORT).show();
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
