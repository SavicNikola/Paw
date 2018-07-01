package com.mosis.paw;

public class FeedItem {
    private String imePrezime;
    private int thumbnail;
    private String timeAgo;
    private String postDesc;
    private int postPicture;

    private Boolean favourite;

    private FeedTypeEnum feedType;

    public FeedItem() {

    }

    public FeedItem(String imePrezime, int thumbnail, String timeAgo, String postDesc, int postPicture, FeedTypeEnum type, Boolean favourite) {
        this.imePrezime = imePrezime;
        this.thumbnail = thumbnail;
        this.timeAgo = timeAgo;
        this.postDesc = postDesc;
        this.postPicture = postPicture;
        this.feedType = type;
        this.favourite = favourite;
    }

    public String getImePrezime() {
        return imePrezime;
    }

    public void setImePrezime(String imePrezime) {
        this.imePrezime = imePrezime;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public int getPostPicture() {
        return postPicture;
    }

    public void setPostPicture(int postPicture) {
        this.postPicture = postPicture;
    }

    public Boolean getFavoruite() {
        return favourite;
    }

    public void setFavoruite(Boolean favourite) {
        this.favourite = favourite;
    }

    public FeedTypeEnum getFeedType() {
        return feedType;
    }
}
