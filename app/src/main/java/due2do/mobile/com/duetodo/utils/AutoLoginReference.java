package due2do.mobile.com.duetodo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Ankit Varshney on 28/03/2018.
 */

public class AutoLoginReference {

    static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    //Store username in shared preferences
    public static void setUserName(Context ctx, String username){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString("username",username);
        editor.commit();
    }

    //Get username from shared preferences
    public static String getUsername(Context ctx){
        return getSharedPreferences(ctx).getString("username","");
    }

    //Clear username from shared preferences
    public static void clearUsername(Context ctx){
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
    }
}
