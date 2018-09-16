package com.anew.whatstheweather;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    EditText cityName;
    TextView weather;
    public class DownloadTask extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL ur;
            HttpURLConnection hur;
            try
            {
                Log.i("b","before");
                ur=new URL(urls[0]);
                hur=(HttpURLConnection) ur.openConnection();
                InputStream ine=hur.getErrorStream();
                if(ine!=null)
                {
                    int data=ine.read();
                    while (data!=-1)
                    {
                        char c=(char)data;
                        result+=c;
                        data=ine.read();
                    }
                    Log.i("result",result);

                }
                else
                {
                    InputStream in=hur.getInputStream();

                    InputStreamReader inr=new InputStreamReader(in);
                    int data=inr.read();
                    while (data!=-1)
                    {
                        char c=(char)data;
                        result+=c;
                        data=inr.read();
                    }
                    Log.i("result",result);

                }
                return result;


            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_LONG).show();
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                String msg="";

                JSONObject js=new JSONObject(s);
                String checky=js.getString("cod");
                Log.i("cod",checky);
                if(checky.equals("404"))
                {
                    Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_LONG).show();
                    weather.setTextColor(Color.RED);
                    weather.setText("no such city found\nenter a proper city name!");

                }
                else
                {
                    String winf=js.getString("weather");
                    String sec=js.getString("main");

                    // Log.i("see",sec);
                    JSONArray arr=new JSONArray(winf);

                    for (int i=0;i<arr.length();i++)
                    {
                        JSONObject jpart=arr.getJSONObject(i);

                        String main="";
                        String description="";

                        main=jpart.getString("main");
                        description=jpart.getString("description");
                        if(main!=""&&description!="")
                        {
                            msg+=main+": "+description+"\r\n";

                        }
                        Log.i("main:",jpart.getString("main"));
                        Log.i("description:",jpart.getString("description"));
                    }


                    JSONObject j2=new JSONObject(sec);
                    String temp="";
                    double t=j2.getDouble("temp");
                    DecimalFormat df=new DecimalFormat("#.00");


                    t-=273.15;
                    temp=df.format(t);

                    String name=js.getString("name");

                    String gname=cityName.getText().toString();
                    if(temp!="")
                    {
                        msg+="\nTemp: "+temp+" °C "+"\r\n";
                    }



                    Log.i("msg",msg);
                    if(msg!=""&&gname.equalsIgnoreCase(name))
                    {
                        weather.setTextColor(Color.BLACK);
                        weather.setText(msg);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_LONG).show();
                        weather.setText("no such city found\nenter a proper city name!");
                    }
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"could not find weather",Toast.LENGTH_LONG).show();
                e.printStackTrace();

            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName=(EditText)findViewById(R.id.city);
        weather=(TextView)findViewById(R.id.resultWeather);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void findWeather(View view)
    {

        DownloadTask dt=new DownloadTask();
        String cname=cityName.getText().toString();
        if(cname.equals(""))
        {
            Toast.makeText(getApplicationContext(),"Please enter a city name",Toast.LENGTH_LONG).show();
            weather.setText("");
        }
        else if(!isNetworkAvailable())
        {
            weather.setText("");
            Toast.makeText(getApplicationContext(), "could not find weather check the internet connection", Toast.LENGTH_LONG).show();

        }
        else {
            Log.i("city=",cityName.getText().toString());

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);
            try {
                String msgenc = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

                dt.execute("http://api.openweathermap.org/data/2.5/weather?q=" + msgenc + "&APPID=b1a5227f8645d53b30b17e0b7b062874");
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "could not find weather..check internet connection", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }


    }

}
