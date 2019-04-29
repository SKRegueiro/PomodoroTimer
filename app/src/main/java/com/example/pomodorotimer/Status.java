package com.example.pomodorotimer;

public class Status {
    private boolean playing;
    private boolean paused;
    private boolean stopped;
    private boolean work;
    private boolean shortBreak;
    private boolean longBreak;



    public Status() {
        stop();
    }

    public void play() {
        playing = true;
        paused = false;
        stopped = false;
    }

    public void pause() {
        playing = false;
        paused = true;
        stopped = false;
    }

    public void stop() {
        playing = false;
        paused = false;
        stopped = true;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isStopped() {
        return stopped;
    }

}
