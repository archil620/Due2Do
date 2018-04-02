package due2do.mobile.com.duetodo.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import due2do.mobile.com.duetodo.R;
import due2do.mobile.com.duetodo.activities.to_do;
import due2do.mobile.com.duetodo.model.Task;
import due2do.mobile.com.duetodo.services.AppBroadcastReceiver;
import due2do.mobile.com.duetodo.services.RingtonePlayingService;

/**
 * Created by Bhargav Dalal on 17/03/2018.
 * Referneces:
 *  [1] “NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID) not working on Oreo Firebase notification,” android - NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID) not working on Oreo Firebase notification - Stack Overflow. [Online].
 *  Available: https://stackoverflow.com/questions/47567676/notificationcompat-buildergetapplicationcontext-channel-id-not-working-on-o.
 *  [Accessed: 02-Apr-2018].
 *
 *  [2] “How to make an Android device vibrate?,” java - How to make an Android device vibrate? - Stack Overflow. [Online].
 *  Available: https://stackoverflow.com/questions/13950338/how-to-make-an-android-device-vibrate#13950364.
 *  [Accessed: 02-Apr-2018].
 */

public class UtilityClass {

    private static final String TAG = "due2do.mobile.com.duetodo";
    Task camrem = new Task();

    public void NotificationManager(Context context, Task task){

        Intent intent = new Intent(context, to_do.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        /*Ringtone r = RingtoneManager.getRingtone(context, notification);
        */
        Intent stopIntent = new Intent(context, AppBroadcastReceiver.class);
        PendingIntent stopPendingIntent =
                PendingIntent.getBroadcast(context, 0, stopIntent, 0);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(task.getTask())
                .setContentText("Your task is due. Complete it")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //https://romannurik.github.io/AndroidAssetStudio/icons-generic.html#source.type=clipart&source.clipart=stop&source.space.trim=1&source.space.pad=0&size=24&padding=8&color=rgb(0%2C%200%2C%200)&name=ic_stop
                .addAction(R.drawable.ic_stop, "Stop",
                        stopPendingIntent)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(1);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(101, mBuilder.build());
        Log.i(TAG, "Inside Utility");
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds

        if(task.getPriority() == "High"){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(1500,VibrationEffect.DEFAULT_AMPLITUDE));
            }else{
                //deprecated in API 26
                v.vibrate(1500);
            }
        }else if(task.getPriority() == "Medium"){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
            }else{
                //deprecated in API 26
                v.vibrate(1000);
            }
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
            }else{
                //deprecated in API 26
                v.vibrate(500);
            }
        }



        //r.play();
        Intent startIntent = new Intent(context, RingtonePlayingService.class);
        //startIntent.putExtra("ringtone-uri", notification);
        context.startService(startIntent);

    }
}
