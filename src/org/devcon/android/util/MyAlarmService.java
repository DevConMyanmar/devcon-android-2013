package org.devcon.android.util;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import org.devcon.android.R;


public class MyAlarmService extends Service

{
     private NotificationManager mManager;

     @Override
     public IBinder onBind(Intent arg0)
     {
       // TODO Auto-generated method stub
        return null;
     }

    @Override
    public void onCreate() 
    {
       // TODO Auto-generated method stub  
       super.onCreate();
    }

   @SuppressWarnings("static-access")
   @Override
   public void onStart(Intent intent, int startId)
   {
       super.onStart(intent, startId);

       PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
       Notification noti = new Notification.Builder(this)
               .setTicker("Schedule is so close")
               .setContentTitle("Session is so Near")
               .setContentText("at Room 205")
               .setSmallIcon(R.drawable.ic_launcher)
               .setContentIntent(pIntent).getNotification();
       noti.flags=Notification.FLAG_AUTO_CANCEL;
       NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
       notificationManager.notify(0, noti);

   }

    @Override
    public void onDestroy() 
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}