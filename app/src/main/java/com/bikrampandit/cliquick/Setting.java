package com.bikrampandit.cliquick;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.rey.material.widget.Switch;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Setting extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        preferences = getSharedPreferences(Constant.PREFERENCE_NAME,MODE_PRIVATE);

        if (android.os.Build.VERSION.SDK_INT >= 11 && getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(true);
        } else if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setupUI();
    }


    private void setupUI(){
        ((Switch)findViewById(R.id.schedule)).setChecked(preferences.getBoolean(Constant.SCHEDULE,false));
        String startTime = preferences.getString(Constant.START_TIME,Constant.DEFAULT_START_TIME);
        String endTime = preferences.getString(Constant.END_TIME,Constant.DEFAULT_END_TIME);
        try {
            ((TextView) findViewById(R.id.start_time)).setText(
                    new SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(new SimpleDateFormat("HH:mm",Locale.ENGLISH).parse(startTime))
            );
        }catch (Exception e){
            ((TextView) findViewById(R.id.start_time)).setText(Constant.DEFAULT_START_TIME);
            e.printStackTrace();
        }
        try{
            ((TextView) findViewById(R.id.end_time)).setText(
                    new SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(new SimpleDateFormat("HH:mm",Locale.ENGLISH).parse(endTime))
            );
        }catch (Exception e){
            ((TextView) findViewById(R.id.start_time)).setText(Constant.DEFAULT_END_TIME);
            e.printStackTrace();
        }
        ((CheckBox)findViewById(R.id.w1)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE,Constant.DEFAULT_WEEK).substring(0,1)) != 0);
        ((CheckBox)findViewById(R.id.w2)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE,Constant.DEFAULT_WEEK).substring(1,1)) != 0);
        ((CheckBox)findViewById(R.id.w3)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE,Constant.DEFAULT_WEEK).substring(2,1)) != 0);
        ((CheckBox)findViewById(R.id.w4)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE,Constant.DEFAULT_WEEK).substring(3,1)) != 0);
        ((CheckBox)findViewById(R.id.w5)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE,Constant.DEFAULT_WEEK).substring(4,1)) != 0);
        ((CheckBox)findViewById(R.id.w6)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE,Constant.DEFAULT_WEEK).substring(5,1)) != 0);
        ((CheckBox)findViewById(R.id.w7)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE,Constant.DEFAULT_WEEK).substring(6,1)) != 0);

        ((Switch)findViewById(R.id.vibrate)).setChecked(preferences.getBoolean(Constant.VIBRATE,true));
        ((EditText)findViewById(R.id.panic_text)).setText(preferences.getString(Constant.PANIC_TEXT,Constant.DEFAULT_PANIC_TEXT));
        ((CheckBox)findViewById(R.id.send_location)).setChecked(preferences.getBoolean(Constant.SEND_LOCATION,true));
        ((CheckBox)findViewById(R.id.send_location_frequency)).setChecked(preferences.getBoolean(Constant.SEND_LOCATION_FREQ,true) && preferences.getBoolean(Constant.SEND_LOCATION,true));
        ((Spinner)findViewById(R.id.freqSpinner)).setSelection(preferences.getInt(Constant.SEND_LOCATION_FREQ_VALUE,1));

        ((CheckBox)findViewById(R.id.w1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

            }
        });



        ((Switch)findViewById(R.id.schedule)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.schedule_text).setEnabled(checked);
                findViewById(R.id.schedule_text1).setEnabled(checked);
                findViewById(R.id.start_time_layout).setEnabled(checked);
                findViewById(R.id.start_time).setEnabled(checked);
                findViewById(R.id.w1).setEnabled(checked);
                findViewById(R.id.w2).setEnabled(checked);
                findViewById(R.id.w3).setEnabled(checked);
                findViewById(R.id.w4).setEnabled(checked);
                findViewById(R.id.w5).setEnabled(checked);
                findViewById(R.id.w6).setEnabled(checked);
                findViewById(R.id.w7).setEnabled(checked);
            }
        });

        ((Switch)findViewById(R.id.vibrate)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.vibrate_text).setEnabled(checked);
                findViewById(R.id.vibrate_text1).setEnabled(checked);
            }
        });

        ((CheckBox) findViewById(R.id.send_location)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                findViewById(R.id.recommendation).setVisibility(checked ? View.GONE : View.VISIBLE);
                preferences.edit().putBoolean(Constant.SEND_LOCATION,checked).apply();
            }
        });

        findViewById(R.id.start_time_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                ((TextView) findViewById(R.id.start_time)).setText(hourOfDay + ":" + minute);
                            }
                        })
                        .setStartTime(10, 10)
                        .setDoneText("OK")
                        .setCancelText("CANCEL")
                        .setForced12hFormat();
                rtpd.show(getSupportFragmentManager(), Constant.TIME_PICKER);
            }
        });

        findViewById(R.id.end_time_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                ((TextView) findViewById(R.id.end_time)).setText(hourOfDay + ":" + minute);
                            }
                        })
                        .setStartTime(10, 10)
                        .setDoneText("OK")
                        .setCancelText("CANCEL")
                        .setForced12hFormat();
                rtpd.show(getSupportFragmentManager(), Constant.TIME_PICKER);
            }
        });
    }
}
