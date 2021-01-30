package com.example.tars.navigation_1;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tars on 2018/4/27.
 */

public class Club extends AppCompatActivity {

    /*      全域變數        */
    // 設定_增加敘述 圖片敘述
    public String[] image_content = {
            "5月10號",
            "鋼鐵人",
            "Vision",
            "雷神索爾",
            "蜘蛛人"
    };
    /*      全域變數        */

    ImageView club_intor_image;
    TextView textView_club_name;
    TextView textView_intro;

    ListView listview;
    ListView listview_history;
    MyAdapter adapter;
    MyAdapter_history adapter_history;

    int club_id;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.club);

        // listview
        listview = (ListView) findViewById(R.id.listview);

        adapter = new MyAdapter(this);
        //listview.setAdapter(adapter);     // 在動態載入此頁面的post_query裡再setAdapter
        listview.setOnItemClickListener(onClickListView);
        // listview
        //建立函數動態設定ListView的高
        //setListViewHeightBasedOnChildren(listview);// 必須放在setAdapter後面

        // init ui元件
        textView_club_name = findViewById(R.id.club_introduce_name);
        //textView_intro = findViewById(R.id.club_introduce_content);
        // init ui元件

        club_intor_image = findViewById(R.id.club_intor_image);

        // 動態載入此頁面
        Intent intent = getIntent();
        club_id = intent.getIntExtra("club_id",1);      //從主頁接收club
        post_query(club_id);
        // 動態載入此頁面
        openDatabase();
        openDatabase_Timeline();

        // listview_history
        listview_history = (ListView) findViewById(R.id.listview_history);

        adapter_history = new MyAdapter_history(this);
        listview_history.setAdapter(adapter_history);
        listview_history.setOnItemClickListener(onClickListView_history);
        // listview_history

        post_query_history(club_id);
    }

    //設定listview內容
    String[] names_des = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
                                        "今夏非看不可 - 穿越金曲_聲歷其鏡",
                                        "三暝三天都不想離去 - Sing際大戰怪song對決",
                                        "三暝三天都不想離去 - Sing際大戰怪song對決"};

    String[] names_description = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
                                                "今夏非看不可 - 穿越金曲_聲歷其鏡",
                                                "三暝三天都不想離去 - Sing際大戰怪song對決",
                                                "三暝三天都不想離去 - Sing際大戰怪song對決"};

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater myInflater;

        public MyAdapter(Context c) {
            myInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return names_des.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return names_des[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub


            if(position == 0){
                convertView = myInflater.inflate(R.layout.contect_club_customer_listview_hot, null);

                TextView name = (TextView) convertView.findViewById(R.id.title);
                name.setText(names_des[0]);
                name.setTextSize(14);

                return convertView;
            }else{
                convertView = myInflater.inflate(R.layout.contect_club_customer_listview, null);

                TextView name = (TextView) convertView.findViewById(R.id.title);
                name.setText(names_des[position]);
                name.setTextSize(14);

                return convertView;
            }
        }

    }
    //設定listview內容
    //點擊listview事件
    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            if(position == 0){
                customDialogEvent_hot();
            }else{
                customDialogEvent_three(position);
            }
            Toast toast = Toast.makeText(Club.this, "點選 第" + (position+1) + "個項目\n內容 : " + names_des[position], Toast.LENGTH_LONG);
            toast.show();
        }
    };
    //點擊listview事件
    //設定listview_history內容
    String[] title_history = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡"};

    String[] description_history = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡"};

    String[] posts_url = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡"};

    public class MyAdapter_history extends BaseAdapter {
        private LayoutInflater myInflater;

        public MyAdapter_history(Context c) {
            myInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return title_history.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return title_history[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        ImageView img_listview;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            convertView = myInflater.inflate(R.layout.contect_club_customer_listview_history, null);

            img_listview = convertView.findViewById(R.id.image_listview);
            img_listview.setTag(position);
            if(posts_url[position].isEmpty())
                get_image_from_url( "https://i.imgur.com/jeUe8Xe.png",2,position);
            else
                get_image_from_url(posts_url[position],2,position);

            TextView name = (TextView) convertView.findViewById(R.id.title_history);
            name.setGravity(Gravity.CENTER_VERTICAL);
            if(description_history[position].length() > 40){
                title_history[position] = description_history[position].substring(0, 40) + "...";
            }else{
                title_history[position] = description_history[position];
            }
            name.setText(title_history[position]);
            name.setTextColor(Color.rgb(192, 192, 192));

            return convertView;
        }

    }
    //設定listview_history內容
    // 點擊listview_history事件
    private AdapterView.OnItemClickListener onClickListView_history = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            customDialogEvent(position);
            Toast toast = Toast.makeText(Club.this, "點選 第" + (position+1) + "個項目\n內容 : " + title_history[position], Toast.LENGTH_LONG);
            toast.show();
        }
    };
    //點擊listview_history事件

    //建立函數將dp轉換為像素
    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    //建立函數將dp轉換為像素

    //建立函數動態設定ListView的高度
    public void setListViewHeightBasedOnChildren(ListView listView) {
        //取得ListView的Adapter

        ListAdapter listAdapter = listView.getAdapter();
        //固定每一行的高度
        int itemHeight = 40;
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            totalHeight += Dp2Px(getApplicationContext(),itemHeight)+listView.getDividerHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;

        listView.setLayoutParams(params);
    }
    //建立函數動態設定ListView的高度

    /*      管理menu    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.club_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.option_1) {
            finish();
            return true;
        }
        if (id == R.id.option_2) {
            add();
            //Toast.makeText(Club.this, "更新囉", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*      SQLite方法    */
    private void openDatabase(){
        dbHelper=new MyDBHelper(this);   //取得DBHelper物件
    }
    private void closeDatabase(){
        dbHelper.close();
    }

    public static final String TABLE_NAME="friends";
    public static final String NAME="name";
    public static final String INTRO="intro";
    public static final String TID="this_club_id";

    private MyDBHelper dbHelper;

    private void add(){
        SQLiteDatabase db=dbHelper.getWritableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在新增、修改與刪除
        ContentValues values=new ContentValues();  //建立 ContentValues 物件並呼叫 put(key,value) 儲存欲新增的資料，key 為欄位名稱  value 為對應值。
        if(data_repeat_check(textView_anotherclub_name) == false){
            values.put(NAME,textView_anotherclub_name);
            values.put(INTRO,clubbox_intro);
            values.put(TID,Integer.valueOf(textView_club_id).intValue());
            db.insert(TABLE_NAME,null,values);
            Toast.makeText(Club.this, textView_anotherclub_name + " 已加入Club Box", Toast.LENGTH_SHORT).show();
            db.close();
        }else{
            Toast.makeText(Club.this, textView_anotherclub_name + " 已存在Club Box", Toast.LENGTH_SHORT).show();
            db.close();
        }
        db.close();
    }

    private boolean data_repeat_check(String name){
        SQLiteDatabase db = dbHelper.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        Cursor cursor = db.rawQuery("select * from friends where name = ?", new String[]{name}); //只有对数据进行查询时，才用rawQuery()，增、删、改和建表，都用execSQl() List<News> list=new ArrayList<News>();

        if(cursor.moveToNext()){
            cursor.close();
            return true;// //有城市在数据库已存在，返回true
        }else{
            cursor.close();
            return false;
        }

    }
    /*      SQLite方法    */

    /*      管理menu    */

    ProgressDialog mLoadingDialog;

    //顯示、取消 載入框
    public void showLoadingDialog(){
        String message = "載入中...";
        if(mLoadingDialog==null){
            mLoadingDialog = new ProgressDialog(this);
            mLoadingDialog.setMessage(message);
        }
        mLoadingDialog.show();
    }

    private void dismissLoadingDialog(){
        if(mLoadingDialog!=null) {
            mLoadingDialog.dismiss();
        }
    }
    //顯示、取消 載入框

    /*                  載入另一個的club頁面                    */

    //使用imgur URL，顯示在imageview
    void get_image_from_url(String imgur_url, final int from_where, final int pos){
        new AsyncTask<String, Void, Bitmap>()
        {
            @Override
            protected Bitmap doInBackground(String... params)
            {
                String url = params[0];
                return getBitmapFromURL(url);     //Url放在此，即可在ImageView顯示
            }

            @Override
            protected void onPostExecute(Bitmap result)
            {
                if(from_where == 0)
                    club_intor_image.setImageBitmap (result);
                if(from_where == 1)
                    imageView_imgur.setImageBitmap (result);
                if(from_where == 2){
                    ImageView imageViewByTag = (ImageView) listview_history.findViewWithTag(pos);
                    imageViewByTag.setImageBitmap (result);
                }
                if(from_where == 3){
                    imageView_cover.setImageBitmap (result);
                }
                // dismissLoadingDialog();             //關閉對話框
                super.onPostExecute(result);
            }
        }.execute(imgur_url);
    }
    //使用imgur URL，顯示在imageview

    //讀取Url圖片，型態為Bitmap
    private static Bitmap getBitmapFromURL(String imageUrl)
    {
        try
        {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    //讀取Url圖片，型態為Bitmap

    String textView_club_id;
    String textView_anotherclub_name;
    String textView_anotherclub_imgururl;
    String textView_anotherclub_intro;

    String clubbox_intro;

    String[] full_picture_url = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決"};


    //在post頁框裡，再動態載入post資訊
    void post_query(int writing_id){
        //連接資料庫，更新
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        writing_data_query(writing_id);         //動態載入 此activity_id活動的資訊
        writing_data_query_hot(writing_id);
        //連接資料庫，更新

        //根據資料庫的資料，設定 活動資訊
        textView_club_name.setText("- " + textView_anotherclub_name + " -");
        //textView_intro.setText(textView_anotherclub_intro);
        //根據資料庫的資料，設定 活動資訊

        listview.setAdapter(adapter);       //更新ListView
    }
    //在post頁框裡，再動態載入post資訊


    //抓資料庫資料
    void writing_data_query(int id){
        try {
            String result = Club_anotherclub_DBConnector.executeQuery(id);      //id由上一個頁面而來，由IntentIntent的getIntent()得到
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                textView_club_id = jsonData.getString("id");        //得到這個club的id
                textView_anotherclub_name = jsonData.getString("name");
                textView_anotherclub_intro = jsonData.getString("intro");
                textView_anotherclub_imgururl = jsonData.getString("clubimgururl");

                clubbox_intro = jsonData.getString("clubpost1intro");

                names_des[1] = jsonData.getString("clubpost1des");
                names_des[2] = jsonData.getString("clubpost2des");
                names_des[3] = jsonData.getString("clubpost3des");

                names_description[0] = jsonData.getString("clubpost1description");
                names_description[1] = jsonData.getString("clubpost2description");
                names_description[2] = jsonData.getString("clubpost3description");

                full_picture_url[0] = jsonData.getString("clubpost1full_picture");
                full_picture_url[1] = jsonData.getString("clubpost2full_picture");
                full_picture_url[2] = jsonData.getString("clubpost3full_picture");

            }

            get_image_from_url(textView_anotherclub_imgururl,0,0);       //Url，顯示在imageview、關閉對話框
            //更新

        } catch(Exception e){
            Log.e("log_tag", e.toString());
        }
    }
    //抓資料庫資料

    String activity_date;
    String activity_content;
    String activity_img_url;

    //抓資料庫資料
    void writing_data_query_hot(int id){
        try {
            String result = Club_anotherclub_DBConnector_hot.executeQuery(id);      //id由上一個頁面而來，由IntentIntent的getIntent()得到
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                //textView_club_id = jsonData.getString("id");        //得到這個club的id
                names_des[0] = jsonData.getString("summary");

                activity_date = jsonData.getString("startdate");
                activity_content = jsonData.getString("description");
                activity_img_url = jsonData.getString("coverphoto_url");

            }

            //更新

        } catch(Exception e){
            Log.e("log_tag", e.toString());
        }
    }
    //抓資料庫資料

    //在post頁框裡，再動態載入post資訊   history
    void post_query_history(int writing_id){
        //連接資料庫，更新
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        writing_data_query_history(writing_id);         //動態載入 此activity_id活動的資訊
        //連接資料庫，更新

        listview_history.setAdapter(adapter_history);       //更新ListView
    }
    //在post頁框裡，再動態載入post資訊   history

    //抓資料庫資料
    void writing_data_query_history(int id){
        try {
            String result = Club_history_DBConnector.executeQuery(id);      //id由上一個頁面而來，由IntentIntent的getIntent()得到
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                title_history[0] = jsonData.getString("title1");
                title_history[1] = jsonData.getString("title2");
                title_history[2] = jsonData.getString("title3");
                title_history[3] = jsonData.getString("title4");
                title_history[4] = jsonData.getString("title5");

                description_history[0] = jsonData.getString("description1");
                description_history[1] = jsonData.getString("description2");
                description_history[2] = jsonData.getString("description3");
                description_history[3] = jsonData.getString("description4");
                description_history[4] = jsonData.getString("description5");

                posts_url[0] = jsonData.getString("full_picture1");
                posts_url[1] = jsonData.getString("full_picture2");
                posts_url[2] = jsonData.getString("full_picture3");
                posts_url[3] = jsonData.getString("full_picture4");
                posts_url[4] = jsonData.getString("full_picture5");

            }

        } catch(Exception e){
            Log.e("log_tag", e.toString());
        }
    }
    //抓資料庫資料

    /*                  載入另一個的club頁面                    */

    /*          3個post的 posts頁面          */
    ImageView imageView_imgur;

    private void customDialogEvent_three(int position) {
        position = position -1;
        final View item = LayoutInflater.from(Club.this).inflate(R.layout.layout_posts, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Club.this);
        final AlertDialog dialog = builder.setView(item).show();
        /*builder.setView(item)
                .show();*/
        //showLoadingDialog();

        //post_query(item,writing_id);

        //imageView_imgur的圖片設，交給imgurpost_query處理
        imageView_imgur = item.findViewById(R.id.imageview_url_result);
        get_image_from_url(full_picture_url[position],1,0);
        //imageView_imgur的圖片設，交給imgurpost_query處理

        TextView textView_writing_organizer = item.findViewById(R.id.post_content);
        textView_writing_organizer.setText("\n" + "      " + names_description[position]);

        ImageView imageView = item.findViewById(R.id.frame_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    /*          3個post的 posts頁面          */

    /*          posts頁面          */
    //ImageView imageView_imgur;

    private void customDialogEvent(int position) {
        final View item = LayoutInflater.from(Club.this).inflate(R.layout.layout_posts, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Club.this);
        final AlertDialog dialog = builder.setView(item).show();
        /*builder.setView(item)
                .show();*/
        //showLoadingDialog();

        //post_query(item,writing_id);

        imageView_imgur = item.findViewById(R.id.imageview_url_result);
        get_image_from_url(posts_url[position],1,0);

        TextView textView_writing_organizer = item.findViewById(R.id.post_content);
        textView_writing_organizer.setText("\n" + "      " + description_history[position]);

        ImageView imageView = item.findViewById(R.id.frame_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    /*          posts頁面          */
    protected void onDestroy() {
        super.onDestroy();
        closeDatabase();     //關閉資料庫
        closeDatabase_Timeline();
    }

    ImageView imageView_cover;

    private void customDialogEvent_hot() {
        final View item = LayoutInflater.from(Club.this).inflate(R.layout.layout_calendar, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Club.this);
        final AlertDialog dialog = builder.setView(item).show();

        ImageView img_add = item.findViewById(R.id.img_add);
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_pipe();
            }
        });

        imageView_cover = item.findViewById(R.id.imageview_url_result);
        if(activity_img_url.isEmpty())
            get_image_from_url("https://i.imgur.com/jeUe8Xe.png",3,0);
        else
            get_image_from_url(activity_img_url, 3,0);

        TextView textView_writing_title = item.findViewById(R.id.textview_Title);
        textView_writing_title.setText(names_des[0]);

        TextView textView_writing_organizer = item.findViewById(R.id.post_content);
        textView_writing_organizer.setText(activity_date + "\n" + "      " + activity_content);

        ImageView imageView = item.findViewById(R.id.frame_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void openDatabase_Timeline(){
        dbHelper_timeline=new MyDBHelper_Timeline(this);   //取得DBHelper物件
    }
    private void closeDatabase_Timeline(){
        dbHelper_timeline.close();
    }

    public static final String TABLE_NAME2="pipe";
    public static final String TITLE="title";
    public static final String PIPE_URL="url";
    public static final String DATE="date";
    public static final String CONTENT="content";

    private MyDBHelper_Timeline dbHelper_timeline;

    private void add_pipe(){
        SQLiteDatabase db=dbHelper_timeline.getWritableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在新增、修改與刪除
        ContentValues values=new ContentValues();  //建立 ContentValues 物件並呼叫 put(key,value) 儲存欲新增的資料，key 為欄位名稱  value 為對應值。
        if(data_repeat_check2(names_des[0]) == false){
            values.put(TITLE,names_des[0]);

            if(activity_img_url.isEmpty())
                values.put(PIPE_URL,"https://i.imgur.com/jeUe8Xe.png");
            else
                values.put(PIPE_URL,activity_img_url);

            values.put(DATE,activity_date);
            values.put(CONTENT,activity_content);
            db.insert(TABLE_NAME2,null,values);
            Toast.makeText(Club.this, names_des[0] + " 已加入活動 Pipe", Toast.LENGTH_SHORT).show();
            db.close();
        }else{
            Toast.makeText(Club.this, names_des[0] + " 已存在活動 Pipe", Toast.LENGTH_SHORT).show();
            db.close();
        }
        db.close();
    }

    private boolean data_repeat_check2(String title){
        SQLiteDatabase db = dbHelper_timeline.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        Cursor cursor = db.rawQuery("select * from pipe where title = ?", new String[]{title}); //只有对数据进行查询时，才用rawQuery()，增、删、改和建表，都用execSQl() List<News> list=new ArrayList<News>();

        if(cursor.moveToNext()){
            cursor.close();
            return true;// //有城市在数据库已存在，返回true
        }else{
            cursor.close();
            return false;
        }

    }
}

/*   FixedSpeedScroller   */
class FixedSpeedScroller extends Scroller {

    private int mDuration =500;

    public void setTime(int scrollerTime){
        mDuration=scrollerTime;
    }
    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @SuppressLint("NewApi") public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }


    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
/*   FixedSpeedScroller   */

