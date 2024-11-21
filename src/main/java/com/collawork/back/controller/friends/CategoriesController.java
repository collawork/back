package com.collawork.back.controller.friends;

import com.collawork.back.model.auth.User;
import com.collawork.back.model.friends.Category;
import com.collawork.back.model.friends.CategoryFriend;
import com.collawork.back.repository.auth.UserRepository;
import com.collawork.back.repository.friends.CategoryFriendRepository;
import com.collawork.back.repository.friends.CategoryRepository;
import com.collawork.back.service.friend.CategoryFriendService;
import com.collawork.back.service.friend.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.collawork.back.security.JwtTokenProvider;

import java.util.Collections;
import java.util.List;


/**
 * 작성자 : 김동규 / 작성일 : 2024-11-21
 * 설명 : 친구 목록 카테고리 추가 처리 컨트롤러
 * */
@Controller
@RequestMapping("/api/category")
//@CrossOrigin(origins = "http://localhost:3000")
public class CategoriesController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryFriendService categoryFriendService;

    @Autowired
    private FriendService friendService;

    @Autowired
    private CategoryFriendRepository categoryFriendRepository;

    @PostMapping("/categories/create")
    public ResponseEntity<String> createCategory(@RequestParam("name") String name,
                                                 @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
//            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("유효하지 않은 요청입니다.");
            }

            String token = authorizationHeader.replace("Bearer ", "");
            String email = jwtTokenProvider.getEmailFromToken(token);

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("인증 실패");
            }

            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("사용자를 찾을 수 없습니다.");
            }

            Category category = new Category();

            if (name == null || name.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("카테고리 이름이 유효하지 않습니다.");
            }

            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 사용자입니다.");
            }

            category.setName(name);
            category.setUser(user);

            System.out.println("Saving category: " + category);

            categoryRepository.save(category);

            return ResponseEntity.ok("카테고리가 생성되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카테고리 생성 실패");
        }
    }



    @PostMapping("/categories/{categoryId}/add-friends")
    public ResponseEntity<?> addFriendsToCategory(
            @PathVariable Long categoryId,
            @RequestBody List<Long> friendIds) {
        try {
            categoryFriendService.addFriendsToCategory(categoryId, friendIds);
            return ResponseEntity.ok("Friends added to category successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding friends to category: " + e.getMessage());
        }
    }


    @GetMapping("/categories/list")
    public ResponseEntity<List<Category>> getCategories(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            System.out.println("Authorization 헤더: " + authorizationHeader);

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
            }

            String token = authorizationHeader.replace("Bearer ", "");
            String email = jwtTokenProvider.getEmailFromToken(token);

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
            }

            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.emptyList());
            }

            List<Category> categories = categoryRepository.findByUserId(user.getId());
            return ResponseEntity.ok(categories);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/categories/{categoryId}/friends")
    public ResponseEntity<List<User>> getFriendsByCategory(@PathVariable Long categoryId) {
        List<User> friends = friendService.getFriendsByCategory(categoryId);
        return ResponseEntity.ok(friends);
    }

    @PostMapping("/categories/{categoryId}/remove-friends")
    public ResponseEntity<Void> removeFriendsFromCategory(
            @PathVariable Long categoryId,
            @RequestBody List<Long> friendIds) {
        categoryFriendService.removeFriendsFromCategory(categoryId, friendIds);
        return ResponseEntity.ok().build();
    }


}
