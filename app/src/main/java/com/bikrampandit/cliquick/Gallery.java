package com.bikrampandit.cliquick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Gallery extends AppCompatActivity {
    private GridView gridView;
    private ArrayList<File> images = new ArrayList<>();
    private GridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if (android.os.Build.VERSION.SDK_INT >= 11 && getActionBar() != null) {
            getActionBar().setDisplayShowHomeEnabled(true);
        } else if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        gridView = (GridView) findViewById(R.id.image_gallery);

        File directory = new File(Constant.IMAGE_PATH);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (int i = files.length - 1; i >= 0; i--) {
                File file = files[i];
                //checking valid image file
                if (file.getName().substring(file.getName().lastIndexOf('.') + 1).equals(Constant.IMAGE_FILE_EXTENSION)) {
                    images.add(file);
                }
            }
        }
        if (images.isEmpty()) {
            Toast.makeText(Gallery.this, "No images found", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            adapter = new GridViewAdapter(this, R.layout.cell_layout, images);
        } else {
            adapter = new GridViewAdapter(this, R.layout.cell_layout_targetapi9, images);
        }
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //TODO
            }
        });

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

            gridView.setMultiChoiceModeListener(new GridView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    if (android.os.Build.VERSION.SDK_INT < 11) {
                        return;
                    }
                    final int checkedCount = gridView.getCheckedItemCount();
                    mode.setTitle(checkedCount + " Selected");
                    adapter.toggleSelection(position);
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    if (android.os.Build.VERSION.SDK_INT < 11) {
                        return false;
                    }
                    mode.getMenuInflater().inflate(R.menu.grid_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete:
                            SparseBooleanArray selected = adapter.getSelectedPositions();
                            for (int i = (selected.size() - 1); i >= 0; i--) {
                                if (selected.valueAt(i)) {
                                    File selecteditem = adapter
                                            .getItem(selected.keyAt(i));
                                    adapter.remove(selecteditem);
                                }
                            }
                            if (android.os.Build.VERSION.SDK_INT < 11) {
                                return false;
                            }
                            mode.finish();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                    adapter.removeSelection();
                }
            });
        } else {
            //TODO
        }
        registerReceiver(receiver, new IntentFilter(Constant.NEW_IMAGE_CAPTURED));
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.NEW_IMAGE_CAPTURED.equals(intent.getAction())) {
                Log.i("biky", "new image captured");
                File file = new File(intent.getStringExtra(Constant.IMAGE_PATH));
                images.add(0, file);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
