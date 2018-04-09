package due2do.mobile.com.duetodo.services;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Bhargav Dalal on 18/03/2018.
 */

public class AppBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "due2do.mobile.com.duetodo";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent stopIntent = new Intent(context, RingtonePlayingService.class);
        context.stopService(stopIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(101);

    }
}
