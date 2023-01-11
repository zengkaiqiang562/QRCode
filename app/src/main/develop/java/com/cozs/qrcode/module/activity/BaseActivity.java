package com.cozs.qrcode.module.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.ConvertUtils;
import com.cozs.qrcode.module.callback.PermissionCallback;
import com.cozs.qrcode.module.library.ActivityStackManager;
import com.cozs.qrcode.module.library.ActivityUtils;
import com.cozs.qrcode.module.library.Logger;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {
    protected boolean isPaused = false;
    protected boolean isResumed = false;
    protected boolean enableGoto = false;

    protected MainHandler<?> handler;

    protected String TAG = "BaseActivity";

    protected abstract void setContentView();
    protected abstract View getTopStub();
    protected abstract String getLogTag();

    protected void handleMessage(@NonNull Message msg) {}

    protected void onResume1st() {
        Logger.e(TAG, "--> onResume1st()");
    }

    /**
     * @return 返回 true 表示可以跳转UI，避免快速重复点击
     */
    public boolean enableGoto() {
        return !enableGoto && (enableGoto = true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getLogTag();
        Logger.e(TAG, "--> onCreate()");
        ImmersionBar.with(this)/*.transparentNavigationBar()*/.transparentStatusBar().statusBarDarkFont(false).init();
        setContentView();
        holdStatusbar(getTopStub());
        ActivityStackManager.getInstance().addActivity(this);
        handler = new MainHandler<>(this);
    }

    private void holdStatusbar(@Nullable View holder) {
        if (holder != null) {
            int statusBarHeight = ImmersionBar.getStatusBarHeight(this);
            int dp44 = ConvertUtils.dp2px(44);
            if (statusBarHeight < dp44) {
                statusBarHeight = dp44;
            }
            ViewGroup.LayoutParams layoutParams = holder.getLayoutParams();
            layoutParams.height = statusBarHeight;
            holder.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.e(TAG, "--> onNewIntent()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logger.e(TAG, "--> onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.e(TAG, "--> onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.e(TAG, "--> onResume()");
        isResumed = true;
        enableGoto = false;
        if (!isPaused) {
            onResume1st();
        }
        isPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.e(TAG, "--> onPause()");
        isResumed = false;
        isPaused = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.e(TAG, "--> onStop()");
        isResumed = false;
        isPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.e(TAG, "--> onDestroy()");
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        ActivityStackManager.getInstance().removeActivity(this);
    }

    protected static class MainHandler<T extends BaseActivity> extends Handler {

        protected WeakReference<T> wRefActivity;

        public MainHandler(T activity) {
            super(Looper.getMainLooper());
            wRefActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            T activity = wRefActivity.get();
            if (!ActivityUtils.checkContext(activity)) {
                return;
            }
            activity.handleMessage(msg);
        }
    }

    protected void checkOrApplyStoragePermission(@NonNull PermissionCallback callback) {
        if (checkStoragePermission()) {
            callback.onCompleted(true);
            return;
        }

        applyStoragePermission(granted -> {
            callback.onCompleted(granted);
        });
    }

    protected boolean checkStoragePermission() {
        boolean rwGranted = XXPermissions.isGranted(this, Permission.Group.STORAGE);
        Logger.e(TAG, "--> checkStoragePermission()  rwGranted=" + rwGranted);
        return rwGranted;
    }

    private void applyStoragePermission(@NonNull PermissionCallback callback) {

        XXPermissions.with(this)
                // 申请多个权限
                .permission(Permission.Group.STORAGE) // Manifest.permission.READ_EXTERNAL_STORAGE & Manifest.permission.WRITE_EXTERNAL_STORAGE
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) { // all 表示是否获取到了所有请求的权限
                        Logger.e(TAG, "--> applyStoragePermission  onGranted()  permissions=" + permissions + "  all=" + all);
                        callback.onCompleted(all);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) { // never 为 true 表示拒绝，且不再询问；false 表示拒绝，下次还会询问
                        Logger.e(TAG, "--> applyStoragePermission  onDenied()  permissions=" + permissions + "  never=" + never);
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(BaseActivity.this, permissions);
                        } else {
                            callback.onCompleted(false);
                        }
                    }
                });
    }
}
