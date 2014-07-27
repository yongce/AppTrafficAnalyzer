package me.ycdev.android.trafficanalyzer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import me.ycdev.android.trafficanalyzer.profile.ProfilesListFragment;
import me.ycdev.android.trafficanalyzer.snaps.SnapshotsListFragment;

public class LauncherActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "LauncherActivity";

    private static final String FRAG_SNAPSHOTS = "snaps";
    private static final String FRAG_PROFILES = "profiles";

    private ActionBar mActionBar;
    private boolean mOptionsMenuShown = true;

    private Fragment mSnapshotsListFrag;
    private Fragment mProfilesListFrag;

    private DrawerLayout mDrawerLayoutView;
    private ActionBarDrawerToggle mDrawerToggle;

    private View mDrawerView;
    private View mDrawerSnapshotsView;
    private View mDrawerProfilesView;

    private String mCurFrag;
    private String mContentTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mActionBar = getActionBar();
        // for drawer toggle
        mActionBar.setDisplayHomeAsUpEnabled(true);

        loadSnapshotsList();

        initViews();
    }

    private void initViews() {
        mDrawerLayoutView = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerView = mDrawerLayoutView.findViewById(R.id.drawer);
        mDrawerSnapshotsView = mDrawerView.findViewById(R.id.snapshots);
        mDrawerSnapshotsView.setOnClickListener(this);
        mDrawerProfilesView = mDrawerView.findViewById(R.id.profiles);
        mDrawerProfilesView.setOnClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayoutView,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayoutView.setDrawerListener(new MyDrawerListener());

        mDrawerLayoutView.openDrawer(mDrawerView);
        mSnapshotsListFrag.setMenuVisibility(false);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        // The following line must be called, otherwise the provided drawer image will be used.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mOptionsMenuShown) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.home_menu, menu);
        }
        return mOptionsMenuShown;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
         * The action bar home/up action should open or close the drawer.
         * mDrawerToggle will take care of this.
         */
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == mDrawerSnapshotsView) {
            loadSnapshotsList();
            mDrawerLayoutView.closeDrawer(mDrawerView);
        } else if (v == mDrawerProfilesView) {
            loadProfilesList();
            mDrawerLayoutView.closeDrawer(mDrawerView);
        }
    }

    private void loadSnapshotsList() {
        FragmentManager fragMgr = getFragmentManager();
        FragmentTransaction ft = fragMgr.beginTransaction();
        if (mSnapshotsListFrag == null) {
            mSnapshotsListFrag = new SnapshotsListFragment();
            ft.add(R.id.frag_container, mSnapshotsListFrag, FRAG_SNAPSHOTS);
        } else {
            if (mSnapshotsListFrag.isHidden()) {
                ft.show(mSnapshotsListFrag);
            }
        }
        if (mProfilesListFrag != null && mProfilesListFrag.isVisible()) {
            ft.hide(mProfilesListFrag);
        }
        ft.commit();

        mCurFrag = FRAG_SNAPSHOTS;
        mContentTitle = getString(R.string.drawer_snapshots);
    }

    private void loadProfilesList() {
        FragmentManager fragMgr = getFragmentManager();
        FragmentTransaction ft = fragMgr.beginTransaction();
        if (mProfilesListFrag == null) {
            mProfilesListFrag = new ProfilesListFragment();
            ft.add(R.id.frag_container, mProfilesListFrag, FRAG_PROFILES);
        } else {
            if (mProfilesListFrag.isHidden()) {
                ft.show(mProfilesListFrag);
            }
        }
        if (mSnapshotsListFrag != null && mSnapshotsListFrag.isVisible()) {
            ft.hide(mSnapshotsListFrag);
        }
        ft.commit();

        mCurFrag = FRAG_PROFILES;
        mContentTitle = getString(R.string.drawer_profiles);
    }

    private class MyDrawerListener implements DrawerLayout.DrawerListener {
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            mDrawerToggle.onDrawerOpened(drawerView);
            mActionBar.setTitle(R.string.app_name);
            mOptionsMenuShown = false;
            invalidateOptionsMenu();
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            mDrawerToggle.onDrawerClosed(drawerView);
            mActionBar.setTitle(mContentTitle);
            mOptionsMenuShown = true;
            invalidateOptionsMenu();
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            mDrawerToggle.onDrawerStateChanged(newState);
        }
    }
}
