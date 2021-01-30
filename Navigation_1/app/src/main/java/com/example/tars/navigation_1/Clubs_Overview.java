package com.example.tars.navigation_1;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.tars.navigation_1.ClubBox.Clubbox;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/*
#本class，載入happytest資料庫的 clubs_overview資料表
使用了 CreateList.java 和 MyAdapter_Clubs_Overview.java
        Clubs_Overview_DBConnector.java
額外使用了 content_clubs_overview_cell_layout.xml
#Intent 去另一club，到Club.java
*/

public class Clubs_Overview extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<CreateList> createLists;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clubs_overview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_clubs_overview);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_clubs_overview);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        //圖片瀏覽 RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.imagegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        createLists = prepareData();
        MyAdapter_Clubs_Overview adapter = new MyAdapter_Clubs_Overview (getApplicationContext(), createLists);
        recyclerView.setAdapter(adapter);

        //圖片瀏覽 RecyclerView

        //收縮的按鈕 ExpandView
        /*      延展功能      *///initExpandView();
        //收縮的按鈕 ExpandView
    }

    /*      延展功能      *///private UMExpandLayout mExpandView;
    /*      延展功能      *///private LinearLayout mLinearLayout;
    /*      延展功能      */
    /*public void initExpandView(){
        mLinearLayout = (LinearLayout)findViewById(R.id.layout_class1);
        mExpandView = (UMExpandLayout) findViewById(R.id.setting_about_content);
        //mExpandView.collapse();                                                         //預設這個UMExpandLayout是收合的狀態
        //mExpandView.setContentView();
        mLinearLayout.setClickable(false);
        mLinearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(mExpandView.isExpand()){
                    mExpandView.collapse();
                    //mTextView.setText("点击向下展开");
                    //mImageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_close));
                }else{
                    mExpandView.expand();
                    //mTextView.setText("点击向上收叠");
                    //mImageView.setImageDrawable(getResources().getDrawable(R.drawable.drive));
                }
            }
        });
    }*/
    /*      延展功能      */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_clubs_overview);
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
            intent.setClass(Clubs_Overview.this, Homepage.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_left_in, R.anim.right_left_out);
            finish();
        } else if (id == R.id.nav_gallery) {
            //this activity
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent();
            intent.setClass(Clubs_Overview.this, Clubbox.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_left_in, R.anim.right_left_out);
            finish();
        } else if (id == R.id.nav_manage) {
            Intent intent_camera = new Intent();
            intent_camera.setClass(Clubs_Overview.this, TimeLineActivity.class);
            startActivity(intent_camera);
            overridePendingTransition(0,0);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_clubs_overview);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*                  圖片瀏覽 RecyclerView                 */
    private final String image_titles[] = {
            "Img1",
            "Img2",
            "Img3",
            "Img4",
            "Img5",
            "Img6",
            "Img7",
            "Img8",
            "Img9",
            "Img10",
            "Img11",
            "Img12",
            "Img13",
            "Img14"
    };

    private final Integer image_ids[] = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image6,
            R.drawable.image7png,
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image6,
            R.drawable.image7png,
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image6,
            R.drawable.image7png,
            R.drawable.image1,
            R.drawable.image2
    };

    private final String image_urls[] = new String[14];
    private final Integer nextclub_id[] = new Integer[14];

    private ArrayList<CreateList> prepareData(){

        ArrayList<CreateList> theimage = new ArrayList<>();
        for(int i = 0; i< image_titles.length; i++){
            post_query(i+1);                //社團從clubs_overview資料表的 id=1 開始
            image_titles[i] = text_anotherclub_name;
            image_urls[i] = text_anotherclub_imgururl;
            nextclub_id[i] = text_nextclub_id;

            CreateList createList = new CreateList();
            createList.set_data_number(image_titles.length-1);
            createList.setImage_title(image_titles[i]);
            createList.setImage_ID(image_ids[i]);
            createList.setImage_url(image_urls[i]);
            createList.set_nextclub_id(nextclub_id[i]);

            theimage.add(createList);
        }
        return theimage;
    }

    int text_club_id;
    String text_anotherclub_name;
    String text_anotherclub_imgururl;
    int text_nextclub_id;

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
        //連接資料庫，更新

    }
    //在post頁框裡，再動態載入post資訊

    //抓資料庫資料
    void writing_data_query(int id){
        try {
            String result = Clubs_Overview_DBConnector.executeQuery(id);      //id由上一個頁面而來，由Intent的getIntent()得到
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                text_club_id = jsonData.getInt("id");        //得到這個club的id
                text_anotherclub_name = jsonData.getString("name");
                text_anotherclub_imgururl = jsonData.getString("imgururl");
                text_nextclub_id = jsonData.getInt("clubid");
            }

            //get_image_from_url(textView_anotherclub_imgururl);       //Url，顯示在imageview、關閉對話框
            //更新

        } catch(Exception e){
            Log.e("log_tag", e.toString());
        }
    }
    //抓資料庫資料
    /*                  圖片瀏覽 RecyclerView                 */
}
