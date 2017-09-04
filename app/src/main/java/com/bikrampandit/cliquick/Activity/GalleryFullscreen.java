package com.bikrampandit.cliquick.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bikrampandit.cliquick.Adapter.FullScreenViewAdapter.ViewPagerAdapter;
import com.bikrampandit.cliquick.Adapter.FullScreenViewAdapter.RecyclerViewAdapter;
import com.bikrampandit.cliquick.R;
import com.bikrampandit.cliquick.Utility.Constant;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GalleryFullscreen extends AppCompatActivity {

    private ArrayList<File> files = new ArrayList<>();
    private ViewPagerAdapter viewPagerAdapter;
    private boolean selectionMode = false;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ViewPager viewPager;
    private View highLightedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_fullscreen);

        if (android.os.Build.VERSION.SDK_INT >= 11 && getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(true);
        } else if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
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

        viewPagerAdapter = new ViewPagerAdapter(this, files);

        viewPager.setAdapter(viewPagerAdapter);

        viewPager.setCurrentItem(currentItem);

        //noinspection deprecation
        viewPager.setOnPageChangeListener(viewPagerAdapter);

        recyclerViewAdapter = new RecyclerViewAdapter(this, files);
        recyclerView.setAdapter(recyclerViewAdapter);

        layoutManager.scrollToPositionWithOffset((currentItem / 4) * 4, 0);

        recyclerViewAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (selectionMode) {

                    findViewById(R.id.delete).setVisibility(View.VISIBLE);

                    recyclerViewAdapter.selectView(position, !view.isSelected());

                    if (recyclerViewAdapter.getSelectedCount() == 0) {
                        selectionMode = false;
                        recyclerViewAdapter.removeSelection();
                        findViewById(R.id.delete).setVisibility(View.GONE);
                    }
                }
                viewPager.setCurrentItem(position, true);
            }
        });

        recyclerViewAdapter.setOnItemLongClickListener(new RecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {

                selectionMode = true;

                findViewById(R.id.delete).setVisibility(View.VISIBLE);

                recyclerViewAdapter.selectView(position, !view.isSelected());

                if (recyclerViewAdapter.getSelectedCount() == 0) {
                    selectionMode = false;
                    recyclerViewAdapter.removeSelection();
                    findViewById(R.id.delete).setVisibility(View.GONE);
                }

                //  Log.i("biky", "on item long click");
                return true;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                layoutManager.scrollToPositionWithOffset((position / 4) * 4, 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        registerReceiver(receiver, new IntentFilter(Constant.NEW_FILE_CREATED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewPagerAdapter != null && viewPagerAdapter.handler != null) {
            viewPagerAdapter.handler.removeCallbacksAndMessages(null);
        }
        unregisterReceiver(receiver);
    }


    @Override
    public void onBackPressed() {
        if (recyclerViewAdapter == null) return;
        if (selectionMode) {
            selectionMode = false;
            recyclerViewAdapter.removeSelection();
            findViewById(R.id.delete).setVisibility(View.GONE);
        } else {
            sendResultToCaller();
            super.onBackPressed();
        }
    }

    private ArrayList<Integer> positions = new ArrayList<>();

    public void deleteFile(View v) {
        if (recyclerViewAdapter == null) return;
        SparseBooleanArray selected = recyclerViewAdapter.getSelectedPositions();
        for (int i = selected.size() - 1; i >= 0; i--) {
            if (selected.valueAt(i)) {
                int position = selected.keyAt(i);
                File file = recyclerViewAdapter.getItem(position);
                recyclerViewAdapter.remove(file, false);
                positions.add(position);
            }
        }

        viewPagerAdapter.notifyDataSetChanged();
        recyclerViewAdapter.notifyDataSetChanged();

        selectionMode = false;

        recyclerViewAdapter.removeSelection();

        findViewById(R.id.delete).setVisibility(View.GONE);

        if (recyclerViewAdapter.getItemCount() == 0) {
            sendResultToCaller();
        }
    }

    private void sendResultToCaller() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(Constant.DELETED_POSITIONS, positions);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.NEW_FILE_CREATED.equals(intent.getAction())) {
                File file = new File(intent.getStringExtra(Constant.FILE_PATH));
                Log.i("biky", "new image captured or video recorded, file path = " + file.getAbsolutePath());
                if (file.getName().endsWith(Constant.IMAGE_FILE_EXTENSION) || file.getName().endsWith(Constant.VIDEO_FILE_EXTENSION)) {
                    if(files.contains(file)){
                        return;
                    }
                    files.add(0, file);
                    recyclerViewAdapter.notifyDataSetChanged();
                    viewPagerAdapter.notifyDataSetChanged();
                }
            }
        }
    };
}
