package com.cozs.qrcode.module.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.FragmentUtils;
import com.cozs.qrcode.R;
import com.cozs.qrcode.databinding.ActivityGuideBinding;
import com.cozs.qrcode.module.fragment.BaseFragment;
import com.cozs.qrcode.module.fragment.LoadFragment;

public class GuideActivity extends BaseActivity {

    private ActivityGuideBinding guideBinding;

    @Override
    protected String getLogTag() {
        return "GuideActivity";
    }

    @Override
    protected void setContentView() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        guideBinding = DataBindingUtil.setContentView(this, R.layout.activity_guide);
    }

    @Override
    protected View getTopStub() {
        return guideBinding.stubStatusbar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        boolean agreed = SPUtils.getInstance().getBoolean(Constants.SPREF_AGREE_PRIVACY_POLICY, false);
//        boolean enableGuide = ConfigLibrary.getInstance().isEnableGuide();
//        Logger.e(TAG, "--> agreed=" + agreed + "  enableGuide=" + enableGuide);
//
//        boolean showGuide = !agreed && enableGuide;
//
//        if (showGuide) {
//            guideBinding.rootGuide.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//        } else {
//            guideBinding.rootGuide.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_common));
//        }
//
//        BaseFragment fragment = showGuide ? new GuideFragment() : new LoadFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        if (fragmentManager.findFragmentByTag(fragment.getFMTag()) == null) {
//            FragmentUtils.add(fragmentManager,
//                    fragment,
//                    R.id.container_guide,
//                    fragment.getFMTag());
//        }

        BaseFragment fragment = new LoadFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(fragment.getFMTag()) == null) {
            FragmentUtils.add(fragmentManager,
                    fragment,
                    R.id.container_guide,
                    fragment.getFMTag());
        }
    }

    @Override
    public void onBackPressed() {
        /* forbidden back button */
    }
}
