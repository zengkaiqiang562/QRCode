package com.cozs.qrcode.module.fragment;

import static com.rd.draw.data.Orientation.*;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.FragmentUtils;
import com.blankj.utilcode.util.SPUtils;
import com.cozs.qrcode.R;
import com.cozs.qrcode.databinding.FragmentGuideBinding;
import com.cozs.qrcode.module.activity.MainActivity;
import com.cozs.qrcode.module.constant.Constants;
import com.cozs.qrcode.module.library.ActivityUtils;
import com.cozs.qrcode.module.library.EventTracker;
import com.rd.animation.type.AnimationType;
import com.rd.draw.data.RtlMode;

import java.util.ArrayList;
import java.util.List;

public class GuideFragment extends BaseFragment implements View.OnClickListener {

    private static final int[] sIconResIds = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
    private static final int[] sTextResIds = new int[]{R.string.guide_prompt_1, R.string.guide_prompt_2, R.string.guide_prompt_3};

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

        initViewPager();

        EventTracker.traceGuideViewShow();
    }

    @Override
    public void onClick(View v) {
        if (v == guideBinding.tvButton) {
            int currentItem = guideBinding.viewpager.getCurrentItem();
            if (currentItem == sIconResIds.length - 1) {
                startApp();
            } else {
                guideBinding.viewpager.setCurrentItem(++currentItem);
            }
        } else if (v == guideBinding.tvPrivacy) {
            ActivityUtils.startUrlViewer(baseActivity, Constants.HTTP_PRIVACY_POLICY);
        } else if (v == guideBinding.tvService) {
            ActivityUtils.startUrlViewer(baseActivity, Constants.HTTP_TERMS_OF_SERVICE);
        }
    }

    private void startApp() {
        SPUtils.getInstance().put(Constants.SPREF_AGREE_PRIVACY_POLICY, true);
        if (shouldJumpMain()) {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
        }
        activity.finish();
    }

    private boolean shouldJumpMain() { // 冷启动 or 热启动时任务栈中无首页 or 首页在栈顶则跳首页
//        return !ActivityStackManager.getInstance().hasActivity(AboutUsActivity.class)
//                && !ActivityStackManager.getInstance().hasActivity(PhotoActivity.class)
//                && !ActivityStackManager.getInstance().hasActivity(PhotoDetailActivity.class)
//                && !ActivityStackManager.getInstance().hasActivity(ReportActivity.class)
//                && !ActivityStackManager.getInstance().hasActivity(VideoActivity.class)
//                && !ActivityStackManager.getInstance().hasActivity(VideoDetailActivity.class);
        return true; // TODO test
    }

    private void initViewPager() {
        guideBinding.indicator.setAnimationType(AnimationType.THIN_WORM);
        guideBinding.indicator.setOrientation(HORIZONTAL);
        guideBinding.indicator.setRtlMode(RtlMode.Off);
        guideBinding.indicator.setInteractiveAnimation(true);
        guideBinding.indicator.setAutoVisibility(true);
        guideBinding.indicator.setFadeOnIdle(false);

        guideBinding.viewpager.setAdapter(new GuideAdapter(createPageList()));

        guideBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                guideBinding.tvButton.setText(position == sIconResIds.length - 1 ? R.string.start : R.string.next_step);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        guideBinding.viewpager.setCurrentItem(0);
        guideBinding.tvButton.setText(R.string.next_step);
    }

    @NonNull
    private List<View> createPageList() {
        List<View> pageList = new ArrayList<>();
        for(int i = 0; i < sIconResIds.length; i++) {
            pageList.add(createPageView(sIconResIds[i], sTextResIds[i]));
        }
        return pageList;
    }

    @NonNull
    private View createPageView(int iconResId, int textResId) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_guide, null);
        ImageView ivIcon = view.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(iconResId);
        TextView tvPrompt = view.findViewById(R.id.tv_prompt);
        tvPrompt.setText(textResId);
        return view;
    }

    @Override
    protected void handleMessage(@NonNull Message msg) {

    }

    private static class GuideAdapter extends PagerAdapter {

        private final List<View> viewPages;

        public GuideAdapter(List<View> viewPages) {
            this.viewPages = viewPages;
        }

        @Override
        public int getCount() {
            return viewPages.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View pageView = viewPages.get(position);
            container.addView(pageView);
            return pageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
