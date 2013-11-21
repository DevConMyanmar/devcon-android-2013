package org.devcon.android;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.devcon.android.db.StorageUtil;
import org.devcon.android.fragments.SpeakerFragment;
import org.devcon.android.objects.Talk;
import org.devcon.android.util.AnimateFirstDisplayListener;
import org.devcon.android.util.LogUtil;
import org.devcon.android.util.MenuUtil;

import java.util.ArrayList;

import static org.devcon.android.util.LogUtil.makeLogTag;

public class SpeakerDetailActivity extends BaseActivity {

    private static final String TAG = makeLogTag(SpeakerFragment.class);

    private String sName, sTitle, sBio, sEmail, photoURL;
    private int sId;
    private StorageUtil store;
    private ArrayList<Talk> talks = new ArrayList<Talk>();
    protected TextView tvName, tvTitle, tvDesc, tvEmail, tvTitleTalk, tvTitleTime;
    protected DisplayImageOptions options;
    private final ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private final ImageLoader imageLoader = ImageLoader.getInstance();
    protected ImageView imgProfilePic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_detail);

        store = StorageUtil.getInstance(this);
        talks = (ArrayList<Talk>) store.ReadArrayListFromSD("talks");

        // TODO extract out the strings
        if (getIntent().hasExtra("id"))
            sId = getIntent().getExtras().getInt("id");
        if (getIntent().hasExtra("sName"))
            sName = getIntent().getExtras().getString("sName");
        if (getIntent().hasExtra("sTitle"))
            sTitle = getIntent().getExtras().getString("sTitle");
        if (getIntent().hasExtra("sEmail"))
            sEmail = getIntent().getExtras().getString("sEmail");
        if (getIntent().hasExtra("sDesc"))
            sBio = getIntent().getExtras().getString("sDesc");
        if (getIntent().hasExtra("photoURL"))
            photoURL = getIntent().getExtras().getString("photoURL");


        getSupportActionBar().setTitle(sName);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        tvName = (TextView) findViewById(R.id.tvName);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
        tvTitleTalk = (TextView) findViewById(R.id.tv_title);
        tvTitleTime = (TextView) findViewById(R.id.tv_time);
        imgProfilePic = (ImageView) findViewById(R.id.imgProfile);

        tvName.setText(sName);
        tvTitle.setText(sTitle);
        tvEmail.setText(sEmail);
        tvDesc.setText(sBio);

        for (Talk talk : talks) {
            String speakername = talk.speaker;
            if (speakername.equalsIgnoreCase(sName)) {
                tvTitleTalk.setText(talk.title);
                tvTitleTime.setText(talk.time);
                break;
            } else {
                tvTitleTalk.setText(getString(R.string.na));
                tvTitleTime.setText(getString(R.string.na));

            }
        }

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.social_person)
                .showImageOnFail(R.drawable.social_person).cacheInMemory(true)
                .cacheOnDisc(true).build();

        imageLoader.displayImage(photoURL, imgProfilePic, options,
                animateFirstListener);
        LogUtil.LOGD(TAG, "sID is " + sId);
        // NOTE You can use your own ImageLoadingListener too
        // https://github.com/nostra13/Android-Universal-Image-Loader#usage
        // In case of the first time loading and if the internet is off,
        // show something here
        // imageLoader.displayImage("assets://logo.png", imgProfilePic);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Ouch. home icon is also a menu
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_about:
                MenuUtil.showAbout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
