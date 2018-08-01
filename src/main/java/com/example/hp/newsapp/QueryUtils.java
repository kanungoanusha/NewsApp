package com.example.hp.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    private static URL createURL(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating url", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();

            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<Article> extractFeatureFromJson(String articlesJson) {
        if (TextUtils.isEmpty(articlesJson)) {
            return null;
        }

        ArrayList<Article> articles = new ArrayList<>();

        try {
            JSONObject rootObject = new JSONObject(articlesJson);
            JSONObject response = rootObject.optJSONObject("response");
            JSONArray jsonArray = response.optJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject article = jsonArray.optJSONObject(i);
                String title = article.optString("webTitle");
                String section = article.optString("sectionName");
                String url = article.optString("webUrl");
                String publicationDate = article.getString("webPublicationDate");
                JSONObject fields = article.optJSONObject("fields");
                String imageUrl = null;
                if (fields != null) {
                    imageUrl = fields.optString("thumbnail");
                }
                JSONArray tags = article.optJSONArray("tags");
                String authorName = null;
                String authorSurname = null;
                if (tags.length() > 0) {
                    JSONObject author = tags.optJSONObject(0);
                    authorName = author.optString("firstName");
                    authorSurname = author.optString("lastName");
                }
                articles.add(new Article(title, url, imageUrl, publicationDate, authorName, authorSurname, section));
                Log.d(LOG_TAG, articles.get(i).toString());
            }
            return articles;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem with parsing JSON", e);
        }
        return null;
    }

    public static ArrayList<Article> fetchArticleData(String requestUrl) {
        URL url = createURL(requestUrl);
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input Stream", e);
        }
        return extractFeatureFromJson(jsonResponse);
    }
}
