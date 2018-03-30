/*package com.example.archil.todo;

*//**
 * Created by Archil on 2018-02-27.
 */package due2do.mobile.com.duetodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity1 extends AppCompatActivity {

    private RecyclerView mTaskList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //mTaskList = findViewById(R.id.task_list);
        mTaskList.setHasFixedSize(true);
        mTaskList.setLayoutManager(new LinearLayoutManager(this));
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Tasks");

        /*TextView bannerDay = findViewById(R.id.bannerDay);
        TextView bannerDate = findViewById(R.id.bannerDate);*//*

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        bannerDay.setText(dayOfTheWeek);

        long date = System.currentTimeMillis();
        SimpleDateFormat sdff = new SimpleDateFormat("MMM MM dd, yyy h:mm a");
        String datestring = sdff.format(date);
        bannerDate.setText(datestring);*/
    }

    public void onClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static  class  TaskViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public  TaskViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){
            TextView task_name= mView.findViewById(R.id.taskName);
            task_name.setText(name);
        }

        public void setTime(String time){
            TextView task_time= mView.findViewById(R.id.taskTime);
            task_time.setText(time);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Task, TaskViewHolder> FBRA = new FirebaseRecyclerAdapter<Task, TaskViewHolder>(

                Task.class,
                R.layout.task_row,
                TaskViewHolder.class,
                mDatabase
        ){


            @Override
            protected void populateViewHolder(TaskViewHolder viewHolder, Task model, int position) {

                final String  task_key = getRef(position).getKey().toString();
                viewHolder.setName(model.getName());
                viewHolder.setTime(model.getTime());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent singleTaskAvtivity = new Intent(MainActivity1.this,SingleTask.class);
                        singleTaskAvtivity.putExtra("TaskId",task_key);
                        startActivity(singleTaskAvtivity);
                    }
                });


            }
        };

        mTaskList.setAdapter(FBRA);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        else if(id == R.id.addTak){
            Intent addIntent = new Intent(MainActivity1.this, AddTask.class);
            startActivity(addIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
