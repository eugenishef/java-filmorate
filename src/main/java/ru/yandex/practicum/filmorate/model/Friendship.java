package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friendship {
    private long userId;
    private long friendId;
    private String status;

    public Friendship(long userId, long friendId, String status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "userId=" + userId +
                ", friendId=" + friendId +
                ", status='" + status + '\'' +
                '}';
    }
}
