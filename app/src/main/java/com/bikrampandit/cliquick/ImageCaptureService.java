package com.bikrampandit.cliquick;

/**
 * Created by Rakesh Pandit on 8/28/2017.
 */


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraService;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageCaptureService extends HiddenCameraService {


    private MediaPlayer cameraSound;
    private Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("biky", "image service  cam. on start command");
        this.intent = intent;

        cameraSound = MediaPlayer.create(this, R.raw.camera_shutter);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
                File imageFile = createImageFile();
                CameraConfig cameraConfig = new CameraConfig()
                        .getBuilder(this)
                        .setCameraFacing(intent.getBooleanExtra(Constant.TAKE_PHOTO_BACK_CAM, true) ?
                                CameraFacing.REAR_FACING_CAMERA : CameraFacing.FRONT_FACING_CAMERA)
                        .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                        .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                        .setImageFile(imageFile)
                        .setImageRotation(intent.getBooleanExtra(Constant.TAKE_PHOTO_BACK_CAM, true) ?
                                CameraRotation.ROTATION_90 : CameraRotation.ROTATION_270)
                        .build();

                startCamera(cameraConfig);

                new android.os.Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        takePicture();
                    }
                });
            } else {
                //Open settings to grant permission for "Draw other apps".
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
            }
        } else {
            //TODO Ask your parent activity for providing runtime permission
            Log.i("biky", "image capture serive  cam. Camera permission not available");
        }
        return START_NOT_STICKY;
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "CQ" + timeStamp;
        File directory = new File(Constant.IMAGE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File imageFile = new File(directory, imageFileName + "." + Constant.IMAGE_FILE_EXTENSION);
        int i = 1;
        while (imageFile.exists()) {
            imageFile = new File(directory, imageFileName + "(" + i + ")." + Constant.IMAGE_FILE_EXTENSION);
            i++;
            //ensure it doesnot repeat forever
            if(i > 10000){
                break;
            }
        }
        return imageFile;
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        //Do something with the bitmap
        AudioManager am =
                (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);
        cameraSound.start();
        Log.i("biky", "image captured by  cam " + imageFile.length() + ", image absolute path = " + imageFile.getAbsolutePath());

        Intent j = new Intent();
        j.setAction(Constant.NEW_IMAGE_CAPTURED);
        j.putExtra(Constant.IMAGE_PATH,imageFile.getAbsolutePath());
        sendBroadcast(j);

        stopSelf();
        //start service again if image needs to capture by both camera
        if (intent.getBooleanExtra(Constant.TAKE_PHOTO_BACK_CAM, true) &&
                intent.getBooleanExtra(Constant.TAKE_PHOTO_FRONT_CAM, false)) {
            Intent i = new Intent(this, ImageCaptureService.class);
            i.putExtra(Constant.TAKE_PHOTO_BACK_CAM, false);
            i.putExtra(Constant.TAKE_PHOTO_FRONT_CAM, true);
            startService(i);
        }
    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Log.i("biky", "Cannot open camera.");
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                Log.i("biky", "Cannot write image captured by camera.");
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                Log.i("biky", "Camera permission not available.");
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Log.i("biky", "Your device does not have  camera.");
                break;
        }

        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("biky", "on destroy. image capture service  cam");
    }


}