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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

import org.devcon.android.R;
import org.devcon.android.TalkDetailPager;
import org.devcon.android.adapter.FavouriteAdapter;
import org.devcon.android.db.StorageUtil;
import org.devcon.android.objects.Talk;
import org.devcon.android.util.MenuUtil;

import java.util.ArrayList;

public class FavouriteFragment extends SherlockFragment {

    private ListView lv;
    private FavouriteAdapter adapter;
    private ArrayList<Talk> favourite = new ArrayList<Talk>();
    private ArrayList<Talk> talks = new ArrayList<Talk>();

    View v, empty;
    StorageUtil store;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // IMPORTANT
        this.setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        store = StorageUtil.getInstance(getSherlockActivity());

        adapter = new FavouriteAdapter(getActivity(), favourite);

        // main view
        v = inflater.inflate(R.layout.fragment_favourite, container, false);
        if (v != null)
            v.setBackgroundColor(Color.WHITE);

        // list view
        if (v != null)
            lv = (ListView) v.findViewById(R.id.fav_lv);
        lv.setFastScrollEnabled(true);

        // empty view
        empty = v.findViewById(R.id.emp_v);

        RelativeLayout atf = (RelativeLayout) v.findViewById(R.id.relative_atf);
        TextView favTxt = (TextView) atf.getChildAt(1);
        if (favTxt != null)
            favTxt.setText(getResources().getString(R.string.no_fav_add_some));

        // if there's no fav item, go to first tab
        atf.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if no item, go to first tab
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    if (getActivity().getActionBar() != null)
                        getActivity().getActionBar().setSelectedNavigationItem(0);
                } else {
                    getSherlockActivity().getSupportActionBar().setSelectedNavigationItem(0);
                }
            }
        });

        // set the empty view
        lv.setEmptyView(empty);

        lv.setAdapter(adapter);

        // on item click for list view
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentAdapter, View view,
                                    int position, long id) {
                Intent i = new Intent(getActivity(), TalkDetailPager.class);
                i.putExtra("talkID", favourite.get(position)._id);
                i.putExtra("talkTime", favourite.get(position).time);
                i.putExtra("talkTitle", favourite.get(position).title);
                i.putExtra("talkSpeaker", favourite.get(position).speaker);
                i.putExtra("talkDesc", favourite.get(position).desc);
                i.putExtra("position", favourite.get(position)._id);
                startActivity(i);
            }
        });

        loadFav();

        return v;
    }

    private void loadFav() {
        favourite.clear();
        talks.clear();
        talks = (ArrayList<Talk>) store.ReadArrayListFromSD("talks");
        for (int i = 0; i < talks.size(); i++) {
            if (talks.get(i).fav) {
                talks.get(i)._id = i;
                favourite.add(talks.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFav();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getSherlockActivity().getSupportMenuInflater().inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Ouch. home icon is also a menu
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.action_about:
                MenuUtil.showAbout(getSherlockActivity());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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

