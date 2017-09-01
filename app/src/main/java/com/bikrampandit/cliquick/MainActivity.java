package com.bikrampandit.cliquick;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Spinner;

import com.rey.material.widget.Switch;

import java.util.ArrayList;
import java.util.Locale;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private MyService myService;
    private boolean isBound;
    private TagContainerLayout mTagContainerLayoutTextMessage;
    private TagContainerLayout mTagContainerLayoutCall;

    private SharedPreferences preferences;
    SQLiteDatabase db;

    private TextToSpeech tts;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        tts = new TextToSpeech(this, this);

        preferences = getSharedPreferences(Constant.PREFERENCE_NAME, MODE_PRIVATE);

        db = new SettingReaderDbHelper(this).getReadableDatabase();

        setupUI();

        handlePermissions();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.VOICE_MATCHED);
        registerReceiver(receiver, filter);
        Intent i = new Intent(this, MyService.class);
        startService(i);
    }

    private void handlePermissions() {
        //record audio permission
        if (preferences.getBoolean(Constant.VOICE_ENABLED, true)) {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) !=
                    PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {

                ((Switch) findViewById(R.id.voice_code_switch)).setCheckedImmediately(false);
                preferences.edit().putBoolean(Constant.VOICE_ENABLED, false).apply();

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSIONS_POCKET_SPHINX);
            }
        }

        //vibrate permission
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.VIBRATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE}, Constant.PERMISSIONS_VIBRATE);
        }

        //camera permission
        if (preferences.getBoolean(Constant.VOLUME_DOWN_ENABLED, true)) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED
                        || (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SYSTEM_ALERT_WINDOW) !=
                        PackageManager.PERMISSION_GRANTED)) {
                    ((Switch) findViewById(R.id.vol_down_switch)).setCheckedImmediately(false);
                    preferences.edit().putBoolean(Constant.VOLUME_DOWN_ENABLED, false).apply();

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.SYSTEM_ALERT_WINDOW}, Constant.PERMISSION_CAPTURE_IMAGE_IN_BACKGROUND);
                }
            } else {
                ((Switch) findViewById(R.id.vol_down_switch)).setCheckedImmediately(false);
                preferences.edit().putBoolean(Constant.VOLUME_DOWN_ENABLED, false).apply();
                Toast.makeText(this, "Your device doesn't have a camera", Toast.LENGTH_SHORT).show();
            }
        }
        //camera permission

        /*
        //write external storage permission
        permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }*/
    }

    private void setupUI() {
        mTagContainerLayoutTextMessage = (TagContainerLayout) findViewById(R.id.tag_container_add_contact_for_text_msg);
        mTagContainerLayoutCall = (TagContainerLayout) findViewById(R.id.tag_container_add_contact_for_call);
        try {
            loadContacts();
        } catch (Exception e) {
            e.printStackTrace();
            db.execSQL(SettingReaderDbHelper.SQL_DELETE_ENTRIES);
            db.execSQL(SettingReaderDbHelper.SQL_CREATE_ENTRIES);
        }
        //set listeners
        mTagContainerLayoutCall.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
                String selection =
                        Constant.COLUMN_CONTACT + " LIKE ? AND " +
                                Constant.COLUMN_FLAG + " LIKE ?";

                String[] selectionArgs = {
                        mTagContainerLayoutCall.getTagText(position), Constant.CALL_ENABLED
                };
                db.delete(Constant.TABLE_NAME, selection, selectionArgs);
                mTagContainerLayoutCall.removeTag(position);
            }
        });

        mTagContainerLayoutTextMessage.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
            }

            @Override
            public void onTagLongClick(int position, String text) {
            }

            @Override
            public void onTagCrossClick(int position) {
                String selection =
                        Constant.COLUMN_CONTACT + " LIKE ? AND " +
                                Constant.COLUMN_FLAG + " LIKE ?";


                String[] selectionArgs = {
                        mTagContainerLayoutTextMessage.getTagText(position), Constant.TEXT_MESSAGING_ENABLED
                };
                db.delete(Constant.TABLE_NAME, selection, selectionArgs);
                mTagContainerLayoutTextMessage.removeTag(position);
            }
        });

        findViewById(R.id.add_contact_for_text_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, Constant.PERMISSIONS_REQUEST_READ_CONTACTS_FOR_TEXT_MSG);
                } else {
                    addContactForTxtMsg();
                }
            }
        });

        findViewById(R.id.add_contact_for_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, Constant.PERMISSIONS_REQUEST_READ_CONTACTS_FOR_CALL);
                } else {
                    addContactForCall();
                }
            }
        });


        ((Switch) findViewById(R.id.vol_up_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.vol_up_text).setEnabled(checked);
                findViewById(R.id.vol_up_text1).setEnabled(checked);
                findViewById(R.id.vol_up_text2).setEnabled(checked);
                findViewById(R.id.vol_up_time_spinner).setEnabled(checked);

                preferences.edit().putBoolean(Constant.VOLUME_UP_ENABLED, checked).apply();
            }
        });

        ((Spinner) findViewById(R.id.vol_up_time_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                preferences.edit().putInt(Constant.VOLUME_UP_TIME_INDEX, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        ((Switch) findViewById(R.id.voice_code_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, final boolean checked) {
                long delay = 0;
                if (checked) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) !=
                            PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                    PackageManager.PERMISSION_GRANTED) {

                        view.setCheckedImmediately(false);
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSIONS_POCKET_SPHINX);
                        return;
                    }
                    if (myService != null) myService.startRecognizerSetup();
                    delay = 500;
                } else {
                    if (myService != null) myService.stopRecognizing();
                }
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.voice_code_switch_text).setEnabled(checked);
                        findViewById(R.id.voice_code1).setEnabled(checked);
                        findViewById(R.id.voice_code2).setEnabled(checked);
                        findViewById(R.id.voice_code3).setEnabled(checked);
                        findViewById(R.id.voice_code4).setEnabled(checked);
                        findViewById(R.id.speak_btn).setVisibility(checked ? View.VISIBLE : View.GONE);
                        findViewById(R.id.mic_note).setEnabled(checked);
                    }
                }, delay);
                preferences.edit().putBoolean(Constant.VOICE_ENABLED, checked).apply();
            }
        });

        ((Spinner) findViewById(R.id.voice_code1)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                StringBuilder voiceCode = new StringBuilder(preferences.getString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE));
                voiceCode.setCharAt(0, (char) (position + '0'));
                //Log.i("biky", "voice code after setting " + voiceCode.toString());
                preferences.edit().putString(Constant.VOICE_CODE, voiceCode.toString()).apply();
                if (myService != null) myService.updateSearchVoiceCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ((Spinner) findViewById(R.id.voice_code2)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                StringBuilder voiceCode = new StringBuilder(preferences.getString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE));
                voiceCode.setCharAt(1, (char) (position + '0'));
                // Log.i("biky", "voice code after setting " + voiceCode.toString());
                preferences.edit().putString(Constant.VOICE_CODE, voiceCode.toString()).apply();
                if (myService != null) myService.updateSearchVoiceCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ((Spinner) findViewById(R.id.voice_code3)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                StringBuilder voiceCode = new StringBuilder(preferences.getString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE));
                voiceCode.setCharAt(2, (char) (position + '0'));
                //Log.i("biky", "voice code after setting " + voiceCode.toString());
                preferences.edit().putString(Constant.VOICE_CODE, voiceCode.toString()).apply();
                if (myService != null) myService.updateSearchVoiceCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ((Spinner) findViewById(R.id.voice_code4)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                StringBuilder voiceCode = new StringBuilder(preferences.getString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE));
                voiceCode.setCharAt(3, (char) (position + '0'));
                //Log.i("biky", "voice code after setting " + voiceCode.toString());
                preferences.edit().putString(Constant.VOICE_CODE, voiceCode.toString()).apply();
                if (myService != null) myService.updateSearchVoiceCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ((Switch) findViewById(R.id.send_message_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.send_message_text).setEnabled(checked);
                findViewById(R.id.add_contact_for_text_msg).setEnabled(checked);
                mTagContainerLayoutTextMessage.setVisibility(checked ? View.VISIBLE : View.GONE);

                preferences.edit().putBoolean(Constant.TEXT_MESSAGING_ENABLED, checked).apply();
            }
        });

        ((Switch) findViewById(R.id.call_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.call_switch_text).setEnabled(checked);
                findViewById(R.id.add_contact_for_call).setEnabled(checked);
                mTagContainerLayoutCall.setVisibility(checked ? View.VISIBLE : View.GONE);

                preferences.edit().putBoolean(Constant.CALL_ENABLED, checked).apply();
            }
        });

        ((Switch) findViewById(R.id.vol_down_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if (checked) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) !=
                            PackageManager.PERMISSION_GRANTED
                            || (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SYSTEM_ALERT_WINDOW) !=
                            PackageManager.PERMISSION_GRANTED)) {

                        view.setCheckedImmediately(false);
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.SYSTEM_ALERT_WINDOW}, Constant.PERMISSION_CAPTURE_IMAGE_IN_BACKGROUND);
                        return;
                    }
                }
                findViewById(R.id.vol_down_text).setEnabled(checked);
                findViewById(R.id.vol_down_text1).setEnabled(checked);
                findViewById(R.id.vol_down_text2).setEnabled(checked);
                findViewById(R.id.vol_down_time_spinner).setEnabled(checked);

                preferences.edit().putBoolean(Constant.VOLUME_DOWN_ENABLED, checked).apply();
            }
        });

        ((Spinner) findViewById(R.id.vol_down_time_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                preferences.edit().putInt(Constant.VOLUME_DOWN_TIME_INDEX, position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ((Switch) findViewById(R.id.backcam_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.backcam_switch_text).setEnabled(checked);
                if (checked) {
                    ((Switch) findViewById(R.id.video_switch)).setChecked(false);
                }
                preferences.edit().putBoolean(Constant.TAKE_PHOTO_BACK_CAM, checked).apply();
            }
        });

        ((Switch) findViewById(R.id.frontcam_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.frontcam_switch_text).setEnabled(checked);
                if (checked) {
                    ((Switch) findViewById(R.id.video_switch)).setChecked(false);
                }
                preferences.edit().putBoolean(Constant.TAKE_PHOTO_FRONT_CAM, checked).apply();
            }
        });

        ((Switch) findViewById(R.id.video_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.video_switch_text).setEnabled(checked);
                findViewById(R.id.video_switch_text1).setEnabled(checked);
                if (checked) {
                    ((Switch) findViewById(R.id.frontcam_switch)).setChecked(false);
                    ((Switch) findViewById(R.id.backcam_switch)).setChecked(false);
                } else {
                    if (myService != null) myService.stopRecordingVideo();
                }
                preferences.edit().putBoolean(Constant.TAKE_VIDEO, checked).apply();
            }
        });

        //pre setup
        ((Switch) findViewById(R.id.vol_up_switch))
                .setCheckedImmediately(preferences.getBoolean(Constant.VOLUME_UP_ENABLED, true));

        ((Spinner) findViewById(R.id.vol_up_time_spinner)).setSelection(preferences.getInt(Constant.VOLUME_UP_TIME_INDEX, 1));

        ((Switch) findViewById(R.id.voice_code_switch))
                .setCheckedImmediately(preferences.getBoolean(Constant.VOICE_ENABLED, true));

        ((Spinner) findViewById(R.id.voice_code1)).setSelection(Integer.parseInt(preferences.getString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE).substring(0, 1)));
        ((Spinner) findViewById(R.id.voice_code2)).setSelection(Integer.parseInt(preferences.getString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE).substring(1, 2)));
        ((Spinner) findViewById(R.id.voice_code3)).setSelection(Integer.parseInt(preferences.getString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE).substring(2, 3)));
        ((Spinner) findViewById(R.id.voice_code4)).setSelection(Integer.parseInt(preferences.getString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE).substring(3, 4)));

        ((Switch) findViewById(R.id.send_message_switch))
                .setCheckedImmediately(preferences.getBoolean(Constant.TEXT_MESSAGING_ENABLED, true));

        ((Switch) findViewById(R.id.call_switch))
                .setCheckedImmediately(preferences.getBoolean(Constant.CALL_ENABLED, false));

        ((Switch) findViewById(R.id.vol_down_switch))
                .setCheckedImmediately(preferences.getBoolean(Constant.VOLUME_DOWN_ENABLED, true));

        ((Spinner) findViewById(R.id.vol_down_time_spinner)).setSelection(preferences.getInt(Constant.VOLUME_DOWN_TIME_INDEX, 1));

        ((Switch) findViewById(R.id.frontcam_switch))
                .setCheckedImmediately(preferences.getBoolean(Constant.TAKE_PHOTO_FRONT_CAM, false));

        ((Switch) findViewById(R.id.backcam_switch))
                .setCheckedImmediately(preferences.getBoolean(Constant.TAKE_PHOTO_BACK_CAM, true));

        ((Switch) findViewById(R.id.video_switch))
                .setCheckedImmediately(preferences.getBoolean(Constant.TAKE_VIDEO, false));
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder myBinder = (MyService.MyBinder) service;
            myService = myBinder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    public void addContactForTxtMsg() {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, Constant.PICK_CONTACT_FOR_TEXT_MSG);
    }

    public void addContactForCall() {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(i, Constant.PICK_CONTACT_FOR_CALL);
    }

    public void onActivityResult(final int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        Log.i("biky", "reqcode = " + reqCode + " result code = " + resultCode);

        if (reqCode == Constant.PICK_CONTACT_FOR_CALL || reqCode == Constant.PICK_CONTACT_FOR_TEXT_MSG) {

            if (resultCode == Activity.RESULT_OK) {

                Uri result = data.getData();
                Log.v("biky", "Got a result: " + result.toString());

// get the phone number id from the Uri
                String id = result.getLastPathSegment();

// query the phone numbers for the selected phone number id
                Cursor c = getApplicationContext().getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id}, null);

                try {
                    if (c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER) <= 0) {

                        makeText(this, "Contact does not have phone number", Toast.LENGTH_SHORT).show();

                        return;
                    }
                } catch (Exception e) {
                    return;
                }

                final ArrayList<String> phoneNumbers = new ArrayList<>();

                String name = "Unknown";
                while (c.moveToNext()) {
                    name = c.getString(c.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = c.getString(c.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneNumber = phoneNumber.replace(" ", "");
                    if (!phoneNumbers.contains(phoneNumber)) {
                        phoneNumbers.add(phoneNumber.replace(" ", ""));
                    }
                }
                c.close();

                if (phoneNumbers.size() == 1) {
                    if (reqCode == Constant.PICK_CONTACT_FOR_TEXT_MSG) {
                        mTagContainerLayoutTextMessage.addTag(name + " (" + phoneNumbers.get(0) + ")");
                        //save to db
                        ContentValues values = new ContentValues();
                        values.put(Constant.COLUMN_CONTACT, name + " (" + phoneNumbers.get(0) + ")");
                        values.put(Constant.COLUMN_FLAG, Constant.TEXT_MESSAGING_ENABLED);
                        db.insert(Constant.TABLE_NAME, null, values);
                    } else {
                        mTagContainerLayoutCall.addTag(name + " (" + phoneNumbers.get(0) + ")");
                        //save to db
                        ContentValues values = new ContentValues();
                        values.put(Constant.COLUMN_CONTACT, name + " (" + phoneNumbers.get(0) + ")");
                        values.put(Constant.COLUMN_FLAG, Constant.CALL_ENABLED);
                        db.insert(Constant.TABLE_NAME, null, values);
                    }
                    return;
                }

                LayoutInflater inflater = LayoutInflater.from(this);
                final View view = inflater.inflate(R.layout.choose_number_dialog, null);

                final TextView contactName = (TextView) view.findViewById(R.id.contact_name);
                final ListView contactNumbers = (ListView) view.findViewById(R.id.contact_numbers);

                contactName.setText(name);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, phoneNumbers);
                contactNumbers.setAdapter(adapter);

                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Choose a contact number")
                        .setView(view)
                        .setNegativeButton("CANCEL", null).create();
                dialog.show();

                contactNumbers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.dismiss();
                        if (reqCode == Constant.PICK_CONTACT_FOR_TEXT_MSG) {
                            mTagContainerLayoutTextMessage.addTag(contactName.getText() + " (" + phoneNumbers.get(position) + ")");
                        } else {
                            mTagContainerLayoutCall.addTag(contactName.getText() + " (" + phoneNumbers.get(position) + ")");
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constant.PERMISSIONS_POCKET_SPHINX:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    ((Switch) findViewById(R.id.voice_code_switch)).setCheckedImmediately(true);
                }
                break;
            case Constant.PERMISSIONS_REQUEST_READ_CONTACTS_FOR_TEXT_MSG:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addContactForTxtMsg();
                }
                break;
            case Constant.PERMISSIONS_REQUEST_READ_CONTACTS_FOR_CALL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addContactForCall();
                }
                break;
            case Constant.PERMISSIONS_VIBRATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
                break;
            case Constant.PERMISSION_CAPTURE_IMAGE_IN_BACKGROUND:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    ((Switch) findViewById(R.id.vol_down_switch)).setCheckedImmediately(true);
                }
                break;
        }

    }

    public void speakCode(View v) {
        String text = String.format(Locale.ENGLISH, "%s %s %s %s",
                ((Spinner) findViewById(R.id.voice_code1)).getSelectedItem().toString(),
                ((Spinner) findViewById(R.id.voice_code2)).getSelectedItem().toString(),
                ((Spinner) findViewById(R.id.voice_code3)).getSelectedItem().toString(),
                ((Spinner) findViewById(R.id.voice_code4)).getSelectedItem().toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                findViewById(R.id.speak_btn).setEnabled(true);
            }

        } else {
            findViewById(R.id.speak_btn).setEnabled(false);
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        db.close();
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    void loadContacts() throws Exception {
        String[] projection = {
                Constant.COLUMN_CONTACT,
                Constant.COLUMN_FLAG,
        };
        Cursor cursor = db.query(
                Constant.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                //selection,                                // The columns for the WHERE clause
                null,
                //selectionArgs,                            // The values for the WHERE clause
                null,
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                //sortOrder                                 // The sort order
                null
        );
        while (cursor.moveToNext()) {
            String contact = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.COLUMN_CONTACT));

            String flag = cursor.getString(
                    cursor.getColumnIndexOrThrow(Constant.COLUMN_FLAG));

            if (Constant.TEXT_MESSAGING_ENABLED.equals(flag)) {
                mTagContainerLayoutTextMessage.addTag(contact);
            } else if (Constant.CALL_ENABLED.equals(flag)) {
                mTagContainerLayoutCall.addTag(contact);
            }
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                Intent i = new Intent(this, Setting.class);
                startActivity(i);
                return true;
            case R.id.gallery:
                startActivity(new Intent(this, GalleryGrid.class));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.VOICE_MATCHED.equals(intent.getAction())) {
                findViewById(R.id.voice_code1).setBackgroundResource(R.drawable.gradient_spinner_voice_detected);
                findViewById(R.id.voice_code2).setBackgroundResource(R.drawable.gradient_spinner_voice_detected);
                findViewById(R.id.voice_code3).setBackgroundResource(R.drawable.gradient_spinner_voice_detected);
                findViewById(R.id.voice_code4).setBackgroundResource(R.drawable.gradient_spinner_voice_detected);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((Switch) findViewById(R.id.voice_code_switch)).setChecked(false);
                        findViewById(R.id.voice_code1).setBackgroundResource(R.drawable.gradient_spinner);
                        findViewById(R.id.voice_code2).setBackgroundResource(R.drawable.gradient_spinner);
                        findViewById(R.id.voice_code3).setBackgroundResource(R.drawable.gradient_spinner);
                        findViewById(R.id.voice_code4).setBackgroundResource(R.drawable.gradient_spinner);
                    }
                }, 500);
            } else if (Constant.ERROR_WHILE_PARSING_VOICE_SEARCH_CODE.equals(intent.getAction())) {
                preferences.edit().putString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE).apply();
                ((Spinner) findViewById(R.id.voice_code1)).setSelection(Integer.parseInt(preferences.getString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE).substring(0, 1)));
                ((Spinner) findViewById(R.id.voice_code2)).setSelection(Integer.parseInt(preferences.getString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE).substring(1, 2)));
                ((Spinner) findViewById(R.id.voice_code3)).setSelection(Integer.parseInt(preferences.getString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE).substring(2, 3)));
                ((Spinner) findViewById(R.id.voice_code4)).setSelection(Integer.parseInt(preferences.getString(Constant.VOICE_CODE, Constant.DEFAULT_VOICE_CODE).substring(3, 4)));
            } else if (Constant.ERROR_RECOGNISING.equals(intent.getAction())) {
                ((Switch) findViewById(R.id.voice_code_switch)).setCheckedImmediately(false);
                Toast.makeText(context, "Sorry something went wrong. Probably some other application is using MIC", Toast.LENGTH_SHORT).show();
            }
        }
    };
}

