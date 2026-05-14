package com.example.tsuki;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleVH> {

    private List<Article> articles;

    public interface OnArticleClickListener {
        void onClick(Article article);
    }

    private OnArticleClickListener listener;

    public ArticleAdapter(List<Article> articles) {
        this.articles = articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    public void setOnArticleClickListener(OnArticleClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArticleVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ArticleVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleVH holder, int position) {
        Article article = articles.get(position);
        holder.image.setImageResource(article.getImageRes());
        holder.title.setText(article.getTitle());
        holder.subtitle.setText(article.getSubtitle());
        holder.readTime.setText(article.getReadTime());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(article);
        });
    }

    @Override
    public int getItemCount() {
        return articles != null ? articles.size() : 0;
    }

    static class ArticleVH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, subtitle, readTime;

        ArticleVH(@NonNull View itemView) {
            super(itemView);
            image    = itemView.findViewById(R.id.articleImage);
            title    = itemView.findViewById(R.id.articleTitle);
            subtitle = itemView.findViewById(R.id.articleSubtitle);
            readTime = itemView.findViewById(R.id.articleReadTime);
        }
    }
}
