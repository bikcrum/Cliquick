package com.bikrampandit.cliquick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Gallery extends AppCompatActivity {
    private GridView gridView;
    public ArrayList<File> files = new ArrayList<>();
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
                Toast.makeText(Gallery.this, "No image found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(Gallery.this, "No image found", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            adapter = new GridViewAdapter(this, R.layout.cell_layout, files);
        } else {
            adapter = new GridViewAdapter(this, R.layout.cell_layout_targetapi9, files);
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
                    adapter.selectView(position, checked);
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
                            if (adapter.getCount() == 0) {
                                finish();
                            }
                            return true;
                        case R.id.select_all:
                            if (adapter.getSelectedCount() == adapter.getCount()) {
                                return true;//if all items are selected return
                            }
                            //select all items
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                gridView.setItemChecked(i, true);
                                adapter.selectView(i, true);
                            }
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
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                    PopupMenu popupMenu = new PopupMenu(Gallery.this, view);
                    popupMenu.getMenuInflater().inflate(R.menu.grid_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete:
                                    File selecteditem = adapter.getItem(position);
                                    adapter.remove(selecteditem);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    return true;
                }
            });
        }
        registerReceiver(receiver, new IntentFilter(Constant.NEW_FILE_CREATED));
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.NEW_FILE_CREATED.equals(intent.getAction())) {
                Log.i("biky", "new image captured or video recorded");
                File file = new File(intent.getStringExtra(Constant.FILE_PATH));
                if (file.getName().endsWith(Constant.IMAGE_FILE_EXTENSION) || file.getName().endsWith(Constant.VIDEO_FILE_EXTENSION)) {
                    files.add(0, file);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
