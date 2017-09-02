package com.bikrampandit.cliquick.Adapter;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bikrampandit.cliquick.Utility.Constant;
import com.bikrampandit.cliquick.R;
import com.bikrampandit.cliquick.Utility.TouchImageView;
import com.bikrampandit.cliquick.Utility.Util;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Rakesh Pandit on 9/1/2017.
 */

public class FullScreenViewAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private ArrayList<File> files;
    private Context context;
    public Handler handler = new Handler();
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public FullScreenViewAdapter(Context context, ArrayList<File> files) {
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
        //Log.i("biky", "instatiate item");
        TouchImageView image;
        final VideoView video;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view = inflater.inflate(R.layout.single_view, container,
                false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, position);
                }
            }
        });

        image = (TouchImageView) view.findViewById(R.id.image);

        video = (VideoView) view.findViewById(R.id.video);

        ((TextView) ((Activity) context).findViewById(R.id.file_info))
                .setText(Util.getFileInfo(files.get(position)));

        FrameLayout videoLayout = (FrameLayout) view.findViewById(R.id.video_layout);

        if (files.get(position).getAbsolutePath().endsWith(Constant.IMAGE_FILE_EXTENSION)) {
            Glide.with(context).load(files.get(position)).into(image);
            image.setVisibility(View.VISIBLE);
            videoLayout.setVisibility(View.GONE);
        } else {
            video.setVideoPath(files.get(position).getPath());
            image.setVisibility(View.GONE);
            videoLayout.setVisibility(View.VISIBLE);

            final ImageButton playButton = ((ImageButton) view.findViewById(R.id.play_btn));
            final TextView seektime = ((TextView) view.findViewById(R.id.seek_time));
            final SeekBar seekbar = ((SeekBar) view.findViewById(R.id.seek_time_bar));
            seekbar.setEnabled(false);
            final TextView totaltime = ((TextView) view.findViewById(R.id.total_time));

            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //   Log.i("biky","runnable running");
                    if (state == ViewPager.SCROLL_STATE_DRAGGING || state == ViewPager.SCROLL_STATE_SETTLING) {
                        video.pause();
                        playButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                        handler.removeCallbacks(this);
                        return;
                    }
                    seekbar.setProgress(video.getCurrentPosition());
                    seektime.setText(Util.getSeekTime(video.getCurrentPosition()));
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(this, 100);
                }
            };

            final View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (video.isPlaying()) {
                        video.pause();
                        playButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                        handler.removeCallbacks(runnable);
                    } else {
                        video.start();
                        playButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pause));
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, 100);
                    }
                }
            };

            final SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (b) {
                        video.seekTo(i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };

            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    video.seekTo(100);
                    totaltime.setText(Util.getSeekTime(video.getDuration()));
                    seektime.setText(Util.getSeekTime(video.getCurrentPosition()));
                    seekbar.setMax(video.getDuration());
                    seekbar.setProgress(video.getCurrentPosition());
                    playButton.setOnClickListener(clickListener);
                    seekbar.setOnSeekBarChangeListener(seekBarChangeListener);
                    seekbar.setEnabled(true);
                }
            });

            video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    playButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_play));
                    video.seekTo(100);
                    seekbar.setProgress(video.getCurrentPosition());
                    seektime.setText(Util.getSeekTime(video.getCurrentPosition()));
                    handler.removeCallbacks(runnable);
                }
            });
        }
        ((ViewPager) container).addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        handler.removeCallbacksAndMessages(null);
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Log.i("biky", "on page scrolled, position " + position);
    }

    @Override
    public void onPageSelected(final int position) {

    }

    private int state;

    @Override
    public void onPageScrollStateChanged(int state) {
        //Log.i("biky", "on page scroll state changed, state =  " + state);
        //     handler.removeCallbacksAndMessages(null);
        this.state = state;
    }
}
