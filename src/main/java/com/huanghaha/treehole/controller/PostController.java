package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.FavoriteService;
import com.huanghaha.treehole.service.LikeService;
import com.huanghaha.treehole.service.PostService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    @Resource
    private PostService postService;

    @Resource
    private LikeService likeService;

    @Resource
    private FavoriteService favoriteService;

    @PostMapping("/publish")
    public Result<String> publish(@RequestParam String content, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        postService.publish(user.getId(), content);
        return Result.success("发布成功");
    }

    @GetMapping("/list")
    public Result<List<Post>> list() {
        List<Post> postList = postService.listAll();
        return Result.success(postList);
    }

    @GetMapping("/page")
    public Result<Map<String, Object>> page(@RequestParam(required = false) Integer page,
                                             @RequestParam(required = false) Integer size) {
        Map<String, Object> result = postService.listByPage(page, size);
        return Result.success(result);
    }

    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        postService.delete(id, user.getId());
        return Result.success("删除成功");
    }

    @PostMapping("/like/{postId}")
    public Result<String> toggleLike(@PathVariable Long postId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        likeService.toggleLike(user.getId(), postId);
        return Result.success("操作成功");
    }

    @GetMapping("/liked/{postId}")
    public Result<Map<String, Object>> isLiked(@PathVariable Long postId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        boolean liked = likeService.isLiked(user.getId(), postId);
        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        return Result.success(data);
    }

    @PostMapping("/favorite/{postId}")
    public Result<String> toggleFavorite(@PathVariable Long postId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        favoriteService.toggleFavorite(user.getId(), postId);
        return Result.success("操作成功");
    }

    @GetMapping("/favorited/{postId}")
    public Result<Map<String, Object>> isFavorited(@PathVariable Long postId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        boolean favorited = favoriteService.isFavorited(user.getId(), postId);
        Map<String, Object> data = new HashMap<>();
        data.put("favorited", favorited);
        return Result.success(data);
    }

    @GetMapping("/favorites")
    public Result<List<Post>> favorites(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        List<Post> posts = favoriteService.listByUserId(user.getId());
        return Result.success(posts);
    }

}
