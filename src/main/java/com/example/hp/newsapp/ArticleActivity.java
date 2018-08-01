package com.example.hp.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ArticleActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {

    private static final String URL_REQUEST = "http://content.guardianapis.com/search?";
    private static final int ARTICLE_LOADER_ID = 1;
    private static final String AND_OPERATOR = "%20AND%20";
    private ArticleAdapter articleAdapter;
    private ProgressBar progressBar;
    private TextView messageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        ListView listView = findViewById(R.id.list);
        progressBar = findViewById(R.id.loading);
        messageView = findViewById(R.id.message_view);
        listView.setEmptyView(messageView);

        articleAdapter = new ArticleAdapter(this, new ArrayList<Article>());
        listView.setAdapter(articleAdapter);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            getLoaderManager().initLoader(ARTICLE_LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            messageView.setText(R.string.no_internet);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(articleAdapter.getItem(position).getUrl()));
                startActivity(intent);
            }
        });


    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String numberOfNews = sharedPreferences.getString(getString(R.string.settings_number_of_news_key), getString(R.string.settings_number_of_news_default));
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));
        Boolean android = sharedPreferences.getBoolean(getString(R.string.settings_theme_android_key), true);
        Boolean ios = sharedPreferences.getBoolean(getString(R.string.settings_theme_ios_key), false);
        Boolean phones = sharedPreferences.getBoolean(getString(R.string.settings_theme_phones_key), false);
        Boolean ai = sharedPreferences.getBoolean(getString(R.string.settings_theme_ai_key), false);
        Boolean gadgets = sharedPreferences.getBoolean(getString(R.string.settings_theme_gadgets_key), false);
        Boolean games = sharedPreferences.getBoolean(getString(R.string.settings_theme_games_key), false);

        StringBuilder themes = new StringBuilder();
        if (android) {
            themes.append(getString(R.string.settings_theme_android_key) + AND_OPERATOR);
        }
        if (ios) {
            themes.append(getString(R.string.settings_theme_ios_key) + AND_OPERATOR);
        }
        if (phones) {
            themes.append(getString(R.string.settings_theme_phones_key) + AND_OPERATOR);
        }
        if (ai) {
            themes.append(getString(R.string.settings_theme_ai_key) + AND_OPERATOR);
        }
        if (gadgets) {
            themes.append(getString(R.string.settings_theme_gadgets_key) + AND_OPERATOR);
        }
        if (games) {
            themes.append(getString(R.string.settings_theme_games_key) + AND_OPERATOR);
        }

        if (themes.toString().endsWith(AND_OPERATOR)) {
            themes.delete(themes.toString().length() - AND_OPERATOR.length(), themes.toString().length());
            Log.i("ArticleActivity", themes.toString());
        }

        Uri baseUri = Uri.parse(URL_REQUEST);

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", themes.toString());
        uriBuilder.appendQueryParameter("section", "technology");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", numberOfNews);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", "17e22047-d9b8-46eb-92bf-0d0e3623a0dc");
        Log.i("ArticleActivity", uriBuilder.toString());
        return new ArticleLoader(this, uriBuilder.toString());
    }
    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        messageView.setText(R.string.no_articles);
        progressBar.setVisibility(View.GONE);
        articleAdapter.clear();

        if (data != null && !data.isEmpty()) {
            articleAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        articleAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}