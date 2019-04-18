package com.example.pomodorotimer;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ClockFormatter {

    private int secondsLeft;
    private int secondsInt;
    private int minutesInt;
    String minutesString;
    String secondsString;
    String time;
    String completedSessions;

    public ClockFormatter(long millisecondsLeft) {
        this.secondsLeft = (int) ((millisecondsLeft) / 1000);
        this.minutesInt = secondsLeft / 60;
        this.secondsInt = secondsLeft - (minutesInt * 60);

        minutesString = String.valueOf(minutesInt);
        secondsString = String.valueOf(secondsInt);

        addZeroToSecondsBelowNine();
        addZeroToMinutesBelowNine();

        this.time = minutesString + ":" + secondsString;
    }

    public ClockFormatter(int completedSessions){
        formatSessionCounter(completedSessions);
    }

    private void formatSessionCounter(int sessionFinished){
        this.completedSessions = "";
        for (int i = 0; i < sessionFinished; i++) {
            this.completedSessions = "I ";
        }
    }

    private void addZeroToSecondsBelowNine() {
        if (secondsInt <= 9) {
            this.secondsString = "0" + secondsString;
        }
    }

    private void addZeroToMinutesBelowNine() {
        if (minutesInt <= 9) {
            this.minutesString = "0" + minutesString;
        }
    }
}
