package com.bikrampandit.cliquick;

/**
 * Created by Rakesh Pandit on 8/30/2017.
 */

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class VideoRecorderService extends Service implements SurfaceHolder.Callback {

    private WindowManager windowManager;
    private SurfaceView surfaceView;
    @SuppressWarnings("deprecation")
    private Camera camera = null;
    private MediaRecorder mediaRecorder = null;

    @Override
    public void onCreate() {
        //noinspection deprecation
        camera = Camera.open();

        Log.i("biky", "video recorder service created");
        // Create new SurfaceView, set its size to 1x1, move it to the top left corner and set this service as a callback
        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        surfaceView = new SurfaceView(this);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                1, 1,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        windowManager.addView(surfaceView, layoutParams);
        surfaceView.getHolder().addCallback(this);

    }

    // Method called right after Surface created (initializing and starting MediaRecorder)
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        mediaRecorder = new MediaRecorder();
        camera.unlock();
        if (Build.VERSION.SDK_INT >= 17) {
            camera.enableShutterSound(false);
        } else {
            AudioManager audio = (AudioManager) this.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audio.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0 /*AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE*/);
        }

        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        //noinspection deprecation
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        //  mediaRecorder.setMaxDuration(25000);//25 seconds in ms
        //   mediaRecorder.setMaxFileSize(20971520);//20 MB in bytes

        File directory = new File(Constant.VIDEO_PATH);
        if (!directory.exists() || !directory.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            directory.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String videoFileName = "CQ" + timeStamp;

        File videoFile = new File(directory, videoFileName + Constant.VIDEO_FILE_EXTENSION);
        int i = 1;
        while (videoFile.exists()) {
            videoFile = new File(directory, videoFileName + "(" + i + ")" + Constant.IMAGE_FILE_EXTENSION);
            i++;
            //ensure it doesnot repeat forever
            if (i > 10000) {
                break;
            }
        }

        mediaRecorder.setOutputFile(videoFile.getPath());
/*
        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int what, int extras) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED || what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                    try {
                        mediaRecorder.stop();
                    } catch (Exception e) {
                        Log.i("biky", "media recorder on info listners. can't stop media recorder " + e.getMessage());
                    }
                }
            }
        });
*/
        Log.i("biky", "media recorder path =  " + videoFile.getPath());

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            long[] pattern = {0,50,50,50};
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(pattern,-1);
        } catch (Exception e) {
            Log.i("biky", "media recorder failed to start " + e.getMessage());
            stopSelf();
        }
    }

    // Stop recording and remove SurfaceView
    @Override
    public void onDestroy() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.reset();
                mediaRecorder.release();
                Log.i("biky", "on destroy. media recorder reset and released");
            }
            if (camera != null) {
                camera.lock();
                camera.release();
                Log.i("biky", "on destroy. camera locked and released");
            }
            long[] pattern = {0,50,50,50};
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(pattern,-1);
            windowManager.removeView(surfaceView);
        } catch (Exception e) {
            Log.i("biky", "error on destroy = " + e.getMessage());
        }
        super.onDestroy();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}