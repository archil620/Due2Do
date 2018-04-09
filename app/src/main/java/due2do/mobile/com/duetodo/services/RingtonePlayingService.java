package due2do.mobile.com.duetodo.services;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

/**
 * Created by Bhargav Dalal on 18/03/2018.
 * References:
 * [1] “How do I stop the currently playing ringtone?,” android - How do I stop the currently playing ringtone? - Stack Overflow. [Online].
 * Available: https://stackoverflow.com/questions/14089380/how-do-i-stop-the-currently-playing-ringtone.
 * [Accessed: 02-Apr-2018].
 */


public class RingtonePlayingService extends Service
{
    private Ringtone ringtone;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    // Play ringtone
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        //Uri ringtoneUri = Uri.parse(intent.getExtras().getString("ringtone-uri"));
        Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        this.ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        ringtone.play();

        return START_NOT_STICKY;
    }

    // Stop ringtone
    @Override
    public void onDestroy()
    {
        ringtone.stop();
    }
}
