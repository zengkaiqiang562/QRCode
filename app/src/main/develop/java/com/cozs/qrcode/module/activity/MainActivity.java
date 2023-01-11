package com.cozs.qrcode.module.activity;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;
import android.view.View;

import com.cozs.qrcode.R;
import com.cozs.qrcode.databinding.ActivityMainBinding;
import com.cozs.qrcode.module.fragment.BaseFragment;
import com.cozs.qrcode.module.fragment.CreateFragment;
import com.cozs.qrcode.module.fragment.ScanFragment;
import com.cozs.qrcode.module.fragment.SettingsFragment;
import com.cozs.qrcode.module.library.ActivityUtils;
import com.cozs.qrcode.module.library.Logger;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final int[] sMainTitleResIds = new int[]{R.string.settings, R.string.scan, R.string.create};

    private ActivityMainBinding binding;

    private SettingsFragment settingsFragment;
    private ScanFragment scanFragment;
    private CreateFragment createFragment;

    private MainPagerAdapter pagerAdapter;

    @Override
    protected void setContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setOnClickListener(this);
    }

    @Override
    protected View getTopStub() {
        return binding.topStub;
    }

    @Override
    protected String getLogTag() {
        return "MainActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragments();
    }

    @Override
    public void onClick(View v) {
        BottomTab bottomTab = null;

        if (v == binding.ivHistory) {
            // TODO
            return;
        }

        if (v == binding.ivFavorite) {
            // TODO
            return;
        }

        if (v == binding.navSettings) {
            bottomTab = BottomTab.SETTINGS;
        } else if (v == binding.navScan) {
            bottomTab = BottomTab.SCAN;
        } else if (v == binding.navCreate) {
            bottomTab = BottomTab.CREATE;
        }

        if (bottomTab == null) {
            return;
        }

        int currentItem = binding.viewpager.getCurrentItem();

        if (currentItem == bottomTab.ordinal()) { // 重复点击同一个导航按钮
            return;
        }

        switch (bottomTab) {
            case SETTINGS:
                setBottomView(BottomTab.SETTINGS);
                break;
            case SCAN:
//                if (!AdConfig.isActiveExtraAd() || !showExtraAd()) {
//                    setBottomView(BottomTab.SCAN);
//                } else {
//                    // 如果展示了 extra ad，则在 ad 消失后再切 BottomTab.PROXY
//                    pendingSwitchProxyTab = true;
//                }
                // TODO
                setBottomView(BottomTab.SCAN);
                break;
            case CREATE:
                setBottomView(BottomTab.CREATE);
                break;
        }
    }

    private void initFragments() {
        resetBottom();
        ArrayList<BaseFragment> fragmentList = newFragment();
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragmentList);
        binding.viewpager.setAdapter(pagerAdapter);
        binding.viewpager.setCurrentItem(BottomTab.SCAN.ordinal());
        binding.navScan.setSelected(true);
        binding.viewpager.setOffscreenPageLimit(fragmentList.size() - 1);
        binding.tvMainTitle.setText(sMainTitleResIds[BottomTab.SCAN.ordinal()]);
    }

    private ArrayList<BaseFragment> newFragment() {
        ArrayList<BaseFragment> fragmentList = new ArrayList<>();
        settingsFragment = new SettingsFragment();
        scanFragment = new ScanFragment();
        createFragment = new CreateFragment();
        fragmentList.add(settingsFragment);
        fragmentList.add(scanFragment);
        fragmentList.add(createFragment);
        return fragmentList;
    }

    private void resetBottom() {
        binding.navSettings.setSelected(false);
        binding.navScan.setSelected(false);
        binding.navCreate.setSelected(false);
    }

    // 避免tab页之前的重复跳转，或在某个tab页中交互时不允许tab页跳转
    public void enableBottom(boolean enable) {
        binding.navSettings.setEnabled(enable);
        binding.navScan.setEnabled(enable);
        binding.navCreate.setEnabled(enable);
    }

    private void setBottomView(@NonNull BottomTab bottomTab) {
        if (!ActivityUtils.checkContext(this)) {
            return;
        }
        Logger.e(TAG, "--> setBottomView()  bottomTab=" + bottomTab);
        resetBottom();
        binding.tvMainTitle.setText(sMainTitleResIds[bottomTab.ordinal()]);
        binding.viewpager.setCurrentItem(bottomTab.ordinal(), false);
        switch (bottomTab) {
            case SETTINGS:
                binding.navSettings.setSelected(true);
                break;
            case SCAN:
                binding.navScan.setSelected(true);
                break;
            case CREATE:
                binding.navCreate.setSelected(true);
                break;
        }
    }

    public enum BottomTab {
        SETTINGS,
        SCAN,
        CREATE
    }

    public static class MainPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<BaseFragment> list;

        public MainPagerAdapter(FragmentManager fm, int behavior, ArrayList<BaseFragment> list) {
            super(fm, behavior);
            this.list=list;
        }

        public void setList(ArrayList<BaseFragment> list) {
            this.list = list;
        }

        @Override
        public BaseFragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }
}