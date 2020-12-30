package edu.iit.news;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NewsSourcesDownloader implements Runnable {
    private MainActivity mainActivity;
    private String url = "https://newsapi.org/v2/sources?language=en&country=us";
    private String key= "c7b641c251fd42e0a6f78e0809619800";
    private ArrayList<Source> sourceArrayList = new ArrayList<>();
    private ArrayList<String> categoryArrayList = new ArrayList<>();
    private String category;

    public NewsSourcesDownloader(MainActivity ma, String category) {
        mainActivity = ma;
        if (category.isEmpty() || category.equals("all")) {
            this.category = "";
        } else {
            this.category = category;
        }
    }

    private void processResults(String result) {
        parseJSON(result);
        mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.setResources(sourceArrayList, categoryArrayList);
                }
        });

    }


    @Override
    public void run() {

        Uri.Builder buildURL = Uri.parse(url).buildUpon();
        buildURL.appendQueryParameter("category", category);
        buildURL.appendQueryParameter("apiKey", key);
        String urlToUse = buildURL.build().toString();


        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.addRequestProperty("User-Agent", "");
            httpURLConnection.setRequestMethod("GET");
            InputStream httpURLConnectionInputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(httpURLConnectionInputStream)));

            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(strLine).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        processResults(stringBuilder.toString());
    }
    private void parseJSON(String s) {
        try {
            //add the vary color code here
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonObjectJSONArray = jsonObject.getJSONArray("sources");
            for (int i = 0; i < jsonObjectJSONArray.length(); i++) {
                Source setSource = new Source();
                setSource.setNewsSourceId(jsonObjectJSONArray.getJSONObject(i).getString("id"));
                setSource.setName(jsonObjectJSONArray.getJSONObject(i).getString("name"));
                setSource.setUrlNews(jsonObjectJSONArray.getJSONObject(i).getString("url"));
                setSource.setCategory(jsonObjectJSONArray.getJSONObject(i).getString("category"));
                sourceArrayList.add(setSource);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < sourceArrayList.size(); i++) {
            if (!categoryArrayList.contains(sourceArrayList.get(i).getCategory())) {
                categoryArrayList.add(sourceArrayList.get(i).getCategory());
            }
        }
    }
}
