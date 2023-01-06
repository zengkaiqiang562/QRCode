/*
 * Copyright (C) 2019 Jenly Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cozs.qrcode.module.zxing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cozs.qrcode.R;
import com.cozs.qrcode.module.activity.BaseActivity;
import com.cozs.qrcode.module.callback.PermissionCallback;
import com.cozs.qrcode.module.library.Logger;
import com.cozs.qrcode.module.zxing.camera.CameraManager;
import com.cozs.qrcode.module.zxing.camera.FrontLightMode;
import com.cozs.qrcode.module.zxing.result.ResultHandler;
import com.google.zxing.Result;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class CaptureFragment extends Fragment {

    private static final String TAG = "CaptureFragment";

    public static final String KEY_RESULT = Intents.Scan.RESULT;

    private View mRootView;

    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;
    private View ivTorch;

    private CaptureHelper mCaptureHelper;

    public static CaptureFragment newInstance() {

        Bundle args = new Bundle();

        CaptureFragment fragment = new CaptureFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        if(isContentView(layoutId)){
            mRootView = inflater.inflate(getLayoutId(),container,false);
        }
        initUI();
        return mRootView;
    }

    /**
     * 初始化
     */
    public void initUI(){
        surfaceView = mRootView.findViewById(getSurfaceViewId());
        int viewfinderViewId = getViewfinderViewId();
        if(viewfinderViewId != 0){
            viewfinderView = mRootView.findViewById(viewfinderViewId);
        }
        int ivTorchId = getIvTorchId();
        if(ivTorchId != 0){
            ivTorch = mRootView.findViewById(ivTorchId);
            ivTorch.setVisibility(View.INVISIBLE);
        }
        initCaptureHelper();
    }

    public void initCaptureHelper(){
        mCaptureHelper = new CaptureHelper(this,surfaceView,viewfinderView,ivTorch);
        mCaptureHelper.setOnCaptureCallback(new OnCaptureCallback() {
            @Override
            public void onResultCallback(Result result, ResultHandler resultHandler) {

            }
        });

        mCaptureHelper.playBeep(true)//播放音效
                .vibrate(true)//震动
                .supportVerticalCode(true)//支持扫垂直条码，建议有此需求时才使用。
//                .decodeFormats(DecodeFormatManager.QR_CODE_FORMATS)//设置只识别二维码会提升速度
//                .framingRectRatio(0.9f)//设置识别区域比例，范围建议在0.625 ~ 1.0之间。非全屏识别时才有效
//                .framingRectVerticalOffset(0)//设置识别区域垂直方向偏移量，非全屏识别时才有效
//                .framingRectHorizontalOffset(0)//设置识别区域水平方向偏移量，非全屏识别时才有效
                .frontLightMode(FrontLightMode.AUTO)//设置闪光灯模式
                .tooDarkLux(45f)//设置光线太暗时，自动触发开启闪光灯的照度值
                .brightEnoughLux(100f)//设置光线足够明亮时，自动触发关闭闪光灯的照度值
                .continuousScan(true)//是否连扫
                .supportLuminanceInvert(true);//是否支持识别反色码（黑白反色的码），增加识别率
    }

    /**
     * 返回true时会自动初始化{@link #mRootView}，返回为false时需自己去通过{@link #setRootView(View)}初始化{@link #mRootView}
     * @param layoutId
     * @return 默认返回true
     */
    public boolean isContentView(@LayoutRes int layoutId){
        return true;
    }

    /**
     * 布局id
     * @return
     */
    public int getLayoutId(){
        return R.layout.fragment_zxing_capture;
    }

    /**
     * {@link ViewfinderView} 的 id
     * @return 默认返回{@code R.id.viewfinderView}, 如果不需要扫码框可以返回0
     */
    public int getViewfinderViewId(){
        return R.id.viewfinderView;
    }

    /**
     * 预览界面{@link #surfaceView} 的id
     * @return
     */
    public int getSurfaceViewId(){
        return R.id.surfaceView;
    }

    /**
     * 获取 {@link #ivTorch} 的ID
     * @return  默认返回{@code R.id.ivTorch}, 如果不需要手电筒按钮可以返回0
     */
    public int getIvTorchId(){
        return R.id.ivTorch;
    }

    /**
     * Get {@link CaptureHelper}
     * @return {@link #mCaptureHelper}
     */
    public CaptureHelper getCaptureHelper(){
        return mCaptureHelper;
    }

    /**
     * Get {@link CameraManager} use {@link #getCaptureHelper()#getCameraManager()}
     * @return {@link #mCaptureHelper#getCameraManager()}
     */
    @Deprecated
    public CameraManager getCameraManager(){
        return mCaptureHelper.getCameraManager();
    }

    //--------------------------------------------

    public View getRootView() {
        return mRootView;
    }

    public void setRootView(View rootView) {
        this.mRootView = rootView;
    }


    //--------------------------------------------

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCaptureHelper.onCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkOrApplyCameraPermission(granted -> {
            if (granted) {
                mCaptureHelper.onResume();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        mCaptureHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCaptureHelper.onDestroy();
    }

    protected boolean checkCameraPermission() {
        boolean granted = XXPermissions.isGranted(getContext(), Permission.CAMERA);
        return granted;
    }

    private void applyCameraPermission(@NonNull PermissionCallback callback) {

        XXPermissions.with(this)
                // 申请多个权限
                .permission(Permission.CAMERA) // Manifest.permission.READ_EXTERNAL_STORAGE & Manifest.permission.WRITE_EXTERNAL_STORAGE
                // 设置权限请求拦截器（局部设置）
                //.interceptor(new PermissionInterceptor())
                // 设置不触发错误检测机制（局部设置）
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) { // all 表示是否获取到了所有请求的权限
                        Logger.e(TAG, "--> applyCameraPermission  onGranted()  permissions=" + permissions + "  all=" + all);
                        callback.onCompleted(all);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) { // never 为 true 表示拒绝，且不再询问；false 表示拒绝，下次还会询问
                        Logger.e(TAG, "--> applyCameraPermission  onDenied()  permissions=" + permissions + "  never=" + never);
                        if (never) {
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(getActivity(), permissions);
                        } else {
                            callback.onCompleted(false);
                        }
                    }
                });
    }

    protected void checkOrApplyCameraPermission(@NonNull PermissionCallback callback) {
        if (checkCameraPermission()) {
            callback.onCompleted(true);
            return;
        }

        applyCameraPermission(granted -> {
            callback.onCompleted(granted);
        });
    }
}
