package com.news.manthanshah.newsapi;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String url;

    //Constructor
    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // Create a background thread
    @Override
    public List<News> loadInBackground() {
        if (this.url == null) {
            return null;
        }

        //Pull news data and return it
        List<News> news = QueryUtils.fetchNewsData(this.url);
        return news;
    }
}
