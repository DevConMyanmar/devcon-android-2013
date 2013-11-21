package org.devcon.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockActivity;
import com.crashlytics.android.Crashlytics;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.devcon.android.db.StorageUtil;
import org.devcon.android.objects.Speaker;
import org.devcon.android.objects.Talk;
import org.devcon.android.util.AppConfig;
import org.devcon.android.util.IsOnline;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.devcon.android.util.LogUtil.LOGD;
import static org.devcon.android.util.LogUtil.makeLogTag;

public class SplashScreenActivity extends SherlockActivity {

    private static final String TAG = makeLogTag(SplashScreenActivity.class);
    private AsyncHttpClient firstClient = new AsyncHttpClient();
    private AsyncHttpClient secondClient = new AsyncHttpClient();
    private ProgressBar mProgressBar;

    private boolean first_time;
    private int first, second;

    // Time in milliseconds
    private static final long splash_time = 1000;

    //Storage Util Example
    private StorageUtil store;
    ArrayList<Talk> arraylist_talks = new ArrayList<Talk>();
    ArrayList<Speaker> arraylist_speakers = new ArrayList<Speaker>();

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

    // Thread for splash screen
    Thread splashTread = new Thread() {
        @Override
        public void run() {
            try {
                Thread.sleep(splash_time);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Intent main = new Intent(getApplicationContext(),
                        HomeActivity.class);
                startActivity(main);
                finish();
            }
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        Crashlytics.start(this);

        //Init Storage Util
        store = StorageUtil.getInstance(this);

        // Let's try this
        // but it doesn't work for me :(
        // if (BuildConfig.DEBUG)
        // Ubertesters.initialize(getApplication());

        checkFirstTime();
        mProgressBar = (ProgressBar) findViewById(R.id.pg);

        LOGD(TAG, "first time ? " + first_time);
        if (first_time && IsOnline.isOnlineOrNot(getApplicationContext())) {
            Async();
        } else if (!IsOnline.isOnlineOrNot(getApplicationContext())) {
            // Load data
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            this.finish();
            // TODO do something man
        } else {
            splashTread.start();
            this.finish();
        }

    }

    void Async() {

        firstClient.get(AppConfig.TALKS_URL, null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        mProgressBar.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onSuccess(int statusCode, JSONArray response) {
                        super.onSuccess(statusCode, response);
                        LOGD(TAG, "first client " + statusCode);
                        first = statusCode;
                        if (response != null) {
                            for (int i = 0; i < response.length(); i++) {
                                Talk t = new Talk();
                                try {
                                    JSONObject talkObj = response
                                            .getJSONObject(i)
                                            .getJSONObject(
                                                    getString(R.string.json_schedule));
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
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
                                        Date pDate;
                                        String pubTime;
                                        pDate = df.parse(talkObj.getString(getString(R.string.json_session_time)));

                                        pubTime = now(pDate);

                                        t.time = pubTime;

                                        if (talkObj.getJSONArray(getString(R.string.json_speakers_name)).get(0).toString() != null) {
                                            t.speaker = talkObj.getJSONArray(getString(R.string.json_speakers_name)).get(0).toString();
                                        } else {
                                            t.speaker = "";
                                        }


                                        LOGD(TAG, t.speaker);
                                        // print out the title first
                                    }

                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }

                                //Add Object to arraylist
                                arraylist_talks.add(t);
                            }

                            store.SaveArrayListToSD("talks", arraylist_talks);
                        }

                        LOGD(TAG, "executing second client..");
                        secondClient.get(AppConfig.SPEAKERS_URL, null, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, JSONArray response) {
                                super.onSuccess(statusCode, response);
                                LOGD(TAG, "second client " + statusCode);
                                second = statusCode;

                                try {
                                    if (response != null) {
                                        for (int i = 0; i < response.length(); i++) {
                                            Speaker s = new Speaker();
                                            JSONObject speakerObj = response.getJSONObject(i)
                                                    .getJSONObject(
                                                            getString(R.string.json_speaker));
                                            if (speakerObj != null) {
                                                // TODO What do we do here
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
                                                //s.schedule_id = speakerObj
                                                //        .getInt(getString(R.string.json_schedule_ids));
                                                IsCompleteOrNot();
                                            }

                                            arraylist_speakers.add(s);
                                        }

                                        store.SaveArrayListToSD("speakers", arraylist_speakers);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers,
                                                  byte[] responseBody, Throwable error) {
                                super.onFailure(statusCode, headers, responseBody, error);
                                LOGD(TAG, "second client response code " + statusCode + " Error " + error);
                            }

                        });


                        LOGD(TAG, "first client response code " + statusCode);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          byte[] responseBody, Throwable error) {
                        super.onFailure(statusCode, headers, responseBody,
                                error);
                        LOGD(TAG, "first client response code " + statusCode + " Error "
                                + error);
                    }
                });
    }

    private void IsCompleteOrNot() {
        if (first == 200 && second == 200) {
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);

            finish();
        }
    }

    public static String now(Date serverDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(serverDate);
        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
        return df.format(cal.getTime());
    }
}
