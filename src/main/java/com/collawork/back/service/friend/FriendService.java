package com.collawork.back.service.friend;

import com.collawork.back.model.auth.User;
import com.collawork.back.model.friends.CategoryFriend;
import com.collawork.back.repository.friends.CategoryFriendRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendService {

    private final CategoryFriendRepository categoryFriendRepository;

    public FriendService(CategoryFriendRepository categoryFriendRepository) {
        this.categoryFriendRepository = categoryFriendRepository;
    }

    public List<User> getFriendsByCategory(Long categoryId) {
        List<CategoryFriend> categoryFriends = categoryFriendRepository.findByCategoryId(categoryId);
        return categoryFriends.stream()
                .map(CategoryFriend::getFriend)
                .collect(Collectors.toList());
    }

}
