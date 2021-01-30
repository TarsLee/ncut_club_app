package com.example.tars.navigation_1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
/*
使用到layout_posts當貼文頁面
*/
public class Info_Activity extends AppCompatActivity{

    TextView textView_result;
    TextView textView_summary;
    TextView textView_startdate;         //startdate顯示在這
    TextView textView_description;
    ProgressDialog mLoadingDialog;
    ImageView img_result;

    ListView listview;
    MyAdapter_Info_Activity adapter;

    int activity_id;
    String text_summary;
    String text_startdate;
    String text_description;

    String text_writing_class;
    String text_writing_title;
    String text_writing_intro;
    String text_writing_clicks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        Intent intent = getIntent();
        activity_id = intent.getIntExtra("id", 0);      //得到要query的id
        textView_result = (TextView) findViewById(R.id.result);
        textView_result.setText("您的活動是 : "+ activity_id);

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

        db_data_query(activity_id);         //動態載入 此activity_id活動的資訊
        //連接資料庫，更新

        //根據資料庫的資料，設定 活動資訊
        textView_summary = findViewById(R.id.text_title);
        textView_summary.setText(text_summary);
        textView_startdate = findViewById(R.id.text_date);
        textView_startdate.setText(text_startdate);
        textView_description = findViewById(R.id.text_introduction);
        textView_description.setText(text_description);
        //根據資料庫的資料，設定 活動資訊

        //使用imgur URL，顯示在imageview
        //建立一個AsyncTask執行緒進行圖片讀取動作，並帶入圖片連結網址路徑
        showLoadingDialog();
        img_result = findViewById(R.id.imageview_url_activity);
        String imgur_url = "https://i.imgur.com/7g5ZpKa.png";
        get_image_from_url(imgur_url);       //Url，顯示在imageview、關閉對話框
        //使用imgur URL，顯示在imageview

        //init listview和adapter
        listview = (ListView) findViewById(R.id.listview_info_activity);
        adapter = new MyAdapter_Info_Activity(Info_Activity.this);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(onClickListView);
        //init listview和adapter
    }

    //抓資料庫資料
    void db_data_query(int id){
        try {
            String result = Info_Activity_DBConnector.executeQuery(id);      //id由上一個頁面而來，由Intent的getIntent()得到 ###
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                text_summary = jsonData.getString("summary");
                text_startdate = jsonData.getString("startdate");
                text_description = jsonData.getString("description");

            }


        } catch(Exception e){
            Log.e("log_tag", e.toString());
        }
    }
    //抓資料庫資料

    //抓資料庫資料 抓writing問題
    void db_data_query_writing(int id){
        try {
            String result = Info_Activity_Writing_DBConnector.executeQuery(id);      //id由上一個頁面而來，由Intent的getIntent()得到
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                text_writing_class = jsonData.getString("class");
                text_writing_title = jsonData.getString("title");
                text_writing_intro = jsonData.getString("intro");
                text_writing_clicks = jsonData.getString("clicks");

            }


        } catch(Exception e){
            Log.e("log_tag", e.toString());
        }
    }
    //抓資料庫資料 抓writing問題

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

    //使用imgur URL，顯示在imageview
    void get_image_from_url(String imgur_url){
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
                img_result.setImageBitmap (result);
                Toast.makeText(Info_Activity.this, "載入成功", Toast.LENGTH_LONG)
                        .show();
                dismissLoadingDialog();             //關閉對話框
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

    //設定listview內容
    //ListView的position mapping到 真正的文章id
    int[] writing_ID = {2,3,28,33,36};
    //ListView的position mapping到 真正的文章id
    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            customDialogEvent(position);
            Toast toast = Toast.makeText(Info_Activity.this, "點選 第" + (position+1) + "個項目\n內容 : " + names[position], Toast.LENGTH_LONG);
            toast.show();
        }
    };

    String[] names = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "復仇再起 - 眾sing雲集 復仇再起",
            "比擬國家歌劇 - 好聲 in the House"};

    int[] images = {R.drawable.voice1, R.drawable.voice2, R.drawable.voice3,
            R.drawable.voice4,R.drawable.voice5};

    //  !!照片需和names元素數量相同
    public class MyAdapter_Info_Activity extends BaseAdapter {

        private LayoutInflater myInflater;

        public MyAdapter_Info_Activity(Context c) {
            myInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return names[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            convertView = myInflater.inflate(R.layout.info_activity_listviewlayout, null);

            db_data_query_writing(position+1);

            TextView class_writing = (TextView) convertView.findViewById(R.id.text_class_info_activity_listviewlayout);
            class_writing.setText(text_writing_class);
            TextView title_writing = (TextView) convertView.findViewById(R.id.text_title_info_activity_listviewlayout);
            title_writing.setText(text_writing_title);
            TextView intro_writing = (TextView) convertView.findViewById(R.id.text_intro_info_activity_listviewlayout);
            intro_writing.setText(text_writing_intro);

            ImageView imageView = convertView.findViewById(R.id.image_info_activity_listviewlayout);
            imageView.setImageResource(images[position]);

            return convertView;
        }

    }
    //設定listview內容

    /*          posts頁面          */
    ImageView imageView_imgur;

    private void customDialogEvent(int writing_id) {
        final View item = LayoutInflater.from(Info_Activity.this).inflate(R.layout.layout_posts, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Info_Activity.this);
        final AlertDialog dialog = builder.setView(item).show();
        /*builder.setView(item)
                .show();*/
        showLoadingDialog();

        post_query(item,writing_id);

        //imageView_imgur的圖片設，交給imgurpost_query處理
        imageView_imgur = item.findViewById(R.id.imageview_url_result);
        //imageView_imgur的圖片設，交給imgurpost_query處理

        ImageView imageView = item.findViewById(R.id.frame_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    TextView textView_writing_organizer;

    String textView_writing_id;
    String textView_writing_fanspage;
    String textView_writing_content;
    String textView_writing_posttime;
    String imageView_text_imgur;

    //在post頁框裡，再動態載入post資訊
    void post_query(View view,int writing_id){
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

        writing_data_query(86+writing_id);         //動態載入 此activity_id活動的資訊
        //連接資料庫，更新

        //根據資料庫的資料，設定 活動資訊
        textView_writing_organizer = view.findViewById(R.id.post_content);
        textView_writing_organizer.setText(textView_writing_content);
        //根據資料庫的資料，設定 活動資訊
    }
    //在post頁框裡，再動態載入post資訊

    //抓資料庫資料
    void writing_data_query(int id){
        try {
            String result = Posts_DBConnector.executeQuery(id);      //id由上一個頁面而來，由Intent的getIntent()得到
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                textView_writing_id = jsonData.getString("id");
                textView_writing_content = jsonData.getString("content");
                imageView_text_imgur = jsonData.getString("imgururl");

            }

            get_image_from_url2(imageView_text_imgur);       //Url，顯示在imageview、關閉對話框

        } catch(Exception e){
            Log.e("log_tag", e.toString());
        }
    }
    //抓資料庫資料

    //使用imgur URL，顯示在imageview
    void get_image_from_url2(String imgur_url){
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
                imageView_imgur.setImageBitmap (result);
                Toast.makeText(Info_Activity.this, "載入成功", Toast.LENGTH_LONG)
                        .show();
                dismissLoadingDialog();             //關閉對話框
                super.onPostExecute(result);
            }
        }.execute(imgur_url);
    }
    //使用imgur URL，顯示在imageview
    /*          posts頁面          */

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
            Toast.makeText(Info_Activity.this, "加入活動Pipe囉", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*      管理menu    */
}
