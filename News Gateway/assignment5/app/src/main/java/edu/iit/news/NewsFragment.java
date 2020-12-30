package edu.iit.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class NewsFragment extends Fragment implements View.OnClickListener{

    public static final String NEWS_ARTICLE_FRAG = "DATA_ARTICLE_FRAG";
    public static final String NEWS_DATA_TOTAL = "DATA_TOTAL";
    public static final String DATA_INDICE = "DATA_INDICE";
    private static final String TAG = "NewsFragment";
    ImageView article_img;
    private Article listArticle;

    public static final NewsFragment newInstance(Article lstArticle, String strIndexs, String strTotal) {

        NewsFragment f = new NewsFragment();
        Bundle newsBundle = new Bundle(1);
        newsBundle.putSerializable(NEWS_ARTICLE_FRAG, lstArticle);
        newsBundle.putString(DATA_INDICE, strIndexs);
        newsBundle.putString(NEWS_DATA_TOTAL, strTotal);
        f.setArguments(newsBundle);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        Log.d(TAG, "onCreateView: here, in fragment");
        View view = layoutInflater.inflate(R.layout.news_fragment, viewGroup, false);
        listArticle = (Article) getArguments().getSerializable(NEWS_ARTICLE_FRAG);
            String position = getArguments().getString(DATA_INDICE);
            String total = getArguments().getString(NEWS_DATA_TOTAL);
            TextView article_title = (TextView) view.findViewById(R.id.article_title);
            TextView article_author = (TextView) view.findViewById(R.id.article_author);
            TextView article_date = (TextView) view.findViewById(R.id.article_date);
            TextView article_preview = (TextView) view.findViewById(R.id.article_preview);
            TextView article_count = (TextView) view.findViewById(R.id.article_count);
            article_img = (ImageView) view.findViewById(R.id.article_img);
            article_title.setOnClickListener(this);
            article_preview.setOnClickListener(this);
            article_img.setOnClickListener(this);
            article_title.setText(listArticle.getNewsTitle().replace("null", ""));
            article_title.setText(listArticle.getNewsTitle().replace("null", ""));
            article_author.setText(listArticle.getNewAuthor().replace("null", ""));
            article_preview.setText(listArticle.getNewsDesc().replace("null", ""));
            article_count.setText(Integer.parseInt(position) + 1 + " of " + total);
            if (listArticle.getDt_Time() != null) {
                DateFormat todate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                todate.setLenient(false);
                DateFormat setUSdate = new SimpleDateFormat("MMM dd, yyyy hh:mmaa");
                setUSdate.setLenient(false);
                String usdateString = listArticle.getDt_Time();
                Date setnewData;
                int count = 0;
                int maxTries = 2;
                boolean pass = false;
                while (!pass) {
                    try {
                        setnewData = todate.parse(usdateString);
                        article_date.setText(setUSdate.format(setnewData));
                        pass = true;
                    } catch (ParseException e) {
                        todate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                        if (++count == maxTries) {
                            pass = true;
                            article_date.setText("");
                        }
                    }
                }
            }
            if (listArticle.getUrl2image().length() > 0)
                uploadPhoto(listArticle.getUrl2image(), view);
            else
                uploadPhoto("null", view);
            return view;
    }

    private void uploadPhoto(String url, View v) {
        Picasso picasso = new Picasso.Builder(this.getContext())
                .listener((picasso1, uri, exception) -> picasso1.load(R.drawable.brokenimage)
                        .into(article_img))
                .build();
//.placeholder(R.drawable.placeholder)
        picasso.load(url)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(article_img);
    }

    @Override
    public void onClick(View view) {
        String stringURL = listArticle.getApiAddress();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(stringURL));
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (bundle != null) {
        }
    }

}