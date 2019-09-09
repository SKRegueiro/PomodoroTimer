package com.example.pomodorotimer;

import android.os.CountDownTimer;
import android.widget.TextView;

public class Clock {
    private CountDownTimer timer;
    private ClockDisplayer clockDisplayer;
    private SessionPreferences sessionPreferences;
    private ClockFormatter formatter;
    private long millisecondsRemaining;
    private TextView textView;
    private Status status;
    private int completedSessions;
    public String countdown;

    public Clock(SessionPreferences sessionPreferences, ClockDisplayer clockDisplayer) {
        this.sessionPreferences = sessionPreferences;
        this.clockDisplayer = clockDisplayer;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void startWorkSession() {
        displayOrder(status.getCurrentSession());
        setProgressMax(sessionPreferences.workMilliseconds);
        startSession(sessionPreferences.workMilliseconds);
    }

    public void startBreakSession() {
        displayOrder(status.getCurrentSession());
        setProgressMax(sessionPreferences.breakMilliseconds);
        startSession(sessionPreferences.breakMilliseconds);
    }

    public void startLongBreakSession() {
        displayOrder(status.getCurrentSession());
        setProgressMax(sessionPreferences.longBreakMilliseconds);
        startSession(sessionPreferences.longBreakMilliseconds);
    }

    private void startSession(long sessionTime) {
        this.timer = new CountDownTimer(sessionTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Hay que encontrar una forma mejor de hacer esto, mucha dependencia
                //MainActivity.displayer.setText(countdown);
                //textView.setText(countdown);
                displayTime(millisUntilFinished);
                displayProgress(millisUntilFinished);
                displayCompletedSessions();
                millisecondsRemaining = millisUntilFinished;
            }
            @Override
            public void onFinish() {
                finishSession();
            }
        }.start();
    }

    public void finishSession() {
        status.stop();
        refillProgress();
        switch (status.getCurrentSession()) {
            case "WORK":
                finishWorkSession();
                break;
            case "BREAK":
                status.setToWork();
                break;
            case "LONG BREAK":
                finishLongBreakSession();
                break;
        }
    }

    public void stopCountdown() {
        timer.cancel();
    }

    public void resumeCountdown() {
        if(status.isPaused()){
        status.play();
        startSession(millisecondsRemaining);
    }
    }

    public void refillProgress() {
        clockDisplayer.refillProgress();
    }

    private void finishWorkSession() {
        completedSessions++;
        if (completedSessions == sessionPreferences.workSessionsBeforeLongBreak) {
            status.setToLongBreak();
        } else {
            status.setToBreak();
        }
    }

    private void finishLongBreakSession() {
        completedSessions = 0;
        status.setToWork();
    }

    public void displayCompletedSessions() {
        formatter = new ClockFormatter(completedSessions);
        clockDisplayer.setCompletedSessions(formatter.completedSessions);
    }

    public void displayOrder(String order) {
        clockDisplayer.setOrder(order);
    }

    public void setProgressMax(long milliseconds) {
        clockDisplayer.setProgressMax(milliseconds);
    }

    public void displayProgress(long millisecondsLeft) {
        clockDisplayer.setProgress(millisecondsLeft);
    }

    public void displayTime(long millisecondsLeft) {
        formatter = new ClockFormatter(millisecondsLeft);
        countdown = formatter.time;
        clockDisplayer.setTime(countdown);
    }
}