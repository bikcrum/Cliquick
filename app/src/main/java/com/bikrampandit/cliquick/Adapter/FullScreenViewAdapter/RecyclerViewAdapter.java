package com.bikrampandit.cliquick.Adapter.FullScreenViewAdapter;

/**
 * Created by Rakesh Pandit on 9/2/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bikrampandit.cliquick.R;
import com.bikrampandit.cliquick.Utility.Constant;
import com.bikrampandit.cliquick.Utility.SquareImageView;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

/**
 * An array adapter that knows how to render views when given File classes
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<File> files;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private SparseBooleanArray selectedPositions;


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemCLickListener) {
        this.onItemClickListener = onItemCLickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongCLickListener) {
        this.onItemLongClickListener = onItemLongCLickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private SquareImageView image;
        private SquareImageView overlay;

        MyViewHolder(View itemView) {
            super(itemView);
            image = (SquareImageView) itemView.findViewById(R.id.img);
            overlay = (SquareImageView) itemView.findViewById(R.id.overlay);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(view, getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onItemLongClickListener != null) {
                        return onItemLongClickListener.onItemLongClick(view, getAdapterPosition());
                    } else {
                        return false;
                    }
                }
            });
        }
    }

    public RecyclerViewAdapter(Context context, ArrayList<File> files) {
        this.context = context;
        this.files = files;
        selectedPositions = new SparseBooleanArray();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_grid, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (files.get(position).getName().endsWith(Constant.VIDEO_FILE_EXTENSION)) {
            holder.overlay.setVisibility(View.VISIBLE);
        } else {
            holder.overlay.setVisibility(View.GONE);
        }
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(metrics.widthPixels / 4, metrics.widthPixels /4);

        holder.itemView.setLayoutParams(layoutParams);

            holder.itemView.setPadding(1, 1, 1, 1);

        holder.itemView.setTag(files.get(position));

        if (selectedPositions.get(position)) {
            holder.itemView.setSelected(true);
            ((FrameLayout) holder.itemView).setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.whiteSelected)));
        } else {
            holder.itemView.setSelected(false);
            ((FrameLayout) holder.itemView).setForeground(context.getResources().getDrawable(R.drawable.selector));
        }
        Glide.with(context).load(files.get(position)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public File getItem(int position) {
        if (position >= 0 && position < files.size()) {
            return files.get(position);
        } else {
            return null;
        }
    }

    public void remove(File file, boolean notify) {
        if (file != null) {
            file.delete();
            files.remove(file);
            if (notify) notifyDataSetChanged();
        }
    }

    public void selectView(int position, boolean value) {
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

    public SparseBooleanArray getSelectedPositions() {
        return selectedPositions;
    }

    public void removeSelection() {
        selectedPositions = new SparseBooleanArray();
        notifyDataSetChanged();
    }


}