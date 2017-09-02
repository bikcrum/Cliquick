package com.bikrampandit.cliquick.Utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Rakesh Pandit on 8/31/2017.
 */

public class Util{
    private static int ring;
    private static int alarm;
    private static int dtmf;
    private static int notification;
    private static int system;
    private static int voice_call;
    private static int music;

    public static void muteEverything(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        ring = audio.getStreamVolume(AudioManager.STREAM_RING);
        alarm = audio.getStreamVolume(AudioManager.STREAM_ALARM);
        dtmf = audio.getStreamVolume(AudioManager.STREAM_DTMF);
        notification = audio.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        system = audio.getStreamVolume(AudioManager.STREAM_SYSTEM);
        voice_call = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        music = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        audio.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        audio.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
        audio.setStreamVolume(AudioManager.STREAM_DTMF, 0, 0);
        audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
        audio.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);
        audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 0, 0);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }

    public static void unmuteEverything(Context context) {
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audio.setStreamVolume(AudioManager.STREAM_RING, ring, 0);
        audio.setStreamVolume(AudioManager.STREAM_ALARM, alarm, 0);
        audio.setStreamVolume(AudioManager.STREAM_DTMF, dtmf, 0);
        audio.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notification, 0);
        audio.setStreamVolume(AudioManager.STREAM_SYSTEM, system, 0);
        audio.setStreamVolume(AudioManager.STREAM_VOICE_CALL, voice_call, 0);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, music, 0);
    }

    public static void vibrate(Context context, long delay) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(delay);
        }
    }

    public static void vibrate(Context context, long[] pattern, int repeat) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(pattern, repeat);
        }
    }

    public static String getSeekTime(int msec) {
        int seconds = msec / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        return (hours <= 0 ? "" : hours + ":") +
                minutes + ":" +
                seconds;
    }

    //returns date in nice format parsed from its name
    public static String getFileInfo(File file) {
        String fileName = file.getName();
        String parcelableDate;
        if (fileName.contains("(")) {
            parcelableDate = fileName.substring(2, fileName.lastIndexOf('('));
        } else {
            parcelableDate = fileName.substring(2, fileName.lastIndexOf('.'));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
        try {
            return new SimpleDateFormat("EEE, MMM d, h:mm:ss a", Locale.ENGLISH).format(sdf.parse(parcelableDate));
        } catch (Exception ex) {
            return parcelableDate;
        }
    }
}

