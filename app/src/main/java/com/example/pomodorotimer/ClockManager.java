package com.example.pomodorotimer;

public class ClockManager {
    private Status status;

    public ClockManager(Status status) {
        this.status = status;
    }

    public void manageClock() {
        if (status.isStopped()) {
            startCountdown();
        } else if (status.isPlaying()) {
            pauseCountdown();
        } else if (status.isPaused()) {
            resumeCountdown();
        }
    }

    private void startCountdown() {
        switch (status.getCurrentSession()) {
            case "work":

                break;
            case "break":

                break;
            case "long break":

                break;
        }
    }

    private void pauseCountdown() {

    }

    private void resumeCountdown(){

    }

    private void stopCountdown(){
        
    }


}
