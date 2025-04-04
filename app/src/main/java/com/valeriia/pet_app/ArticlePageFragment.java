package com.valeriia.pet_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ArticlePageFragment extends Fragment {

    private ImageView articleImage;
    private TextView articleTitle;
    private TextView articleTheme;
    private TextView articleText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        articleImage = view.findViewById(R.id.articlePageImage);
        articleTitle = view.findViewById(R.id.articlePageTitle);
        articleTheme = view.findViewById(R.id.articlePageTheme);
        articleText = view.findViewById(R.id.articlePageText);

        if (getArguments() != null) {
            String title = getArguments().getString("articleTitle", "Default Title");
            String theme = getArguments().getString("articleTheme", "Default Theme");
            String text = getArguments().getString("articleText", "Default Text");
            int imageResId = getArguments().getInt("articleImage", 0);

            articleTitle.setText(title);
            articleTheme.setText(theme);
            articleText.setText(text);
            articleImage.setImageResource(imageResId);

            articleText.setMovementMethod(new ScrollingMovementMethod());
        }
    }
}