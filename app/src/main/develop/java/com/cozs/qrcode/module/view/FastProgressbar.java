package com.cozs.qrcode.module.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cozs.qrcode.R;
import com.cozs.qrcode.module.library.Logger;

public class FastProgressbar extends FrameLayout {

    private static final String TAG = "FastProgressbar";

    private static final int MSG_UPDATE_PROGRESS = 10001;
    private static final long INTERVAL_UPDATE_PROGRESS = 50L; // 100ms: 每隔 100ms 更新一次 seekbar，更新值为 step
    private static final long DURATION_FAST_PROGRESS = 500L; // 500ms: 0.5s 内快速走完进度

    private final SeekBar seekBar;

    private long duration; // 总时长 ms

    private float step; // progress/100ms :  每100ms的进度值

    private float fProgress;

    private ProgressHandler progressHandler;

    private FastProgressListener progressListener;

    public FastProgressbar(@NonNull Context context) {
        this(context, null);
    }

    public FastProgressbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FastProgressbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.widget_fast_progress_bar, this, true);
        seekBar = findViewById(R.id.seekbar);
        progressHandler = new ProgressHandler();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progressListener != null) {
                    progressListener.onProgressChanged(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setFastProgressListener(FastProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void startAutoProgress() {
        fProgress = 0; // reset
        if (seekBar != null) {
            seekBar.setProgress(0); // reset
        }
        computeNormalStep();
        progressHandler.sendProgressMsg(true);
    }

    public void stopAutoProgress() {
        progressHandler.removeProgressMsg();
    }

    public void triggerFastModel() {
        computeFastStep();
    }

    private void computeNormalStep() {
        if (duration <= 0) {
            Log.e(TAG, "--> computeNormalStep() failed !!! because of invalid duration=" + duration);
            return;
        }
        int remainProgress = seekBar.getMax() - seekBar.getProgress();
        // 100ms step => duration/100ms * step = remainProgress
        step = INTERVAL_UPDATE_PROGRESS * remainProgress * 1.0f / duration;
        Logger.e(TAG, "--> computeNormalStep()  step=" + step);
    }

    private void computeFastStep() {
        int remainProgress = seekBar.getMax() - seekBar.getProgress();
        // 100ms step => fastTime/100ms * step = remainProgress
        step = INTERVAL_UPDATE_PROGRESS * remainProgress * 1.0f / DURATION_FAST_PROGRESS;
        Logger.e(TAG, "--> computeFastStep()  step=" + step);
    }

    private void updateProgress() {
        if (seekBar == null) {
            return;
        }

        int maxProgress = seekBar.getMax();

        if (fProgress < maxProgress) {
            fProgress += step;
        }

        if (fProgress > maxProgress) {
            fProgress = maxProgress;
        }

//        Slog.e(TAG, "--> updateSplashProgress()  fProgress=" + fProgress);

        seekBar.setProgress((int) fProgress);

        if (fProgress < maxProgress) {
            progressHandler.sendProgressMsg(false);
        } else {
            progressHandler.removeProgressMsg();
            if (progressListener != null) {
                postDelayed(() -> {
                    progressListener.onProgressCompleted();
                }, 50); // 延迟 50ms 发出进度完成通知，保存 seekbar 显示完
            }
        }
    }

    private class ProgressHandler extends Handler {

        public ProgressHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_UPDATE_PROGRESS) {
                updateProgress();
            }
        }

        private void sendProgressMsg(boolean fromUser) {
            if (hasMessages(MSG_UPDATE_PROGRESS)) {
                removeMessages(MSG_UPDATE_PROGRESS);
            }
            sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, fromUser ? 0 : INTERVAL_UPDATE_PROGRESS);
        }

        private void removeProgressMsg() {
            if (hasMessages(MSG_UPDATE_PROGRESS)) {
                removeMessages(MSG_UPDATE_PROGRESS);
            }
        }
    }

    public interface FastProgressListener {
        void onProgressChanged(int progress);
        void onProgressCompleted();
    }
}
