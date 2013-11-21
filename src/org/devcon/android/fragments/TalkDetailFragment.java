package org.devcon.android.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.analytics.tracking.android.EasyTracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.devcon.android.R;
import org.devcon.android.db.StorageUtil;
import org.devcon.android.objects.Speaker;
import org.devcon.android.objects.Talk;
import org.devcon.android.util.AnimateFirstDisplayListener;

import java.util.ArrayList;

import static org.devcon.android.util.LogUtil.makeLogTag;

public class TalkDetailFragment extends SherlockFragment {

    private String talkID;
    public static final String TALKTIME = "talkTime";
    public static final String TALKTITLE = "talkTitle";
    public static final String TALKSPEAKER = "talkSpeaker";
    public static final String TALKDESC = "talkDescription";
    public static final String TALKPOSITION = "talkPosition";
    public static final String TALKDATE = "talkDate";

    private StorageUtil store;
    private Integer position;
    private ArrayList<Talk> talks = new ArrayList<Talk>();
    private ArrayList<Speaker> sp = new ArrayList<Speaker>();
    private TextView favTxt;
    private ImageView favImg;
    private static final String TAG = makeLogTag(TalkDetailFragment.class);

    private final ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private final ImageLoader imageLoader = ImageLoader.getInstance();

    public static TalkDetailFragment newInstance(String talkID, String talkTime,
                                                 String talkTitle, String talkSpeaker,
                                                 String talkDescription, String talkDate, Integer position) {
        TalkDetailFragment f = new TalkDetailFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString("TalkID", talkID);
        bdl.putString(TALKTIME, talkTime);
        bdl.putString(TALKTITLE, talkTitle);
        bdl.putString(TALKSPEAKER, talkSpeaker);
        bdl.putString(TALKDESC, talkDescription);
        bdl.putInt(TALKPOSITION, position);
        bdl.putString(TALKDATE, talkDate);
        f.setArguments(bdl);
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_talk_detail, container,
                false);

        ActionBar myActionBar = getSherlockActivity().getSupportActionBar();
        myActionBar.setTitle(getString(R.string.title));
        myActionBar.setSubtitle(getString(R.string.year));
        myActionBar.setDisplayHomeAsUpEnabled(true);
        myActionBar.setIcon(R.drawable.logo_copy);

        assert view != null;

        store = StorageUtil.getInstance(getActivity().getApplicationContext());
        talks = (ArrayList<Talk>) store.ReadArrayListFromSD("talks");

        position = getArguments().getInt(TALKPOSITION);

        ImageView profile = (ImageView) view.findViewById(R.id.imageView1);

        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvTime.setText(getArguments().get(TALKTIME).toString());

        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText(getArguments().get(TALKTITLE).toString());

        TextView tvSpeaker = (TextView) view.findViewById(R.id.tv_speaker);
        tvSpeaker.setText(getArguments().get(TALKSPEAKER).toString());

        TextView tvDate = (TextView) view.findViewById(R.id.tv_date);
        tvDate.setText(getArguments().get(TALKDATE).toString());

        TextView tvDescription = (TextView) view.findViewById(R.id.tv_desc);
        if (getArguments().get(TALKDESC).equals("")) {
            tvDescription.setText(getString(R.string.na));
            tvDescription.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        } else {
            tvDescription.setText(getArguments().get(TALKDESC).toString());
        }

        RelativeLayout atf = (RelativeLayout) view.findViewById(R.id.relative_atf);
        int count = atf.getChildCount();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.img_profile)
                .showImageOnFail(R.drawable.img_profile).cacheInMemory(true)
                .cacheOnDisc(true).build();
        imageLoader
                .displayImage(
                        getPhoto(talks.get(position).speaker),
                        profile, options, animateFirstListener);

        // alternatively you can access with findViewWithTag
        for (int i = 0; i < count; i++) {
            favImg = (ImageView) atf.getChildAt(0);
            favTxt = (TextView) atf.getChildAt(1);
        }

        if (talks.get(position).fav) {
            favImg.setImageResource(R.drawable.remove);
            favTxt.setText(getResources().getString(R.string.rm_from_fav));
        } else {
            favImg.setImageResource(R.drawable.add);
            favTxt.setText(getResources().getString(R.string.add_to_fav));
        }

        atf.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (talks.get(position).fav) {
                    talks.get(position).fav = false;
//                    CancelAlarm();
                    favImg.setImageResource(R.drawable.add);
                    favTxt.setText(getResources()
                            .getString(R.string.add_to_fav));
                } else {
                    talks.get(position).fav = true;
                    favImg.setImageResource(R.drawable.remove);
                    favTxt.setText(getResources().getString(
                            R.string.rm_from_fav));
                }

                store.SaveArrayListToSD("talks", talks);
            }
        });
        return view;
    }

    public String getPhoto(String name) {
        // clear the array list
        String photoURL = null;
        sp.clear();
        sp = (ArrayList<Speaker>) store.ReadArrayListFromSD("speakers");

        for (Speaker spk : sp) {
            if (spk.name.equalsIgnoreCase(name)) {
                photoURL = spk.photo;
            }
        }

        return photoURL;
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




