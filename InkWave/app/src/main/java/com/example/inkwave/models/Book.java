package com.example.inkwave.models;

import com.google.gson.annotations.SerializedName;

public class Book {
    @SerializedName("id")
    private Long id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("cover_url")
    private String coverUrl;

    @SerializedName("author_id")
    private String authorId;

    @SerializedName("status")
    private String status;

    @SerializedName("views")
    private int views;

    @SerializedName("likes")
    private int likes;

    // Для отображения в списке (имя автора пока не из базы)
    private String authorName = "Автор";

    public Book() {}

    // Геттеры
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCoverUrl() { return coverUrl; }
    public String getAuthorId() { return authorId; }
    public String getStatus() { return status; }
    public int getViews() { return views; }
    public int getLikes() { return likes; }
    public String getAuthorName() { return authorName; }

    // Сеттеры
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public void setStatus(String status) { this.status = status; }
    public void setViews(int views) { this.views = views; }
    public void setLikes(int likes) { this.likes = likes; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
}