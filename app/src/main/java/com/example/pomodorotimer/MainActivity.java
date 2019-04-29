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
import android.os.CountDownTimer;
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
    TextView clock;
    TextView orderDisplayer;
    TextView sessionCounter;
    CountDownTimer countDownTimer;
    ClockFormatter clockFormatter;
    SessionPreferences sessionPreferences;
    ProgressBar progressBar;
    Intent settingsIntent;
    SharedPreferences sharedPreferences;
    FloatingActionButton floatingButton;

    int workTime;
    int breakTime;
    int longBreakTime;
    int workSessionsBeforeLongBreak;
    int completedSessions = 0;
    long millisecondsRemaining;
    String session;
    Status status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingButton = findViewById(R.id.floatingActionButton);
        clock = findViewById(R.id.timerDisplay);
        progressBar = findViewById(R.id.progressBar);
        orderDisplayer = findViewById(R.id.orderDisplayer);
        sessionCounter = findViewById(R.id.sessionCounter);
        sessionPreferences = new SessionPreferences();
        sharedPreferences = this.getSharedPreferences("com.example.pomodorotimer", Context.MODE_PRIVATE);
        session = "work";
        workSessionsBeforeLongBreak = 4;
        status = new Status();
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        getSavedTimePreferences();
        setTimePreferences();
        displayTime(sessionPreferences.workTime);
        setStopLongClickListener();
    }

    public void manageCountDown(View view) {
        if (status.isStopped()) {
            selectAndStartSession();
            startPlayToPauseAnimation();
        } else if (status.isPlaying()) {
            pauseCountDown();
            startPauseToPlayAnimation();
        } else if (status.isPaused()) {
            resumeCountDown();
            startPlayToPauseAnimation();
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

    private void selectAndStartSession() {
        setStatusToPlaying();
        startTaggedSession();
    }

    private void startTaggedSession() {
        switch (session) {
            case "work":
                startWork();
                break;
            case "break":
                startBreak();
                break;
            case "long break":
                startLongBreak();
                break;
        }
    }

    private void setStopLongClickListener() {
        floatingButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                resetCountDown();
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

    private void startWork() {
        startCountDownTimer(sessionPreferences.workTime);
        orderDisplayer.setText("WORK");
    }

    private void startBreak() {
        startCountDownTimer(sessionPreferences.breakTime);
    }

    private void startLongBreak() {
        startCountDownTimer(sessionPreferences.longBreakTime);
    }

    private void displayCompletedSessions() {
        clockFormatter = new ClockFormatter(completedSessions);
        sessionCounter.setText(clockFormatter.completedSessions);
    }

    private void setStatusToPaused() {
        status.pause();
    }

    private void setStatusToPlaying() {
        status.play();
    }

    private void setStatusToStoped() {
        status.stop();
    }

    private void pauseCountDown() {
        setStatusToPaused();
        countDownTimer.cancel();
    }

    private void resumeCountDown() {
        setStatusToPlaying();
        startCountDownTimer(millisecondsRemaining);
    }

    public void resetCountDown() {
        if (countDownTimer != null) {
            resetClock();
        }
        setStatusToStoped();
        completedSessions = 0;
        displayCompletedSessions();
        orderDisplayer.setText("WORK");
        session = "work";
    }

    private void resetClock() {
        countDownTimer.cancel();
        displayTime(sessionPreferences.workTime);
    }

    //Button changes to start one second late
    private void startCountDownTimer(final long timeSelected) {
        countDownTimer = new CountDownTimer(timeSelected, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                displayTime(millisUntilFinished);
                setUpProgressBar(millisUntilFinished - 1000);
                millisecondsRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                finishTaggedSession();
            }
        }.start();
    }

    private void finishTaggedSession() {
        notifyEndOf(session);
        switch (session) {
            case "work":
                displayTime(sessionPreferences.breakTime);
                completedSessions++;
                displayCompletedSessions();
                setStatusToStoped();
                if (completedSessions == sessionPreferences.workSessionsBeforeLongBreak) {
                    session = "long break";
                    orderDisplayer.setText("LONGBREAK");
                } else {
                    session = "break";
                    orderDisplayer.setText("BREAK");
                }
                break;

            case "break":
                orderDisplayer.setText("WORK");
                session = "work";
                displayTime(sessionPreferences.workTime);
                setStatusToStoped();
                break;

            case "long break":
                setStatusToPaused();
                completedSessions = 0;
                resetCountDown();
                break;
        }
        startPauseToPlayAnimation();
    }

    private void displayTime(long milliseconds) {
        String timeToDisplay = getFormattedTime(milliseconds);
        clock.setText(timeToDisplay);
    }

    private String getFormattedTime(long milliseconds) {
        clockFormatter = new ClockFormatter(milliseconds);
        return clockFormatter.time;
    }

    private void setUpProgressBar(long milliseconds) {
        int progress = (int) milliseconds;
        int max = (int) sessionPreferences.;

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(max);
        progressBar.setProgress(progress);
    }


    /*private void refillProgressBar() {

        progressBar.setMax(100);
        for (int i = 0; i < progressBar.getMax() ; i++) {
            progressBar.setProgress(i);
            try {
                Thread.sleep(100);
            }catch (Exception e){

            }

        }
    }*/

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
        startActivityForResult(settingsIntent, 1);
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
        sessionPreferences.setWorkTime(workTime);
        sessionPreferences.setBreakTime(breakTime);
        sessionPreferences.setLongBreakTime(longBreakTime);
        sessionPreferences.setWorkSessionsBeforeLongBreak(workSessionsBeforeLongBreak);
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
                //Starts new activity instead of
                .setContentIntent(pendingIntent)
                .build();
        notification.sound = Uri.parse("android.resource://com.example.pomodorotimer/" + R.raw.light);

        notificationManagerCompat.notify(1, notification);
    }
}

