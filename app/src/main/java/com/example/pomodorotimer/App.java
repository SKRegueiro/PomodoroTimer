package com.example.pomodorotimer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_1_ID = "channel1";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    public void createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID, "Time's up notification",
                    NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Notifies when the current session ended");
            channel1.setLightColor(Color.WHITE);
            Uri notificationSound = Uri.parse("android.resource://com.example.notificationemo/" + R.raw.light);
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            channel1.setSound(notificationSound, att);


        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel1);

        }
    }
}
