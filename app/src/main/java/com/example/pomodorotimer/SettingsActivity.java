package com.example.pomodorotimer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    SeekBar workManager;
    SeekBar breakManager;
    SeekBar longBreakManager;
    SeekBar workSessionsBeforeLongBreakManager;
    TextView workTimeDisplayer;
    TextView breakTimeDisplayer;
    TextView longBreakTimeDisplayer;
    TextView workSessionBeforeLongBreakDisplayer;
    SharedPreferences sharedPreferences;
    SeekBar.OnSeekBarChangeListener listener;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;

    int newWorkMinutes;
    int newBreakMinutes;
    int newLongBreakMinutes;
    int newWorkSessionsBeforeLongBreak;
    int savedWorkTime;
    int savedBreakTime;
    int savedLongBreakTime;
    int savedWorkSessionsBeforeLongBreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        workManager = findViewById(R.id.workManager);
        breakManager = findViewById(R.id.breakManager);
        longBreakManager = findViewById(R.id.longBreakManager);
        workSessionsBeforeLongBreakManager = findViewById(R.id.workSessionsBeforeLongBreakManager);
        workTimeDisplayer = findViewById(R.id.workMInutesDisplayer);
        breakTimeDisplayer = findViewById(R.id.breakTimeDisplayer);
        longBreakTimeDisplayer = findViewById(R.id.longBreakTimeDisplayer);
        workSessionBeforeLongBreakDisplayer = findViewById(R.id.sessionsBeforeLongBreakDisplayer);

        sharedPreferences = this.getSharedPreferences("com.example.pomodorotimer", Context.MODE_PRIVATE);

        setManagersAtributes();
        displayCurrentPreferences();
        createAlertDialog();
    }

    public void displayCurrentPreferences() {
        getSavedTimePreferences();
        showSavedValues();
    }

    public void getSavedTimePreferences() {
        savedWorkTime = sharedPreferences.getInt("newWorkMinutes", 25);
        savedBreakTime = sharedPreferences.getInt("newBreakMinutes", 5);
        savedLongBreakTime = sharedPreferences.getInt("newLongBreakMinutes", 15);
        savedWorkSessionsBeforeLongBreak = sharedPreferences.getInt("newWorkSessionsBeforeLongBreak", 4);
    }

    public void showSavedValues() {
        setDisplayersToSavedTime();
        setSeekBarsProgressToSavedTime();
    }

    public void setDisplayersToSavedTime() {
        workTimeDisplayer.setText(String.valueOf(savedWorkTime));
        breakTimeDisplayer.setText(String.valueOf(savedBreakTime));
        longBreakTimeDisplayer.setText(String.valueOf(savedLongBreakTime));
        workSessionBeforeLongBreakDisplayer.setText(String.valueOf(savedWorkSessionsBeforeLongBreak));
    }

    public void setSeekBarsProgressToSavedTime() {
        workManager.setProgress(savedWorkTime);
        breakManager.setProgress(savedBreakTime);
        longBreakManager.setProgress(savedLongBreakTime);
        workSessionsBeforeLongBreakManager.setProgress(savedWorkSessionsBeforeLongBreak);
    }

    private void showProgressOnDisplayers(SeekBar seekBar, int progress) {
        switch (seekBar.getTag().toString()) {
            case "work":
                setDisplayersText(workTimeDisplayer, progress);
                break;
            case "break":
                setDisplayersText(breakTimeDisplayer, progress);
                break;
            case "longBreak":
                setDisplayersText(longBreakTimeDisplayer, progress);
                break;
            case "workSessionsBeforeLongBreak":
                setDisplayersText(workSessionBeforeLongBreakDisplayer, progress);
                break;
        }
    }

    private void setDisplayersText(TextView displayer, int progress) {
        if (displayer.getId() == R.id.sessionsBeforeLongBreakDisplayer) {
            workSessionBeforeLongBreakDisplayer.setText(String.valueOf(progress));
        } else {
            displayer.setText(String.valueOf(progress));
        }
    }

    public void showAlertDialog(View view) {
        alertDialog.show();
    }

    public void saveNewPreferencesAndFinish() {
        getSelectedValues();
        saveSelectedValuesOnSharedPreferences();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("change preferences", true);
        setResult(MainActivity.RESULT_OK, returnIntent);
        finish();
    }

    public void getSelectedValues() {
        String workMinutesString = workTimeDisplayer.getText().toString();
        String breakMinutesString = breakTimeDisplayer.getText().toString();
        String longBreakMinutesString = longBreakTimeDisplayer.getText().toString();
        String workSessionsBeforeLongBreakString = workSessionBeforeLongBreakDisplayer.getText().toString();

        newWorkMinutes = Integer.parseInt(workMinutesString);
        newBreakMinutes = Integer.parseInt(breakMinutesString);
        newLongBreakMinutes = Integer.parseInt(longBreakMinutesString);
        newWorkSessionsBeforeLongBreak = Integer.parseInt(workSessionsBeforeLongBreakString);
    }

    private void saveSelectedValuesOnSharedPreferences() {
        sharedPreferences.edit().putInt("newWorkMinutes", newWorkMinutes).apply();
        sharedPreferences.edit().putInt("newBreakMinutes", newBreakMinutes).apply();
        sharedPreferences.edit().putInt("newLongBreakMinutes", newLongBreakMinutes).apply();
        sharedPreferences.edit().putInt("newWorkSessionsBeforeLongBreak", newWorkSessionsBeforeLongBreak).apply();
    }

    private void createAlertDialog() {
        dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
        dialogBuilder.setTitle("Are you sure?");
        dialogBuilder.setMessage("This will restart your session");
        dialogBuilder.setPositiveButton(Html.fromHtml("<font color='#494949'>YES</font>"),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        saveNewPreferencesAndFinish();
                    }
                });


        alertDialog = dialogBuilder.create();
    }

    private void setManagersAtributes() {
        setManagersMinToOne();
        setManagersMax();
        setManagersTags();
        setManagersListener();
    }

    private void setManagersMinToOne() {
        workManager.setMin(1);
        breakManager.setMin(1);
        longBreakManager.setMin(1);
        workSessionsBeforeLongBreakManager.setMin(1);
    }

    private void setManagersMax() {
        workManager.setMax(60);
        breakManager.setMax(30);
        longBreakManager.setMax(60);
        workSessionsBeforeLongBreakManager.setMax(10);
    }

    private void setManagersTags() {
        workManager.setTag("work");
        breakManager.setTag("break");
        longBreakManager.setTag("longBreak");
        workSessionsBeforeLongBreakManager.setTag("workSessionsBeforeLongBreak");
    }

    private void setManagersListener() {
        listener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showProgressOnDisplayers(seekBar, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        workManager.setOnSeekBarChangeListener(listener);
        breakManager.setOnSeekBarChangeListener(listener);
        longBreakManager.setOnSeekBarChangeListener(listener);
        workSessionsBeforeLongBreakManager.setOnSeekBarChangeListener(listener);
    }
}
