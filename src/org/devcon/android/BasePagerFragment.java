package org.devcon.android;

import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.PageIndicator;

import org.devcon.android.adapter.TestFragmentAdapter;

public class BasePagerFragment extends SherlockFragmentActivity {

    TestFragmentAdapter mAdapter;
    ViewPager mPager;
}
