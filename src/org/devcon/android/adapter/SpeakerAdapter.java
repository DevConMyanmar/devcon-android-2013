package org.devcon.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.devcon.android.R;
import org.devcon.android.objects.Speaker;
import org.devcon.android.util.AnimateFirstDisplayListener;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class SpeakerAdapter extends ArrayAdapter<Speaker> implements
        StickyListHeadersAdapter {

    private final Context mContext;
    private final ArrayList<Speaker> mSpeaker;
    private final LayoutInflater mInflater;
    private final ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private final ImageLoader imageLoader = ImageLoader.getInstance();

    public SpeakerAdapter(Context context, ArrayList<Speaker> data) {
        super(context, R.layout.row_speaker, data);
        this.mContext = context;
        this.mSpeaker = data;
        this.mInflater = LayoutInflater.from(context);
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));

    }

    class ViewHolder {
        public TextView tvName;
        public TextView tvEmail;
        public ImageView imgProfilePic;
    }

    class HeaderViewHolder {
        TextView text;
    }

    private ViewHolder getHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.tvName = (TextView) v.findViewById(R.id.tv_name);
        holder.tvEmail = (TextView) v.findViewById(R.id.tv_title);
        holder.imgProfilePic = (ImageView) v.findViewById(R.id.profile_pic);
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
            vi = inflater.inflate(R.layout.row_speaker, null);
            holder = getHolder(vi);
            assert vi != null;
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        String photoURL = mSpeaker.get(position).photo;

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.social_person)
                .showImageOnFail(R.drawable.social_person)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisc(true).build();

        imageLoader.displayImage(photoURL, holder.imgProfilePic, options,
                animateFirstListener);
        // NOTE You can use your own ImageLoadingListener too
        // https://github.com/nostra13/Android-Universal-Image-Loader#usage
        // In case of the first time loading and if the Internet is off,
        // show something here
        // if (!IsOnline.isOnlineOrNot(mContext))
        // imageLoader.displayImage("assets://logo.png",
        // holder.imgProfilePic);

        holder.tvName.setText(mSpeaker.get(position).name);
        holder.tvEmail.setText(mSpeaker.get(position).title);

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
        String headerChar = mSpeaker.get(position).name.substring(0, 1)
                .toUpperCase();

        holder.text.setText(headerChar);

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return mSpeaker.get(position).name.subSequence(0, 1).charAt(0);
    }
}
