package org.devcon.android.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created with IntelliJ IDEA.
 * User: indexer
 * Date: 11/18/13
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class AlarmReciever extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service1 = new Intent(context, MyAlarmService.class);
        context.startService(service1);
    }
}
