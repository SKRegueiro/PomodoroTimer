package com.example.pomodorotimer;

import android.os.CountDownTimer;

public class Clock {
    private CountDownTimer timer;

    public Clock(CountDownTimer timer){
        this.timer = timer;
    }

    public long startSession(long sessionTime){
        timer = new CountDownTimer() {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

            }
        }
    }
}
