package com.example.tars.navigation_1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MyAdapter_Clubs_Overview extends RecyclerView.Adapter<MyAdapter_Clubs_Overview.ViewHolder> {
    private ArrayList<CreateList> galleryList;
    private Context context;

    public MyAdapter_Clubs_Overview(Context context, ArrayList<CreateList> galleryList) {
        this.galleryList = galleryList;
        this.context = context;
    }

    @Override
    public MyAdapter_Clubs_Overview.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_clubs_overview_cell_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter_Clubs_Overview.ViewHolder viewHolder, final int i) {
        viewHolder.title.setText(galleryList.get(i).getImage_title());
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        viewHolder.img.setImageResource((galleryList.get(i).getImage_ID()));
        String club_imgururl = galleryList.get(i).getImage_url();


        //圖片的點擊事件
        get_image_from_url(club_imgururl,viewHolder);
        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Image",Toast.LENGTH_SHORT).show();

                int nextclub_id = galleryList.get(i).get_nextclub_id();
                Intent intent = new Intent();
                intent.putExtra("club_id",nextclub_id);
                intent.setClass(context, Club.class);
                context.startActivity(intent);
            }
        });
        //圖片的點擊事件
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private ImageView img;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title_clubs_overview);
            img = (ImageView) view.findViewById(R.id.img_clubs_overview);
        }
    }

    //使用imgur URL，顯示在imageview
    void get_image_from_url(String imgur_url, final MyAdapter_Clubs_Overview.ViewHolder viewHolder){
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
                viewHolder.img.setImageBitmap (result);

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
}
