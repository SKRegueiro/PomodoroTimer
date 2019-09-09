package com.example.pomodorotimer;

import android.animation.ValueAnimator;
import android.support.design.widget.FloatingActionButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ClockDisplayer {
    private TextView timeDisplayer;
    private TextView orderDisplayer;
    private TextView completedSessionsDisplayer;
    private ProgressBar progressBar;
    private FloatingActionButton button;

    public ClockDisplayer() {
    }

    public void setTimeDisplayer(TextView timeDisplayer) {
        this.timeDisplayer = timeDisplayer;
    }

    public void setOrderDisplayer(TextView orderDisplayer) {
        this.orderDisplayer = orderDisplayer;
    }

    public void setCompletedSessionsDisplayer(TextView completedSessionsDisplayer) {
        this.completedSessionsDisplayer = completedSessionsDisplayer;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setButton(FloatingActionButton FAB) {
        this.button = FAB;
    }

    public void setTime(String time) {
        timeDisplayer.setText(time);
    }

    public void setOrder(String order) {
        orderDisplayer.setText(order);
    }

    public void setCompletedSessions(String completedSessions) {
        completedSessionsDisplayer.setText(completedSessions);
    }

    public void setProgressMax(long milliseconds) {
        int max = (int) milliseconds;
        this.progressBar.setMax(max);
    }

    public void setProgress(long millisecondsLeft) {
        int progress = (int) millisecondsLeft;
        progressBar.setProgress(progress);
    }

    public void refillProgress() {
        int startingValue = progressBar.getProgress();
        ValueAnimator animator = ValueAnimator.ofInt(startingValue, progressBar.getMax());
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progressBar.setProgress((Integer) animation.getAnimatedValue());
            }
        });
        animator.start();
    }
}

