package com.bikrampandit.cliquick.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.bikrampandit.cliquick.Adapter.GridViewAdapter;
import com.bikrampandit.cliquick.R;
import com.bikrampandit.cliquick.Utility.Constant;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

public class GalleryGrid extends AppCompatActivity {
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
                Toast.makeText(GalleryGrid.this, "No image or video found taken with this app", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(GalleryGrid.this, "No image or video found taken with this app", Toast.LENGTH_SHORT).show();
            finish();
        }
        adapter = new GridViewAdapter(this, R.layout.single_grid, files);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                startActivityForResult(new Intent(GalleryGrid.this,
                        GalleryFullscreen.class).putExtra(Constant.CURRENT_FILE_SELECTION, position), Constant.REQUEST_DELETED_POSITIONS);
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
                    mode.setTitle(adapter.getSelectedCount() + " selected");
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
                            for (int i = selected.size() - 1; i >= 0; i--) {
                                if (selected.valueAt(i)) {
                                    int position = selected.keyAt(i);
                                    File selectedFile = adapter.getItem(position);
                                    adapter.remove(selectedFile, false);
                                }
                            }
                            adapter.notifyDataSetChanged();
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
                public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long id) {
                    final PopupMenu popupMenu = new PopupMenu(GalleryGrid.this, view);
                    popupMenu.getMenuInflater().inflate(R.menu.grid_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete:
                                    if (position >= 0 && position < files.size()) {
                                        if (files.get(position).delete()) {
                                            files.remove(position);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.NEW_FILE_CREATED.equals(intent.getAction())) {
                File file = new File(intent.getStringExtra(Constant.FILE_PATH));
                Log.i("biky", "new image captured or video recorded, file path = " + file.getAbsolutePath());
                if (file.getName().endsWith(Constant.IMAGE_FILE_EXTENSION) || file.getName().endsWith(Constant.VIDEO_FILE_EXTENSION)) {
                    if (files.contains(file)) {
                        return;
                    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("biky", "request code = " + requestCode + " result code = " + resultCode);
        if (requestCode == Constant.REQUEST_DELETED_POSITIONS && resultCode == RESULT_OK) {
            ArrayList<Integer> positions = data.getIntegerArrayListExtra(Constant.DELETED_POSITIONS);
            for (int position : positions) {
                if (position >= 0 && position < files.size()) {
                    adapter.remove(files.get(position), false);
                }
            }
            if (files.isEmpty()) {
                finish();
            }
            adapter.notifyDataSetChanged();
        }
    }
}
