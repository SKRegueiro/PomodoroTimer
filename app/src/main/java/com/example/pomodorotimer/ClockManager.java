package com.example.pomodorotimer;

public class ClockManager {
    private Status status;
    private Clock clock;

    public ClockManager(Status status, Clock clock) {
        this.status = status;
        this.clock = clock;
    }

    public void manageClock() {
        if (status.isStopped()) {
            startClockSession();
        } else if (status.isPlaying()) {
            pauseClock();
        } else if (status.isPaused()) {
            resumeClock();
        }
    }

    private void startClockSession() {
        status.play();
        switch (status.getCurrentSession()) {
            case "WORK":
                startWork();
                break;
            case "BREAK":
                startBreak();
                break;
            case "LONG BREAK":
                startLongBreak();
                break;
        }
    }

    private void startWork(){
        status.setToWork();
        clock.startWorkSession();
    }
    private void startBreak(){
        status.setToBreak();
        clock.startBreakSession();
    }
    private void startLongBreak(){
        status.setToLongBreak();
        clock.startLongBreakSession();
    }

    private void resumeClock() {
        status.play();
        clock.resumeCountdown();
    }

    private void pauseClock(){
        status.pause();
        clock.stopCountdown();
    }

    public void stopClock() {
        status.stop();
        clock.stopCountdown();
    }

}
