package due2do.mobile.com.duetodo.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.model.Task;

/**
 * Created by dalalbhargav07 on 01-04-2018.
 */

class ListProvider implements RemoteViewsService.RemoteViewsFactory {
    private Context context = null;

    private int appWidgetId;

    private ArrayList<ListItem> listItemList = new ArrayList<ListItem>();


    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser;

    public ListProvider(Context applicationContext, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        populateListItem();
    }

    private void populateListItem() {

        DatabaseReference readRef = mDatabaseReference.child(mUser.getUid());

        readRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //displayList.clear();
                for(DataSnapshot ds : dataSnapshot.child("CameraTask").getChildren()){
                    Task details = ds.getValue(Task.class);
                    ListItem listitem = new ListItem();
                    listitem.task = details.getTask();
                    listItemList.add(listitem);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.task_row);
        ListItem lstitem = listItemList.get(position);
        remoteView.setTextViewText(R.id.task, lstitem.task);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        return listItemList.size();
    }
}
