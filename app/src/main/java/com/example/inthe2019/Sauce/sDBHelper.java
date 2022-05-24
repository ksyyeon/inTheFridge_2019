package com.example.inthe2019.Sauce;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class sDBHelper extends SQLiteOpenHelper {

    public sDBHelper(Context context) {
        super(context, "sauce.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table sauce(_id integer primary key autoincrement, name text,"
                + "date date, chk text);");
        db.execSQL("insert into sauce values(null,'소금',null, '0');");
        db.execSQL("insert into sauce values(null,'간장',null, '0');");
        db.execSQL("insert into sauce values(null,'고춧가루',null, '0');");
        db.execSQL("insert into sauce values(null,'고추장',null, '0');");
        db.execSQL("insert into sauce values(null,'액젓',null, '0');");
        db.execSQL("insert into sauce values(null,'깨소금',null, '0');");
        db.execSQL("insert into sauce values(null,'녹말',null, '0');");
        db.execSQL("insert into sauce values(null,'다진마늘',null, '0');");
        db.execSQL("insert into sauce values(null,'생강',null, '0');");
        db.execSQL("insert into sauce values(null,'된장',null, '0');");
        db.execSQL("insert into sauce values(null,'청주',null, '0');");
        db.execSQL("insert into sauce values(null,'식용유',null, '0');");
        db.execSQL("insert into sauce values(null,'식초',null,'0');");
        db.execSQL("insert into sauce values(null,'참기름',null,'0');");
        db.execSQL("insert into sauce values(null,'후춧가루',null, '0');");
        db.execSQL("insert into sauce values(null,'설탕',null, '0');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists sauce;");
        onCreate(db);
    }
}