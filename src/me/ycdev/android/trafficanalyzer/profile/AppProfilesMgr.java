package me.ycdev.android.trafficanalyzer.profile;

import java.util.List;

import android.content.Context;

public class AppProfilesMgr {
    private Context mAppContext;

    private static volatile AppProfilesMgr sInstance;

    private AppProfilesMgr(Context cxt) {
        mAppContext = cxt.getApplicationContext();
    }

    public static AppProfilesMgr getInstance(Context cxt) {
        if (sInstance == null) {
            synchronized (AppProfilesMgr.class) {
                if (sInstance == null) {
                    sInstance = new AppProfilesMgr(cxt);
                }
            }
        }
        return sInstance;
    }

    public List<AppProfile> getAllProfiles() {
        return null; // TODO
    }

    public AppProfile getProfile(int uid) {
        return new AppProfile(uid); // TODO
    }
}
