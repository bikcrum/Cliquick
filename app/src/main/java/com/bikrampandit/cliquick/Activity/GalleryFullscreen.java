package com.bikrampandit.cliquick.Activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bikrampandit.cliquick.Adapter.FullScreenViewAdapter;
import com.bikrampandit.cliquick.Adapter.RecyclerViewAdapter;
import com.bikrampandit.cliquick.R;
import com.bikrampandit.cliquick.Utility.Constant;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GalleryFullscreen extends AppCompatActivity {

    private ArrayList<File> files = new ArrayList<>();
    private FullScreenViewAdapter fullScreenViewAdapter;

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
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager  = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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

        fullScreenViewAdapter = new FullScreenViewAdapter(this, files);

        viewPager.setAdapter(fullScreenViewAdapter);

        viewPager.setCurrentItem(currentItem);

        //noinspection deprecation
        viewPager.setOnPageChangeListener(fullScreenViewAdapter);

        final RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this,files);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                viewPager.setCurrentItem(position,true);
                view.setSelected(true);
            }
        });

        fullScreenViewAdapter.setOnItemClickListener(new FullScreenViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view,final int position) {
                Log.i("biky","on item click view pager adapter");
                new android.os.Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.scrollToPosition(position);

                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fullScreenViewAdapter.handler != null) {
            fullScreenViewAdapter.handler.removeCallbacksAndMessages(null);
        }
    }
}
