package com.news.manthanshah.newsapi;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>>
{
    private static final String NEWS_REQUEST_URL =
            "http://content.guardianapis.com/world/";

    private static final int NEWS_LOADER_ID = 1;

    private NewsAdapter adapter;

    private TextView emptyStateTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create new listView
        ListView newsListView = findViewById(R.id.list);

        emptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(emptyStateTextView);

        adapter = new NewsAdapter(this, 0 ,new ArrayList<News>());

        //Connect listView with adapter
        newsListView.setAdapter(adapter);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = adapter.getItem(position);

                if(!news.getUrl().equals(""))
                {
                    Uri link = Uri.parse(news.getUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, link);
                    if(intent.resolveActivity(getPackageManager())!= null){
                        startActivity(intent);
                    }
                }
            }
        });
        //Check internet connection
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            emptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String country = sharedPrefs.getString(
                getString(R.string.settings_country_key),
                getString(R.string.settings_country_default));
        Uri baseUri = Uri.parse(NEWS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendEncodedPath(country);
        uriBuilder.appendQueryParameter("api-key", "b36b5eaf-651c-4d40-9a18-97d0acb68793");

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> data) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        emptyStateTextView.setText(R.string.no_news);

        adapter.clear();

        //Check data exist
        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        adapter.clear();
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
