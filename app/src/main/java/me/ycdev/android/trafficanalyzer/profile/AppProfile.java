package me.ycdev.android.trafficanalyzer.profile;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.SparseArray;

import me.ycdev.android.trafficanalyzer.utils.AppLogger;

public class AppProfile {
    private static final String TAG = "AppProfile";

    private String mPkgName;
    private int mSavedAppUid;
    private String mSavedAppName;
    private SparseArray<String> mTagNames;

    public AppProfile(String pkgName, int appUid, String appName) {
        mPkgName = pkgName;
        mSavedAppUid = appUid;
        mSavedAppName = appName;
    }

    public String getPackageName() {
        return mPkgName;
    }

    public int getSavedAppUid() {
        return mSavedAppUid;
    }

    public int getRealAppUid(Context cxt) {
        try {
            ApplicationInfo ai = cxt.getPackageManager().getApplicationInfo(mPkgName, 0);
            return ai.uid;
        } catch (PackageManager.NameNotFoundException e) {
            AppLogger.w(TAG, "app was uninstalled: " + mPkgName, e);
        }
        return mSavedAppUid;
    }

    public String getSavedAppName() {
        return mSavedAppName;
    }

    public String getRealAppName(Context cxt) {
        try {
            ApplicationInfo ai = cxt.getPackageManager().getApplicationInfo(mPkgName, 0);
            return ai.loadLabel(cxt.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            AppLogger.w(TAG, "app was uninstalled: " + mPkgName, e);
        }
        return mSavedAppName;
    }

    /**
     * Get tag name for specified tag.
     * @param tag
     * @return null will be returned if no tag name
     */
    public String getTagName(int tag) {
        return mTagNames.get(tag);
    }

    public void exportToFile(String filePath) {
        // TODO
    }

    public static AppProfile loadFromFile(String filePath) {
        return null; // TODO
    }
}
