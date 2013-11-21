package org.devcon.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;

import org.devcon.android.adapter.AppSectionsPagerAdapter;
import org.devcon.android.db.StorageUtil;
import org.devcon.android.objects.Speaker;
import org.devcon.android.objects.Talk;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.devcon.android.util.LogUtil.LOGD;
import static org.devcon.android.util.LogUtil.LOGI;
import static org.devcon.android.util.LogUtil.makeLogTag;

public class HomeActivity extends BaseActivity implements ActionBar.TabListener {

    ActionBar mActionBar;
    ViewPager mViewPager;
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    private boolean first_time;
    private static final String TAG = makeLogTag(HomeActivity.class);
    private ArrayList<Talk> talk = new ArrayList<Talk>();
    StorageUtil store;

    public void checkFirstTime() {
        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("my_first_time", true)) {
            LOGD(TAG, "First time");
            settings.edit().putBoolean("my_first_time", false).commit();
            first_time = true;
        } else {
            LOGD(TAG, "no");
            first_time = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        assert resolveInfo.activityInfo != null;
        String currentHomePackage = resolveInfo.activityInfo.packageName;
        LOGD(TAG, "launcher name: " + currentHomePackage);

        EasyTracker.getInstance(this).activityStart(this);  // Add this method.

        store = StorageUtil.getInstance(getApplicationContext());
        checkFirstTime();

        if (first_time) {
            loadSchedule();
            loadSpeaker();
        }

        Crashlytics.start(this);


        // TODO check if the current launcher is the default launcher or not
        // Removing the shortcut only works with default launcher
        // http://stackoverflow.com/q/12853443/2438460
        if (resolveInfo.activityInfo.packageName.equals("com.android.launcher")) {
            installShortCut();
        }

        final List<ResolveInfo> list = getPackageManager()
                .queryIntentActivities(intent, 0);

        LOGD(TAG, "running launcher: "
                + list.get(0).activityInfo.packageName);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(
                getSupportFragmentManager());

        mActionBar = getSupportActionBar();

        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setHomeButtonEnabled(false);

        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        mActionBar.setSelectedNavigationItem(position);
                    }
                });

        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            mActionBar.addTab(mActionBar.newTab()
                    .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }


    }

    public void installShortCut() {
        Intent shortcutIntent = new Intent(this, HomeActivity.class);

        Intent intent = shortcutIntent.setClassName(getApplicationContext().getPackageName(),
                "org.devcon.android.HomeActivity");
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "DevCon2013");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
        addIntent.putExtra("duplicate", false);

        addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        this.sendBroadcast(addIntent);
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        this.sendBroadcast(addIntent);

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
                              FragmentTransaction fragmentTransaction) {
        // To change body of implemented methods use File | Settings | File
        // Templates.
        LOGI(TAG, "Tab Position " + tab.getPosition());
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    // This fixes #1
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void loadSchedule() {
        String json = null;
        try {
            InputStream is = getAssets().open("schedules.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            LOGD(TAG, "json " + json);

            JSONArray scheduleObj = new JSONArray(json);
            for (int i = 0; i < scheduleObj.length(); i++) {
                Talk t = new Talk();
                JSONObject talkObj = scheduleObj.getJSONObject(i)
                        .getJSONObject(getString(R.string.json_schedule));
                if (talkObj != null) {
                    t._id = i;
                    t.sch_id = talkObj.getString(
                            getString(R.string.json_schedule_id))
                    ;
                    t.title = talkObj.getString(
                            getString(R.string.json_title))
                    ;
                    t.desc = talkObj.getString(
                            getString(R.string.json_description))
                    ;

                    // THIS IS SO WRONG BUT WHAT TO DO
                    t.speaker_id = i;

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
                    Date pDate;
                    String pubTime, pubDate;
                    pDate = df.parse(talkObj.getString(getString(R.string.json_session_time)));
                    pubTime = now(pDate);
                    pubDate = now_d(pDate);
                    t.time = pubTime;
                    t.date = pubDate;
                    LOGD(TAG, "dates " + pubDate);
                    if (talkObj.getJSONArray(getString(R.string.json_speakers_name)).get(0).toString() != null) {
                        t.speaker = talkObj.getJSONArray(getString(R.string.json_speakers_name)).get(0).toString();
                    } else {
                        t.speaker = "";
                    }
                }
                talk.add(t);
                store.SaveArrayListToSD("talks", talk);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void loadSpeaker() {
        String s_json = null;
        try {
            ArrayList<Speaker> speaker = new ArrayList<Speaker>();
            InputStream ss = getAssets().open("speakers.json");
            int ssize = ss.available();
            byte[] sbuffer = new byte[ssize];
            ss.read(sbuffer);
            ss.close();
            s_json = new String(sbuffer, "UTF-8");

            JSONArray speakerArray = new JSONArray(s_json);

            for (int i = 0; i < speakerArray.length(); i++) {
                Speaker s = new Speaker();
                JSONObject speakerObj = speakerArray.getJSONObject(i)
                        .getJSONObject(
                                getString(R.string.json_speaker));
                if (speakerObj != null) {
                    s._id = i;
                    s.name = speakerObj
                            .getString(getString(R.string.json_speaker_name));
                    s.title = speakerObj
                            .getString(getString(R.string.json_title));
                    s.bio = speakerObj
                            .getString(getString(R.string.json_biography));
                    s.photo = speakerObj
                            .getString(getString(R.string.json_speaker_photo));
                    s.schedule_id = i;

                }
                speaker.add(s);
            }
            store.SaveArrayListToSD("speakers", speaker);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public static String now_d(Date serverDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(serverDate);
        SimpleDateFormat df = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        return df.format(cal.getTime());
    }


    public static String now(Date serverDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(serverDate);
        SimpleDateFormat df = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        return df.format(cal.getTime());
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
