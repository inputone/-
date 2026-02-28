package com.huanghaha.treehole;

import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.mapper.UserMapper;
import com.huanghaha.treehole.service.PostService;
import com.huanghaha.treehole.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;
import java.util.List;

@SpringBootTest
public class PostServiceTest {

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    private User createTestUser() {
        String username = "user_" + System.currentTimeMillis();
        userService.register(username, "123456");

        User user = userMapper.findByUsername(username);
        user.setStatus(1);
        userMapper.insert(user); // 如果你没有 update 方法，需要补一个

        return user;
    }

    @Test
    public void testPublishSuccess() {

        User user = createTestUser();

        postService.publish(user.getId(), "今天心情不错");

        List<Post> posts = postService.listAll();

        Assertions.assertFalse(posts.isEmpty());
    }

    @Test
    public void testPublishEmptyContent() {

        User user = createTestUser();

        Assertions.assertThrows(RuntimeException.class, () -> {
            postService.publish(user.getId(), "");
        });
    }

    @Test
    public void testDeleteOwnPost() {

        User user = createTestUser();

        postService.publish(user.getId(), "测试删除");

        List<Post> posts = postService.listAll();
        Long postId = posts.get(0).getId();

        postService.delete(postId, user.getId());

        Post deleted = null;
        for (Post p : postService.listAll()) {
            if (p.getId().equals(postId)) {
                deleted = p;
            }
        }

        Assertions.assertNull(deleted);
    }

}