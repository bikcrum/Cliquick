package com.bikrampandit.cliquick;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Rakesh Pandit on 8/29/2017.
 */

class GridViewAdapter extends ArrayAdapter<File> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<File> files = new ArrayList<>();
    private SparseBooleanArray selectedPositions;

    GridViewAdapter(Context context, int layoutResourceId, ArrayList<File> files) {
        super(context, layoutResourceId, files);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.files = files;
        selectedPositions = new SparseBooleanArray();
    }

    @Override
    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();

            holder.image = (ImageView) row.findViewById(R.id.img);
            holder.overLay = (ImageView)row.findViewById(R.id.overlay);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        if(files.get(position).getName().endsWith(Constant.VIDEO_FILE_EXTENSION)){
            holder.overLay.setVisibility(View.VISIBLE);
        }else{
            holder.overLay.setVisibility(View.GONE);
        }
        Glide.with(context).load(files.get(position)).into(holder.image);

        return row;
    }

    private static class ViewHolder {
        ImageView image;
        ImageView overLay;
    }

    @Nullable
    @Override
    public File getItem(int position) {
        return files.get(position);
    }

    @Override
    public void remove(File image) {
        if (image.delete()) {
            files.remove(image);
            notifyDataSetChanged();
        }

    }

    void removeSelection() {
        selectedPositions = new SparseBooleanArray();
        notifyDataSetChanged();
    }


    void selectView(int position, boolean value) {
        if (value) {
            selectedPositions.put(position, true);
        } else {
            selectedPositions.delete(position);
        }
        notifyDataSetChanged();
    }


    int getSelectedCount() {
        return selectedPositions.size();
    }

    SparseBooleanArray getSelectedPositions() {
        return selectedPositions;
    }
}