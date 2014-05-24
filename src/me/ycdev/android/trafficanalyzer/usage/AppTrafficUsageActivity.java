package me.ycdev.android.trafficanalyzer.usage;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import me.ycdev.android.trafficanalyzer.R;
import me.ycdev.android.trafficanalyzer.profile.AppProfile;
import me.ycdev.android.trafficanalyzer.stats.StatsParseException;
import me.ycdev.android.trafficanalyzer.stats.StatsSnapshot;
import me.ycdev.android.trafficanalyzer.stats.TagTrafficStats;
import me.ycdev.android.trafficanalyzer.stats.UidTrafficStats;
import me.ycdev.android.trafficanalyzer.utils.AppLogger;
import me.ycdev.androidlib.base.WeakHandler;
import me.ycdev.androidlib.utils.DateTimeUtils;
import me.ycdev.androidlib.utils.IoUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AppTrafficUsageActivity extends Activity implements WeakHandler.MessageHandler,
        OnCheckedChangeListener {
    private static final boolean DEBUG = AppLogger.DEBUG;
    private static final String TAG = "AppTrafficUsageActivity";

    private static final String EXTRA_UID = "extra.uid";
    private static final String EXTRA_OLD_SNAPSHOT = "extra.oldsnap";
    private static final String EXTRA_NEW_SNAPSHOT = "extra.newsnap";

    private int mAppUid;
    private StatsSnapshot mOldSnapshot;
    private StatsSnapshot mNewSnapshot;

    private UidTrafficStats mOldUidStats;
    private UidTrafficStats mNewUidStats;
    private UidTrafficStats mUidUsage;

    private LayoutInflater mInflater;
    private CheckBox mFgTrafficCheckBox;
    private CheckBox mBgTrafficCheckBox;
    private GridView mIfaceChoicesView;
    private TrafficIfacesAdapter mIfacesAdapter;
    private ListView mTagsStatsView;
    private TrafficUsageAdapter mUsageAdapter;

    private Handler mHandler = new WeakHandler(this);

    @Override
    public void handleMessage(Message msg) {
        // nothing to do
    }

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
        setContentView(R.layout.usage_main);
        mInflater = LayoutInflater.from(this);

        TextView snap1View = (TextView) findViewById(R.id.snapshot_old);
        snap1View.setText(getString(R.string.usage_snapshot_old, mOldSnapshot.fileName));
        TextView snap2View = (TextView) findViewById(R.id.snapshot_new);
        snap2View.setText(getString(R.string.usage_snapshot_new, mNewSnapshot.fileName));

        mFgTrafficCheckBox = (CheckBox) findViewById(R.id.fg_traffic);
        mFgTrafficCheckBox.setChecked(true);
        mFgTrafficCheckBox.setOnCheckedChangeListener(this);
        mBgTrafficCheckBox = (CheckBox) findViewById(R.id.bg_traffic);
        mBgTrafficCheckBox.setChecked(true);
        mBgTrafficCheckBox.setOnCheckedChangeListener(this);

        mIfaceChoicesView = (GridView) findViewById(R.id.iface_choices);
        mIfacesAdapter = new TrafficIfacesAdapter(mInflater, this);
        mIfaceChoicesView.setAdapter(mIfacesAdapter);

        mTagsStatsView = (ListView) findViewById(R.id.tags_stats);
        mTagsStatsView.setEmptyView(findViewById(R.id.empty_no_usage));
        TrafficUsageAdapter.addHeaderView(mInflater, mTagsStatsView);
        mUsageAdapter = new TrafficUsageAdapter(mInflater);
        mTagsStatsView.setAdapter(mUsageAdapter);
    }

    private void loadData() {
        final ProgressDialog dlg = new ProgressDialog(this);
        dlg.setMessage(getString(R.string.tips_load_data));
        dlg.setCancelable(false);
        dlg.show();

        final Context context = getApplicationContext();
        new Thread() {
            @Override
            public void run() {
                boolean loadSuccess = false;
                try {
                    if (DEBUG) AppLogger.i(TAG, "parsing old snapshot..." + mOldSnapshot.fileName);
                    mOldUidStats = mOldSnapshot.parse(mAppUid);
                    if (DEBUG) AppLogger.i(TAG, "parsing new snapshot..." + mNewSnapshot.fileName);
                    mNewUidStats = mNewSnapshot.parse(mAppUid);
                    loadSuccess = true;
                } catch (IOException e) {
                    AppLogger.w(TAG, "failed to load uid stats", e);
                } catch (StatsParseException e) {
                    AppLogger.w(TAG, "failed to load uid stats", e);
                }

                if (loadSuccess) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dlg.setMessage(context.getString(R.string.tips_computing_usage_ongoing));
                        }
                    });
                    if (DEBUG) AppLogger.i(TAG, "computing usage...");
                    mUidUsage = mNewUidStats.subtract(mOldUidStats);
                }

                if (!loadSuccess) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, R.string.tips_computing_usage_failed,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    HashSet<String> uncheckedIfaces = new HashSet<String>();
                    uncheckedIfaces.add("lo");

                    List<String> ifaces = mUidUsage.getAllIfaces();
                    final List<TrafficIfaceItem> data = new ArrayList<TrafficIfaceItem>();
                    for (String iface : ifaces) {
                        boolean checked = !uncheckedIfaces.contains(iface);
                        data.add(new TrafficIfaceItem(iface, checked));
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mIfacesAdapter.setData(data);
                            computeTrafficUsage();
                        }
                    });
                }
                dlg.dismiss();
            }
        }.start();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        computeTrafficUsage();
    }

    private void computeTrafficUsage() {
        boolean fgSelected = mFgTrafficCheckBox.isChecked();
        boolean bgSelected = mBgTrafficCheckBox.isChecked();
        Set<String> ifacesSelected = mIfacesAdapter.getSelectedIfaces();

        List<TagTrafficStats> allTagsUsage = mUidUsage.getAllTagsStats();
        List<TrafficUsageItem> filteredUsage = new ArrayList<TrafficUsageItem>(allTagsUsage.size());
        TagTrafficStats totalItem = new TagTrafficStats();
        totalItem.iface = "*";

        for (TagTrafficStats item : allTagsUsage) {
            if (item.foreground && !fgSelected) {
                continue;
            }
            if (!item.foreground && !bgSelected) {
                continue;
            }
            if (!ifacesSelected.contains(item.iface)) {
                continue;
            }
            filteredUsage.add(new TrafficUsageItem(item, false));
            totalItem.sendBytes += item.sendBytes;
            totalItem.recvBytes += item.recvBytes;
        }

        if (filteredUsage.size() > 0) {
            filteredUsage.add(new TrafficUsageItem(totalItem, true));
        }

        mUsageAdapter.setData(filteredUsage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.traffic_usage_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.export) {
            exportTrafficUsage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exportTrafficUsage() {
        final List<TrafficUsageItem> trafficUsageItems = mUsageAdapter.getData();
        if (trafficUsageItems == null || trafficUsageItems.size() == 0) {
            Toast.makeText(this, R.string.tips_usage_export_no_data, Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog dlg = new ProgressDialog(this);
        dlg.setMessage(getString(R.string.tips_usage_exporting));
        dlg.setCancelable(false);
        dlg.show();

        final Context context = getApplicationContext();
        new Thread() {
            @Override
            public void run() {
                final String fileName = "usage-" + DateTimeUtils.generateFileName(System.currentTimeMillis());
                FileOutputStream fos = null;
                final boolean[] success = new boolean[] { false };
                try {
                    fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
                    // write header line
                    writer.append(getString(R.string.usage_traffic_column_iface))
                            .append('\t').append(getString(R.string.usage_traffic_column_tag))
                            .append('\t').append(getString(R.string.usage_traffic_column_fg))
                            .append('\t').append(getString(R.string.usage_traffic_column_send))
                            .append('\t').append(getString(R.string.usage_traffic_column_recv))
                            .append('\t').append(getString(R.string.usage_traffic_column_total))
                            .append('\n');
                    // write all data lines
                    for (TrafficUsageItem item : trafficUsageItems) {
                        if (item.isTotal) {
                            writer.append("*\t*\t*");
                        } else {
                            writer.append(item.usage.iface)
                                    .append('\t').append("0x" + Integer.toHexString(item.usage.tag))
                                    .append('\t').append(item.usage.foreground ? "Y" : "N");
                        }
                        writer.append("\t").append(String.valueOf(item.usage.sendBytes))
                                .append('\t').append(String.valueOf(item.usage.recvBytes))
                                .append('\t').append(String.valueOf(item.usage.sendBytes + item.usage.recvBytes))
                                .append('\n');
                    }
                    writer.flush();
                    success[0] = true;
                } catch (FileNotFoundException e) {
                    AppLogger.w(TAG, "failed to export traffic usage", e);
                } catch (IOException e) {
                    AppLogger.w(TAG, "failed to export traffic usage", e);
                } finally {
                    IoUtils.closeQuietly(fos);
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showExportFinishDialog(success[0],
                                context.getFileStreamPath(fileName).getAbsolutePath());
                    }
                });
                dlg.dismiss();
            }
        }.start();
    }

    private void showExportFinishDialog(boolean success, String filePath) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (success) {
            builder.setTitle(R.string.dlg_title_hint);
            builder.setMessage(getString(R.string.tips_usage_export_success, filePath));
        } else {
            builder.setTitle(R.string.dlg_title_warning);
            builder.setMessage(R.string.tips_usage_export_failure);
        }
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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

