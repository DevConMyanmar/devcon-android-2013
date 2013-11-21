package org.devcon.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

import org.devcon.android.adapter.TestFragmentAdapter;
import org.devcon.android.db.StorageUtil;
import org.devcon.android.fragments.TalkDetailFragment;
import org.devcon.android.objects.Talk;

import java.util.ArrayList;
import java.util.List;


public class TalkDetailPager extends BasePagerFragment {
    private Intent intent;
    private Integer position;
    private StorageUtil store;
    private ArrayList<Talk> talks = new ArrayList<Talk>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_titles_bottom);


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);

        List<Fragment> fragments = getFragments();
        mAdapter = new TestFragmentAdapter(getSupportFragmentManager(), fragments);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(position);

//        mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
//        mIndicator.setViewPager(mPager);

    }


    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();

        store = StorageUtil.getInstance(this);
        talks = (ArrayList<Talk>) store.ReadArrayListFromSD("talks");

        for (int i = 0; i < talks.size(); i++) {
            String talkTime = talks.get(i).time;
            String talkTitle = talks.get(i).title;
            String speaker = "";
            String talkSpeaker = talks.get(i).speaker;
            if (talkSpeaker == null) {
            } else {
                speaker = talkSpeaker;
            }
            String talkDescription = talks.get(i).desc;
            String talkDate = talks.get(i).date;

            fList.add(TalkDetailFragment.newInstance("", talkTime, talkTitle, speaker, talkDescription, talkDate, i));

        }

        return fList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.menu_talk_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.feedback:
                Intent shareIntent =
                        new Intent(android.content.Intent.ACTION_SEND);
                //set the type
                shareIntent.setType("text/plain");

                //add a subject
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        "www.http://devconmyanmar.org/2013");

                //build the body of the message to be shared
                //add the message
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        talks.get(position).title + " " + "www.http://devconmyanmar.org/2013" + " " + "via DevCon 2013");

                //start the chooser for sharing
                startActivity(Intent.createChooser(shareIntent,
                        "Insert share chooser title here"));
            default:
        }
        return true;
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
