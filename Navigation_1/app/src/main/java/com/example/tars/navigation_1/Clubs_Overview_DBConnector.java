package com.example.tars.navigation_1;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Clubs_Overview_DBConnector {
    public static String executeQuery(int id) {
        String result = "";

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://netlab.csie.ncut.edu.tw:32768/test/query_activity/query_clubs_orverview.php");
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", "'" + id + "'"));
            //params.add(new BasicNameValuePair("query_string", query_string));
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
            HttpResponse httpResponse = httpClient.execute(httpPost);
            //view_account.setText(httpResponse.getStatusLine().toString());
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();

            BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder builder = new StringBuilder();
            String line = null;
            while((line = bufReader.readLine()) != null) {
                builder.append(line + "\n");
            }
            inputStream.close();
            result = builder.toString();
            JSONObject json = new JSONObject("{\"content\":" + result + "}");

            Log.i("qwe", json.toString());

        } catch(Exception e) {
            Log.e("log_tag", e.toString());
        }

        return result;
    }
}
