package com.example.tars.navigation_1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tars.navigation_1.Time_Line.DateTimeUtils;
import com.example.tars.navigation_1.Time_Line.OrderStatus;
import com.example.tars.navigation_1.Time_Line.Orientation;
import com.example.tars.navigation_1.Time_Line.TimeLineModel;
import com.example.tars.navigation_1.Time_Line.VectorDrawableUtils;
import com.github.vipulasri.timelineview.TimelineView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<TimeLineModel> mFeedList;
    private Context mContext;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private LayoutInflater mLayoutInflater;

    public TimeLineAdapter(List<TimeLineModel> feedList, Orientation orientation, boolean withLinePadding) {
        mFeedList = feedList;
        mOrientation = orientation;
        mWithLinePadding = withLinePadding;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,mFeedList.size());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;

        view = mLayoutInflater.inflate(R.layout.content_timeline_item, parent, false);

        return new TimeLineViewHolder(view, viewType);
    }

    private final String event_titles[] = {
            "近年來射鏢風潮逐漸崛起 我們資工系學會永遠走在最前面\n" +
                    "快來跟上這個活動潮流吧 一起來打鏢 認識新朋友 <3 <3",
            "\uD83C\uDF93畢業季\uD83C\uDF93\n" +
                    "回首4年 懷抱著希望 一起來到勤益",
            "#文創人 四文一甲\n" +
                    "馬若紋 這位同學平時就很喜歡用塗鴉的方式紀錄生活周遭的事物",
            "今天是一年一度的聖誕節!!!\n" +
                    "景觀系學會為了慰勞大家趕總評的辛勞",
            "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n" +
                    "恭喜入圍決賽的各位資管唱將們 \uD83C\uDF8A",
            "Img2",
            "Img3",
            "Img4",
            "Img5",
            "Img2",
            "Img3",
            "Img4",
            "Img5"
    };

    private final Integer image_ids[] = {
            R.drawable.i9,
            R.drawable.i1,
            R.drawable.i3,
            R.drawable.i8,
            R.drawable.i10,
            R.drawable.i9,
            R.drawable.i1,
            R.drawable.i3,
            R.drawable.i8,
            R.drawable.i10
    };

    @Override
    public void onBindViewHolder(final TimeLineViewHolder holder, final int position) {

        final TimeLineModel timeLineModel = mFeedList.get(position);

        if(timeLineModel.getStatus() == OrderStatus.INACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_inactive, android.R.color.darker_gray));
        } else if(timeLineModel.getStatus() == OrderStatus.ACTIVE) {
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.colorPrimary));
        } else {
            holder.mTimelineView.setMarker(ContextCompat.getDrawable(mContext, R.drawable.ic_marker), ContextCompat.getColor(mContext, R.color.colorPrimary));
        }

        if(!timeLineModel.getDate().isEmpty()) {
            holder.mDate.setVisibility(View.VISIBLE);
            holder.mDate.setText(DateTimeUtils.parseDateTime(timeLineModel.getDate(), "yyyy-MM-dd HH:mm", "hh:mm a, dd-MMM-yyyy"));
        }
        else
            holder.mDate.setVisibility(View.GONE);

        //holder.mImage.setImageResource(image_ids[position]);
        if(timeLineModel.getmUrl().isEmpty())
            get_image_from_url("https://i.imgur.com/jeUe8Xe.png", holder, null,0);
        else
            get_image_from_url(timeLineModel.getmUrl(), holder, null,0);
        holder.mTitle.setText(timeLineModel.getmTitle());
        holder.mDate.setText(timeLineModel.getDate());
        String short_des = timeLineModel.getMessage().substring(0,30) + "...";
        holder.mMessage.setText(short_des);
        //holder.mMessage.setText(event_titles[position]);
        final String title = event_titles[position];
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, title, Toast.LENGTH_SHORT).show();
                customDialogEvent(timeLineModel, holder);
            }
        });
    }

    private void customDialogEvent(TimeLineModel timeLineModel, TimeLineViewHolder holder) {
        final View item = LayoutInflater.from(mContext).inflate(R.layout.layout_calendar, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.setView(item).show();

        if(timeLineModel.getmUrl().isEmpty())
            get_image_from_url("https://i.imgur.com/jeUe8Xe.png", holder, item, 1);
        else
            get_image_from_url(timeLineModel.getmUrl(), holder, item,1);

        TextView textView_writing_title = item.findViewById(R.id.textview_Title);
        textView_writing_title.setText(timeLineModel.getmTitle());

        TextView textView_writing_organizer = item.findViewById(R.id.post_content);
        textView_writing_organizer.setText(timeLineModel.getDate() + "\n" + "      " + timeLineModel.getMessage());

        ImageView imageView = item.findViewById(R.id.frame_close);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ImageView imageView2 = item.findViewById(R.id.img_add);
        imageView2.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

    //使用imgur URL，顯示在imageview
    void get_image_from_url(String imgur_url, final TimeLineViewHolder holder, final View item, final int from_where){
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
                    holder.mImage.setImageBitmap (result);
                if(from_where == 1) {
                    ImageView imageView2 = item.findViewById(R.id.imageview_url_result);
                    imageView2.setImageBitmap(result);
                }
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

