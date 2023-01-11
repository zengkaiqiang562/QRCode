package com.cozs.qrcode.module.fragment;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.cozs.qrcode.R;
import com.cozs.qrcode.databinding.FragmentCreateBinding;

public class CreateFragment extends BaseFragment {

    private FragmentCreateBinding binding;

    @Override
    protected String getLogTag() {
        return "CreateFragment";
    }

    @Override
    protected View getRoot(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create, container, false);
        return binding.getRoot();
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }

    @Override
    protected void handleMessage(@NonNull Message msg) {

    }
}
