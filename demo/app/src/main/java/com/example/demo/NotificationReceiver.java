package com.example.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.example.demo.ApplicationClass.ACTION_NEXT;
import static com.example.demo.ApplicationClass.ACTION_PLAY;
import static com.example.demo.ApplicationClass.ACTION_PREVIOUS;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent serviceIntend = new Intent(context, MusicService.class);
        if (actionName != null){
            switch (actionName)
            {
                case ACTION_PLAY:
                    serviceIntend.putExtra("ActionName", "playPause");
                    context.startService(serviceIntend);
                    break;

                case ACTION_PREVIOUS:
                    serviceIntend.putExtra("ActionName", "previous");
                    context.startService(serviceIntend);
                    break;

                case ACTION_NEXT:
                    serviceIntend.putExtra("ActionName", "next");
                    context.startService(serviceIntend);
                    break;
            }
        }
    }
}
