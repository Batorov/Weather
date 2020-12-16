package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?id=2023469&appid=cc64378385089a69a4c4fe3cc2c48319";
    TextView TempField;
    TextView HumField;
    TextView SunRaise;
    TextView SunSet;
    TextView DayLength;
    JSONObject data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TempField = findViewById(R.id.temp);
        HumField = findViewById(R.id.hum);
        SunRaise = findViewById(R.id.sunraise);
        SunSet = findViewById(R.id.sunset);
        DayLength = findViewById(R.id.daylength);

        try {
            getJSON();
            JSONObject details = data.getJSONArray("weather").getJSONObject(0);
            JSONObject main = data.getJSONObject("main");
            JSONObject sys = data.getJSONObject("sys");

            TempField.setText(String.format("%.2f", main.getDouble("temp") - 273.15)+ " ℃");
            HumField.setText("Влажность: " + main.getString("humidity")+"%");
            //TempField.setText(Long.toString((Long.parseLong(sys.getString("sunset")) - Long.parseLong(sys.getString("sunrise"))) * 1000));
            SunRaise.setText("Восход " + new java.text.SimpleDateFormat("HH:mm").format(new Date(Long.parseLong(sys.getString("sunrise")) * 1000)));
            SunSet.setText("Закат " + new java.text.SimpleDateFormat("HH:mm").format(new Date(Long.parseLong(sys.getString("sunset")) * 1000)));
            DayLength.setText("Долгота дня " + new java.text.SimpleDateFormat("HH:mm").format(new Date((Long.parseLong(sys.getString("sunset")) - Long.parseLong(sys.getString("sunrise")) - 60*60*8) * 1000)));

        } catch (Exception e) {
            System.out.println("Exception "+ e.getMessage());
            Toast.makeText(getApplicationContext(), "Ошибка ввода-вывода", Toast.LENGTH_SHORT).show();
        }

    }

    public void getJSON() throws ExecutionException, InterruptedException {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL("https://api.openweathermap.org/data/2.5/weather?id=2023469&APPID=cc64378385089a69a4c4fe3cc2c48319");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";

                    while((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    data = new JSONObject(json.toString());

                    if(data.getInt("cod") != 200) {
                        System.out.println("Cancelled");
                        return null;
                    }


                } catch (Exception e) {
                    //Toast.makeText(getApplicationContext(), "Ошибка ввода-вывода", Toast.LENGTH_SHORT).show();
                    System.out.println("Exception "+ e.getMessage());
                    return null;
                }

                return null;
            }

        }.execute().get();

    }


}
