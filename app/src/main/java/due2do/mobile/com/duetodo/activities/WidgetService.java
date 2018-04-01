package due2do.mobile.com.duetodo.activities;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by dalalbhargav07 on 31-03-2018.
 */

public class WidgetService extends RemoteViewsService
{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        return (new WidgetRemoteViewsFactory(this.getApplicationContext(), intent));
    }

}