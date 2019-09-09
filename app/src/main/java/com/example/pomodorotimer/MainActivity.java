package com.example.pomodorotimer;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.example.pomodorotimer.App.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {
    final Handler handler = new Handler();

    private NotificationManagerCompat notificationManagerCompat;
    private TextView displayer;
    private TextView orderDisplayer;
    private TextView sessionCounter;
    private SessionPreferences sessionPreferences;
    private ProgressBar progressBar;
    private Intent settingsIntent;
    private SharedPreferences sharedPreferences;
    private FloatingActionButton floatingButton;
    private Clock clock;
    private ClockManager clockManager;
    private ClockDisplayer clockDisplayer;
    private Status status;

    private int workTime;
    private int breakTime;
    private int longBreakTime;
    private int workSessionsBeforeLongBreak;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        floatingButton = findViewById(R.id.floatingActionButton);
        displayer = findViewById(R.id.timerDisplay);
        progressBar = findViewById(R.id.progressBar);
        orderDisplayer = findViewById(R.id.orderDisplayer);
        sessionCounter = findViewById(R.id.sessionCounter);
        sessionPreferences = new SessionPreferences();
        sharedPreferences = this.getSharedPreferences("com.example.pomodorotimer", Context.MODE_PRIVATE);
        workSessionsBeforeLongBreak = 4;
        status = new Status();
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        getSavedTimePreferences();
        setTimePreferences();
        setStopLongClickListener();
        clockDisplayer = new ClockDisplayer();
        clock = new Clock(sessionPreferences, clockDisplayer);
        clock.setStatus(status);
        setClockDisplayer();
        clockManager = new ClockManager(status, clock);
    }

    public void manageCountDown(View view) {
        clockManager.manageClock();

        if (status.isPlaying()) {
            startPlayToPauseAnimation();
        } else {
            startPauseToPlayAnimation();
        }

    }
    private void startPlayToPauseAnimation() {
        floatingButton.setImageResource(R.drawable.play_to_pause);
        AnimatedVectorDrawable animation = (AnimatedVectorDrawable) floatingButton.getDrawable();
        animation.start();
    }

    private void startPauseToPlayAnimation() {
        floatingButton.setImageResource(R.drawable.pause_to_play);
        AnimatedVectorDrawable animation = (AnimatedVectorDrawable) floatingButton.getDrawable();
        animation.start();
    }

    private void setStopLongClickListener() {
        floatingButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //resetCountDown();
                clockManager.stopClock();
                clockDisplayer.refillProgress();
                floatingButton.setImageResource(R.drawable.ic_stop_black_24dp);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        floatingButton.setImageResource(R.drawable.ic_play_arrow);
                    }
                }, 500);
                return true;
            }
        });
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                openSettingsActivity();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        openSettingsActivity();
        return true;
    }

    public void openSettingsActivity() {
        settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivityForResult(settingsIntent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                getSavedTimePreferences();
                setTimePreferences();
                resetCountDown();
            }
        }
    }

    public void getSavedTimePreferences() {
        workTime = sharedPreferences.getInt("newWorkMinutes", 25);
        breakTime = sharedPreferences.getInt("newBreakMinutes", 5);
        longBreakTime = sharedPreferences.getInt("newLongBreakMinutes", 15);
        workSessionsBeforeLongBreak = sharedPreferences.getInt("newWorkSessionsBeforeLongBreak", 4);
    }

    public void setTimePreferences() {
        sessionPreferences.setWorkMilliseconds(workTime);
        sessionPreferences.setBreakMilliseconds(breakTime);
        sessionPreferences.setLongBreakMilliseconds(longBreakTime);
        sessionPreferences.setWorkSessionsBeforeLongBreak(workSessionsBeforeLongBreak);
    }

    public void setClockDisplayer(){
        clockDisplayer.setCompletedSessionsDisplayer(sessionCounter);
        clockDisplayer.setProgressBar(progressBar);
        clockDisplayer.setTimeDisplayer(displayer);
        clockDisplayer.setButton(floatingButton);
        clockDisplayer.setOrderDisplayer(orderDisplayer);
    }

    public void notifyEndOf(String session) {
        Intent resultIntent = getIntent();
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resultIntent.setAction(Intent.ACTION_MAIN);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                resultIntent, 0);
        Uri notificationSound = Uri.parse("android.resource://com.example.pomodorotimer/" + R.raw.light);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
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

