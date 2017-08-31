package com.bikrampandit.cliquick;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Rakesh Pandit on 8/31/2017.
 */

class Util {
    static private int ring;
    static private int alarm;
    static private int dtmf;
    static private int notification;
    static private int system;
    static private int voice_call;
    static private int music;

    static void muteEverything(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ring = audio.getStreamVolume(AudioManager.STREAM_RING);
        alarm = audio.getStreamVolume(AudioManager.STREAM_ALARM);
        dtmf = audio.getStreamVolume(AudioManager.STREAM_DTMF);
        notification = audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        system = audio.getStreamVolume(AudioManager.STREAM_SYSTEM);
        voice_call = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        music = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    static void unmuteEverything(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audio.setStreamVolume(AudioManager.STREAM_RING, ring, 0);
        audio.setStreamVolume(AudioManager.STREAM_ALARM, alarm, 0);
        audio.setStreamVolume(AudioManager.STREAM_DTMF, dtmf, 0);
        audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notification, 0);
        audio.setStreamVolume(AudioManager.STREAM_SYSTEM, system, 0);
        audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, voice_call, 0);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, music, 0);
    }

    static void vibrate(Context context, long delay) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(delay);
        }
    }

    static void vibrate(Context context, long[] pattern, int repeat) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(pattern, repeat);
        }
    }
}

