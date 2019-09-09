package com.example.pomodorotimer;

public class SessionPreferences {
    long workMilliseconds;
    long breakMilliseconds;
    long longBreakMilliseconds;
    int workSessionsBeforeLongBreak;

    public SessionPreferences() {
    }

    public void setWorkMilliseconds(int minutes) {
        long minutesSelected = (long) minutes;
        this.workMilliseconds = minutesSelected * 60000;
    }

    public void setBreakMilliseconds(int minutes) {
        long minutesSelected = (long) minutes;
        this.breakMilliseconds = minutesSelected * 60000;
    }

    public void setLongBreakMilliseconds(int minutes) {
        long minutesSelected = (long) minutes;
        this.longBreakMilliseconds = minutesSelected * 60000;
    }

    public void setWorkSessionsBeforeLongBreak(int sessions) {
        this.workSessionsBeforeLongBreak = sessions;
    }
}
