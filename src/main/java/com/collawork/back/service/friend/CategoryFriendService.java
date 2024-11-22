package com.collawork.back.service.friend;

import com.collawork.back.model.auth.User;
import com.collawork.back.model.friends.Category;
import com.collawork.back.model.friends.CategoryFriend;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.friends.CategoryFriendRepository;
import com.collawork.back.repository.friends.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryFriendService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryFriendRepository categoryFriendRepository;

    public CategoryFriendService(CategoryRepository categoryRepository, UserRepository userRepository, CategoryFriendRepository categoryFriendRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.categoryFriendRepository = categoryFriendRepository;
    }

    public void addFriendsToCategory(Long categoryId, List<Long> friendIds) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 categoryId" + categoryId));

        List<User> friends = userRepository.findAllById(friendIds);
        if (friends.isEmpty()) {
            throw new IllegalArgumentException("제공된 ID에 적합한 친구를 찾을 수 없습니다");
        }

        List<CategoryFriend> categoryFriends = friends.stream()
                .map(friend -> {
                    CategoryFriend categoryFriend = new CategoryFriend();
                    categoryFriend.setCategory(category);
                    categoryFriend.setFriend(friend);
                    return categoryFriend;
                })
                .toList();

        categoryFriendRepository.saveAll(categoryFriends);
    }

    public void removeFriendsFromCategory(Long categoryId, List<Long> friendIds) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        // 제거할 친구-카테고리 관계 삭제
        List<CategoryFriend> categoryFriends = categoryFriendRepository.findByCategoryIdAndFriendIdIn(categoryId, friendIds);

        if (!categoryFriends.isEmpty()) {
            categoryFriendRepository.deleteAll(categoryFriends);
        }
    }

    public void deleteCategory(Long categoryId) {
        if (categoryRepository.existsById(categoryId)) {
            categoryRepository.deleteById(categoryId);
        } else {
            throw new IllegalArgumentException("카테고리 아이디  " + categoryId + " 은(는) 존재하지 않습니다.");
        }
    }
}

