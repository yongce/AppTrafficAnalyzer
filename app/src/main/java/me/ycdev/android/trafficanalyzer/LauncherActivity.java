package me.ycdev.android.trafficanalyzer;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class LauncherActivity extends Activity {
    private static final String TAG = "LauncherActivity";

    private static final String FRAG_HOME = "home";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        FragmentManager fragMgr = getFragmentManager();
        if (fragMgr.findFragmentByTag(FRAG_HOME) == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.container, new SnapshotsListFragment(), FRAG_HOME);
            ft.commit();
        }
    }

}
