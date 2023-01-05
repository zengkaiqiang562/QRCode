package com.cozs.qrcode.module.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.cozs.qrcode.R;
import com.cozs.qrcode.databinding.FragmentLoadBindingImpl;
import com.cozs.qrcode.module.activity.MainActivity;
import com.cozs.qrcode.module.advert.AdvertManager;
import com.cozs.qrcode.module.advert.AdvertResource;
import com.cozs.qrcode.module.constant.AdvertPlace;
import com.cozs.qrcode.module.constant.Constants;
import com.cozs.qrcode.module.event.EventManager;
import com.cozs.qrcode.module.event.IEventListener;
import com.cozs.qrcode.module.event.bean.AdvertEvent;
import com.cozs.qrcode.module.library.ActivityStackManager;
import com.cozs.qrcode.module.library.ConfigLibrary;
import com.cozs.qrcode.module.library.Logger;
import com.cozs.qrcode.module.library.EventTracker;
import com.cozs.qrcode.module.view.FastProgressbar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoadFragment extends BaseFragment implements IEventListener<AdvertEvent> {
    private static final int MSG_AD_PREPARE = 5001;

    private FragmentLoadBindingImpl loadBinding;

    private AdPrepareHandler adPrepareHandler;

    private AdvertResource<?> startAdvertResource;

    //    private boolean firstLaunch = false;
    private boolean adPrepared = false;

    private int timeAwayStart;

    @Override
    protected String getLogTag() {
        return "LoadFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        loadBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_load, container, false);
        return loadBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adPrepared = false;
        adPrepareHandler = new AdPrepareHandler();

        AdvertManager.getInstance().loadEveryAdvert();

        EventManager.register(this);

        // 在这里销毁掉之前 app 在前台时正展示的 start or int 类型的广告
        AdvertManager.destroyAdActivity();

        EventTracker.traceLoadViewShow();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBinding.fastProgressbar.startAutoProgress();
        timeAwayStart = 0;
        adPrepareHandler.sendAdPrepareMsg(Constants.DEFAULT_MIN_LAUNCH_DURATION);
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        int splashDuration = ConfigLibrary.getInstance().getSplashDuration();
        loadBinding.fastProgressbar.setDuration(splashDuration * 1000L);
        loadBinding.fastProgressbar.setFastProgressListener(new FastProgressbar.FastProgressListener() {
            @Override
            public void onProgressChanged(int progress) {
                String strProgress = progress + "%";
                loadBinding.textProgress.setText(strProgress);
                float translationX = loadBinding.fastProgressbar.getWidth() * progress / 100f;
                loadBinding.textProgress.setTranslationX(translationX);
            }

            @Override
            public void onProgressCompleted() {
                // 进度完成，总是会尝试去展示下 start
                tryShowStartAd();
            }
        });
    }

    private void continueEnjoyApp(long delay, boolean startAdShowed) { // delay: ms
//        if (!ActivityStackManager.getInstance().hasActivity(AboutUsActivity.class)
//                && !ActivityStackManager.getInstance().hasActivity(PhotoActivity.class)
//                && !ActivityStackManager.getInstance().hasActivity(PhotoDetailActivity.class)
//                && !ActivityStackManager.getInstance().hasActivity(ReportActivity.class)
//                && !ActivityStackManager.getInstance().hasActivity(VideoActivity.class)
//                && !ActivityStackManager.getInstance().hasActivity(VideoDetailActivity.class)) { // 冷启动 or 热启动时任务栈中无首页 or 首页在栈顶则跳首页
        if (true) { // TODO
            Logger.e(TAG, "--> continueEnjoyApp() start SCHomeActivity");
            handler.postDelayed(() -> {
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
            }, delay);
        }

        handler.postDelayed(() -> { // post 中结束页面，避免当任务栈为空时，切换页面会显示桌面
            activity.finish();
        }, delay);

        ConfigLibrary.getInstance().updateApp();
    }

    @Override
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(AdvertEvent event) {
        Logger.e(TAG, "--> onEventReceived()  event=" + event);
        switch (event.getType()) {
            case EVENT_AD_PREPARED:
                // 没显示过广告，收到 start 广告准备好的回调，且启动动画播放了 3s 以上，则停止动画，显示广告
                if (!adPrepared && isResumed() && timeAwayStart >= Constants.DEFAULT_MIN_LAUNCH_DURATION) {
                    // 收到 start 广告准备好的回调，且启动动画播放了 3s 以上，则停止动画，显示广告
                    Logger.e(TAG, "--> EVENT_AD_PREPARED show startAd");
                    adPrepareHandler.removeAdPrepareMsg();
                    loadBinding.fastProgressbar.triggerFastModel();
                }
                break;
            case EVENT_AD_DISMISS:
            case EVENT_AD_UNSHOW:
                if (startAdvertResource == event.getBean()) {
                    continueEnjoyApp(300, event.getType() == AdvertEvent.Type.EVENT_AD_DISMISS);
                }
                break;
            case EVENT_AD_SHOW:
                if (startAdvertResource == event.getBean()) {
                    EventTracker.traceStartAdShow();
                }
                break;
        }
    }

    @Override
    protected void handleMessage(@NonNull Message msg) {}

//    public void setFirstLaunch(boolean firstLaunch) {
//        this.firstLaunch = firstLaunch;
//    }

    @Override
    public void onStop() {
        super.onStop();
        loadBinding.fastProgressbar.stopAutoProgress();
        if (adPrepareHandler != null) {
            adPrepareHandler.removeAdPrepareMsg();
        }
        startAdvertResource = null; // 置为 null，当显示 start or int 类型的广告时退到后台，再进入时保证不因之前的 billProvider 而跳转界面
        AdvertManager.destroyAdActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adPrepared = false;
        EventManager.unregister(this);
    }

    private void tryShowStartAd() {
        startAdvertResource = AdvertManager.getInstance().retrieveAdvertResource(AdvertPlace.LAUNCH);
        boolean unshow = true;
        if (startAdvertResource != null) {
            unshow = !startAdvertResource.show(activity, null);
        }

        if (unshow) { // 如果广告显示失败，设置跳转标记，并跳下一页（如果显示成功，则在广告关闭的回调中跳下一页）
            Logger.d("无可展示的<" + AdvertPlace.LAUNCH.place + ">广告，跳转到首页");
            continueEnjoyApp(0, false);
        }
    }

    private class AdPrepareHandler extends Handler {

        public AdPrepareHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG_AD_PREPARE) {
                Logger.e(TAG, "--> MSG_AD_PREPARE delay=" + msg.arg1);
                checkAdPrepare(msg.arg1);
            }
        }

        private void sendAdPrepareMsg(int delay) {
            Logger.e(TAG, "--> sendAdPrepareMsg()  delay=" + delay);

            if (hasMessages(MSG_AD_PREPARE)) {
                removeMessages(MSG_AD_PREPARE);
            }

            Message message = obtainMessage();
            message.what = MSG_AD_PREPARE;
            message.arg1 = delay;
            sendMessageDelayed(message, delay * 1000L);
        }

        private void removeAdPrepareMsg() {
            if (hasMessages(MSG_AD_PREPARE)) {
                removeMessages(MSG_AD_PREPARE);
            }
        }

        private void checkAdPrepare(int delay) {

            timeAwayStart += delay;

            int splashDuration = ConfigLibrary.getInstance().getSplashDuration();

            adPrepared = AdvertManager.getInstance().checkAdvertPrepared();

            Logger.e(TAG, "--> checkAdPrepare()  splashDuration=" + splashDuration + " timeAwayStart=" + timeAwayStart + "  adPrepared=" + adPrepared);

            Logger.d("启动页的动画时长已经历<" + timeAwayStart + ">秒");

            if (adPrepared) { // 广告已准备好
//            if (true) { // TODO test 广告已准备好
                loadBinding.fastProgressbar.triggerFastModel();
                return;
            }

            // 没拉到继续等待
            delay = splashDuration - timeAwayStart;
            if (delay > 0) {
                adPrepareHandler.sendAdPrepareMsg(delay);
                return;
            }

            Logger.d("启动页的动画已达到最大时长");
        }
    }
}
