package me.ycdev.android.trafficanalyzer;

import me.ycdev.android.trafficanalyzer.R;
import me.ycdev.android.trafficanalyzer.profile.AppProfile;
import me.ycdev.android.trafficanalyzer.stats.StatsSnapshot;
import me.ycdev.android.trafficanalyzer.utils.AppLogger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TableLayout;
import android.widget.TextView;

public class AppTrafficUsageActivity extends Activity {
    private static final String TAG = "AppTrafficUsageActivity";

    private static final String EXTRA_UID = "extra.uid";
    private static final String EXTRA_OLD_SNAPSHOT = "extra.oldsnap";
    private static final String EXTRA_NEW_SNAPSHOT = "extra.newsnap";

    private int mAppUid;
    private StatsSnapshot mOldSnapshot;
    private StatsSnapshot mNewSnapshot;

    private CheckBox mFgTrafficCheckBox;
    private CheckBox mBgTrafficCheckBox;
    private GridView mIfaceChoicesView;
    private TableLayout mTagsStatsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mAppUid = intent.getIntExtra(EXTRA_UID, -1);
        mOldSnapshot = intent.getParcelableExtra(EXTRA_OLD_SNAPSHOT);
        mNewSnapshot = intent.getParcelableExtra(EXTRA_NEW_SNAPSHOT);
        if (mAppUid == -1 || mOldSnapshot == null || mNewSnapshot == null) {
            AppLogger.w(TAG, "bad arguments, uid: " + mAppUid + ", snap1: " + mOldSnapshot
                    + ", snap2: " + mNewSnapshot);
            finish();
            return;
        }

        initViews();
        loadData();
    }

    private void initViews() {
        setContentView(R.layout.app_traffic_usage);

        TextView snap1View = (TextView) findViewById(R.id.snapshot_old);
        snap1View.setText(getString(R.string.usage_snapshot_old, mOldSnapshot.fileName));
        TextView snap2View = (TextView) findViewById(R.id.snapshot_new);
        snap2View.setText(getString(R.string.usage_snapshot_new, mNewSnapshot.fileName));

        mFgTrafficCheckBox = (CheckBox) findViewById(R.id.fg_traffic);
        mFgTrafficCheckBox.setChecked(true);
        mBgTrafficCheckBox = (CheckBox) findViewById(R.id.bg_traffic);
        mBgTrafficCheckBox.setChecked(true);

        mIfaceChoicesView = (GridView) findViewById(R.id.iface_choices);

        mTagsStatsView = (TableLayout) findViewById(R.id.tags_stats);
    }

    private void loadData() {
        final ProgressDialog dlg = new ProgressDialog(this);
        dlg.setMessage(getString(R.string.tips_load_data));
        dlg.setCancelable(false);
        dlg.show();

        mIfaceChoicesView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dlg.isShowing()) {
                    dlg.dismiss();
                }
            }
        }, 5000);
    }

    private void computeTrafficUsage() {
        
    }

    public static void showTrafficUsage(Context cxt, AppProfile appProfile,
            StatsSnapshot oldSnapshot, StatsSnapshot newSnapshot) {
        Intent intent = new Intent(cxt, AppTrafficUsageActivity.class);
        intent.putExtra(EXTRA_UID, appProfile.getAppUid());
        intent.putExtra(EXTRA_OLD_SNAPSHOT, oldSnapshot);
        intent.putExtra(EXTRA_NEW_SNAPSHOT, newSnapshot);
        cxt.startActivity(intent);
    }

}

