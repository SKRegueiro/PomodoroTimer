package com.example.pomodorotimer;

import android.os.CountDownTimer;

public class Clock {
    private CountDownTimer timer;
    private SessionPreferences sessionPreferences;
    private ClockFormatter formatter;
    public String countdown;
    private long millisecondsRemaining;

    public Clock(SessionPreferences sessionPreferences){
    this.sessionPreferences = sessionPreferences;
    }

    private void startSession(long sessionTime){
       this.timer = new CountDownTimer(sessionTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                formatter = new ClockFormatter(millisUntilFinished);
                countdown = formatter.time;
                millisecondsRemaining = millisUntilFinished;
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }

    public void startWorkSession(){
        startSession(sessionPreferences.workTime);
    }

    public void startBreakSession(){
        startSession(sessionPreferences.breakTime);
    }

    public void startLongBreakSession(){
        startSession(sessionPreferences.longBreakTime);
    }

    public void stopCountdown(){
        timer.cancel();
    }

    public void resumeCountdown(){
        startSession(millisecondsRemaining);
    }
}
