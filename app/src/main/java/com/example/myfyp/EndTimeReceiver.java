package com.example.myfyp;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

//class that allows an application to respond to messages that are broadcasted by the Android application

public class EndTimeReceiver extends BroadcastReceiver {
    private AudioManager audioManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        //creates a notification when silent mode is oFF
        Intent i =new Intent(context,MyEvents.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);


        //NOTIFICATION
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"myFypS")
                .setSmallIcon(R.drawable.logos)
                .setContentTitle("My Fyp")
                .setContentText("Silent Mode Is Off")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        //Accessing the Audio manager of the device in order to change ringer settings
        //setting ringer mode to NORMAL
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123,builder.build());
    }
}
