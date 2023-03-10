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
import com.cozs.qrcode.module.zxing.result.AddressBookResultHandler;
import com.cozs.qrcode.module.zxing.result.CalendarResultHandler;
import com.cozs.qrcode.module.zxing.result.EmailAddressResultHandler;
import com.cozs.qrcode.module.zxing.result.GeoResultHandler;
import com.cozs.qrcode.module.zxing.result.ISBNResultHandler;
import com.cozs.qrcode.module.zxing.result.ProductResultHandler;
import com.cozs.qrcode.module.zxing.result.ResultHandler;
import com.cozs.qrcode.module.zxing.result.SMSResultHandler;
import com.cozs.qrcode.module.zxing.result.TelResultHandler;
import com.cozs.qrcode.module.zxing.result.TextResultHandler;
import com.cozs.qrcode.module.zxing.result.URIResultHandler;
import com.cozs.qrcode.module.zxing.result.WifiResultHandler;
import com.google.zxing.Result;
import com.google.zxing.client.result.AddressBookParsedResult;
import com.google.zxing.client.result.CalendarParsedResult;
import com.google.zxing.client.result.EmailAddressParsedResult;
import com.google.zxing.client.result.ExpandedProductParsedResult;
import com.google.zxing.client.result.GeoParsedResult;
import com.google.zxing.client.result.ISBNParsedResult;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ProductParsedResult;
import com.google.zxing.client.result.SMSParsedResult;
import com.google.zxing.client.result.TelParsedResult;
import com.google.zxing.client.result.TextParsedResult;
import com.google.zxing.client.result.URIParsedResult;
import com.google.zxing.client.result.WifiParsedResult;
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
     * ?????????
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
                handResult(result, resultHandler);
            }
        });

        mCaptureHelper.playBeep(true)//????????????
                .vibrate(true)//??????
                .supportVerticalCode(true)//?????????????????????????????????????????????????????????
//                .decodeFormats(DecodeFormatManager.QR_CODE_FORMATS)//???????????????????????????????????????
//                .framingRectRatio(0.9f)//??????????????????????????????????????????0.625 ~ 1.0????????????????????????????????????
//                .framingRectVerticalOffset(0)//?????????????????????????????????????????????????????????????????????
//                .framingRectHorizontalOffset(0)//?????????????????????????????????????????????????????????????????????
                .frontLightMode(FrontLightMode.AUTO)//?????????????????????
                .tooDarkLux(45f)//???????????????????????????????????????????????????????????????
                .brightEnoughLux(100f)//?????????????????????????????????????????????????????????????????????
                .continuousScan(true)//????????????
                .supportLuminanceInvert(true);//?????????????????????????????????????????????????????????????????????
    }

    /**
     * ??????true?????????????????????{@link #mRootView}????????????false?????????????????????{@link #setRootView(View)}?????????{@link #mRootView}
     * @param layoutId
     * @return ????????????true
     */
    public boolean isContentView(@LayoutRes int layoutId){
        return true;
    }

    /**
     * ??????id
     * @return
     */
    public int getLayoutId(){
        return R.layout.fragment_zxing_capture;
    }

    /**
     * {@link ViewfinderView} ??? id
     * @return ????????????{@code R.id.viewfinderView}, ????????????????????????????????????0
     */
    public int getViewfinderViewId(){
        return R.id.viewfinderView;
    }

    /**
     * ????????????{@link #surfaceView} ???id
     * @return
     */
    public int getSurfaceViewId(){
        return R.id.surfaceView;
    }

    /**
     * ?????? {@link #ivTorch} ???ID
     * @return  ????????????{@code R.id.ivTorch}, ??????????????????????????????????????????0
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
                // ??????????????????
                .permission(Permission.CAMERA) // Manifest.permission.READ_EXTERNAL_STORAGE & Manifest.permission.WRITE_EXTERNAL_STORAGE
                // ?????????????????????????????????????????????
                //.interceptor(new PermissionInterceptor())
                // ???????????????????????????????????????????????????
                //.unchecked()
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) { // all ?????????????????????????????????????????????
                        Logger.e(TAG, "--> applyCameraPermission  onGranted()  permissions=" + permissions + "  all=" + all);
                        callback.onCompleted(all);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) { // never ??? true ?????????????????????????????????false ?????????????????????????????????
                        Logger.e(TAG, "--> applyCameraPermission  onDenied()  permissions=" + permissions + "  never=" + never);
                        if (never) {
                            // ??????????????????????????????????????????????????????????????????
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

    private void handResult(@NonNull Result result, @NonNull ResultHandler resultHandler) {
        ParsedResult parsedResult = resultHandler.getResult();
        ParsedResultType parsedResultType = resultHandler.getType();
        switch (parsedResultType) {
            case ADDRESSBOOK:
                AddressBookResultHandler addressBookResultHandler = (AddressBookResultHandler) resultHandler;
                AddressBookParsedResult addressResult = (AddressBookParsedResult) parsedResult;
                break;
            case EMAIL_ADDRESS:
                EmailAddressResultHandler emailAddressResultHandler = (EmailAddressResultHandler) resultHandler;
                EmailAddressParsedResult emailResult = (EmailAddressParsedResult) parsedResult;
                break;
            case PRODUCT:
                ProductResultHandler productResultHandler = (ProductResultHandler) resultHandler;
                if (parsedResult instanceof ProductParsedResult) {
                    ProductParsedResult productParsedResult = (ProductParsedResult) parsedResult;
                }
                if (parsedResult instanceof ExpandedProductParsedResult) {
                    ExpandedProductParsedResult expandedProductParsedResult = (ExpandedProductParsedResult) parsedResult;
                }
                break;
            case URI:
                URIResultHandler uriResultHandler = (URIResultHandler) resultHandler;
                URIParsedResult uriParsedResult = (URIParsedResult) parsedResult;
                break;
            case WIFI:
                WifiResultHandler wifiResultHandler = (WifiResultHandler) resultHandler;
                WifiParsedResult wifiParsedResult = (WifiParsedResult) parsedResult;
                break;
            case GEO:
                /**
                 * ?????? {@link ResultHandler#getDirections(double, double)} ??????????????????????????? {@link GeoResultHandler#handleButtonPress(int)}
                 */
                GeoResultHandler geoResultHandler = (GeoResultHandler) resultHandler;
                GeoParsedResult geoParsedResult = (GeoParsedResult) parsedResult;
                break;
            case TEL:
                TelResultHandler telResultHandler = (TelResultHandler) resultHandler;
                TelParsedResult telParsedResult = (TelParsedResult) parsedResult;
                break;
            case SMS:
                SMSResultHandler smsResultHandler = (SMSResultHandler) resultHandler;
                SMSParsedResult smsParsedResult = (SMSParsedResult) parsedResult;
                break;
            case CALENDAR:
                CalendarResultHandler calendarResultHandler = (CalendarResultHandler) resultHandler;
                CalendarParsedResult calendarParsedResult = (CalendarParsedResult) parsedResult;
                break;
            case ISBN:
                ISBNResultHandler isbnResultHandler = (ISBNResultHandler) resultHandler;
                ISBNParsedResult isbnParsedResult = (ISBNParsedResult) parsedResult;
                break;
            default:
                TextResultHandler textResultHandler = (TextResultHandler) resultHandler;
                TextParsedResult textParsedResult = (TextParsedResult) parsedResult;
                break;
        }
    }
}
