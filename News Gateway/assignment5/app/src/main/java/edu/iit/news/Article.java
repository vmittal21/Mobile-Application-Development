package edu.iit.news;

import java.io.Serializable;

public class Article implements Serializable {
    private String newAuthor;
    private String newsTitle;
    private String newsDesc;
    private String apiAddress;
    private String newsImage;
    private String dt_Time;

    public Article() {
    }

    public String getNewAuthor() {
        return newAuthor;
    }

    public void setNewAuthor(String newAuthor) {
        this.newAuthor = newAuthor;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsDesc() {
        return newsDesc;
    }

    public void setNewsDesc(String newsDesc) {
        this.newsDesc = newsDesc;
    }

    public String getApiAddress() {
        return apiAddress;
    }

    public void setApiAddress(String apiAddress) {
        this.apiAddress = apiAddress;
    }

    public String getUrl2image() {
        return newsImage;
    }

    public void setUrl2image(String url2image) {
        this.newsImage = url2image;
    }

    public String getDt_Time() {
        return dt_Time;
    }

    public void setDt_Time(String dt_Time) {
        this.dt_Time = dt_Time;
    }
}
