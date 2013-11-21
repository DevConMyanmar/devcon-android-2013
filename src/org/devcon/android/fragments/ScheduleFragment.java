package org.devcon.android.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.apache.http.Header;
import org.devcon.android.R;
import org.devcon.android.TalkDetailPager;
import org.devcon.android.adapter.ScheduleAdapter;
import org.devcon.android.db.StorageUtil;
import org.devcon.android.objects.Talk;
import org.devcon.android.util.AppConfig;
import org.devcon.android.util.MenuUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static org.devcon.android.util.LogUtil.LOGD;
import static org.devcon.android.util.LogUtil.makeLogTag;

public class ScheduleFragment extends SherlockFragment {

    private static final String TAG = makeLogTag(ScheduleFragment.class);
    private ScheduleAdapter adapter;
    private ArrayList<Talk> talk = new ArrayList<Talk>();
    private ArrayList<Talk> bktalk = new ArrayList<Talk>();
    private ProgressBar mProgressBar;
    private TextView timer;
    private final Handler mHandler = new Handler();
    private ViewGroup v;
    private AsyncHttpClient client = new AsyncHttpClient();
    StickyListHeadersListView stickyList;
    Talk t;
    MixpanelAPI mX;
    TextView empty;

    //Storage Utils
    StorageUtil store;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

        // TODO don't forget to add mix panel api key
        mX = MixpanelAPI.getInstance(getActivity(),
                getString(R.string.mix_panel_api));
        LOGD(TAG, "Current Class " + ((Object) this).getClass().getSimpleName());

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        store = StorageUtil.getInstance(getSherlockActivity());

        // NOTE from API Demo
        // View => Layout animation 2
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(50);
        set.addAnimation(animation);
        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(100);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(
                set, 0.5f);

        v = (ViewGroup) inflater.inflate(R.layout.fragment_talk, container,
                false);
        assert v != null;

        v.setBackgroundColor(Color.WHITE);

        empty = (TextView) v.findViewById(R.id.empty);
        mProgressBar = (ProgressBar) v.findViewById(R.id.pg);
        stickyList = (StickyListHeadersListView) v.findViewById(R.id.sticky_list);

        //Read Talks Array List from Private file storage
        talk = (ArrayList<Talk>) store.ReadArrayListFromSD("talks");
        bktalk = (ArrayList<Talk>) store.ReadArrayListFromSD("talks");
        adapter = new ScheduleAdapter(getActivity(), talk);

//        sF();
        setStickyHeader();

        refresh();
        return v;
    }


    private void sF() {
        Collections.sort(talk, new CustomComparator());
        adapter.notifyDataSetChanged();

    }

    // TODO maybe, if we add sorting.
    private class CustomComparator implements Comparator<Talk> {
        @Override
        public int compare(Talk t1, Talk t2) {
            return t1.date.compareToIgnoreCase(t2.date);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void refresh() {
        mHandler.removeCallbacksAndMessages(null);

        final long currentTimeMillis = AppConfig.getCurrentTime();
        if (currentTimeMillis < AppConfig.CONFERENCE_START_MILLIS) {
            setupBefore();
        } else if (currentTimeMillis > AppConfig.CONFERENCE_END_MILLIS) {
            setupAfter();
        } else {
            timer.setText(getResources().getString(R.string.thank_you));
        }
    }

    private final Runnable mCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            int remainingSec = (int) Math.max(0,
                    (AppConfig.CONFERENCE_START_MILLIS - AppConfig
                            .getCurrentTime()) / 1000);
            final boolean conferenceStarted = remainingSec == 0;

            if (conferenceStarted) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                }, 100);
                return;
            }

            final int secs = remainingSec % 86400;
            final int days = remainingSec / 86400;

            final String str;
            if (days == 0) {
                str = getResources().getString(
                        R.string.whats_on_countdown_title_0,
                        DateUtils.formatElapsedTime(secs));
            } else {
                str = getResources().getQuantityString(
                        R.plurals.whats_on_countdown_title, days, days,
                        DateUtils.formatElapsedTime(secs));
            }
            timer.setText(str);
            mHandler.postDelayed(mCountDownRunnable, 1000);
        }
    };

    private void setupBefore() {
        // Before conference, show countdown.
        timer = (TextView) v.findViewById(R.id.timer);
        mHandler.post(mCountDownRunnable);
    }

    private void setupAfter() {
        timer = (TextView) v.findViewById(R.id.timer);
    }

    // TODO sticky header for two days
    private void setStickyHeader() {

        // stickyList.setVerticalScrollBarEnabled(false);
        stickyList.setDrawingListUnderStickyHeader(true);
        stickyList.setAreHeadersSticky(true);
        stickyList.setAdapter(adapter);

        updateView();

        stickyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view,
                                    int position, long id) {
                JSONObject properties = new JSONObject();

               Intent i = new Intent(getActivity(), TalkDetailPager.class);
                i.putExtra("talkID", talk.get(position)._id);
                i.putExtra("talkTime", talk.get(position).time);
                i.putExtra("talkTitle", talk.get(position).title);
                i.putExtra("talkSpeaker", talk.get(position).speaker);
                i.putExtra("talkDesc", talk.get(position).desc);
                i.putExtra("talkDate", talk.get(position).date);
                i.putExtra("position", position);
                startActivity(i);
                try {
                    properties.put("talk_title", talk.get(position).title);
                    mX.track("You click " + talk.get(position).title,
                            properties);
                    mX.flush();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void Async() {
        client.get(AppConfig.TALKS_URL, null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                empty.setVisibility(TextView.GONE);
                stickyList.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, JSONArray response) {
                super.onSuccess(statusCode, response);
                mProgressBar.setVisibility(View.GONE);
                LOGD(TAG, "first client " + statusCode);
                if (response != null) {
                    talk.clear();
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
                                        "schedule_id")
                                ;
                                t.title = talkObj.getString(
                                        getString(R.string.json_title))
                                ;
                                t.desc = talkObj.getString(
                                        getString(R.string.json_description))
                                ;

                                // THIS IS SO WRONG BUT WHAT TO DO
                                t.speaker_id = i;

                                t.fav = isFav(t.sch_id);
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

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace(); //To change body of catch statement use File | Settings | File Templates.
                        }
                        talk.add(t);
                    }

                    adapter.notifyDataSetChanged();
                    bktalk.clear();
                    bktalk.addAll(talk);
                    store.SaveArrayListToSD("talks", talk);

                    updateView();
                }

                LOGD(TAG, "first client response code " + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                super.onFailure(statusCode, headers, responseBody,
                        error);
                LOGD(TAG, "first client response code " + statusCode + " Error "
                        + error);

                updateView();
            }
        });

    }

    private void updateView() {
//        sF();
        if (talk.size() == 0 || client.getTimeout() > 10000) {
            LOGD(TAG, "adapter " + adapter.getCount());
            LOGD(TAG, "talk " + talk.size());

            empty.setVisibility(TextView.VISIBLE);
            mProgressBar.setVisibility(ProgressBar.GONE);
            stickyList.setVisibility(StickyListHeadersListView.GONE);

        } else {
            empty.setVisibility(TextView.GONE);
            mProgressBar.setVisibility(ProgressBar.GONE);
            stickyList.setVisibility(StickyListHeadersListView.VISIBLE);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        getSherlockActivity().getSupportMenuInflater().inflate(R.menu.schedules_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Ouch. home icon is also a menu
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.refresh:
                Async();
                break;
            case R.id.action_about:
                MenuUtil.showAbout(getSherlockActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    private boolean isFav(String sch_id) {
        for (Talk aBktalk : bktalk) {
            if (aBktalk.sch_id.equalsIgnoreCase(sch_id) && aBktalk.fav) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(getSherlockActivity()).activityStart(getSherlockActivity());  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(getSherlockActivity()).activityStop(getSherlockActivity());  // Add this method.
    }
}
