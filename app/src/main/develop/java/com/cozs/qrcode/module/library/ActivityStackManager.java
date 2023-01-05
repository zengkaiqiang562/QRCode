package com.cozs.qrcode.module.library;

import android.app.Activity;

import com.cozs.qrcode.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ActivityStackManager {

    private volatile static ActivityStackManager instance;

    private final List<WeakReference<Activity>> stack;

    private ActivityStackManager() {
        stack = new ArrayList<>();
    }

    public static ActivityStackManager getInstance() {
        if (instance == null) {
            synchronized (ActivityStackManager.class) {
                if (instance == null) {
                    instance = new ActivityStackManager();
                }
            }
        }
        return instance;
    }

    public synchronized void addActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        if (!hasActivity(activity)) {
            stack.add(new WeakReference<>(activity));
        }
    }

    public synchronized void removeActivity(Activity activity) {
        if (stack.size() <= 0) {
            return;
        }

        for (int i = stack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = stack.get(i);
            Activity tmpActivity = wRefActivity.get();
            if (tmpActivity == null || tmpActivity == activity) {
                wRefActivity.clear();
                stack.remove(i);
            }
        }
    }

    public synchronized Activity peekActivity(Class<? extends Activity> clazz) {
        if (stack.size() <= 0) {
            return null;
        }

        for (int i = stack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = stack.get(i);
            Activity activity = wRefActivity.get();
            if (activity == null) {
                wRefActivity.clear();
                stack.remove(i);
                continue;
            }
            if (activity.getClass() == clazz) {
                return activity;
            }
        }
        return null;
    }

    public synchronized Activity topActivity() {
        if (stack.size() <= 0) {
            return null;
        }
        return stack.get(stack.size() - 1).get();
    }

    public synchronized void finishActivity(Class<? extends Activity> clazz) {
        finishActivity(clazz, false);
    }

    /**
     * 结束指定类名的Activity
     */
    public synchronized void finishActivity(Class<? extends Activity> clazz, boolean _super) {
        if (stack.size() <= 0) {
            return;
        }

        for (int i = stack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = stack.get(i);
            Activity activity = wRefActivity.get();
            if (activity == null) {
                wRefActivity.clear();
                stack.remove(i);
                continue;
            }
            Class<? extends Activity> cls = activity.getClass();
            if (cls == clazz) {
                activity.finish();
                activity.overridePendingTransition(R.anim.right_in,
                        R.anim.right_out);

                wRefActivity.clear();
                stack.remove(i);
                continue;
            }

            if (_super) {
                Class<?> clsSuper = cls.getSuperclass();
                while (clsSuper != null) {
                    if (clsSuper == clazz) {
                        activity.finish();
                        activity.overridePendingTransition(R.anim.right_in,
                                R.anim.right_out);

                        wRefActivity.clear();
                        stack.remove(i);
                        break;
                    }
                    clsSuper = clsSuper.getSuperclass();
                }
            }
        }
    }

    public synchronized void finishAll() {
        if (stack.size() <= 0) {
            return;
        }

        for (int i = stack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = stack.get(i);
            Activity activity = wRefActivity.get();
            if (activity == null) {
                continue;
            }
            activity.finish();
            wRefActivity.clear();
            stack.remove(i);
        }
        stack.clear();
    }

    public synchronized boolean hasActivity(Activity activity) {
        if (activity == null || stack.size() <= 0) {
            return false;
        }

        for (int i = stack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = stack.get(i);
            Activity tmpActivity = wRefActivity.get();
            if (tmpActivity == null) {
                wRefActivity.clear();
                stack.remove(i);
                continue;
            }
            if (tmpActivity == activity) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean hasActivity(Class<? extends Activity> clazz) {
        if (stack.size() <= 0) {
            return false;
        }

        for (int i = stack.size() - 1; i >= 0; i--) {
            WeakReference<Activity> wRefActivity = stack.get(i);
            Activity activity = wRefActivity.get();
            if (activity == null) {
                wRefActivity.clear();
                stack.remove(i);
                continue;
            }
            if (activity.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }
}
