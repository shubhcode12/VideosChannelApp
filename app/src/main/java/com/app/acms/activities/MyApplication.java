package com.app.acms.activities;

import android.app.Activity;
import android.app.Application;

public class MyApplication extends Application {

    Activity activity;
    public static final String TAG = MyApplication.class.getSimpleName();
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

}
