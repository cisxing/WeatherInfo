package aitmyhelloapp.examples.android.ait.hu.weatherinfoapp.network;

/**
 * Created by xinyunxing on 11/27/2014.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import aitmyhelloapp.examples.android.ait.hu.weatherinfoapp.R;

/**
 * the reason we are making a new package is because package name should be named after the use case instead of technology(?)
 */
public class HttpAsyncTask extends AsyncTask<String,Void, String>{


    public static final String FILTER_WEATHER = "FILTER_WEATHER";
    public static final String KEY_WEATHER = "KEY_WEATHER";
    private Context ctx;

    public HttpAsyncTask(Context context) {
        this.ctx = context;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";

        HttpURLConnection conn = null;
        InputStream is = null;
        try{
            URL url = new URL(params[0]);
            conn = (HttpURLConnection) url.openConnection();
            is = conn.getInputStream();
            int ch;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while((ch= is.read())!=-1){
                bos.write(ch);
            }
            result= new String(bos.toByteArray());
        }catch(Exception e)
        {
         result = e.getMessage();
        }finally{
            if(is!= null)
            {
                try {
                    is.close();
                }catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        Intent i = new Intent(FILTER_WEATHER);
        i.putExtra(KEY_WEATHER,result);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(i);

    }
}
