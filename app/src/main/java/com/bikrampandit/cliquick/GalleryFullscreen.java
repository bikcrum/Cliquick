package com.bikrampandit.cliquick;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Visibility;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class GalleryFullscreen extends AppCompatActivity {

    public ArrayList<File> files = new ArrayList<>();
    private Map<File,Boolean> pages = new HashMap<>();
    ImageButton playButton;
    TextView seekTime;
    SeekBar seek;
    TextView totalTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_fullscreen);

        if (android.os.Build.VERSION.SDK_INT >= 11 && getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(true);
        } else if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        playButton = (ImageButton) findViewById(R.id.play_btn);
        seekTime = (TextView) findViewById(R.id.seek_time);
        seek = (SeekBar) findViewById(R.id.seek_time_bar);
        totalTime = (TextView) findViewById(R.id.total_time);
        //  fileInfo = (TextView) findViewById(R.id.file_info);

        //   fileInfo.setText(Util.getFileInfo(files.get(position)));

        final int currentItem = getIntent().getIntExtra(Constant.CURRENT_FILE_SELECTION, 0);

        File directory = new File(Constant.FILE_PATH);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    //checking if the file is image file
                    return file.getName().endsWith(Constant.IMAGE_FILE_EXTENSION) || file.getName().endsWith(Constant.VIDEO_FILE_EXTENSION);
                }
            });
            if (files != null && files.length > 0) {
                //sort in descending order
                Arrays.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return f2.getName().compareTo(f1.getName());
                    }
                });
                this.files = new ArrayList<>(Arrays.asList(files));
            } else {
                Toast.makeText(GalleryFullscreen.this, "No image or video found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(GalleryFullscreen.this, "No image or video found", Toast.LENGTH_SHORT).show();
            finish();
        }

        final FullScreenViewAdapter adapter = new FullScreenViewAdapter(this, files);

        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(currentItem);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //     Log.i("biky", "on page scrolled, position "+position);
            }

            @Override
            public void onPageSelected(final int position) {
                //already we have add listenrs and stuffs
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.i("biky", "on page scroll state changed, state =  " + state);
            }
        });
    }
}
