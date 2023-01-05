package com.cozs.qrcode.module.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;

import com.cozs.qrcode.R;
import com.cozs.qrcode.module.activity.BaseActivity;

public abstract class BaseDialog extends DialogFragment {
    protected String TAG = "BaseDialog";
    protected Context context;
    protected BaseActivity activity;
    protected ViewGroup rootView;
    protected ViewGroup parentView;
    protected ViewGroup contentView;

    protected DialogListener listener;

    protected boolean isClickConfirm = false;
    protected boolean isClickCancel = false;

    protected void init() {}
    protected void setOnKeyListener(Dialog dialog) {}
    protected abstract String getLogTag();

    /**
     * Dialog的内容布局
     */
    protected abstract @LayoutRes int getLayoutId();

    /**
     * Dialog主题样式
     */
    protected @StyleRes int getDialogStyle() {
        return R.style.DialogBackgroundStyle;
    }

    /**
     * 点击 Dialog 以外区域能否隐藏 Dialog
     */
    protected boolean enableOutsideCancel() {
        return true;
    }

    public void resetClickState() {
        isClickCancel = false;
        isClickConfirm = false;
    }

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    protected final <T extends View> T findViewById(@IdRes int id) {
        return rootView.findViewById(id);
    }

    public String getFMTag() {
        return getLogTag() + "#" + hashCode();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        context = getActivity();

        if (context instanceof BaseActivity) {
            activity = (BaseActivity) context;
        }

        Dialog dialog = new Dialog(context, getDialogStyle());

        rootView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.dialog_root, null);
        View.inflate(context, getLayoutId(), rootView);
        parentView = (ViewGroup) rootView.getChildAt(0);
        contentView = (ViewGroup) parentView.getChildAt(0);

        rootView.setOnClickListener(v -> {
            if (enableOutsideCancel()) {
                dismiss();
            }
        });

        parentView.setOnClickListener(v -> {
            if (enableOutsideCancel()) {
                dismiss();
            }
        });

        contentView.setOnClickListener(v -> {
            /* do nothing*/
        });

        dialog.setContentView(rootView);
        dialog.setCancelable(enableOutsideCancel());
        dialog.setCanceledOnTouchOutside(enableOutsideCancel());
        setOnKeyListener(dialog);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.DialogAnimationStyle);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
//        params.gravity = Gravity.CENTER;
        window.setAttributes(params);

        init();

        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listener != null) {
            listener.onResume();
        }
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null && !isClickConfirm && !isClickCancel) {
            listener.onDimiss();
        }
    }

    public interface DialogListener {
        void onCancel();
        void onConfirm();
        void onResume();
        void onDimiss();
    }
}
