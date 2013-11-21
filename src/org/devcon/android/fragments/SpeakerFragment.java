package org.devcon.android.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

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
import org.devcon.android.SpeakerDetailActivity;
import org.devcon.android.adapter.SpeakerAdapter;
import org.devcon.android.db.StorageUtil;
import org.devcon.android.objects.Speaker;
import org.devcon.android.util.AppConfig;
import org.devcon.android.util.LogUtil;
import org.devcon.android.util.MenuUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static org.devcon.android.util.LogUtil.LOGD;
import static org.devcon.android.util.LogUtil.makeLogTag;

public class SpeakerFragment extends SherlockFragment {

    private static final String TAG = makeLogTag(SpeakerFragment.class);
    ListView lv;
    private SpeakerAdapter adapter;
    private ArrayList<Speaker> speaker = new ArrayList<Speaker>();
    private ProgressBar mProgressBar;
    private AsyncHttpClient client = new AsyncHttpClient();
    View v, empty, useless;
    MixpanelAPI mMixpanel;
    private StorageUtil store;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        mMixpanel = MixpanelAPI.getInstance(getActivity(),
                getString(R.string.mix_panel_api));
        // Because there is "Ambiguous method call" bug
        LOGD(TAG, "Current Class " + ((Object) this).getClass().getSimpleName());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        store = StorageUtil.getInstance(getSherlockActivity());
        speaker = (ArrayList<Speaker>) store.ReadArrayListFromSD("speakers");

        v = inflater.inflate(R.layout.fragment_speaker, container, false);
        assert v != null;
        v.setBackgroundColor(Color.WHITE);
        lv = (ListView) v.findViewById(R.id.listview);
        empty = v.findViewById(R.id.empty);
        useless = v.findViewById(R.id.useless_view);
        mProgressBar = (ProgressBar) v.findViewById(R.id.pg);

        lv.setItemsCanFocus(false);
        lv.setVerticalFadingEdgeEnabled(true);

        // Don't forget the version check
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            lv.setFastScrollAlwaysVisible(true);
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view,
                                    int position, long id) {
                JSONObject properties = new JSONObject();

                LogUtil.LOGD(TAG, "You clicked at " + position);
                // TODO Shall we go to Speaker detail ?
                Intent i = new Intent(getActivity(),
                        SpeakerDetailActivity.class);
                i.putExtra("id", position);
                i.putExtra("sName", speaker.get(position).name);
                i.putExtra("sTitle", speaker.get(position).title);
                i.putExtra("sDesc", speaker.get(position).bio);
                i.putExtra("photoURL", speaker.get(position).photo);

                try {
                    properties.put("speaker_name", speaker.get(position).name);
                    mMixpanel.track("You click " + speaker.get(position).name,
                            properties);
                    mMixpanel.flush();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity(i);
            }
        });

        adapter = new SpeakerAdapter(getActivity(), speaker);
        if (speaker.size() == 0) {
            lv.setEmptyView(empty);
        } else {
            // set sticky headers for list items
            setStickyHeader();
            sF();
            lv.setFastScrollEnabled(true);
            lv.setAdapter(adapter);
        }

        return v;
    }

    // set sticky to where necessary
    private void setStickyHeader() {
        StickyListHeadersListView stickyList = (StickyListHeadersListView) v
                .findViewById(R.id.sticky_list);

        // I don't know why I have to do that
        stickyList
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parentAdapter,
                                            View view, int position, long id) {
                        // TODO Shall we go to Speaker detail ?
                        Intent i = new Intent(getActivity(),
                                SpeakerDetailActivity.class);
                        i.putExtra("id", position);
                        i.putExtra("sName", speaker.get(position).name);
                        i.putExtra("sTitle", speaker.get(position).title);
                        i.putExtra("sDesc", speaker.get(position).bio);
                        i.putExtra("photoURL", speaker.get(position).photo);
                        startActivity(i);
                    }
                });

        // stickyList.setVerticalScrollBarEnabled(false);
        stickyList.setDrawingListUnderStickyHeader(true);
        stickyList.setAreHeadersSticky(true);
        stickyList.setAdapter(adapter);
        LOGD(TAG, "i am sticky");
    }

    private class CustomComparator implements Comparator<Speaker> {
        @Override
        public int compare(Speaker s1, Speaker s2) {
            return s1.name.compareToIgnoreCase(s2.name);
        }
    }

    private void sF() {
        Collections.sort(speaker, new CustomComparator());
        adapter.notifyDataSetChanged();

    }

    // TODO maybe, if we add sorting.
    private class CustomComparator2 implements Comparator<Speaker> {
        @Override
        public int compare(Speaker s1, Speaker s2) {
            return s2.name.compareToIgnoreCase(s1.name);
        }
    }

    private void sF2() {
        Collections.sort(speaker, new CustomComparator2());
        setStickyHeader();
        adapter.notifyDataSetChanged();

    }

    void Async() {
        client.get(AppConfig.SPEAKERS_URL, null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                mProgressBar.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                useless.setVisibility(View.GONE);
                LOGD(TAG, "starting ..");
            }

            @Override
            public void onSuccess(int statusCode, JSONArray response) {
                super.onSuccess(statusCode, response);
                mProgressBar.setVisibility(View.GONE);
                try {
                    if (response != null) {
                        speaker.clear();
                        for (int i = 0; i < response.length(); i++) {
                            Speaker s = new Speaker();
                            JSONObject speakerObj = response.getJSONObject(i)
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
                                //s.schedule_id = speakerObj
                                //        .getInt(getString(R.string.json_schedule_ids));

                            }
                            speaker.add(s);
                        }
                        useless.setVisibility(View.VISIBLE);
                        adapter = new SpeakerAdapter(getSherlockActivity(), speaker);
                        adapter.notifyDataSetChanged();
                        sF();
                        setStickyHeader();
                        lv.setAdapter(adapter);
                        store.SaveArrayListToSD("speakers", speaker);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                super.onFailure(statusCode, headers, responseBody, error);
                LOGD(TAG, "second client response code " + statusCode + " Error " + error);
            }

        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getSherlockActivity().getSupportMenuInflater().inflate(R.menu.speakers_menu, menu);
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
