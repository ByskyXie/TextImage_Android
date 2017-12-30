package com.github.bysky.textimage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by asus on 2017/12/30.
 */

public class RecordDatabaseOpenHelper extends SQLiteOpenHelper {

    private static String CREATE_RECORD =
            "CREATE TABLE RECORD(" +
            "FILE_PATH TEXT PRIMARY KEY," +
            "FILE_NAME TEXT NOT NULL" +
            ")";
    RecordDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECORD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
