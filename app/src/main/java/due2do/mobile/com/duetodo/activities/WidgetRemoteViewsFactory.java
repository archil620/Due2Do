package due2do.mobile.com.duetodo.activities;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.adapter.ReminderAdapter;
import due2do.mobile.com.duetodo.model.CameraReminder;

/**
 * Created by dalalbhargav07 on 31-03-2018.
 */

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
    private Context context = null;
    private int appWidgetId;

    private List<String> widgetList = new ArrayList<String>();
    CameraReminder cameraReminder = new CameraReminder();
    ReminderAdapter adapter;

    List<CameraReminder> reminderList = new ArrayList<>();

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public WidgetRemoteViewsFactory(Context context, Intent intent)
    {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.d("AppWidgetId", String.valueOf(appWidgetId));
        final FirebaseUser mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void updateWidgetListView()
    {
        final FirebaseUser mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference readRef = mDatabaseReference.child(mUser.getUid());
        final DatabaseReference simpleReadRef = mDatabaseReference.child(mUser.getUid()).child("SimpleTask");

        //String[] task = dbhelper.retrieveFruitsList();
        //List<String> convertedToList = new ArrayList<String>(Arrays.asList(widgetFruitsArray));
        //this.widgetList = convertedToList;
        readRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.child("CameraTask").getChildren()){

                    cameraReminder = ds.getValue(CameraReminder.class);
                    reminderList.add(cameraReminder);
                }

                for(DataSnapshot ds : dataSnapshot.child("SimpleTask").getChildren()){

                    cameraReminder = ds.getValue(CameraReminder.class);
                    reminderList.add(cameraReminder);
                }
                adapter = new ReminderAdapter(widgetList.this, reminderList);
                widgetList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getCount()
    {
        return widgetList.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public RemoteViews getLoadingView()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position)
    {
        Log.d("WidgetCreatingView", "WidgetCreatingView");
        RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                R.layout.listitem_widget);

        Log.d("Loading", widgetList.get(position));
        remoteView.setTextViewText(R.id.list_view, widgetList.get(position));

        return remoteView;
    }

    @Override
    public int getViewTypeCount()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean hasStableIds()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged()
    {
        // TODO Auto-generated method stub
        updateWidgetListView();
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        widgetList.clear();
    }
}