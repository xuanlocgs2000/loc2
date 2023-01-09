package com.example.android.docbao.MainActivity;

public class ArticleObject {
    public int id;
    public String title;
    public String link;
    public String image;
    public String date;




    public ArticleObject(String title, String link, String image, String date) {
        this.title = title;
        this.link = link;
        this.image = image;
        this.date = date;
    }

    public ArticleObject(int id, String title, String link, String image, String date) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.image = image;
        this.date = date;
    }
}
