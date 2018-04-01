package due2do.mobile.com.duetodo.activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.adapter.ReminderAdapter;
import due2do.mobile.com.duetodo.model.CameraReminder;

/**
 * Implementation of App Widget functionality.
 */
public class todoWidget extends AppWidgetProvider {

    TextView taskName,taskDate;
    boolean isopen = false;
    CameraReminder cameraReminder = new CameraReminder();
    RecyclerView recyclerView;
    ReminderAdapter adapter;

    List<CameraReminder> reminderList = new ArrayList<>();

    private DatabaseReference mDatabaseReference;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public static final String UPDATE_MEETING_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";

    public static final String EXTRA_ITEM = "com.example.edockh.EXTRA_ITEM";



    public void onReceive(Context context, Intent intent) {

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);

        if (intent.getAction().equals(UPDATE_MEETING_ACTION)) {

            int appWidgetIds[] = mgr.getAppWidgetIds(new ComponentName(context,todoWidget.class));

            Log.e("received", intent.getAction());

            mgr.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view);



        }

        super.onReceive(context, intent);

    }



    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.todo_widget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {


            /*
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            String name = user.getDisplayName(); // https://stackoverflow.com/questions/42056333/getting-user-name-lastname-and-id-in-firebase

            // database
            final FirebaseUser mUser = firebaseAuth.getCurrentUser();
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference readRef = mDatabaseReference.child(mUser.getUid());
            final DatabaseReference simpleReadRef = mDatabaseReference.child(mUser.getUid()).child("SimpleTask");


            recyclerView = (RecyclerView) findViewById(R.id.recylerView);
            recyclerView.setHasFixedSize(true);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //Read From Firebase Database
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
                    adapter = new ReminderAdapter(todoWidget.this, reminderList);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            */



            Intent intent = new Intent(context, ListViewWidgetService.class);

            // Add the app widget ID to the intent extras.

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // Instantiate the RemoteViews object for the app widget layout.

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Set up the RemoteViews object to use a RemoteViews adapter.

            // This adapter connects

            // to a RemoteViewsService  through the specified intent.

            // This is how you populate the data.

            rv.setRemoteAdapter(appWidgetIds[i], R.id.list_view, intent);

            // Trigger listview item click

            Intent startActivityIntent = new Intent(context,MainActivity.class);

            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            rv.setPendingIntentTemplate(R.id.list_view, startActivityPendingIntent);

            // The empty view is displayed when the collection has no items.

            // It should be in the same layout used to instantiate the RemoteViews  object above.

            rv.setEmptyView(R.id.list_view, R.id.empty_view);

            //

            // Do additional processing specific to this app widget...

            //

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);

        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }





            try {
                Intent intent = new Intent(context, to_do.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.todo_widget);

                views.setOnClickPendingIntent(R.id.appwidget_task,pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);

            } catch (ActivityNotFoundException e) {
                Toast.makeText(context.getApplicationContext(),
                        "There was a problem loading the application: ",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

