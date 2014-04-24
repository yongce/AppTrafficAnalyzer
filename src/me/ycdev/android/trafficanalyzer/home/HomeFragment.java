package me.ycdev.android.trafficanalyzer.home;

import java.util.List;

import me.ycdev.android.trafficanalyzer.R;
import me.ycdev.android.trafficanalyzer.stats.SnapshotItem;
import me.ycdev.android.trafficanalyzer.stats.SnapshotsMgr;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private ListView mListView;
    private SnapshotsAdapter mAdapter;

    private List<SnapshotItem> mSnapshotItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View rootView = getView();
        mListView = (ListView) rootView.findViewById(R.id.list);
        mListView.setEmptyView(rootView.findViewById(R.id.empty_view));
        mAdapter = new SnapshotsAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        loadTrafficStatsSnapshot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addTrafficStatsSnapshot();
                return true;

            case R.id.clear:
                clearTrafficStatsSnapshots();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTrafficStatsSnapshot() {
        new MyTask(getString(R.string.tips_load_snapshot), new Runnable() {
            @Override
            public void run() {
                // force loading the snapshots
                SnapshotsMgr.getInstance(getActivity()).getAllSnapshots();
            }
        }).execute();
    }

    private void addTrafficStatsSnapshot() {
        new MyTask(getString(R.string.tips_create_snapshot), new Runnable() {
            @Override
            public void run() {
                SnapshotsMgr.getInstance(getActivity()).createSnapshot("");
            }
        }).execute();
    }

    private void clearTrafficStatsSnapshots() {
        new MyTask(getString(R.string.tips_clear_snapshots), new Runnable() {
            @Override
            public void run() {
                SnapshotsMgr.getInstance(getActivity()).clearAllSnapshots();
            }
        }).execute();
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        private String mTips;
        private Runnable mTargetTask;
        private ProgressDialog mDialog;

        public MyTask(String tips, Runnable task) {
            mTips = tips;
            mTargetTask = task;
        }

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage(mTips);
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mTargetTask.run();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mDialog.dismiss();
            mSnapshotItems = SnapshotsMgr.getInstance(getActivity()).getAllSnapshots();
            mAdapter.setData(mSnapshotItems);
        }
    }
}
