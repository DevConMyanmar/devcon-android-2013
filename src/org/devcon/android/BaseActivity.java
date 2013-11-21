package org.devcon.android;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;

public class BaseActivity extends SherlockFragmentActivity {
    com.actionbarsherlock.app.ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionBar = getSupportActionBar();
        mActionBar.setTitle(getString(R.string.title));
        mActionBar.setSubtitle(getString(R.string.year));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setIcon(R.drawable.logo_copy);

        if (enableHomeIconActionBack()) {
            if (mActionBar != null)
                mActionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public boolean enableHomeIconActionBack() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

}
