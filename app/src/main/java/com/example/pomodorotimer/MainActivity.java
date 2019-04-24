package com.example.pomodorotimer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.example.pomodorotimer.App.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {
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

    int workTime;
    int breakTime;
    int longBreakTime;
    int workSessionsBeforeLongBreak;
    int completedSessions = 0;
    long millisecondsRemaining;
    String session;
    Status status;
    ImageView playButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clock = findViewById(R.id.timerDisplay);
        progressBar = findViewById(R.id.progressBar);
        orderDisplayer = findViewById(R.id.orderDisplayer);
        sessionCounter = findViewById(R.id.sessionCounter);
        playButton = findViewById(R.id.playButton);
        sessionPreferences = new SessionPreferences();
        sharedPreferences = this.getSharedPreferences("com.example.pomodorotimer", Context.MODE_PRIVATE);
        session = "work";
        workSessionsBeforeLongBreak = 4;
        status = new Status();
        notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        getSavedTimePreferences();
        setTimePreferences();
        displayTime(sessionPreferences.workTime);

    }

    public void manageCountDown(View view) {
        if (status.isStopped()) {
            selectAndStartSession();
            playButton.setImageResource(R.drawable.ic_pause_circle_filled);
        } else if (status.isPlaying()) {
            pauseCountDown();
            playButton.setImageResource(R.drawable.ic_play_circle_filled);
        } else if (status.isPaused()) {
            resumeCountDown();
            playButton.setImageResource(R.drawable.ic_pause_circle_filled);
        }
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
                //displayCompletedSessions();
                break;
            case "long break":
                startLongBreak();
                break;
        }
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

    //Need to simplify completedSessions Array
    //something wrong with this, with clockFormatter prob
    private void displayCompletedSessions() {
        clockFormatter = new ClockFormatter(completedSessions);
        sessionCounter.setText(clockFormatter.completedSessions);
    }

    //Isn't this redundant?
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

    public void stopCountDown(View view) {
        resetCountDown();
    }

    public void resetCountDown() {
        resetClock();
        setStatusToStoped();
        completedSessions = 0;
        displayCompletedSessions();
        progressBar.setVisibility(View.INVISIBLE);
        playButton.setImageResource(R.drawable.ic_play_circle_filled);
        orderDisplayer.setText("WORK");
        session = "work";

    }

    private void resetClock() {
        countDownTimer.cancel();
        displayTime(sessionPreferences.workTime);
    }

    //Remember to delete division on the countDown parameter
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
                displayTime(sessionPreferences.breakTime);
                setStatusToStoped();
                break;
            case "long break":
                setStatusToPaused();
                completedSessions = 0;
                resetCountDown();
                break;
        }
        playButton.setImageResource(R.drawable.ic_play_circle_filled);
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
        int max = (int) sessionPreferences.workTime;

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(max);
        progressBar.setProgress(progress);
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
        startActivity(settingsIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSavedTimePreferences();
        setTimePreferences();
    }

    public void setTimePreferences() {
        sessionPreferences.setWorkTime(workTime);
        sessionPreferences.setBreakTime(breakTime);
        sessionPreferences.setLongBreakTime(longBreakTime);
        sessionPreferences.setWorkSessionsBeforeLongBreak(workSessionsBeforeLongBreak);
    }

    public void getSavedTimePreferences() {
        workTime = sharedPreferences.getInt("newWorkMinutes", 25);
        breakTime = sharedPreferences.getInt("newBreakMinutes", 5);
        longBreakTime = sharedPreferences.getInt("newLongBreakMinutes", 15);
        workSessionsBeforeLongBreak = sharedPreferences.getInt("newWorkSessionsBeforeLongBreak", 4);
    }

    public void notifyEndOf(String session) {
        Uri notificationSound = Uri.parse("android.resource://com.example.pomodorotimer/" + R.raw.light);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_alarm_on_black_24dp)
                .setContentTitle("Time's up!")
                .setContentText("Your " + session + " session has ended")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(notificationSound)
                .build();

        notificationManagerCompat.notify(1, notification);
    }
}

