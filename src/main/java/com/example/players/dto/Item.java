package com.example.players.dto;

import jakarta.validation.constraints.Min;

public class Item {
    @Min(1)
    private long id;

    @Min(1)
    private long resourceId;

    @Min(1)
    private int count;

    @Min(1)
    private int level;

    public Item(long id, long resourceId, int count, int level) {
        this.id = id;
        this.resourceId = resourceId;
        this.count = count;
        this.level = level;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "{id=" + id +
                ", resourceId=" + resourceId +
                ", count=" + count +
                ", level=" + level +
                '}';
    }
}
