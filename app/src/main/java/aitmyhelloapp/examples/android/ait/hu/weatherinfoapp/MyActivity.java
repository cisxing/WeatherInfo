package aitmyhelloapp.examples.android.ait.hu.weatherinfoapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Date;

import aitmyhelloapp.examples.android.ait.hu.weatherinfoapp.network.HttpAsyncTask;


public class MyActivity extends Activity {

    private static final String CITY_NAME = "CITY_NAME";
    private TextView tvData;
    private EditText etCity;
    private ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        tvData = (TextView) findViewById(R.id.tvData);
        etCity = (EditText) findViewById(R.id.etCity);



        Button btnGet = (Button) findViewById(R.id.btnGetWeather);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = etCity.getText().toString();
                String url =
                        "http://api.openweathermap.org/data/2.5/weather?q="+city+"&units=imperial";
                new HttpAsyncTask(getApplicationContext()).execute(url);


            }
        });
        imageView = (ImageView) findViewById(R.id.my_image);

    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                weatherReceiver, new IntentFilter(HttpAsyncTask.FILTER_WEATHER)
        );


    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(weatherReceiver);
    }

    public BroadcastReceiver weatherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String rawJson = intent.getStringExtra(HttpAsyncTask.KEY_WEATHER);

            try {
                JSONObject root = new JSONObject(rawJson);
                String des ="Description: "+
                        ((JSONObject)root.getJSONArray("weather").get(0)).getString(
                                "description");
                String min_temp = "Minimum temperature: "+((JSONObject)root.getJSONObject("main")).getString(
                        "temp_min");
                JSONObject main = root.getJSONObject("main");

                String max_temp = "Maximum temperature: "+main.getString("temp_max");
                String humidity = "Humidity: "+main.getString("humidity")+"%";
                long sunrise =((JSONObject)root.getJSONObject("sys")).getInt("sunrise");
                Date sunriseT= new Date(sunrise*1000);
                String sunriseTime= "Sunrise Time: "+sunriseT.toString();
                long sunset =((JSONObject)root.getJSONObject("sys")).getInt("sunset");
                Date sunsetT = new Date(sunset*1000);
                String sunsetTime = "Sunset Time: "+sunsetT.toString();
                String summary = min_temp+"\n"+max_temp+"\n"+humidity+"\n"+sunriseTime+"\n"+sunsetTime.toString()+"\n"+des;
                    tvData.setText(summary);
                String icon_id = ((JSONObject)root.getJSONArray("weather").get(0)).getString(
                        "icon");
                Glide.with(getApplicationContext()).load("http://openweathermap.org/img/w/"+icon_id+".png").into(imageView);
            } catch (Exception e) {
                tvData.setText("Weather information for this city does not exist, please search for a different one");
                Glide.clear(imageView);
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sp = getSharedPreferences("SP",MODE_PRIVATE);


        etCity.setText(sp.getString(CITY_NAME,""));

        if(etCity.getText().toString()!=null)
        {
            String url =
                    "http://api.openweathermap.org/data/2.5/weather?q="+etCity.getText().toString()+"&units=imperial";
            new HttpAsyncTask(getApplicationContext()).execute(url);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sp = getSharedPreferences("SP",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CITY_NAME,etCity.getText().toString());


        editor.commit();
    }

}