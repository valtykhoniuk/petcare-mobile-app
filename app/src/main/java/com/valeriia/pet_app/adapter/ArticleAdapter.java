package com.valeriia.pet_app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.valeriia.pet_app.ArticlePageFragment;
import com.valeriia.pet_app.R;
import com.valeriia.pet_app.model.Article;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    Context context;
    List<Article> articles;

    public ArticleAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }


    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View articleItems = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ArticleAdapter.ArticleViewHolder(articleItems);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, @SuppressLint("RecyclerView") int position) {

        int imageId = context.getResources().getIdentifier("ic_" + articles.get(position).getImg(), "drawable", context.getPackageName());
        holder.articleImage.setImageResource(imageId);
        holder.articleTheme.setText(articles.get(position).getTheme());
        holder.articleTitle.setText(articles.get(position).getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof FragmentActivity) {
                    FragmentActivity fragmentActivity = (FragmentActivity) context;
                    ArticlePageFragment articlePageFragment = new ArticlePageFragment();

                    Bundle bundle = new Bundle();
                    bundle.putInt("articleImage", imageId);
                    bundle.putString("articleTheme", articles.get(position).getTheme());
                    bundle.putString("articleTitle", articles.get(position).getTitle());
                    bundle.putString("articleText", articles.get(position).getText());

                    articlePageFragment.setArguments(bundle);

                    fragmentActivity.getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentContainer, articlePageFragment)
                            .addToBackStack(null) // This allows the user to navigate back
                            .commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static final class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView articleImage;
        TextView articleTitle, articleTheme;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);

            articleImage = itemView.findViewById(R.id.articleItemImage);
            articleTitle = itemView.findViewById(R.id.articleItemTitle);
            articleTheme = itemView.findViewById(R.id.articleItemTheme);
        }
    }
}