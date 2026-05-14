package com.example.tsuki;

public class Article {

    public enum Category { PERIOD, WELLNESS, FERTILITY }

    private final int imageRes;
    private final String title;
    private final String subtitle;
    private final String readTime;
    private final Category category;

    public Article(int imageRes, String title, String subtitle,
                   String readTime, Category category) {
        this.imageRes = imageRes;
        this.title    = title;
        this.subtitle = subtitle;
        this.readTime = readTime;
        this.category = category;
    }

    public int getImageRes()    { return imageRes; }
    public String getTitle()    { return title; }
    public String getSubtitle() { return subtitle; }
    public String getReadTime() { return readTime; }
    public Category getCategory() { return category; }
}
