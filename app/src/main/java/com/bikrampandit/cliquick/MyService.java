package com.bikrampandit.cliquick;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class MyService extends Service implements RecognitionListener {
    private IBinder binder = new MyBinder();
    private boolean isBound = false;
    private MediaPlayer mediaPlayer;

    private static final String KWS_SEARCH = "thisdoesntwork";
    private SpeechRecognizer recognizer = null;

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("biky", "on start command");
        mediaPlayer = MediaPlayer.create(this, R.raw.cliquick);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(broadcastReceiver, filter);

        startRecognizerSetup();
        return START_STICKY;
    }

    @Override
    public void onRebind(Intent intent) {
        isBound = true;
        Log.d("biky", "on rebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("biky", "on unbind");
        isBound = false;
        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("biky", "on bind");
        isBound = true;
        return binder;
    }

    public class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    int prevVolume = -1;
    long prevMillis = System.currentTimeMillis();
    long totalTime0 = 0;
    long totalTime15 = 0;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                //    mediaPlayer.start();
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                //   mediaPlayer.stop();
            }
            //Log.i("biky", intent.getAction());
            if ("android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction())) {
                int volume = (Integer) intent.getExtras().get("android.media.EXTRA_VOLUME_STREAM_VALUE");
                Log.i("biky", "volume = " + volume);
                long currMillis = System.currentTimeMillis();
                long delta = currMillis - prevMillis;
                Log.i("biky", "delta = " + delta);
                //if delta < 500 means button is pressed within half seconds
                if (delta < 500) {
                    if (volume == 0) {
                        totalTime0 += delta;
                        totalTime15 = 0;
                    } else if (volume == 15) {
                        totalTime0 = 0;
                        totalTime15 += delta;
                    }
                } else {
                    totalTime0 = 0;
                    totalTime15 = 0;
                }

                Log.i("biky", "total time 0 = " + totalTime0);
                Log.i("biky", "total time 15 = " + totalTime15);


                if (totalTime0 > 3000) {
                    event1();
                }
                if (totalTime15 > 3000) {
                    event2();
                }

                // prevVolume = volume;
                prevMillis = currMillis;
            }
        }
    };

    private void event1() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(200);
    }

    private void event2() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(200);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        unregisterReceiver(broadcastReceiver);
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
    }

    void startRecognizerSetup() {
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(getApplicationContext());
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Log.i("biky", "Failed to init recognizer " + result);
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }


    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them
        if(recognizer != null) return; //already setup

        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                // .setRawLogDir(assetsDir)

                // Threshold to tune for keyphrase to balance between false alarms and misses
             //   .setKeywordThreshold(1e-45f)

                // Use context-independent phonetic search, context-dependent is too slow for mobile
               // .setBoolean("-allphone_ci", true)

                .getRecognizer();
        recognizer.addListener(this);

        File digitGrammar = new File(assetsDir, "digits.gram");
        recognizer.addGrammarSearch(KWS_SEARCH, digitGrammar);
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr().trim();

        if (text.contains(KWS_SEARCH)) {
            panic();
        }

        Log.i("biky", "on partial result " + text);
    }

    /**
     * This callback is called when we stop the recognizer.
     */
    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis == null) return;

        String text = hypothesis.getHypstr();
        Log.i("biky", "on result " + text);

        if (text.contains(KWS_SEARCH)) {
            panic();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("biky", "on beginning of speech : ");
    }

    /**
     * We stop recognizer here to get a final result
     */
    @Override
    public void onEndOfSpeech() {
        Log.d("biky", "on end of speech : ");
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            switchSearch(KWS_SEARCH);
    }

    @Override
    public void onError(Exception error) {
        Log.i("biky", error.getMessage());
    }

    @Override
    public void onTimeout() {
        Log.d("biky", "on time out : ");
        switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();
        recognizer.cancel();
        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        recognizer.startListening(searchName, 10000);
    }

    private void panic() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(200);
    }


}
