package com.example.tars.navigation_1;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tars.navigation_1.ClubBox.Clubbox;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tars on 2018/4/10.
 */

public class Homepage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String IMAGE_TYPE = "image/*";
    private final int IMAGE_CODE = 0; //這裏的IMAGE_CODE是自己任意定義的

    Club_LoopViewPager viewpager_homepage;
    int numberof_viewpagerimage = 0;

    // HorizontalScrollView
    private LinearLayout toolbarItems;
    private int current;//当前图片的下标

    int[] imagesID=new int[]{R.drawable.image8,R.drawable.image9,
            R.drawable.image10,R.drawable.image11,R.drawable.image12,R.drawable.image14,
            R.drawable.image13,R.drawable.image7png,R.drawable.image8};

    private int[] loopimage_view = { 0,3,4 };

    ImageView[] imags;
    // HorizontalScrollView

    MyAdapter_hot_posts adapter_hot_posts;
    ListView listview_hot_posts;

    ListView listview;
    MyAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_homepage);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_homepage);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        //init viewpager
        viewpager_homepage = (Club_LoopViewPager) findViewById(R.id.viewpager_homepage);
        SamplePagerAdapter pagerAdapter = new SamplePagerAdapter();
        numberof_viewpagerimage = pagerAdapter.getCount();
        viewpager_homepage.setAdapter(pagerAdapter);
        //init viewpager
        // init 定時滑動
        initHandle();
        startAutoScroller();    //此activity開啟就開始自動滑動viewpager
        // init 定時滑動
        // 頁面被選取
        viewpager_homepage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    stopAutoScroller();         //定時滑動, 若選取頁面則停止計時
                    setScrollerTime(100);
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    currentIndex = viewpager_homepage.getCurrentItem();      //這行若註解，則系統會跑自己的頁數，而不是你所選擇的地方
                    startAutoScroller();        //定時滑動, 若放開頁面則開始計時
                }
                return false;
            }
        });
        // 頁面被選取
        // listview
        listview = (ListView) findViewById(R.id.listview_homepage);

        adapter = null;
        adapter = new MyAdapter(this);
        listview.setOnItemClickListener(onClickListView);
        calendar_post_query();
        // listview
        // HorizontalScrollView  //需時間
        /*toolbarItems=(LinearLayout) this.findViewById(R.id.toolbar_items_homepage);
        init_imageview();*/
        // HorizontalScrollView  //需時間

        // listview
        listview_hot_posts = (ListView) findViewById(R.id.listview_hot_posts);

        adapter_hot_posts = new MyAdapter_hot_posts(this);
        //listview.setAdapter(adapter);     // 在動態載入此頁面的post_query裡再setAdapter
        listview_hot_posts.setOnItemClickListener(onClickListView_hot_posts);
        // listview
        listview_hot_posts.setAdapter(adapter_hot_posts);       //更新ListView
        post_query_hot_posts();

        get_image_from_url("https://scontent.xx.fbcdn.net/v/t1.0-0/p480x480/32207300_180435406109672_5562208374621208576_n.jpg?_nc_cat=0&oh=55c4b533fe6e1ad4aa0ba3a0df645c49&oe=5BAB5CB8", 0,0);

        /*getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.homepage);*/

        openDatabase();

        Intent intent = new Intent(this, AutoReceiver.class);
        intent.setAction("VIDEO_TIMER");
        // PendingIntent这个类用于处理即将发生的事情
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        // AlarmManager.ELAPSED_REALTIME_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用相对时间
        // SystemClock.elapsedRealtime()表示手机开始到现在经过的时间
        Calendar cal = Calendar.getInstance();
        // 設定於 3 分鐘後執行
        cal.add(Calendar.MINUTE, 3);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
        /*Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle("Hi")
                .setContentText("Nice to meet you.")
                .setContentIntent(sender)
                .build(); // available from API level 11 and onwards
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManger.notify(0, notification);*/
    }

@Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_homepage);
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
            //this activity
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent();
            intent.setClass(Homepage.this, Clubs_Overview.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_left_in, R.anim.right_left_out);
            finish();
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent();
            intent.setClass(Homepage.this, Clubbox.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_left_in, R.anim.right_left_out);
            finish();
        } else if (id == R.id.nav_manage) {
            Intent intent_camera = new Intent();
            intent_camera.setClass(Homepage.this, TimeLineActivity.class);
            startActivity(intent_camera);
            overridePendingTransition(0,0);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_homepage);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // LoopViewPager SamplePagerAdapter
    public class SamplePagerAdapter extends PagerAdapter {
        private int mSize;

        public SamplePagerAdapter() {
            mSize = images.length;
        }   // 設定_增加圖片 數量

        public SamplePagerAdapter(int count) {
            mSize = count;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView((View) object);
        }

        // 設定_增加圖片 圖片來源
        private int[] images = {
                R.drawable.act_img1,
                R.drawable.act_img2,
                R.drawable.act_img3
        };

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {

            ImageView imageView = new ImageView(view.getContext());

            imageView.setImageResource(images[position]);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialogEvent_activity(loopimage_view[position]);
                }
            });
            view.addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return imageView;
        }

        // 增加item
        public void addItem() {
            mSize++;
            notifyDataSetChanged();
        }

        // 删除item
        public void removeItem() {
            mSize--;
            mSize = mSize < 0 ? 0 : mSize;

            notifyDataSetChanged();
        }
    }
    // LoopViewPager SamplePagerAdapter

    /*      定時滑動        */
    private Timer mTimer=null;//定時
    private Handler mHandler;//處理更換圖片消息
    // 不使用ArrayList, private ArrayList mPageViewList=new ArrayList();//數據
    public int currentIndex=0;//當前顯示View頁面的序號
    private FixedSpeedScroller scroller=null;

    public boolean startAutoScroller(){
        return startTime();
    }

    public boolean stopAutoScroller(){
        if(mTimer!=null){
            mTimer.cancel();
            mTimer=null;
            return true;
        }else{
            return false;
        }
    }

    private boolean startTime(){
        if(mTimer==null){
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg=new Message();
                    msg.what=1;
                    mHandler.sendMessage(msg);
                }
            },2000,2000); //每2秒執行一次
            return true;
        }else{
            return false;
        }
    }

    public void initHandle(){
        mHandler = new Handler() {
            @SuppressLint("NewApi")
            public void handleMessage(Message msg) {
                if(msg.what==1){
                    setScrollerTime(700);
                    viewpager_homepage.setCurrentItem(currentIndex);
                    if(numberof_viewpagerimage-1==currentIndex){       // 不使用ArrayList, if(mPageViewList.size()-1==currentIndex){
                        currentIndex=0;
                    }else{
                        currentIndex++;
                    }
                }
            };
        };
    }

    public void setScrollerTime(int scrollerTime){
        try {
            if(scroller!=null){
                scroller.setTime(scrollerTime);
            }else{
                Field mScroller;
                mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                scroller= new FixedSpeedScroller(viewpager_homepage.getContext(),new AccelerateInterpolator());
                scroller.setTime(scrollerTime);
                mScroller.set(viewpager_homepage, scroller);
            }
        } catch (Exception e) {
        }
    }
    /*      定時滑動        */

    //設定listview內容
    String[] text_summary = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "復仇再起 - 眾sing雲集 復仇再起",
            "比擬國家歌劇 - 好聲 in the House"};

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater myInflater;

        public MyAdapter(Context c) {
            myInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return text_summary.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return text_summary[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            convertView = myInflater.from(Homepage.this).inflate(R.layout.contect_homepage_customer_listview,parent, false);

            TextView name = (TextView) convertView.findViewById(R.id.title_homepage);
            name.setGravity(Gravity.CENTER);
            name.setText(text_summary[position]);
            name.setHeight(34);

            if(position%2 == 0){
                name.setBackgroundResource(R.color.colorPrimary);
                name.setTextColor(Color.rgb(255, 255, 255));
            }else{
                name.setBackgroundResource(R.color.colorO);
                name.setTextColor(Color.rgb(0, 0, 0));
            }

            return convertView;
        }

    }
    //設定listview內容

    //ListView的position mapping到 真正的活動id
    int[] activity_ID = {1,2,3,4,5};
    //ListView的position mapping到 真正的活動id
    //點擊listview事件
    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            customDialogEvent_activity(position);
            Toast toast = Toast.makeText(Homepage.this, "點選 第" + (position+1) + "個項目\n內容 : " + text_summary[position], Toast.LENGTH_LONG);
            toast.show();
            /*Intent intent = new Intent(Homepage.this, Info_Activity.class);
            intent.putExtra("id", activity_ID[position]);       //activity_ID[position] 才為真正的 活動id
            startActivity(intent);*/
        }
    };
    //點擊listview事件

    // HorizontalScrollView
    /*private void init_imageview() {

        imags = new ImageView[imagesID.length];
        for (int i = 0; i < imags.length; i++) {
            imags[i] = new ImageView(Homepage.this);
            imags[i].setImageResource(imagesID[i]);
            imags[i].setId(imagesID[i]);//标记，在ImageClick()中用到
            imags[i].setLayoutParams(new ViewGroup.LayoutParams(450, 450));
            imags[i].setPadding(20, 0, 0, 0);
            toolbarItems.addView(imags[i]);
            imags[i].setOnClickListener(new ImageClick());
        }

    }*/

    /**
     * 点击HorizontalScrollView中的图片
     */
    /*public class ImageClick implements View.OnClickListener {
        @SuppressLint("ResourceType")
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void onClick(View v) {
            ImageView img = (ImageView) v;
            for (int i = 0; i < imags.length; i++) {
                if (img == imags[i]) {
                    current = i;
                    break;
                }
            }
            Toast.makeText(Homepage.this,"在" + current + "頁",Toast.LENGTH_SHORT).show();

        }
    }*/
    // HorizontalScrollView

    //設定listview內容
    String[] names_des = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決"};

    String[] text_description = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決"};

    String[] hot_club_name = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決"};

    String[] hot_posts_url = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決"};


    public class MyAdapter_hot_posts extends BaseAdapter {
        private LayoutInflater myInflater;

        public MyAdapter_hot_posts(Context c) {
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            convertView = myInflater.inflate(R.layout.contect_homepage_listview_hot, null);

            img_listview = convertView.findViewById(R.id.image_hot);
            img_listview.setTag(position);
            if(hot_posts_url[position].isEmpty())
                get_image_from_url( "https://i.imgur.com/jeUe8Xe.png",2,position);
            else
                get_image_from_url(hot_posts_url[position],2,position);

            TextView title_lv = (TextView) convertView.findViewById(R.id.title_lv);
            title_lv.setText(hot_club_name[position]);
            title_lv.setTextColor(Color.rgb(255, 255, 255));

            TextView name = (TextView) convertView.findViewById(R.id.title_content);
            name.setText(names_des[position] + "\n" + "熱門度 : " + posts_clicks[position]);
            name.setTextSize(12);
            name.setTextColor(Color.rgb(192, 192, 192));

            return convertView;
        }

    }
    ImageView img_listview;
    //設定listview內容
    //點擊listview事件
    private AdapterView.OnItemClickListener onClickListView_hot_posts = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            customDialogEvent(position);
            post_query_hot_posts_click(position + 1,posts_clicks[position] + 1);
            Toast toast = Toast.makeText(Homepage.this, "點選 第" + (position+1) + "個項目\n內容 : " + names_des[position], Toast.LENGTH_LONG);
            toast.show();
        }
    };

    ImageView imageView_hot;        //3

    private void customDialogEvent(int position) {
        final View item = LayoutInflater.from(Homepage.this).inflate(R.layout.layout_posts, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Homepage.this);
        final AlertDialog dialog = builder.setView(item).show();
        /*builder.setView(item)
                .show();*/
        //showLoadingDialog();imageView_cover = item.findViewById(R.id.imageview_url_result);
        //        if(activity_img_url[position].isEmpty())
        //            get_image_from_url("https://i.imgur.com/jeUe8Xe.png",1,0);
        //        else
        //            get_image_from_url(activity_img_url[position], 1,0);

        imageView_hot = item.findViewById(R.id.imageview_url_result);
        if(hot_posts_url[position].isEmpty())
            get_image_from_url("https://i.imgur.com/jeUe8Xe.png",3,0);
        else
            get_image_from_url(hot_posts_url[position], 3,0);

        TextView textView_writing_organizer = item.findViewById(R.id.post_content);
        textView_writing_organizer.setText("\n" + "      " + text_description[position]);

        ImageView imageView = item.findViewById(R.id.frame_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    //點擊listview事件

    String text_startdate;

    //在post頁框裡，再動態載入post資訊
    void calendar_post_query(){
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

        writing_data_query();         //動態載入 此activity_id活動的資訊
        for(int j=3;j>=0;j--){
            writing_data_query_next(text_startdate,j);
        }
        //連接資料庫，更新
        for(int j=0;j<5;j++){
            if(text_summary[j].length() > 20){
                text_summary[j] = text_summary[j].substring(0,20) + "...";
            }
        }

        listview.setAdapter(adapter);       //更新ListView
    }
    //在post頁框裡，再動態載入post資訊

    //抓資料庫資料
    void writing_data_query(){
        try {
            String result = Homepage_calendar_DBConnector.executeQuery(1);      //id由上一個頁面而來，由IntentIntent的getIntent()得到
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                activity_ID[4] = jsonData.getInt("id");        //得到這個club的id
                text_summary[4] = jsonData.getString("summary");
                text_startdate = jsonData.getString("startdate");

                activity_title[4] = jsonData.getString("summary");
                activity_date[4] = jsonData.getString("startdate");
                activity_content[4] = jsonData.getString("description");
                activity_img_url[4] = jsonData.getString("coverphoto_url");

                /*names_des[0] = jsonData.getString("clubpost1des");
                names_des[1] = jsonData.getString("clubpost2des");
                names_des[2] = jsonData.getString("clubpost3des");

                names_description[0] = jsonData.getString("clubpost1description");
                names_description[1] = jsonData.getString("clubpost2description");
                names_description[2] = jsonData.getString("clubpost3description");*/
            }

        } catch(Exception e){
            Log.e("log_tag", e.toString());
        }
    }
    //抓資料庫資料
    //抓資料庫資料
    void writing_data_query_next(String startdate,int j){
        try {
            String result = Homepage_calendar_next_DBConnector.executeQuery(startdate);      //id由上一個頁面而來，由IntentIntent的getIntent()得到
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                activity_ID[j] = jsonData.getInt("id");        //得到這個club的id
                text_summary[j] = jsonData.getString("summary");
                text_startdate = jsonData.getString("startdate");

                activity_title[j] = jsonData.getString("summary");
                activity_date[j] = jsonData.getString("startdate");
                activity_content[j] = jsonData.getString("description");
                activity_img_url[j] = jsonData.getString("coverphoto_url");

                /*names_des[0] = jsonData.getString("clubpost1des");
                names_des[1] = jsonData.getString("clubpost2des");
                names_des[2] = jsonData.getString("clubpost3des");

                names_description[0] = jsonData.getString("clubpost1description");
                names_description[1] = jsonData.getString("clubpost2description");
                names_description[2] = jsonData.getString("clubpost3description");*/
            }

        } catch(Exception e){
            Log.e("log_tag", e.toString());
        }
    }
    //抓資料庫資料

    //在post頁框裡，再動態載入post資訊
    void post_query_hot_posts(){
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

        for(int j=1;j<6;j++){
            writing_data_query_hot_posts(j);         //動態載入 此activity_id活動的資訊
        }
        //連接資料庫，更新

        listview_hot_posts.setAdapter(adapter_hot_posts);       //更新ListView
    }
    //在post頁框裡，再動態載入post資訊

    int[] posts_clicks = {1,2,3,4,5};

    //抓資料庫資料
    void writing_data_query_hot_posts(int id){
        try {
            String result = Homepage_hot_posts_DBConnector.executeQuery(id);      //id由上一個頁面而來，由IntentIntent的getIntent()得到
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                names_des[id-1] = jsonData.getString("title");
                text_description[id-1] = jsonData.getString("description");
                posts_clicks[id-1] = jsonData.getInt("clicks");
                hot_club_name[id-1] = jsonData.getString("club");
                hot_posts_url[id-1] = jsonData.getString("post_url");


            }

        } catch(Exception e){
            Log.e("log_tag", e.toString());
        }
    }
    //抓資料庫資料

    String[] activity_title = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決"};

    String[] activity_date = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決"};

    String[] activity_content = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決"};

    String[] activity_img_url = new String[] { "年度之最 - 聖誕晚會_舞動野性，全zoo爆走中",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決",
            "今夏非看不可 - 穿越金曲_聲歷其鏡",
            "三暝三天都不想離去 - Sing際大戰怪song對決"};

    ImageView imageView_cover;

    private void customDialogEvent_activity(final int position) {
        final View item = LayoutInflater.from(Homepage.this).inflate(R.layout.layout_calendar, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Homepage.this);
        final AlertDialog dialog = builder.setView(item).show();

        ImageView img_add = item.findViewById(R.id.img_add);
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_pipe(position);

                /*
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


                Calendar mCal = Calendar.getInstance();
                try {
                    mCal.setTime(formatter.parse(activity_date[position]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int sqlday=mCal.get(Calendar.DAY_OF_YEAR);

                Calendar mCal2 = Calendar.getInstance();
                mCal2.setTime(new Date());
                int today=mCal2.get(Calendar.DAY_OF_YEAR);

                int diffday=today-sqlday;
                if(diffday != 0){
                    Toast.makeText(Homepage.this, diffday, Toast.LENGTH_SHORT).show();
                }*/


                /*
                // 自明天起, 連續 5 天的 14:00 執行
                mCal.add(Calendar.DATE, x);
                mCal.set(Calendar.HOUR_OF_DAY, 14);
                mCal.set(Calendar.MINUTE, 0);
                mCal.set(Calendar.SECOND, 0);*/
            }
        });

        imageView_cover = item.findViewById(R.id.imageview_url_result);
        if(activity_img_url[position].isEmpty())
            get_image_from_url("https://i.imgur.com/jeUe8Xe.png",1,0);
        else
            get_image_from_url(activity_img_url[position], 1,0);

        TextView textView_writing_title = item.findViewById(R.id.textview_Title);
        textView_writing_title.setText(activity_title[position]);

        TextView textView_writing_organizer = item.findViewById(R.id.post_content);
        textView_writing_organizer.setText(activity_date[position] + "\n" + "      " + activity_content[position]);

        ImageView imageView = item.findViewById(R.id.frame_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    //點擊listview事件

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
                if(from_where == 1)
                    imageView_cover.setImageBitmap (result);
                if(from_where == 2){
                    ImageView imageViewByTag = (ImageView) listview_hot_posts.findViewWithTag(pos);
                    imageViewByTag.setImageBitmap (result);
                }
                if(from_where == 3)
                    imageView_hot.setImageBitmap (result);

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

    private void add_pipe(int activity_pos){
        SQLiteDatabase db=dbHelper_timeline.getWritableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在新增、修改與刪除
        ContentValues values=new ContentValues();  //建立 ContentValues 物件並呼叫 put(key,value) 儲存欲新增的資料，key 為欄位名稱  value 為對應值。
        if(data_repeat_check(activity_title[activity_pos]) == false){
            values.put(TITLE,activity_title[activity_pos]);

            if(activity_img_url[activity_pos].isEmpty())
                values.put(PIPE_URL,"https://i.imgur.com/jeUe8Xe.png");
            else
                values.put(PIPE_URL,activity_img_url[activity_pos]);

            values.put(DATE,activity_date[activity_pos]);
            values.put(CONTENT,activity_content[activity_pos]);
            db.insert(TABLE_NAME,null,values);
            Toast.makeText(Homepage.this, activity_title[activity_pos] + " 已加入活動 Pipe", Toast.LENGTH_SHORT).show();
            db.close();
        }else{
            Toast.makeText(Homepage.this, activity_title[activity_pos] + " 已存在活動 Pipe", Toast.LENGTH_SHORT).show();
            db.close();
        }
        db.close();
    }

    private boolean data_repeat_check(String title){
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
    /*      SQLite方法    */

    protected void onDestroy() {
        super.onDestroy();
        closeDatabase();     //關閉資料庫
    }

    //在post頁框裡，再動態載入post資訊
    void post_query_hot_posts_click(int id,int clicks){
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

        writing_data_query_hot_posts_click(id,clicks);
        for(int j=1;j<6;j++){
            writing_data_query_hot_posts(j);         //動態載入 此activity_id活動的資訊
        }
        //連接資料庫，更新

        listview_hot_posts.setAdapter(adapter_hot_posts);       //更新ListView
    }
    //在post頁框裡，再動態載入post資訊

    //抓資料庫資料
    void writing_data_query_hot_posts_click(int id,int clicks){
        try {
            String result = Homepage_hot_posts_DBConnector_click.executeQuery(id,clicks);      //id由上一個頁面而來，由IntentIntent的getIntent()得到
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
            /*JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                names_des[id-1] = jsonData.getString("title");
                text_description[id-1] = jsonData.getString("description");
                posts_clicks[id-1] = jsonData.getInt("clicks");
                hot_club_name[id-1] = jsonData.getString("club");
                hot_posts_url[id-1] = jsonData.getString("post_url");


            }*/

        } catch(Exception e){
            Log.e("log_tag", e.toString());
        }
    }
    //抓資料庫資料
}

