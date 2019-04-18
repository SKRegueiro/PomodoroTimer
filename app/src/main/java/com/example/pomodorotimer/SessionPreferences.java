package com.example.pomodorotimer;

public class SessionPreferences {
    long workTime;
    long breakTime;
    long longBreakTime;
    int workSessionsBeforeLongBreak;

    public SessionPreferences() {
    }

    public void setWorkTime(int minutes) {
        long minutesSelected = (long) minutes;
        this.workTime = minutesSelected * 60000;
    }

    public void setBreakTime(int minutes) {
        long minutesSelected = (long) minutes;
        this.breakTime = minutesSelected * 60000;
    }

    public void setLongBreakTime(int minutes) {
        long minutesSelected = (long) minutes;
        this.longBreakTime = minutesSelected * 60000;
    }

    public void setWorkSessionsBeforeLongBreak(int sessions) {
        this.workSessionsBeforeLongBreak = sessions;
    }
}
