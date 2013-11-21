package org.devcon.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.devcon.android.util.AppConfig;

import static org.devcon.android.util.LogUtil.LOGD;
import static org.devcon.android.util.LogUtil.makeLogTag;

public class FeedbackActivity extends SherlockActivity {

    private static final String TAG = makeLogTag(FeedbackActivity.class);
    private String talkID, talkTime, talkTitle, talkSpeaker, talkDescription;
    private ActionBar ab;
    private RatingBar overall, useful, content, speaker;
    private Button submit;
    private EditText msg;
    private final RequestParams params = new RequestParams();
    private TelephonyManager tele;
    private PackageInfo manager;
    private final AsyncHttpClient client = new AsyncHttpClient();
    private ProgressDialog pg;
    private TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        pg = new ProgressDialog(getApplicationContext());
        tele = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        getData();

        ab = getSupportActionBar();
        ab.setTitle(talkTitle);
        ab.setSubtitle(getResources().getString(R.string.subtitle));
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setIcon(R.drawable.logo_copy);

        overall = (RatingBar) findViewById(R.id.ratingQ1);
        useful = (RatingBar) findViewById(R.id.ratingQ2);
        content = (RatingBar) findViewById(R.id.ratingQ3);
        speaker = (RatingBar) findViewById(R.id.ratingQ4);
        submit = (Button) findViewById(R.id.btnSubmit);
        msg = (EditText) findViewById(R.id.edSubmit);
        title = (TextView) findViewById(R.id.tvTalkTitle);

        title.setText(talkTitle);

        submit.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    manager = getPackageManager().getPackageInfo(
                            getPackageName(), 0);
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }

                params.put("android_id", Secure.getString(
                        getApplicationContext().getContentResolver(),
                        Secure.ANDROID_ID));
                Time now = new Time();
                now.setToNow();
                String versionName = manager.versionName;
                params.put(getResources().getString(R.string.overall_rating),
                        (int) overall.getRating());
                params.put(getResources().getString(R.string.useful),
                        (int) useful.getRating());
                params.put(getResources().getString(R.string.content),
                        (int) content.getRating());
                params.put(getResources().getString(R.string.speaker),
                        (int) speaker.getRating());

                // that sucks but it works
                params.put("anything", "" + msg.getText());
                LOGD(TAG, "anything " + msg.getText());

                params.put(getResources().getString(R.string.package_name),
                        getApplicationContext().getPackageName());
                params.put(getResources().getString(R.string.version_name),
                        versionName);
                params.put(getResources().getString(R.string.current_time),
                        now.toString());
                params.add(getResources().getString(R.string.network_name),
                        String.valueOf(tele.getNetworkType()));
                params.add(getResources().getString(R.string.phone_type),
                        String.valueOf(tele.getPhoneType()));
                params.put(getResources().getString(R.string.api),
                        android.os.Build.VERSION.SDK_INT);
                params.put(getResources().getString(R.string.model),
                        android.os.Build.MODEL);
                params.put(getResources().getString(R.string.vendor),
                        android.os.Build.MANUFACTURER);

                String url = AppConfig.FEEDBACKS_URL;

                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        pg = ProgressDialog.show(FeedbackActivity.this, "",
                                "Sending Feedback", true, true,
                                new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        // cancel the request on back press
                                        // I hope it works ^_^
                                        client.cancelRequests(
                                                getApplicationContext(), true);
                                    }
                                });
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          String content) {
                        LOGD(TAG, "Status code : " + statusCode);
                        if (statusCode == 201) {
                            pg.dismiss();
                            finish();
                        }
                    }
                });
            }
        });

    }

    public void getData() {
        if (getIntent().hasExtra("talkID"))
            talkID = getIntent().getExtras().getString("talkID");
        if (getIntent().hasExtra("talkTime"))
            talkTime = getIntent().getExtras().getString("talkTime");
        if (getIntent().hasExtra("talkTitle"))
            talkTitle = getIntent().getExtras().getString("talkTitle");
        if (getIntent().hasExtra("talkSpeaker"))
            talkSpeaker = getIntent().getExtras().getString("talkSpeaker");
        if (getIntent().hasExtra("talkDesc"))
            talkDescription = getIntent().getExtras().getString("talkDesc");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
