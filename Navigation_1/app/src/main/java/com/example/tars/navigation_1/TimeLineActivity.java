package com.example.tars.navigation_1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.tars.navigation_1.ClubBox.Clubbox;
import com.example.tars.navigation_1.Time_Line.OrderStatus;
import com.example.tars.navigation_1.Time_Line.Orientation;
import com.example.tars.navigation_1.Time_Line.TimeLineModel;

import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;

public class TimeLineActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLineModel> mDataList = new ArrayList<>();
    private Orientation mOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);

        /*     測欄 & Toolbar     */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar8);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_timeline);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_timeline);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(3).setChecked(true);
        /*     測欄 & Toolbar     */

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mOrientation = Orientation.VERTICAL;

        initView();

        //mRecyclerView.smoothScrollToPosition(5);

    }

    private void initView() {
        openDatabase();
        setDataListItems();
        boolean mWithLinePadding = false;
        mTimeLineAdapter = new TimeLineAdapter(mDataList, mOrientation, mWithLinePadding);
        //mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mTimeLineAdapter);
    }

    int pipe_num = 0; //有多少活動

    private void setDataListItems(){

        pipe_num = query_data_num();
        query_data_id();
        int data_id;

        for(int i = pipe_num; i >= 1; i--){
            data_id = pipe_ids[i-1];
            mDataList.add(new TimeLineModel(query_data_imgurl(data_id), query_data_title(data_id), query_data_des(data_id), query_data_date(data_id), OrderStatus.COMPLETED));
        }
        /*for(int i = 1; i <= pipe_num; i++){
            data_id = pipe_ids[i-1];
            mDataList.add(new TimeLineModel(query_data_imgurl(data_id), query_data_title(data_id), query_data_des(data_id), query_data_date(data_id), OrderStatus.COMPLETED));
        }*/

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_timeline);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent();
            intent.setClass(TimeLineActivity.this, Homepage.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_left_in, R.anim.right_left_out);
            finish();
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent();
            intent.setClass(TimeLineActivity.this, Clubs_Overview.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_left_in, R.anim.right_left_out);
            finish();
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent();
            intent.setClass(TimeLineActivity.this, Clubbox.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_left_in, R.anim.right_left_out);
            finish();
        } else if (id == R.id.nav_manage) {
            //this activity
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_timeline);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        closeDatabase();     //關閉資料庫
    }

    private final String pipe_title[] = {
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J"
    };

    private final String pipe_url[] = {
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J"
    };

    private final String pipe_date[] = {
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J"
    };

    private final String pipe_content[] = {
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J"
    };

    /*      SQLite方法    */
    private void openDatabase(){
        dbHelper_timeline=new MyDBHelper_Timeline(this);   //取得DBHelper物件
    }
    private void closeDatabase(){
        dbHelper_timeline.close();
    }

    public static final String TABLE_NAME="pipe";
    public static final String TITLE="title";
    public static final String PIPE_URL="url";
    public static final String DATE="date";
    public static final String CONTENT="content";

    private MyDBHelper_Timeline dbHelper_timeline;

    private Cursor getCursor(){
        SQLiteDatabase db=dbHelper_timeline.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        String[] columns={_ID,TITLE,PIPE_URL,DATE,CONTENT};
        Cursor cursor = db.query(TABLE_NAME,columns,null,null,null,null,null);  //查詢所有欄位的資料
        return cursor;
    }

    private int query_data_num(){
        Cursor cursor = getCursor();  //取得查詢物件Cursor
        StringBuilder resultData = new StringBuilder("Result:\n");
        int num=0;
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String url = cursor.getString(2);
            String date = cursor.getString(2);
            String content = cursor.getString(2);
            //club_ids[num] = id;
            pipe_title[num] = title;
            pipe_url[num] = url;
            pipe_date[num] = date;
            pipe_content[num] = content;
            /*resultData.append(id).append(": ");
            resultData.append(name).append(": ");
            resultData.append(intro).append("\n");*/

            num++;
        }
        cursor.close();
        return num;
    }

    private final int pipe_ids[] = {
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0
    };

    private void query_data_id(){
        /*SQLiteDatabase db1=dbHelper.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        Cursor cursor = db1.rawQuery("select * from friends ", null); //只有对数据进行查询时，才用rawQuery()，增、删、改和建表，都用execSQl() List<News> list=new ArrayList<News>();*/
        SQLiteDatabase db0 = dbHelper_timeline.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        String[] columns = {_ID,TITLE,PIPE_URL,DATE,CONTENT};
        Cursor cursor = db0.rawQuery("select * from pipe order by date", null);
        //Cursor cursor = db0.query(TABLE_NAME,columns,null,null,null,null,null);  //查詢所有欄位的資料
        int num=0;
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            pipe_ids[num] = id;

            num++;
        }
        cursor.close();
        db0.close();
    }

    private String query_data_title(int id){
        SQLiteDatabase db2=dbHelper_timeline.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        Cursor cursor = db2.rawQuery("select * from pipe where _ID = " + id, null); //只有对数据进行查询时，才用rawQuery()，增、删、改和建表，都用execSQl() List<News> list=new ArrayList<News>();

        String title = null;
        if(cursor.moveToFirst())//Move the cursor to the first row. This method will return false if the cursor is empty.
        {
            title = cursor.getString(cursor.getColumnIndex("title"));
        }
        cursor.close();
        db2.close();
        return title;
        //return c.getString(c.getColumnIndex(NAME));
    }

    private String query_data_imgurl(int id){
        SQLiteDatabase db2=dbHelper_timeline.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        Cursor cursor = db2.rawQuery("select * from pipe where _ID = " + id, null); //只有对数据进行查询时，才用rawQuery()，增、删、改和建表，都用execSQl() List<News> list=new ArrayList<News>();

        String url = null;
        if(cursor.moveToFirst())//Move the cursor to the first row. This method will return false if the cursor is empty.
        {
            url = cursor.getString(cursor.getColumnIndex("url"));
        }
        cursor.close();
        db2.close();
        return url;
        //return c.getString(c.getColumnIndex(NAME));
    }

    private String query_data_des(int id){
        SQLiteDatabase db2=dbHelper_timeline.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        Cursor cursor = db2.rawQuery("select * from pipe where _ID = " + id, null); //只有对数据进行查询时，才用rawQuery()，增、删、改和建表，都用execSQl() List<News> list=new ArrayList<News>();

        String des = null;
        if(cursor.moveToFirst())//Move the cursor to the first row. This method will return false if the cursor is empty.
        {
            des = cursor.getString(cursor.getColumnIndex("content"));
        }
        cursor.close();
        db2.close();
        return des;
        //return c.getString(c.getColumnIndex(NAME));
    }

    private String query_data_date(int id){
        SQLiteDatabase db2=dbHelper_timeline.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        Cursor cursor = db2.rawQuery("select * from pipe where _ID = " + id, null); //只有对数据进行查询时，才用rawQuery()，增、删、改和建表，都用execSQl() List<News> list=new ArrayList<News>();

        String date = null;
        if(cursor.moveToFirst())//Move the cursor to the first row. This method will return false if the cursor is empty.
        {
            date = cursor.getString(cursor.getColumnIndex("date"));
        }
        cursor.close();
        db2.close();
        return date;
        //return c.getString(c.getColumnIndex(NAME));
    }
}
