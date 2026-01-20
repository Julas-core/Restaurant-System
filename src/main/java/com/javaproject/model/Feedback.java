package com.javaproject.model;

import java.sql.Timestamp;

public class Feedback {
    private long id;
    private long userId;
    private int rating;
    private String comment;
    private Timestamp createdAt;
    // Optional: caching user name if needed for display, otherwise fetch it
    private String userName;
    private String adminReply;
    private boolean archived;
    private Long menuItemId;
    private String menuItemName;
    private String tags;

    public Feedback(long id, long userId, int rating, String comment, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }
    
    // Full constructor for retrieval
    public Feedback(long id, long userId, String userName, int rating, String comment, Timestamp createdAt, String adminReply, boolean archived, Long menuItemId, String menuItemName, String tags) {
        this(id, userId, rating, comment, createdAt);
        this.userName = userName;
        this.adminReply = adminReply;
        this.archived = archived;
        this.menuItemId = menuItemId;
        this.menuItemName = menuItemName;
        this.tags = tags;
    }

    public long getId() { return id; }
    public long getUserId() { return userId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getUserName() { return userName; }
    public String getAdminReply() { return adminReply; }
    public boolean isArchived() { return archived; }
    public Long getMenuItemId() { return menuItemId; }
    public String getMenuItemName() { return menuItemName; }
    public String getTags() { return tags; }

    public void setUserName(String userName) { this.userName = userName; }
    public void setAdminReply(String adminReply) { this.adminReply = adminReply; }
    public void setArchived(boolean archived) { this.archived = archived; }
    public void setTags(String tags) { this.tags = tags; }
}
