package edu.iit.news;

import java.io.Serializable;

public class Source implements Serializable, Comparable<Source> {
    private String newsSourceId;
    private String name;
    private String urlNews;
    private String NewsCategory;

    public Source() {
    }

    public String getNewsSourceId() {
        return newsSourceId;
    }

    public void setNewsSourceId(String newsSourceId) {
        this.newsSourceId = newsSourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlNews() {
        return urlNews;
    }

    public void setUrlNews(String urlNews) {
        this.urlNews = urlNews;
    }

    public String getCategory() {
        return NewsCategory;
    }

    public void setCategory(String NewsCategory) {
        this.NewsCategory = NewsCategory;
    }

    public int compareTo(Source other) {
        return name.compareTo(other.name);
    }

}
