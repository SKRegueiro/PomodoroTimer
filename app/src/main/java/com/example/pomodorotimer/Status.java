package com.example.pomodorotimer;

public class Status {
    private boolean playing;
    private boolean paused;
    private boolean stopped;
    private String session;

    public Status() {
        stop();
        setToWork();
    }

    public void setToWork(){
        this.session = "WORK";
    }

    public void setToBreak(){
        this.session = "BREAK";
    }

    public void setToLongBreak(){
        this.session = "LONG BREAK";
    }

    public String getCurrentSession(){
        return session;
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
