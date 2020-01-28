package com.example.pomodorotimer;

import android.animation.ValueAnimator;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.example.pomodorotimer.App.CHANNEL_1_ID;

public class ClockDisplayer {
    private TextView timeDisplayer;
    private TextView orderDisplayer;
    private TextView completedSessionsDisplayer;
    private ProgressBar progressBar;
    private FloatingActionButton button;
    private Intent resultIntent;
    private Context applicationContext;
    private NotificationManagerCompat notificationManagerCompat;

    public ClockDisplayer(Intent intent, Context applicationContext) {
        this.resultIntent = intent;
        this.applicationContext = applicationContext;
        this.notificationManagerCompat = NotificationManagerCompat.from(this.applicationContext);
    }

    public void setTimeDisplayer(TextView timeDisplayer) {
        this.timeDisplayer = timeDisplayer;
    }

    public void setOrderDisplayer(TextView orderDisplayer) {
        this.orderDisplayer = orderDisplayer;
    }

    public void setCompletedSessionsDisplayer(TextView completedSessionsDisplayer) {
        this.completedSessionsDisplayer = completedSessionsDisplayer;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setButton(FloatingActionButton FAB) {
        this.button = FAB;
    }

    public void setTime(String time) {
        timeDisplayer.setText(time);
    }

    public void setOrder(String order) {
        orderDisplayer.setText(order);
    }

    public void setCompletedSessions(String completedSessions) {
        completedSessionsDisplayer.setText(completedSessions);
    }

    public void setProgressMax(long milliseconds) {
        int max = (int) milliseconds;
        this.progressBar.setMax(max);
    }

    public void setProgress(long millisecondsLeft) {
        int progress = (int) millisecondsLeft;
        progressBar.setProgress(progress);
    }

    public void startPlayToPauseAnimation() {
        this.button.setImageResource(R.drawable.play_to_pause);
        AnimatedVectorDrawable animation = (AnimatedVectorDrawable) this.button.getDrawable();
        animation.start();
    }

    public void startPauseToPlayAnimation() {
        this.button.setImageResource(R.drawable.pause_to_play);
        AnimatedVectorDrawable animation = (AnimatedVectorDrawable) this.button.getDrawable();
        animation.start();
    }


    public void refillProgress() {
        int startingValue = progressBar.getProgress();
        if(startingValue != progressBar.getMax()) {
            ValueAnimator animator = ValueAnimator.ofInt(startingValue, progressBar.getMax());
            animator.setDuration(1000);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progressBar.setProgress((Integer) animation.getAnimatedValue());
                }
            });
            animator.start();
        }
    }

    public void notifyEndOf(String session) {
        this.resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        this.resultIntent.setAction(Intent.ACTION_MAIN);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.applicationContext, 0,
                resultIntent, 0);
        Uri notificationSound = Uri.parse("android.resource://com.example.pomodorotimer/" + R.raw.light);
        Notification notification = new NotificationCompat.Builder(this.applicationContext, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_alarm_on_black_24dp)
                .setContentTitle("Time's up!")
                .setContentText("Your " + session + " session has ended")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent)
                .build();
        notification.sound = Uri.parse("android.resource://com.example.pomodorotimer/" + R.raw.light);
        notificationManagerCompat.notify(1, notification);
    }
}

