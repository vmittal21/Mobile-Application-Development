package edu.iit.news;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

import static edu.iit.news.MainActivity.ACTION_SERVICE_MSG;
import static edu.iit.news.MainActivity.ACTION_STORY_NEWS;
import static edu.iit.news.MainActivity.DATA_SOURCE;
import static edu.iit.news.MainActivity.NEWS_DATA_ARTICLE;

public class NewsService extends Service {
    private static final String TAG = "NewsService";
    private boolean running = true;
    private ServiceReceiver newsServiceReceiver;
    private ArrayList<Article> articlesList = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        newsServiceReceiver = new ServiceReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_SERVICE_MSG);
        registerReceiver(newsServiceReceiver, intentFilter);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            stopSelf();
            unregisterReceiver(newsServiceReceiver);
            running = false;
            super.onDestroy();
        }catch(IllegalArgumentException e){
            Log.d(TAG, "onDestroy: in catch");            
        }
    }

    public void setArticleList(ArrayList<Article> arrayList) {
        articlesList.clear();
        articlesList = new ArrayList<>(arrayList);
        sendIntent();
    }

    private void sendIntent() {

                Intent intent = new Intent(ACTION_STORY_NEWS);
                intent.putExtra(NEWS_DATA_ARTICLE, (Serializable) articlesList);
                sendBroadcast(intent);
                articlesList.clear();

    }


    public class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_SERVICE_MSG:
                    if (intent.hasExtra(DATA_SOURCE)) {
                        Source source = (Source) intent.getSerializableExtra(DATA_SOURCE);
                        NewsArticleDownloader newsArticleDownloader = new NewsArticleDownloader(NewsService.this, "" + source.getNewsSourceId());
                        new Thread(newsArticleDownloader).start();
                        Log.d(TAG, "onReceive: in receiver in service");
                    }
                    break;
            }
        }
    }

}