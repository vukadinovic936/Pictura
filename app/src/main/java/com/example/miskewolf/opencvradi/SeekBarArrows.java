
package com.example.miskewolf.opencvradi;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import java.util.Locale;
// Source: https://stackoverflow.com/questions/9787906/android-seekbar-solution/20654485
public class SeekBarArrows extends LinearLayout implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = Recognition.class.getSimpleName() + "/" + SeekBarArrows.class.getSimpleName();
    private SeekBar mSeekBar;
    private TextView mSeekBarValue;
    private float multiplier;
    private int nValues;
    private int min;

    public SeekBarArrows(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.seek_bar_arrows, this);

        TypedArray styledAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.SeekBarArrows);

        String mSeekBarText = styledAttrs.getString(R.styleable.SeekBarArrows_text);
        min = styledAttrs.getInt(R.styleable.SeekBarArrows_min, 0);
        float max = styledAttrs.getFloat(R.styleable.SeekBarArrows_max, 0);
        nValues = styledAttrs.getInt(R.styleable.SeekBarArrows_n_values, 0);

        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        ((TextView) findViewById(R.id.text)).setText(mSeekBarText);
        mSeekBarValue = (TextView) findViewById(R.id.value);

        setMax(max);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setProgress(mSeekBar.getMax() / 2);


        new OnArrowListener(findViewById(R.id.rightArrow), mSeekBar, true);
        new OnArrowListener(findViewById(R.id.leftArrow), mSeekBar, false);

        styledAttrs.recycle();
    }

    interface OnSeekBarArrowsChangeListener {
        void onProgressChanged(float progress);
    }

    private OnSeekBarArrowsChangeListener mOnSeekBarArrowsChangeListener;

    public void setOnSeekBarArrowsChangeListener(OnSeekBarArrowsChangeListener l) {
        mOnSeekBarArrowsChangeListener = l;
    }

    public float getProgress() {
        return mSeekBar.getProgress() * multiplier + min;
    }

    public void setProgress(float value) {
        mSeekBar.setProgress((int) (value / multiplier) - min);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
        setMax(getMax());
    }

    public float getMax() {
        return mSeekBar.getMax() * multiplier;
    }

    public void setMax(float max) {
        multiplier = max / (float)nValues;
        mSeekBar.setMax(nValues - min);
        Log.i(TAG, "Max: " + max + " Raw: " + mSeekBar.getMax() + " Multiplier: " + multiplier);
    }

    private String getFormat() {
        return multiplier <= 0.00001f ? "%.5f" : multiplier <= 0.0001f ? "%.4f" : multiplier <= 0.001f ? "%.3f" : multiplier <= 0.01f ? "%.2f" : multiplier <= 0.1f ? "%.1f" : "%.0f";
    }

    public String progressToString(float value) {
        String format = getFormat();
        return String.format(Locale.US, format, value);
    }

    public String progressToString(int value) {
        String format = getFormat();
        return String.format(Locale.US, format, (float)value * multiplier);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mSeekBarValue.setText(progressToString((progress + min))+"%");
        if (mOnSeekBarArrowsChangeListener != null)
            mOnSeekBarArrowsChangeListener.onProgressChanged((float)progress * multiplier + min);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private class OnArrowListener implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
        private Handler handler = new Handler();
        private static final int repeatInterval = 300;
        private SeekBar mSeekbar;
        private boolean positive;

        OnArrowListener(View v, SeekBar mSeekbar, boolean positive) {
            Button mButton = (Button) v;
            this.mSeekbar = mSeekbar;
            this.positive = positive;

            mButton.setOnClickListener(this);
            mButton.setOnLongClickListener(this);
            mButton.setOnTouchListener(this);
        }

        private int round10(int n) {
            return Math.round((float)n / 10.0f) * 10;
        }

        private void longClick() {
            mSeekbar.setProgress(round10(mSeekbar.getProgress() + (positive ? 10 : -10)) - min);
        }

        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                longClick();
                handler.postDelayed(this, repeatInterval);
            }
        };

        @Override
        public void onClick(View v) {

            mSeekbar.setProgress(mSeekbar.getProgress() + (positive ? 1 : -1));
        }

        @Override
        public boolean onLongClick(View v) {
            longClick();
            handler.postDelayed(runnable, repeatInterval);
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(runnable);
            }
            return false;
        }
    }
}
