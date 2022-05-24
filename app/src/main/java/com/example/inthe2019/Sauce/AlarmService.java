package com.example.inthe2019.Sauce;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.inthe2019.R;

public class AlarmService extends Service {
    int n = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String getname = null;
        String name = "";
        getname = intent.getStringExtra( "name" );
        Log.d( "name", getname );
        Integer num = intent.getIntExtra( "num", 0 );

        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        Intent i = new Intent( AlarmService.this.getApplicationContext( ), sauce.class );
        i.addFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( AlarmService.this, n, i, PendingIntent.FLAG_UPDATE_CURRENT );

        Notification Notify = new Notification.Builder( getApplicationContext( ) )  //notification 객체 생성
                .setSmallIcon( R.drawable.icon )
                .setTicker( "Inthe냉장고" )
                .setContentTitle( getname + "의 유통기한을 확인해주세요." )
                .setContentIntent( pendingNotificationIntent )
                .setAutoCancel( true )
                .setOngoing( true )
                .build( );
        notificationManager.notify( n++, Notify );

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy( );
    }
}