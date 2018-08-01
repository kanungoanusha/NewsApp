package com.example.hp.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;


public class ArticleLoader extends AsyncTaskLoader<List<Article>> {

    private String url;
    public static final String LOG_TAG = ArticleLoader.class.getSimpleName();

    public ArticleLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    public List<Article> loadInBackground() {
        if (url == null) {
            return null;
        }
        return QueryUtils.fetchArticleData(url);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}