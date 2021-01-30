package com.example.tars.navigation_1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import static android.provider.BaseColumns._ID;

public class MyDBHelper_Timeline extends SQLiteOpenHelper {

    public static final String TABLE_NAME="pipe";
    public static final String TITLE="title";
    public static final String PIPE_URL="url";
    public static final String DATE="date";
    public static final String CONTENT="content";

    private final static String DATABASE_NAME="demo_pipe.db";  //資料庫檔案名稱
    private final static int DATABASE_VERSION=3;   //資料庫版本

    private Context myContext = null;

    public MyDBHelper_Timeline(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String INIT_TABLE="create table "+TABLE_NAME+
                "("+_ID+" integer primary key autoincrement,"+TITLE+" char,"+PIPE_URL+" char,"+DATE+" char,"+CONTENT+" char)";
        db.execSQL(INIT_TABLE);

        Toast.makeText(myContext, "创建数据库", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String DROP_TABLE="drop table if exists "+TABLE_NAME;
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}

