package com.example.pomodorotimer;

import java.lang.reflect.Array;

public class Status {
    private boolean playing;
    private boolean paused;
    private boolean stopped;
    private String session;

    public Status() {
        stop();
        this.session = "work";
    }

    public String getCurrentSession(){
        return session;
    }

    public void setToWork(){
        this.session = "work";
    }

    public void setToBreak(){
        this.session = "break";
    }

    public void setToLongBreak(){
        this.session = "long break";
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
