package edu.iit.news;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NewsArticleDownloader implements Runnable{

    private static final String TAG = "NewsArticleDownloader";
    private NewsService newsService;
    private String url = "https://newsapi.org/v2/everything?";
    private String key = "c7b641c251fd42e0a6f78e0809619800";
    private ArrayList<Article> newsListArticle = new ArrayList<>();
    private String source;


    public NewsArticleDownloader(NewsService newsService, String source) {
        this.newsService = newsService;
        this.source = source;
    }

    @Override
    public void run() {

        Uri.Builder buildURL = Uri.parse(url).buildUpon();
        buildURL.appendQueryParameter("sources", source);
        buildURL.appendQueryParameter("language", "en");
        buildURL.appendQueryParameter("pageSize", "100");
        buildURL.appendQueryParameter("apiKey", key);
        String urlToUse = buildURL.build().toString();


        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.addRequestProperty("User-Agent", "");
            httpURLConnection.setRequestMethod("GET");
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (Exception e) {
            return;
        }
        processResults(stringBuilder.toString());
    }

    private void parseJSON(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jObjectJSONArray = jsonObject.getJSONArray("articles");
            for (int i = 0; i < jObjectJSONArray.length(); i++) {
                Article articleJson = new Article();
                articleJson.setNewAuthor(jObjectJSONArray.getJSONObject(i).getString("author").replace("null", ""));
                articleJson.setNewsTitle(jObjectJSONArray.getJSONObject(i).getString("title").replace("null", ""));
                articleJson.setApiAddress(jObjectJSONArray.getJSONObject(i).getString("url"));
                articleJson.setUrl2image(jObjectJSONArray.getJSONObject(i).getString("urlToImage"));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                articleJson.setDt_Time(jObjectJSONArray.getJSONObject(i).getString("publishedAt"));
                articleJson.setNewsDesc(jObjectJSONArray.getJSONObject(i).getString("description"));

                newsListArticle.add(articleJson);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void processResults(String result) {
        parseJSON(result);
        newsService.setArticleList(newsListArticle);

    }

}
