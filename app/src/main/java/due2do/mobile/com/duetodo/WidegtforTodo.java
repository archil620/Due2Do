package due2do.mobile.com.duetodo;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Implementation of App Widget functionality.
 */
public class WidegtforTodo extends AppWidgetProvider {

    public static String taskName;
    public static String taskDate;

    public static String tn = "Hello";

    //String taskName,taskDate;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        FirebaseDatabase database = FirebaseDatabase.getInstance();


        final DatabaseReference readRef = database.getReference().child("CameraTask");
        //readRef.keepSynced(true);

        //read from db
        readRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    CameraReminder camrem = new CameraReminder();
                    camrem = ds.getValue(CameraReminder.class);
                    taskName = String.valueOf(camrem.getTask());
                    taskDate = String.valueOf(camrem.getYear() + "/" + camrem.getMonth() + "/" + camrem.getDay() + " " + camrem.getHour() + ":" + camrem.getMinute());
                    //taskName.setText(String.valueOf(camrem.getTask()));
                    //taskDate.setText(String.valueOf(camrem.getYear() + "/" + camrem.getMonth() + "/" + camrem.getDay() + " " + camrem.getHour() + ":" + camrem.getMinute()));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widegtfor_todo);
        views.setTextViewText(R.id.appwidget_task, tn);
        views.setTextViewText(R.id.appwidget_date, taskDate);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
          updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        /*
        final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[j];

            try {
                Intent intent = new Intent(context,to_do.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        context, 0, intent, 0);
                RemoteViews views = new RemoteViews(context.getPackageName(),
                        R.layout.activity_to_do);
                views.setOnClickPendingIntent(view Id on which onclick to be handled, pendingIntent);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context.getApplicationContext(),
                        "There was a problem loading the application: ",
                        Toast.LENGTH_SHORT).show();
            }

        }
        */


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

