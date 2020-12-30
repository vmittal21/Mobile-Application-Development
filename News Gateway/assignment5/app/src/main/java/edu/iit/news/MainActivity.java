package edu.iit.news;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mdrawerLayout;
    private ListView mlistView;
    private ArrayList<String> stringListArray = new ArrayList<>();
    private Menu opt_menu;
    private ArrayList<Article> articleArrayList = new ArrayList<>();
    private NewsReceiver receiver;
    private ArrayList<String> categorylist = null;
    private MyPageAdapter pageAdapter;
    private ActionBarDrawerToggle mactionBarDrawerToggle;
    private HashMap<String, Source> stringSourceHashMap = new HashMap<String, Source>();
    private List<Fragment> fragmentList;
    private ViewPager viewPager;
    static final String ACTION_SERVICE_MSG = "ACTION_SERVICE_MSG";
    static final String ACTION_STORY_NEWS = "ACTION_STORY_NEWS";
    static final String DATA_SOURCE = "DATA_SOURCE";
    static final String NEWS_DATA_ARTICLE = "NEWS_DATA_ARTICLE";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);
        Log.d(TAG, "onCreate: in main");
        receiver = new NewsReceiver();
        IntentFilter intentFilter = new IntentFilter(ACTION_STORY_NEWS);
        registerReceiver(receiver, intentFilter);
        mdrawerLayout = findViewById(R.id.drawer_layout);
        mlistView = findViewById(R.id.left_drawer);
        mlistView.setAdapter(new ArrayAdapter<>(this, R.layout.drawer_list_item, stringListArray));
        mlistView.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                    }
                }
        );
        mactionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mdrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (networkStatus()) {
            if (savedInstanceState != null) {
                setTitle(savedInstanceState.getCharSequence("title"));
                setResources((ArrayList<Source>) savedInstanceState.getSerializable("sourcelist"), savedInstanceState.getStringArrayList("categorylist"));
            } else {
                NewsSourcesDownloader newsSourcesDownloader = new NewsSourcesDownloader(this, "");
                new Thread(newsSourcesDownloader).start();
            }
        } else {
            dialogBox();
        }

        fragmentList = getFragmentList();
        Log.d(TAG, "onCreate: fragmentList" + fragmentList);
        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(pageAdapter);
        viewPager.setOffscreenPageLimit(10);

        if (savedInstanceState != null) {

            for (int i = 0; i < savedInstanceState.getInt("size"); i++) {
                Log.d(TAG, "onCreate: in here for fragments");
                fragmentList.add(getSupportFragmentManager().getFragment(savedInstanceState, "NewsFragment" + Integer.toString(i)));
            }
        } else {
            viewPager.setBackgroundResource(R.drawable.background);
        }
        pageAdapter.notifyDataSetChanged();
    }
    public void dialogBox()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No network connection");
        builder.setMessage("Error in Network Connection");
        builder.setNegativeButton(Html.fromHtml("<font color='#254E58'>OK</font>"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Log.e(TAG, "No");
            }
        });
        builder.show();
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onPostCreate: in  main");
        super.onPostCreate(savedInstanceState);
        mactionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        Log.d(TAG, "onConfigurationChanged: in main");
        super.onConfigurationChanged(configuration);
        mactionBarDrawerToggle.onConfigurationChanged(configuration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        opt_menu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mactionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        NewsSourcesDownloader newsSourcesDownloader = new NewsSourcesDownloader(this, "" + item);
        new Thread(newsSourcesDownloader).start();
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int pos) {
        Log.d(TAG, "selectItem: in main");
        Toast.makeText(this, stringListArray.get(pos), Toast.LENGTH_SHORT).show();
        viewPager.setBackground(null);
        setTitle(stringListArray.get(pos));
        Intent intent = new Intent(ACTION_SERVICE_MSG);
        intent.putExtra(DATA_SOURCE, stringSourceHashMap.get(stringListArray.get(pos)));
        Log.d(TAG, "selectItem: in main");
        sendBroadcast(intent);
        mdrawerLayout.closeDrawer(mlistView);
    }

    public void setResources(ArrayList<Source> sourceArrayList, ArrayList<String> categoryArrayList) {
        stringSourceHashMap.clear();
        stringListArray.clear();
        Collections.sort(sourceArrayList);
        for (Source s : sourceArrayList) {
            stringListArray.add(s.getName());
            stringSourceHashMap.put(s.getName(), s);
        }
        ((ArrayAdapter<String>) mlistView.getAdapter()).notifyDataSetChanged();

        if (this.categorylist == null) {
            this.categorylist = new ArrayList<>(categoryArrayList);
            if (opt_menu != null) {
                this.categorylist.add(0, "all");
                for (String c : this.categorylist) {
                    opt_menu.add(c);
                }
            }
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.categorylist != null) {
            menu.clear();
            for (String viewList : this.categorylist) {
                menu.add(viewList);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private List<Fragment> getFragmentList() {
        Log.d(TAG, "getFragmentList: in main");
        List<Fragment> fList = new ArrayList<Fragment>();
        return fList;
    }

    @Override
    protected void onDestroy() {
        try{
            super.onDestroy();
            Intent intent = new Intent(MainActivity.this, NewsService.class);
            stopService(intent);
            Log.d(TAG, "onDestroy: in main ");
            unregisterReceiver(receiver);
        }
        catch(IllegalArgumentException e){
            Log.d(TAG, "onDestroy: in main");
        }
    }
    @Override
    protected void onStop() {
        try{
            super.onStop();
            Intent intent = new Intent(MainActivity.this, NewsService.class);
            stopService(intent);
            Log.d(TAG, "onStop: in main");
            unregisterReceiver(receiver);
            }
        catch(IllegalArgumentException e){
                Log.d(TAG, "onStop: in here 1");
        }
    }
    @Override
    protected void onPause() {
        try{
            Log.d(TAG, "onPause: ");
            unregisterReceiver(receiver);
            super.onPause();}
        catch(IllegalArgumentException e){
            Log.d(TAG, "onPause: in here 2");
        }
    }



    public boolean networkStatus() {
        boolean ret = true;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return ret;
        } else {
            return !ret;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundlesavedInstanceState) {
        Log.d(TAG, "onSaveInstanceState: ");
        super.onSaveInstanceState(bundlesavedInstanceState);
        int comp = 0;
        for (int i = 0; i < fragmentList.size(); i++) {
            if (fragmentList.get(i).isAdded()) {
                comp++;
                
                String fragTemp = "NewsFragment" + i;
                getSupportFragmentManager()
                        .putFragment(bundlesavedInstanceState, fragTemp, fragmentList.get(i));
            }

        }
        bundlesavedInstanceState.putInt("size", comp);
        bundlesavedInstanceState.putStringArrayList("categorylist", categorylist);
        ArrayList<Source> sourceArrayList = new ArrayList<>();
        for (String strKey : stringSourceHashMap.keySet()) {
            sourceArrayList.add(stringSourceHashMap.get(strKey));
        }
        bundlesavedInstanceState.putSerializable("sourcelist", sourceArrayList);
        bundlesavedInstanceState.putCharSequence("title", getTitle());
    }


    public class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case ACTION_STORY_NEWS:
                    if (intent.hasExtra(NEWS_DATA_ARTICLE)) {
                        articleArrayList = (ArrayList) intent.getSerializableExtra(NEWS_DATA_ARTICLE);
                        
                        changeFragment(articleArrayList);
                        
                    }
                    break;
            }
        }

        private void changeFragment(List<Article> articleList) {
            if(!networkStatus())
            {
                dialogBox();
            }
            for (int j = 0; j < pageAdapter.getCount(); j++)
                pageAdapter.notifyChangeInPosition(j);

            fragmentList.clear();
            Log.d(TAG, "changeFragment: in main");
            for (int j = 0; j < articleList.size(); j++) {

                fragmentList.add(NewsFragment.newInstance((articleList.get(j)), "" + j, "" + articleList.size()));
            }

            pageAdapter.notifyDataSetChanged();
            viewPager.setCurrentItem(0);
        }
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }

    }

}