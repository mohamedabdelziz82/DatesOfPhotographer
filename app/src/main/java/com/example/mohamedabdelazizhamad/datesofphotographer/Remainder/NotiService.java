package com.example.mohamedabdelazizhamad.datesofphotographer.Remainder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.mohamedabdelazizhamad.datesofphotographer.Activities.MainActivity;
import com.example.mohamedabdelazizhamad.datesofphotographer.R;

public class NotiService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int NOTIFICATION_ID = 42;
        String description = getString(R.string.reminder_title);
        String name=getString(R.string.reminder_title);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = null;
        String id = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(name, name, importance);
            manager.createNotificationChannel(channel);
            id = channel.getId();

        }

        //Display a notification to view the task details
        Intent action = new Intent(this, MainActivity.class);
        PendingIntent operation = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(action)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);



        Notification note = new NotificationCompat.Builder(this,id)
                .setContentTitle(getString(R.string.reminder_title))
                .setContentText(description)
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(operation)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
                .build();

        manager.notify(NOTIFICATION_ID, note);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
