package com.bikrampandit.cliquick;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        preferences = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);

        if (android.os.Build.VERSION.SDK_INT >= 11 && getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(true);
        } else if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setupUI();
    }

    private void setupUI() {
        final String startTime = preferences.getString(Constant.START_TIME, Constant.DEFAULT_START_TIME);
        final String endTime = preferences.getString(Constant.END_TIME, Constant.DEFAULT_END_TIME);

        //listeners
        ((Switch) findViewById(R.id.schedule)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.schedule_text).setEnabled(checked);
                findViewById(R.id.schedule_text1).setEnabled(checked);
                findViewById(R.id.start_time_layout).setEnabled(checked);
                findViewById(R.id.start_time).setEnabled(checked);
                findViewById(R.id.start_time1).setEnabled(checked);
                findViewById(R.id.end_time_layout).setEnabled(checked);
                findViewById(R.id.end_time).setEnabled(checked);
                findViewById(R.id.end_time1).setEnabled(checked);
                findViewById(R.id.repeat_text).setEnabled(checked);
                findViewById(R.id.repeat_checkbox).setEnabled(checked);
                findViewById(R.id.w1).setEnabled(checked);
                findViewById(R.id.w2).setEnabled(checked);
                findViewById(R.id.w3).setEnabled(checked);
                findViewById(R.id.w4).setEnabled(checked);
                findViewById(R.id.w5).setEnabled(checked);
                findViewById(R.id.w6).setEnabled(checked);
                findViewById(R.id.w7).setEnabled(checked);
                preferences.edit().putBoolean(Constant.SCHEDULE, checked).apply();
            }
        });

        findViewById(R.id.start_time_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                try {
                                    String newStartTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                                            new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(hourOfDay + ":" + minute));
                                    ((TextView) findViewById(R.id.start_time)).setText(newStartTime);
                                    preferences.edit().putString(Constant.START_TIME, hourOfDay + ":" + minute).apply();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setStartTime(Integer.parseInt(startTime.split(":")[0]), Integer.parseInt(startTime.split(":")[1]))
                        .setDoneText("OK")
                        .setCancelText("CANCEL")
                        .setForced12hFormat();
                rtpd.show(getSupportFragmentManager(), Constant.TIME_PICKER);
            }
        });

        findViewById(R.id.end_time_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(new RadialTimePickerDialogFragment.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
                                try {
                                    String newStartTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                                            new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(hourOfDay + ":" + minute));
                                    ((TextView) findViewById(R.id.end_time)).setText(newStartTime);
                                    preferences.edit().putString(Constant.END_TIME, hourOfDay + ":" + minute).apply();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setStartTime(Integer.parseInt(endTime.split(":")[0]), Integer.parseInt(endTime.split(":")[1]))
                        .setDoneText("OK")
                        .setCancelText("CANCEL")
                        .setForced12hFormat();
                rtpd.show(getSupportFragmentManager(), Constant.TIME_PICKER);
            }
        });

        ((CheckBox) findViewById(R.id.repeat_checkbox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                preferences.edit().putBoolean(Constant.REPEAT_EVERY_WEEK, checked).apply();
            }
        });

        ((CheckBox) findViewById(R.id.w1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                StringBuilder weeks = new StringBuilder(
                        preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK)
                );
                weeks.setCharAt(0, checked ? '1' : '0');
                preferences.edit().putString(Constant.WEEK_ENABLE, weeks.toString()).apply();
            }
        });
        ((CheckBox) findViewById(R.id.w2)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                StringBuilder weeks = new StringBuilder(
                        preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK)
                );
                weeks.setCharAt(1, checked ? '1' : '0');
                preferences.edit().putString(Constant.WEEK_ENABLE, weeks.toString()).apply();
            }
        });
        ((CheckBox) findViewById(R.id.w3)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                StringBuilder weeks = new StringBuilder(
                        preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK)
                );
                weeks.setCharAt(2, checked ? '1' : '0');
                preferences.edit().putString(Constant.WEEK_ENABLE, weeks.toString()).apply();
            }
        });
        ((CheckBox) findViewById(R.id.w4)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                StringBuilder weeks = new StringBuilder(
                        preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK)
                );
                weeks.setCharAt(3, checked ? '1' : '0');
                preferences.edit().putString(Constant.WEEK_ENABLE, weeks.toString()).apply();
            }
        });
        ((CheckBox) findViewById(R.id.w5)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                StringBuilder weeks = new StringBuilder(
                        preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK)
                );
                weeks.setCharAt(4, checked ? '1' : '0');
                preferences.edit().putString(Constant.WEEK_ENABLE, weeks.toString()).apply();
            }
        });
        ((CheckBox) findViewById(R.id.w6)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                StringBuilder weeks = new StringBuilder(
                        preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK)
                );
                weeks.setCharAt(5, checked ? '1' : '0');
                preferences.edit().putString(Constant.WEEK_ENABLE, weeks.toString()).apply();
            }
        });
        ((CheckBox) findViewById(R.id.w7)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                StringBuilder weeks = new StringBuilder(
                        preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK)
                );
                weeks.setCharAt(6, checked ? '1' : '0');
                preferences.edit().putString(Constant.WEEK_ENABLE, weeks.toString()).apply();
            }
        });

        ((Switch) findViewById(R.id.shutter_sound)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.shutter_sound_text).setEnabled(checked);
                findViewById(R.id.shutter_sound_text1).setEnabled(checked);
                preferences.edit().putBoolean(Constant.SHUTTER_SOUND, checked).apply();
            }

        });

        findViewById(R.id.panic_text_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(Setting.this);
                final View view = inflater.inflate(R.layout.setting_dialog, null);

                final EditText panicText = (EditText) view.findViewById(R.id.panic_text);
                panicText.setText(preferences.getString(Constant.PANIC_TEXT, Constant.DEFAULT_PANIC_TEXT));

                AlertDialog dialog = new AlertDialog.Builder(Setting.this)
                        .setTitle("Panic Text")
                        .setView(view)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((TextView) findViewById(R.id.panic_text_view)).setText(panicText.getText().toString() + " (Tap to change)");
                                preferences.edit().putString(Constant.PANIC_TEXT, panicText.getText().toString()).apply();
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .create();
                dialog.show();
            }
        });

        ((CheckBox) findViewById(R.id.send_location)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                findViewById(R.id.send_location_text).setEnabled(checked);
                findViewById(R.id.freqSpinner).setEnabled(checked);
                preferences.edit().putBoolean(Constant.SEND_LOCATION, checked).apply();
            }
        });

        ((Spinner) findViewById(R.id.freqSpinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                preferences.edit().putInt(Constant.SEND_LOCATION_FREQUENCY, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //pre setup
        ((Switch) findViewById(R.id.schedule)).setChecked(preferences.getBoolean(Constant.SCHEDULE, false));
        try {
            ((TextView) findViewById(R.id.start_time)).setText(
                    new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(startTime))
            );
        } catch (Exception e) {
            ((TextView) findViewById(R.id.start_time)).setText(Constant.DEFAULT_START_TIME);
            e.printStackTrace();
        }
        try {
            ((TextView) findViewById(R.id.end_time)).setText(
                    new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new SimpleDateFormat("HH:mm", Locale.ENGLISH).parse(endTime))
            );
        } catch (Exception e) {
            ((TextView) findViewById(R.id.start_time)).setText(Constant.DEFAULT_END_TIME);
            e.printStackTrace();
        }
        ((CheckBox) findViewById(R.id.repeat_checkbox)).setChecked(preferences.getBoolean(Constant.REPEAT_EVERY_WEEK, true));
        ((CheckBox) findViewById(R.id.w1)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK).substring(0, 1)) != 0);
        ((CheckBox) findViewById(R.id.w2)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK).substring(1, 2)) != 0);
        ((CheckBox) findViewById(R.id.w3)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK).substring(2, 3)) != 0);
        ((CheckBox) findViewById(R.id.w4)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK).substring(3, 4)) != 0);
        ((CheckBox) findViewById(R.id.w5)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK).substring(4, 5)) != 0);
        ((CheckBox) findViewById(R.id.w6)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK).substring(5, 6)) != 0);
        ((CheckBox) findViewById(R.id.w7)).setChecked(Integer.parseInt(preferences.getString(Constant.WEEK_ENABLE, Constant.DEFAULT_WEEK).substring(6, 7)) != 0);

        ((Switch) findViewById(R.id.shutter_sound)).setChecked(preferences.getBoolean(Constant.SHUTTER_SOUND, false));
        ((TextView) findViewById(R.id.panic_text_view)).setText(preferences.getString(Constant.PANIC_TEXT, Constant.DEFAULT_PANIC_TEXT) + " (Tap to change)");
        ((CheckBox) findViewById(R.id.send_location)).setChecked(preferences.getBoolean(Constant.SEND_LOCATION, true));
        findViewById(R.id.send_location_text).setEnabled(preferences.getBoolean(Constant.SEND_LOCATION, true));
        findViewById(R.id.freqSpinner).setEnabled(preferences.getBoolean(Constant.SEND_LOCATION, true));
        ((Spinner) findViewById(R.id.freqSpinner)).setSelection(preferences.getInt(Constant.SEND_LOCATION_FREQUENCY, 1));
    }

}
