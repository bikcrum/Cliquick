package com.bikrampandit.cliquick;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.text.SimpleDateFormat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Rakesh Pandit on 9/1/2017.
 */

public class FullScreenViewAdapter extends PagerAdapter {
    private ArrayList<File> files;
    private Context context;

    FullScreenViewAdapter(Context context, ArrayList<File> files) {
        this.context = context;
        this.files = files;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView image;
        final VideoView video;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_view, container,
                false);

        image = (ImageView) view.findViewById(R.id.image);

        video = (VideoView) view.findViewById(R.id.video);

        ((TextView) ((Activity) context).findViewById(R.id.file_info))
                .setText(Util.getFileInfo(files.get(position)));

        if (files.get(position).getAbsolutePath().endsWith(Constant.IMAGE_FILE_EXTENSION)) {
            Glide.with(context).load(files.get(position)).into(image);
            video.setVisibility(View.GONE);
            ((Activity) context).findViewById(R.id.videocontroller).setVisibility(View.GONE);
        } else {
            ((Activity) context).findViewById(R.id.videocontroller).setVisibility(View.VISIBLE);

            video.setVideoPath(files.get(position).getPath());

            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    video.seekTo(100);
                }
            });

            final ImageButton playButton = (ImageButton) ((Activity) context).findViewById(R.id.play_btn);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (video.isPlaying()) {
                        video.pause();
                        playButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                    } else {
                        video.start();
                        playButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                    }
                }
            });
            image.setVisibility(View.GONE);
        }
        ((ViewPager) container).addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
