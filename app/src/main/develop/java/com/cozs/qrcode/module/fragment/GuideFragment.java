package com.cozs.qrcode.module.fragment;

import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.SPUtils;
import com.cozs.qrcode.R;
import com.cozs.qrcode.databinding.FragmentGuideBinding;
import com.cozs.qrcode.module.constant.Constants;
import com.cozs.qrcode.module.library.ActivityUtils;
import com.cozs.qrcode.module.library.EventTracker;

public class GuideFragment extends BaseFragment implements View.OnClickListener {

    private FragmentGuideBinding guideBinding;

    @Override
    protected String getLogTag() {
        return "GuideFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        guideBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_guide, container, false);
        guideBinding.setOnClickListener(this);
        return guideBinding.getRoot();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        CharSequence privacyText = guideBinding.tvPrivacy.getText();
        SpannableStringBuilder ssbPrivacyPolicy = new SpannableStringBuilder(privacyText);
        ssbPrivacyPolicy.setSpan(new UnderlineSpan(), 0, privacyText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        guideBinding.tvPrivacy.setText(ssbPrivacyPolicy);

        CharSequence serviceText = guideBinding.tvService.getText();
        SpannableStringBuilder ssbTermsOfService = new SpannableStringBuilder(serviceText);
        ssbTermsOfService.setSpan(new UnderlineSpan(), 0, serviceText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        guideBinding.tvService.setText(ssbTermsOfService);

        guideBinding.rootGuide1.setVisibility(View.VISIBLE);
        guideBinding.rootGuide2.setVisibility(View.GONE);

        EventTracker.traceGuideViewShow();
    }

    @Override
    public void onClick(View v) {
        if (v == guideBinding.tvGuide1Next) {
            guideBinding.rootGuide1.setVisibility(View.GONE);
            guideBinding.rootGuide2.setVisibility(View.VISIBLE);
        } else if (v == guideBinding.tvGuide2Next) {
            SPUtils.getInstance().put(Constants.SPREF_AGREE_PRIVACY_POLICY, true);
            LoadFragment loadFragment = new LoadFragment();
            FragmentUtils.replace(activity.getSupportFragmentManager(), loadFragment, R.id.container_guide, loadFragment.getFMTag());
        } else if (v == guideBinding.tvPrivacy) {
            ActivityUtils.startUrlViewer(baseActivity, Constants.HTTP_PRIVACY_POLICY);
        } else if (v == guideBinding.tvService) {
            ActivityUtils.startUrlViewer(baseActivity, Constants.HTTP_TERMS_OF_SERVICE);
        }
    }

    @Override
    protected void handleMessage(@NonNull Message msg) {

    }
}
