package com.example.tars.navigation_1.ClubBox;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tars.navigation_1.Club;
import com.example.tars.navigation_1.Clubcob_DBConnector;
import com.example.tars.navigation_1.Clubs_Overview;
import com.example.tars.navigation_1.Homepage;
import com.example.tars.navigation_1.MyDBHelper;
import com.example.tars.navigation_1.R;
import com.example.tars.navigation_1.TimeLineActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.provider.BaseColumns._ID;


public class Clubbox  extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    private final int club_ids[] = {
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

    private final String club_titles[] = {
            "國立勤益科技大學工業工程與管理系學會",
            "國立勤益科技大學化工與材料工程系學會",
            "國立勤益科技大學文化創意事業系學會",
            "國立勤益科技大學企業管理系學會",
            "國立勤益科技大學休閒產業管理系學會",
            "國立勤益科技大學流通管理系學會",
            "國立勤益科技大學冷凍空調與能源系學會",
            "國立勤益科技大學景觀系學會",
            "國立勤益科技大學資訊工程系學會",
            "國立勤益科技大學資訊管理系學會"
    };

    private final String club_intro[] = {
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

    private final String image_url[] = {
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

    int item_num = 0; //有多少個社團

    ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

    RecyclerView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clubbox);

        /*      側欄      */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar7);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_clubbox);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_clubbox);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);
        /*      側欄      */

        openDatabase();

        item_num = query_data_num();
        query_data_id();

        for(int i = 1; i <= item_num; i++){
            HashMap<String, Object> maps = new HashMap<String, Object>();

            int data_id = club_ids[i-1];
            clubs_query(data_id, i-1);

            maps.put("ItemId", data_id);
            maps.put("ItemText", query_data_name(data_id));
            maps.put("ItemText_intro", query_data_intro(data_id));
            maps.put("ItemImage", image_url[i-1]);
            maps.put("THIS_id", query_data_this_id(data_id));
            listItem.add(maps);
        }
        final MyAdapter myAdapter = new MyAdapter(listItem);
        mList = (RecyclerView) findViewById(R.id.list_view);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mList.setLayoutManager(layoutManager);
        mList.setAdapter(myAdapter);

        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.START|ItemTouchHelper.END) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                myAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(getDragDirs(recyclerView, viewHolder), getSwipeDirs(recyclerView, viewHolder));
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                listItem.remove(position);
                del(club_ids[position]);
                //myDataset.remove(position);
                myAdapter.notifyItemRemoved(position);
            }
        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(mList);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<HashMap<String, Object>> mData;


        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mImage;
            public TextView mTextView;
            public TextView mTextView_content;
            public LinearLayout linearLayout;
            public ViewHolder(View v) {
                super(v);
                mImage = (ImageView) v.findViewById(R.id.img);
                mTextView = (TextView) v.findViewById(R.id.info_text);
                mTextView_content = (TextView) v.findViewById(R.id.info_content);
                linearLayout = v.findViewById(R.id.linear_view);
            }
        }

        /*public MyAdapter(List<String> data) {
            mData = data;
        }*/
        public MyAdapter(List<HashMap<String, Object>> data) {
            mData = data;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.content_clubbox_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.mImage.setTag(position);
            get_image_from_url(mData.get(position).get("ItemImage").toString(), position);
            holder.mTextView.setText(mData.get(position).get("ItemText").toString());
            holder.mTextView_content.setText(mData.get(position).get("ItemText_intro").toString());
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 動態載入此頁面
                    Intent intent = new Intent();
                    intent.putExtra("club_id", Integer.valueOf(mData.get(position).get("THIS_id").toString()).intValue());
                    intent.setClass(Clubbox.this, Club.class);
                    startActivity(intent);
                    // 動態載入此頁面
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    /*          SQLite方法          */
    private void openDatabase(){
        dbHelper=new MyDBHelper(this);   //取得DBHelper物件
    }
    private void closeDatabase(){
        dbHelper.close();
    }

    public static final String TABLE_NAME="friends";
    public static final String NAME="name";
    public static final String INTRO="intro";

    private MyDBHelper dbHelper;

    private Cursor getCursor(){
        SQLiteDatabase db=dbHelper.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        String[] columns={_ID,NAME,INTRO};
        Cursor cursor = db.query(TABLE_NAME,columns,null,null,null,null,null);  //查詢所有欄位的資料
        return cursor;
    }

    private int query_data_num(){
        Cursor cursor = getCursor();  //取得查詢物件Cursor
        StringBuilder resultData = new StringBuilder("Result:\n");
        int num=0;
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String intro = cursor.getString(2);
            //club_ids[num] = id;
            club_titles[num] = name;
            club_intro[num] = intro;
            resultData.append(id).append(": ");
            resultData.append(name).append(": ");
            resultData.append(intro).append("\n");

            num++;
        }
        cursor.close();
        return num;
    }
    private void query_data_id(){
        /*SQLiteDatabase db1=dbHelper.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        Cursor cursor = db1.rawQuery("select * from friends ", null); //只有对数据进行查询时，才用rawQuery()，增、删、改和建表，都用execSQl() List<News> list=new ArrayList<News>();*/
        SQLiteDatabase db0 = dbHelper.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        String[] columns = {_ID,NAME,INTRO};
        Cursor cursor = db0.query(TABLE_NAME,columns,null,null,null,null,null);  //查詢所有欄位的資料
        int num=0;
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            club_ids[num] = id;

            num++;
        }
        cursor.close();
        db0.close();
    }
    private String query_data_name(int id){
        SQLiteDatabase db1=dbHelper.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        Cursor cursor = db1.rawQuery("select * from friends where _ID = " + id, null); //只有对数据进行查询时，才用rawQuery()，增、删、改和建表，都用execSQl() List<News> list=new ArrayList<News>();

        String name = null;
        if(cursor.moveToFirst())//Move the cursor to the first row. This method will return false if the cursor is empty.
        {
            name = cursor.getString(cursor.getColumnIndex("name"));
        }
        cursor.close();
        db1.close();
        return name;
        //return c.getString(c.getColumnIndex(NAME));
    }
    private String query_data_intro(int id){
        SQLiteDatabase db2=dbHelper.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        Cursor cursor = db2.rawQuery("select * from friends where _ID = " + id, null); //只有对数据进行查询时，才用rawQuery()，增、删、改和建表，都用execSQl() List<News> list=new ArrayList<News>();

        String intro = null;
        if(cursor.moveToFirst())//Move the cursor to the first row. This method will return false if the cursor is empty.
        {
            intro = cursor.getString(cursor.getColumnIndex("intro"));
        }
        cursor.close();
        db2.close();
        return intro;
        //return c.getString(c.getColumnIndex(NAME));
    }
    private int query_data_this_id(int id){
        SQLiteDatabase db2=dbHelper.getReadableDatabase();  //透過dbHelper取得讀取資料庫的SQLiteDatabase物件，可用在查詢
        Cursor cursor = db2.rawQuery("select * from friends where _ID = " + id, null); //只有对数据进行查询时，才用rawQuery()，增、删、改和建表，都用execSQl() List<News> list=new ArrayList<News>();

        int this_id = 0;
        if(cursor.moveToFirst())//Move the cursor to the first row. This method will return false if the cursor is empty.
        {
            this_id = cursor.getInt(cursor.getColumnIndex("this_club_id"));
        }
        cursor.close();
        db2.close();
        return this_id;
        //return c.getString(c.getColumnIndex(NAME));
    }
    private  void del(int id){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME,_ID+"="+id,null);
    }
    /*          SQLite方法          */
    /* 側欄 */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_clubbox);
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
            intent.setClass(Clubbox.this, Homepage.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_left_in, R.anim.right_left_out);
            finish();
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent();
            intent.setClass(Clubbox.this, Clubs_Overview.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_left_in, R.anim.right_left_out);
            finish();
        } else if (id == R.id.nav_slideshow) {
            //this activity
        } else if (id == R.id.nav_manage) {
            Intent intent_camera = new Intent();
            intent_camera.setClass(Clubbox.this, TimeLineActivity.class);
            startActivity(intent_camera);
            overridePendingTransition(0,0);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_clubbox);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /* 側欄 */

    protected void onDestroy() {
        super.onDestroy();
        closeDatabase();     //關閉資料庫
    }

    //使用imgur URL，顯示在imageview
    void get_image_from_url(String imgur_url, final int pos){
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
                ImageView imageViewByTag = (ImageView) mList.findViewWithTag(pos);
                imageViewByTag.setImageBitmap (result);
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

    //在post頁框裡，再動態載入post資訊
    void clubs_query(int club_id, int url_pos){
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

        data_query(club_id, url_pos);         //動態載入 此activity_id活動的資訊
    }
    //在post頁框裡，再動態載入post資訊

    //抓資料庫資料
    void data_query(int club_id, int url_pos){
        try {
            String result = Clubcob_DBConnector.executeQuery(club_id);      //id由上一個頁面而來，由IntentIntent的getIntent()得到
                /*
                    SQL 結果有多筆資料時使用JSONArray
                    只有一筆資料時直接建立JSONObject物件
                    JSONObject jsonData = new JSONObject(result);
                */
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                image_url[url_pos] = jsonData.getString("imgururl");

            }

        } catch(Exception e){
            Log.e("log_tag", e.toString());
        }
    }
    //抓資料庫資料
}