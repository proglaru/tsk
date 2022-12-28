package com.example.players.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class Currency {
    @Min(1)
    private long id;

    @Min(1)
    private long resourceId;

    @NotBlank
    private String name;

    @Min(1)
    private int count;

    public Currency(long id, long resourceId, String name, int count) {
        this.id = id;
        this.resourceId = resourceId;
        this.name = name;
        this.count = count;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "{id=" + id +
                ", resourceId=" + resourceId +
                ", name='" + name + '\'' +
                ", count=" + count +
                '}';
    }
}
