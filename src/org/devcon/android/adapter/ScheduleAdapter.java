package org.devcon.android.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.devcon.android.R;
import org.devcon.android.objects.Talk;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class ScheduleAdapter extends ArrayAdapter<Talk> implements
        StickyListHeadersAdapter {

    private final Context mContext;
    private final ArrayList<Talk> mTalk;
    private final LayoutInflater mInflater;

    public ScheduleAdapter(Context context, ArrayList<Talk> data) {
        super(context, R.layout.row_talk, data);
        this.mContext = context;
        this.mTalk = data;
        this.mInflater = LayoutInflater.from(context);
    }

    class ViewHolder {
        public TextView tvTime;
        public TextView tvTitle;
        public TextView tvSpeaker;
    }

    class HeaderViewHolder {
        TextView text;
    }

    private ViewHolder getHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.tvTime = (TextView) v.findViewById(R.id.tv_time);
        holder.tvTitle = (TextView) v.findViewById(R.id.tv_title);
        holder.tvSpeaker = (TextView) v.findViewById(R.id.tv_speaker);
        return holder;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View vi = convertView;
        if (vi == null || vi.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.row_talk, null);
            holder = getHolder(vi);
            assert vi != null;
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        // set text inside the textview into center
        holder.tvTime.setGravity(Gravity.CENTER);

        // set the data here
        holder.tvTime.setText(mTalk.get(position).time);
        holder.tvTitle.setText(mTalk.get(position).title);
        holder.tvSpeaker.setText(mTalk.get(position).speaker);

        return vi;
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            assert convertView != null;
            holder.text = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        String headerChar = mTalk.get(position).date
                .toUpperCase();
        holder.text.setText(headerChar);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return mTalk.get(position).date.subSequence(0, 2).charAt(1);
    }

}
