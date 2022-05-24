package com.example.inthe2019.Sauce;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String getname = null;
        getname = intent.getStringExtra("name");
        int num = intent.getIntExtra("num", 0);

        Intent si = new Intent(context, AlarmService.class);
        si.putExtra("num", num);
        si.putExtra("name", getname);

        this.context.startService(si);
    }

}