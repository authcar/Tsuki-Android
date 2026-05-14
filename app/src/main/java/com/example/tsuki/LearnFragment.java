package com.example.tsuki;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LearnFragment extends Fragment {

    private TextView tabPeriod, tabWellness, tabFertility;
    private ArticleAdapter adapter;
    private Article.Category currentCategory = Article.Category.PERIOD;

    private static final String[] QUOTES = {
            "Your body does something remarkable every month.\nTreat it with love and gentleness.",
            "Listen to your body. It knows more than you think.",
            "Rest is not laziness. It is part of healing.",
            "You are stronger than your worst day.",
            "Taking care of yourself is an act of love.",
            "Your cycle is not a burden. It is a sign of life.",
            "Be patient with yourself. Growth takes time.",
            "Every phase of your cycle has its own beauty.",
            "Nourish your body and it will take care of you.",
            "You deserve rest, care, and kindness — especially from yourself.",
            "Small steps every day lead to big changes.",
            "Your health is your greatest wealth.",
            "Embrace the rhythm of your body.",
            "Healing is not linear. Be gentle with yourself.",
            "You are enough, exactly as you are today.",
            "Track your cycle, understand your body, love yourself.",
            "Every month is a new beginning.",
            "Your body is wise. Trust it.",
            "Wellness is a journey, not a destination.",
            "Breathe. You are doing better than you think.",
            "Honor your energy levels — they change for a reason.",
            "Self-care is not selfish. It is necessary.",
            "Your feelings are valid, every day of the month.",
            "A healthy body starts with a kind mind.",
            "You are not alone in this journey.",
            "Celebrate your body for all it does for you.",
            "Rest when you need to. Rise when you are ready.",
            "Your cycle is your superpower.",
            "Be the friend to yourself that you are to others.",
            "Today is a good day to take care of yourself."
    };

    private void setupQuoteOfTheDay(View view) {
        android.widget.TextView tvQuote = view.findViewById(R.id.tvQuote);
        // Gunakan dayOfYear sebagai index — quote sama untuk hari yang sama
        int dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR);
        String quote = QUOTES[dayOfYear % QUOTES.length];
        tvQuote.setText(quote);
    }

    // ─── Data artikel ─────────────────────────────────────────────────────────

    private List<Article> getAllArticles() {
        List<Article> list = new ArrayList<>();

        // Period articles — pakai aset article_*.xml
        list.add(new Article(R.drawable.article_cramps,
                "5 Ways to Ease Cramps",
                "Simple, natural tips to find better remedy for your period",
                "5 min read", Article.Category.PERIOD));

        list.add(new Article(R.drawable.article_cd,
                "Menstrual Cycle",
                "Simple, natural tips to find better remedy for your period",
                "5 min read", Article.Category.PERIOD));

        list.add(new Article(R.drawable.article_flowers,
                "Self-care During Period",
                "Simple, natural tips to find better remedy for your period",
                "5 min read", Article.Category.PERIOD));

        // Wellness articles — placeholder pakai aset yang sama sampai aset baru tersedia
        list.add(new Article(R.drawable.article_flowers,
                "Staying Active During Your Cycle",
                "How light exercise can help reduce period symptoms",
                "4 min read", Article.Category.WELLNESS));

        list.add(new Article(R.drawable.article_cd,
                "Nutrition & Your Cycle",
                "Foods that support hormonal balance throughout the month",
                "6 min read", Article.Category.WELLNESS));

        // Fertility articles
        list.add(new Article(R.drawable.article_cramps,
                "Understanding Your Fertile Window",
                "Learn when you are most likely to conceive",
                "5 min read", Article.Category.FERTILITY));

        list.add(new Article(R.drawable.article_flowers,
                "Ovulation Signs to Watch For",
                "Natural signs your body gives during ovulation",
                "4 min read", Article.Category.FERTILITY));

        return list;
    }

    private List<Article> filterByCategory(Article.Category category) {
        List<Article> filtered = new ArrayList<>();
        for (Article a : getAllArticles()) {
            if (a.getCategory() == category) filtered.add(a);
        }
        return filtered;
    }

    // ─── Fragment lifecycle ───────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_learn, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabPeriod    = view.findViewById(R.id.tabPeriod);
        tabWellness  = view.findViewById(R.id.tabWellness);
        tabFertility = view.findViewById(R.id.tabFertility);

        // Quote of the day
        setupQuoteOfTheDay(view);

        // Menu button
        view.findViewById(R.id.btnMenuLearn).setOnClickListener(v ->
                startActivity(new android.content.Intent(requireContext(), NotificationActivity.class)));

        // Setup RecyclerView
        RecyclerView rv = view.findViewById(R.id.rvArticles);
        adapter = new ArticleAdapter(filterByCategory(currentCategory));
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        // Tab click listeners
        tabPeriod.setOnClickListener(v -> selectTab(Article.Category.PERIOD));
        tabWellness.setOnClickListener(v -> selectTab(Article.Category.WELLNESS));
        tabFertility.setOnClickListener(v -> selectTab(Article.Category.FERTILITY));

        // Article click — buka ArticleFragment dengan data artikel
        adapter.setOnArticleClickListener(article -> {
            // Body teks placeholder — nanti bisa diganti dengan konten nyata per artikel
            String body = "This is a detailed article about \"" + article.getTitle() + "\".\n\n"
                    + article.getSubtitle() + "\n\n"
                    + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
                    + "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. "
                    + "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.\n\n"
                    + "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum "
                    + "dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non "
                    + "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

            ArticleFragment articleFragment = ArticleFragment.newInstance(article, body);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right)
                    .replace(R.id.mainFragmentContainer, articleFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Set tab awal
        updateTabUI();
    }

    private void selectTab(Article.Category category) {
        currentCategory = category;
        adapter.setArticles(filterByCategory(category));
        updateTabUI();
    }

    private void updateTabUI() {
        // Reset semua tab ke inactive
        tabPeriod.setBackgroundResource(R.drawable.bg_tab_inactive);
        tabPeriod.setTextColor(requireContext().getColor(R.color.primary_pink));

        tabWellness.setBackgroundResource(R.drawable.bg_tab_inactive);
        tabWellness.setTextColor(requireContext().getColor(R.color.primary_pink));

        tabFertility.setBackgroundResource(R.drawable.bg_tab_inactive);
        tabFertility.setTextColor(requireContext().getColor(R.color.primary_pink));

        // Set tab aktif
        TextView activeTab;
        switch (currentCategory) {
            case WELLNESS:  activeTab = tabWellness;  break;
            case FERTILITY: activeTab = tabFertility; break;
            default:        activeTab = tabPeriod;    break;
        }
        activeTab.setBackgroundResource(R.drawable.bg_tab_active);
        activeTab.setTextColor(requireContext().getColor(R.color.white));
    }
}
