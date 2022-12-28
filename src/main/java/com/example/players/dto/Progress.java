package com.example.players.dto;

import jakarta.validation.constraints.Min;

public class Progress {
    @Min(1)
    private long id;

    @Min(1)
    private long playerId;

    @Min(1)
    private long resourceId;

    private int score;

    @Min(1)
    private int maxScore;

    public Progress(long id, long playerId, long resourceId, int score, int maxScore) {
        this.id = id;
        this.playerId = playerId;
        this.resourceId = resourceId;
        this.score = score;
        this.maxScore = maxScore;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    @Override
    public String toString() {
        return "{id=" + id +
                ", resourceId=" + resourceId +
                ", score=" + score +
                ", maxScore=" + maxScore +
                '}';
    }
}
