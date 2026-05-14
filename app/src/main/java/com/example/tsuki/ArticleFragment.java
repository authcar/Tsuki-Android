package com.example.tsuki;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ArticleFragment extends Fragment {

    // Keys untuk Bundle
    public static final String ARG_IMAGE_RES = "image_res";
    public static final String ARG_TITLE     = "title";
    public static final String ARG_SUBTITLE  = "subtitle";
    public static final String ARG_READ_TIME = "read_time";
    public static final String ARG_CATEGORY  = "category";
    public static final String ARG_BODY      = "body";

    /**
     * Factory method — buat instance dengan data artikel.
     */
    public static ArticleFragment newInstance(Article article, String body) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE_RES,  article.getImageRes());
        args.putString(ARG_TITLE,    article.getTitle());
        args.putString(ARG_SUBTITLE, article.getSubtitle());
        args.putString(ARG_READ_TIME, article.getReadTime());
        args.putString(ARG_CATEGORY, article.getCategory().name());
        args.putString(ARG_BODY,     body);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) return;

        // Bind data ke view
        ImageView heroImage = view.findViewById(R.id.articleHeroImage);
        TextView tvCategory = view.findViewById(R.id.tvCategory);
        TextView tvReadTime = view.findViewById(R.id.tvReadTime);
        TextView tvTitle    = view.findViewById(R.id.tvArticleTitle);
        TextView tvSubtitle = view.findViewById(R.id.tvArticleSubtitle);
        TextView tvBody     = view.findViewById(R.id.tvArticleBody);
        ImageButton btnBack = view.findViewById(R.id.btnBack);

        heroImage.setImageResource(args.getInt(ARG_IMAGE_RES));
        tvTitle.setText(args.getString(ARG_TITLE));
        tvSubtitle.setText(args.getString(ARG_SUBTITLE));
        tvReadTime.setText(args.getString(ARG_READ_TIME));
        tvBody.setText(args.getString(ARG_BODY));

        // Format nama kategori
        String categoryRaw = args.getString(ARG_CATEGORY, "PERIOD");
        String categoryLabel = categoryRaw.charAt(0)
                + categoryRaw.substring(1).toLowerCase();
        tvCategory.setText(categoryLabel);

        // Back button
        btnBack.setOnClickListener(v -> {
            if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
