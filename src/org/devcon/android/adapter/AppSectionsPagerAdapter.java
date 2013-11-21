package org.devcon.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.devcon.android.fragments.FavouriteFragment;
import org.devcon.android.fragments.ScheduleFragment;
import org.devcon.android.fragments.SpeakerFragment;

import java.util.Locale;

public class AppSectionsPagerAdapter extends FragmentStatePagerAdapter {

    private static final String[] TITLES = new String[]{"Agenda", "Speakers",
            "My Schedule"};

    public static final int NUM_TITLES = TITLES.length;

    public AppSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new ScheduleFragment();
            case 1:
                return new SpeakerFragment();
            case 2:
                return new FavouriteFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position % NUM_TITLES].toUpperCase(Locale.getDefault());
    }
}
