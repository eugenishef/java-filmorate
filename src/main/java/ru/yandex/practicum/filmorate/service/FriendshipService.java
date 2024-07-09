package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

public class FriendshipService {
    private final List<Friendship> friendships = new ArrayList<>();

    public void addFriend(User user, User friend) {
        friendships.add(new Friendship(user.getId(), friend.getId(), "pending"));
        user.getFriends().add((int) friend.getId());
        friend.getFriends().add((int) user.getId());
    }

    public void acceptFriendRequest(long userId, long friendId) {
        for (Friendship friendship : friendships) {
            if (friendship.getUserId() == userId && friendship.getFriendId() == friendId) {
                friendship.setStatus("accepted");
            } else if (friendship.getUserId() == friendId && friendship.getFriendId() == userId) {
                friendship.setStatus("accepted");
            }
        }
    }

    public void removeFriend(User user, User friend) {
        friendships.removeIf(friendship ->
                (friendship.getUserId() == user.getId() && friendship.getFriendId() == friend.getId()) ||
                        (friendship.getUserId() == friend.getId() && friendship.getFriendId() == user.getId())
        );
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }

    public List<Friendship> getFriends(long userId) {
        List<Friendship> result = new ArrayList<>();
        for (Friendship friendship : friendships) {
            if (friendship.getUserId() == userId || friendship.getFriendId() == userId) {
                result.add(friendship);
            }
        }
        return result;
    }
}
