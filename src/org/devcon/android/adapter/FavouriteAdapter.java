package org.devcon.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.devcon.android.R;
import org.devcon.android.db.StorageUtil;
import org.devcon.android.objects.Talk;

import java.util.ArrayList;

import static org.devcon.android.util.LogUtil.LOGD;
import static org.devcon.android.util.LogUtil.makeLogTag;

public class FavouriteAdapter extends ArrayAdapter<Talk> {
    private Context mContext;
    private ArrayList<Talk> mTalk = new ArrayList<Talk>();
    String talkID;

    private static final String TAG = makeLogTag(FavouriteAdapter.class);

    private StorageUtil store;
    private ArrayList<Talk> talks = new ArrayList<Talk>();

    public FavouriteAdapter(Context context, ArrayList<Talk> data) {
        super(context, R.layout.row_favourite, data);
        this.mContext = context;
        this.mTalk = data;

        store = StorageUtil.getInstance(mContext);
        talks = (ArrayList<Talk>) store.ReadArrayListFromSD("talks");
    }

    class ViewHolder {
        private TextView tvTime, tvTitle, tvSpeaker;
        private Button btnFav;
    }

    private ViewHolder getHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.tvTime = (TextView) v.findViewById(R.id.tv_time);
        holder.tvTitle = (TextView) v.findViewById(R.id.tv_title);
        holder.tvSpeaker = (TextView) v.findViewById(R.id.tv_speaker);
        holder.btnFav = (Button) v.findViewById(R.id.atf);
        return holder;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View vi = convertView;
        if (vi == null || vi.getTag() == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // don't forget to inflate the same layout
            vi = inflater.inflate(R.layout.row_favourite, null);
            holder = getHolder(vi);
            assert vi != null;
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        // set the data here
        holder.tvTime.setText(mTalk.get(position).time);
        holder.tvTitle.setText(mTalk.get(position).title);
        holder.tvSpeaker.setText(mTalk.get(position).speaker);
        holder.btnFav.setFocusableInTouchMode(false);
        holder.btnFav.setFocusable(false);

        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // say 'meow~' when you click it ;-)
                talks.get(mTalk.get(position)._id).fav = false;
                store.SaveArrayListToSD("talks", talks);
                LOGD(TAG, "id: " + talkID + " meow~");

                mTalk.remove(position);
                // don't forget to notify when some actions are done.
                notifyDataSetChanged();
            }
        });
        return vi;
    }
}