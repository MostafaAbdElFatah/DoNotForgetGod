package com.example.user.donotforgetgod.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;


public class AlarmReceiver extends BroadcastReceiver
{


    @Override
    public void onReceive(Context context, Intent intent)
    {
        //get and send location information
        SharedPreferences sharedPreferences = context.getSharedPreferences("Setting"
                , Context.MODE_PRIVATE);
        Boolean checked = sharedPreferences.getBoolean("checked", false);
        Toast.makeText(context,"checked:"+checked,Toast.LENGTH_SHORT).show();

        if (Audio.isRunningAudio() || !checked)
            return;
        else {
            Audio.playAudio(context);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Audio.releaseAudio();
    }

}