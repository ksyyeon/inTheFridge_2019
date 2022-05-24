package com.example.inthe2019.Sauce;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.inthe2019.R;

import java.util.ArrayList;

public class SauceAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<SauceMemo> al;

    public SauceAdapter(Context context, int layout, ArrayList<SauceMemo> al) {
        this.context = context;
        this.layout = layout;
        this.al = al;
    }

    @Override
    public int getCount() {
        return al.size( );
    }

    @Override
    public Object getItem(int i) {
        SauceMemo sm = new SauceMemo( al.get( i ).getName( ),
                al.get( i ).getDate( ),
                al.get( i ).getChk( ) );
        return sm;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null)
            view = View.inflate( context, layout, null );
        TextView tv1 = view.findViewById( R.id.sname );
        TextView tv2 = view.findViewById( R.id.sdate );
        LinearLayout ll = view.findViewById( R.id.ll );

        tv1.setText( al.get( i ).getName( ) );
        tv2.setText( al.get( i ).getDate( ) );

        if (al.get( i ).getChk( ).equals( "1" )) {
            ll.setBackgroundColor( Color.rgb( 250, 224, 212 ) );
            tv1.setTextColor( Color.BLACK );
            tv2.setTextColor( Color.BLACK );
        }
        else if (al.get( i ).getChk( ).equals( "2" )) {
            ll.setBackgroundColor( Color.BLACK );
            tv1.setTextColor( Color.YELLOW );
            tv2.setTextColor( Color.YELLOW );
        }
        else {
            ll.setBackgroundColor( Color.WHITE );
            tv1.setTextColor( Color.BLACK );
            tv2.setTextColor( Color.BLACK );
        }
        return view;
    }
}

