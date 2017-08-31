package com.bikrampandit.cliquick;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Rakesh Pandit on 8/29/2017.
 */

class GridViewAdapter extends ArrayAdapter<File> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<File> images = new ArrayList<>();
    private SparseBooleanArray selectedPositions;

    GridViewAdapter(Context context, int layoutResourceId, ArrayList<File> images) {
        super(context, layoutResourceId, images);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.images = images;
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

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Glide.with(context).load(images.get(position)).into(holder.image);
        return row;
    }

    private static class ViewHolder {
        ImageView image;
    }

    @Nullable
    @Override
    public File getItem(int position) {
        return images.get(position);
    }

    @Override
    public void remove(File image) {
        if (image.delete()) {
            images.remove(image);
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


    public int getSelectedCount() {
        return selectedPositions.size();
    }

    SparseBooleanArray getSelectedPositions() {
        return selectedPositions;
    }
}