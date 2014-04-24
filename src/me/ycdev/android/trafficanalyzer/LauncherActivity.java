package me.ycdev.android.trafficanalyzer;

import me.ycdev.android.trafficanalyzer.home.HomeFragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class LauncherActivity extends Activity {
    private static final String TAG = "LauncherActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.container, new HomeFragment());
        ft.commit();
    }

}
