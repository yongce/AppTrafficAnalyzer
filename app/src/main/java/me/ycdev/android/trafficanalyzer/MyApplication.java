package me.ycdev.android.trafficanalyzer;

import me.ycdev.android.trafficanalyzer.utils.AppLogger;

import android.app.Application;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        AppLogger.i(TAG, "app start..");
    }

}
