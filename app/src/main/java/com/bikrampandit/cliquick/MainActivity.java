package com.bikrampandit.cliquick;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.Spinner;
import com.rey.material.widget.Switch;

import java.util.ArrayList;
import java.util.Locale;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    /* Named searches allow to quickly reconfigure the decoder */

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;


    private MyService myService;
    private boolean isBound;
    private TagContainerLayout mTagContainerLayoutTextMessage;
    private TagContainerLayout mTagContainerLayoutCall;

    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

        Intent i = new Intent(this, MyService.class);
        startService(i);

        mTagContainerLayoutTextMessage = (TagContainerLayout) findViewById(R.id.tag_container_add_contact_for_text_msg);
        mTagContainerLayoutCall = (TagContainerLayout) findViewById(R.id.tag_container_add_contact_for_call);

        mTagContainerLayoutCall.addTag("Add contact");
        mTagContainerLayoutTextMessage.addTag("Add contact");

        mTagContainerLayoutCall.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                if ("Add contact".equals(text)) {
                    addContactForCall();
                }
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });

        mTagContainerLayoutTextMessage.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                if ("Add contact".equals(text)) {
                    addContactForTxtMsg();
                }
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {

            }
        });

        setupUI();
    }

    private void setupUI() {
        ((Switch) findViewById(R.id.vol_up_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.vol_up_text).setEnabled(checked);
                findViewById(R.id.vol_up_text1).setEnabled(checked);
                findViewById(R.id.time_spinner).setEnabled(checked);
            }
        });

        ((Switch) findViewById(R.id.voice_code_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.voice_code_switch_text).setEnabled(checked);
                findViewById(R.id.voice_code1).setEnabled(checked);
                findViewById(R.id.voice_code2).setEnabled(checked);
                findViewById(R.id.voice_code3).setEnabled(checked);
                findViewById(R.id.voice_code4).setEnabled(checked);
            }
        });

        ((Switch) findViewById(R.id.send_message_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.send_message_text).setEnabled(checked);
                findViewById(R.id.tag_container_add_contact_for_text_msg).setEnabled(checked);
            }
        });

        ((Switch) findViewById(R.id.call_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.call_switch_text).setEnabled(checked);
                findViewById(R.id.tag_container_add_contact_for_call).setEnabled(checked);
            }
        });


        ((Switch) findViewById(R.id.vol_down_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.vol_down_text).setEnabled(checked);
                findViewById(R.id.vol_down_text1).setEnabled(checked);
                findViewById(R.id.time2_spinner).setEnabled(checked);
            }
        });

        ((Switch) findViewById(R.id.backcam_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.backcam_switch_text).setEnabled(checked);
            }
        });

        ((Switch) findViewById(R.id.frontcam_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.frontcam_switch_text).setEnabled(checked);
            }
        });

        ((Switch) findViewById(R.id.video_switch)).setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                findViewById(R.id.video_switch_text).setEnabled(checked);
            }
        });


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
                    } else {
                        mTagContainerLayoutCall.addTag(name + " (" + phoneNumbers.get(0) + ")");
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
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
        }
    }

    public void speakCode(View v) {
        TextToSpeech textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        textToSpeech.setLanguage(Locale.US);

        String text = String.format(Locale.ENGLISH, "%s %s %s %s",
                ((Spinner) findViewById(R.id.voice_code1)).getSelectedItem(),
                ((Spinner) findViewById(R.id.voice_code2)).getSelectedItem().toString(),
                ((Spinner) findViewById(R.id.voice_code3)).getSelectedItem().toString(),
                ((Spinner) findViewById(R.id.voice_code4)).getSelectedItem().toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}

