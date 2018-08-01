package com.example.hp.newsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.squareup.picasso.Picasso;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {

    public static final String LOG_TAG = ArticleAdapter.class.getSimpleName();
    public ArticleAdapter(@NonNull Context context, @NonNull List<Article> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Article currentArticle = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.article, parent, false);
        }

        ImageView articleImageView = convertView.findViewById(R.id.article_image_view);
        String imageUrl = currentArticle.getImageUrl();

        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(articleImageView);
        } else {
            Picasso.get().load(R.drawable.placeholder).into(articleImageView);
        }

        TextView sectionTextView = convertView.findViewById(R.id.article_section_text_view);
        sectionTextView.setText(currentArticle.getSection());

        TextView articleAuthorTextView = convertView.findViewById(R.id.article_author_text_view);
        String author = currentArticle.getAuthor();

        if (author != null) {
            articleAuthorTextView.setText(author);
        } else {
            articleAuthorTextView.setVisibility(View.GONE);
        }

        TextView articleTitleTextView = convertView.findViewById(R.id.article_title_text_view);
        articleTitleTextView.setText(currentArticle.getTitle());

        TextView articleDateTextView = convertView.findViewById(R.id.article_date_text_view);
        articleDateTextView.setText(currentArticle.getFormatedDate());

        return convertView;

    }
}
