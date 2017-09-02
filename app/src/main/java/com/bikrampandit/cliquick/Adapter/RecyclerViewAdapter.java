package com.bikrampandit.cliquick.Adapter;

/**
 * Created by Rakesh Pandit on 9/2/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bikrampandit.cliquick.R;
import com.bikrampandit.cliquick.Utility.Constant;
import com.bikrampandit.cliquick.Utility.SquareImageView;
import com.bikrampandit.cliquick.Utility.SquareImageView1;
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

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    interface OnItemLongClickListener {
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
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (Build.VERSION.SDK_INT >= 11) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_recycler_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_recycler_item_targetapi9, parent, false);
        }
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

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(metrics.widthPixels / 4, metrics.widthPixels / 4);

        holder.image.setLayoutParams(layoutParams);
        holder.overlay.setLayoutParams(layoutParams);
        holder.image.setPadding(1, 1, 1, 1);
        holder.overlay.setPadding(1, 1, 1, 1);
        if (Build.VERSION.SDK_INT >= 11) {
            holder.image.setAlpha(0.8f);
            holder.overlay.setAlpha(0.8f);
        } else {
            //noinspection deprecation
            holder.image.setAlpha(200);
            //noinspection deprecation
            holder.overlay.setAlpha(200);

        }
        Glide.with(context).load(files.get(position)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}