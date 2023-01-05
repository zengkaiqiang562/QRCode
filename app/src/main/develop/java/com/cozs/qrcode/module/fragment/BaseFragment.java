package com.cozs.qrcode.module.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.cozs.qrcode.module.activity.BaseActivity;
import com.cozs.qrcode.module.library.ActivityUtils;
import com.cozs.qrcode.module.library.Logger;

import java.lang.ref.WeakReference;

public abstract class BaseFragment extends Fragment {
    protected String TAG = "BaseFragment";

    protected boolean enableGoto = false;

    protected FragmentActivity activity;
    protected BaseActivity baseActivity;

    protected MainHandler<?> handler;

    protected boolean isViewCreated = false;

    /**
     * @return 返回 true 表示可以跳转UI，避免快速重复点击
     */
    protected boolean enableGoto() {
        return !enableGoto && (enableGoto = true);
    }

    protected abstract String getLogTag();
    protected abstract View getRoot(LayoutInflater inflater, ViewGroup container);
    protected abstract void initView(View view, Bundle savedInstanceState);
    protected abstract void handleMessage(@NonNull Message msg);

    public String getFMTag() {
        return getLogTag() + "#" + hashCode();
    }

    public boolean handleBackKey() {
        return false;
    }

    public boolean isViewCreated() {
        return isViewCreated;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = getLogTag();
        Logger.e(TAG, "--> onAttach() context=" + context);
        if (context instanceof FragmentActivity) {
            activity = (FragmentActivity) context;
        }
        if (activity instanceof BaseActivity) {
            baseActivity = (BaseActivity) activity;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.e(TAG, "--> onCreate()");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.e(TAG, "--> onCreateView()");
        if (activity == null) {
            activity = getActivity();
            if (activity instanceof BaseActivity) {
                baseActivity = (BaseActivity) activity;
            }
        }

        handler = new MainHandler<>(this);
        return getRoot(LayoutInflater.from(activity), container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.e(TAG, "--> onViewCreated()");
        initView(view, savedInstanceState);
        isViewCreated = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.e(TAG, "--> onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.e(TAG, "--> onResume()");
        enableGoto = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.e(TAG, "--> onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.e(TAG, "--> onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.e(TAG, "--> onDestroyView()");
        isViewCreated = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.e(TAG, "--> onDestroy()");
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        activity = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.e(TAG, "--> onDetach()");
    }

    protected static class MainHandler<T extends BaseFragment> extends Handler {

        protected WeakReference<T> wRefFragment;

        public MainHandler(T fragment) {
            super(Looper.getMainLooper());
            wRefFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            T fragment = wRefFragment.get();
            if (fragment == null || !ActivityUtils.checkContext(fragment.activity)) {
                return;
            }
            fragment.handleMessage(msg);
        }
    }
}
