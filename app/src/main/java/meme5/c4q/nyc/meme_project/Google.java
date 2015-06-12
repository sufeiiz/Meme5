package meme5.c4q.nyc.meme_project;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by sufeizhao on 6/11/15.
 */
public class Google extends Activity {

    private static final String ENDPOINT = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=";
    EditText editText;
    Button search;
    GridView grid;
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google);

        editText = (EditText) findViewById(R.id.edittext);
        search = (Button) findViewById(R.id.search);
        grid = (GridView) findViewById(R.id.grid);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncLoad().execute();
            }
        });
    }

    private class AsyncLoad extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {

            List<String> list = new ArrayList<>();
            String url = ENDPOINT + editText.getText().toString() + "+meme";

            try {
                URL jsonUrl = new URL(url);
                HttpsURLConnection connection = null;
                connection = (HttpsURLConnection) jsonUrl.openConnection();
                connection.setConnectTimeout(0);
                connection.setReadTimeout(0);
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }
                String jsonString = builder.toString();

                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject response = jsonObject.getJSONObject("responseData");
                JSONArray results = response.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject image = (JSONObject) results.get(i);
                    String imageUrl = image.getString("url");
                    if (imageUrl != null) {
                        list.add(imageUrl);
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<String> list) {
            adapter = new ImageAdapter(getApplicationContext(), list);
            grid.setAdapter(adapter);
        }
    }
}