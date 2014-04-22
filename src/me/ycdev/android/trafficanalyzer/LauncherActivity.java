package me.ycdev.android.trafficanalyzer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class LauncherActivity extends Activity {
    private static final String TAG = "LauncherActivity";

    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mListView = (ListView) findViewById(R.id.list);
        mListView.setEmptyView(findViewById(R.id.empty_view));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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

    private void addTrafficStatsSnapshot() {
        Toast.makeText(this, "add snapshot", Toast.LENGTH_SHORT).show();
    }

    private void clearTrafficStatsSnapshots() {
        Toast.makeText(this, "clear snapshots", Toast.LENGTH_SHORT).show();
    }
}
