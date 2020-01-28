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
                finishBreakSession();
                break;
            case "LONG BREAK":
                finishLongBreakSession();
                break;
        }
    }

    public void resetCountdown() {

        stopCountdown();
        resetSessionValues();
        updateSessionInfomation(sessionPreferences.workMilliseconds);
    }

    public void stopCountdown() {

        if (timer != null){timer.cancel();}
    }

    public void resetSessionValues() {
        completedSessions = 0;
        status.setToWork();
    }

    public void resumeCountdown() {
        status.play();
        startSession(millisecondsRemaining);
    }


    public void refillProgress() {
        clockDisplayer.refillProgress();
    }

    private void finishWorkSession() {
        clockDisplayer.notifyEndOf(status.getCurrentSession());
        completedSessions++;
        if (completedSessions == sessionPreferences.workSessionsBeforeLongBreak) {
            status.setToLongBreak();
            updateSessionInfomation(sessionPreferences.longBreakMilliseconds);
        } else {
            status.setToBreak();
            updateSessionInfomation(sessionPreferences.breakMilliseconds);
        }
    }

    private void finishBreakSession(){
        clockDisplayer.notifyEndOf(status.getCurrentSession());
        updateSessionInfomation(sessionPreferences.workMilliseconds);
        status.setToWork();
    }
    private void finishLongBreakSession() {
        clockDisplayer.notifyEndOf(status.getCurrentSession());
        resetSessionValues();
        updateSessionInfomation(sessionPreferences.workMilliseconds);
    }

    private void updateSessionInfomation(long nextSessionMillisecons){
        displayCompletedSessions();
        displayOrder(status.getCurrentSession());
        displayTime(nextSessionMillisecons);
        clockDisplayer.startPauseToPlayAnimation();
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