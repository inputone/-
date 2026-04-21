package com.huanghaha.treehole.controller;

import com.huanghaha.treehole.common.Result;
import com.huanghaha.treehole.entity.Post;
import com.huanghaha.treehole.entity.User;
import com.huanghaha.treehole.service.FavoriteService;
import com.huanghaha.treehole.service.LikeService;
import com.huanghaha.treehole.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子控制器
 * 提供帖子发布、列表查询、分页查询、删除、点赞、收藏相关接口
 * 发布/删除/点赞/收藏相关接口需登录（由 LoginInterceptor 拦截）
 */
@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FavoriteService favoriteService;

    /** 发布帖子（需登录） */
    @PostMapping("/publish")
    public Result<String> publish(@RequestParam String content, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        postService.publish(user.getId(), content);
        return Result.success("发布成功");
    }

    /** 查询所有帖子（无需登录） */
    @GetMapping("/list")
    public Result<List<Post>> list() {
        List<Post> postList = postService.listAll();
        return Result.success(postList);
    }

    /** 分页查询帖子，支持 Redis 缓存（无需登录） */
    @GetMapping("/page")
    public Result<Map<String, Object>> page(@RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        Map<String, Object> result = postService.listByPage(page, size);
        return Result.success(result);
    }

    /** 删除自己的帖子（需登录） */
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam Long id, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        postService.delete(id, user.getId());
        return Result.success("删除成功");
    }

    /** 切换点赞状态（需登录） */
    @PostMapping("/like/{postId}")
    public Result<String> toggleLike(@PathVariable Long postId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        likeService.toggleLike(user.getId(), postId);
        return Result.success("操作成功");
    }

    /** 查询当前用户是否已点赞某帖子（需登录） */
    @GetMapping("/liked/{postId}")
    public Result<Map<String, Object>> isLiked(@PathVariable Long postId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        boolean liked = likeService.isLiked(user.getId(), postId);
        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        return Result.success(data);
    }

    /** 切换收藏状态（需登录） */
    @PostMapping("/favorite/{postId}")
    public Result<String> toggleFavorite(@PathVariable Long postId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        favoriteService.toggleFavorite(user.getId(), postId);
        return Result.success("操作成功");
    }

    /** 查询当前用户是否已收藏某帖子（需登录） */
    @GetMapping("/favorited/{postId}")
    public Result<Map<String, Object>> isFavorited(@PathVariable Long postId, HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        boolean favorited = favoriteService.isFavorited(user.getId(), postId);
        Map<String, Object> data = new HashMap<>();
        data.put("favorited", favorited);
        return Result.success(data);
    }

    /** 获取当前用户收藏的帖子列表（需登录） */
    @GetMapping("/favorites")
    public Result<List<Post>> favorites(HttpSession session) {
        User user = (User) session.getAttribute("loginUser");
        List<Post> posts = favoriteService.listByUserId(user.getId());
        return Result.success(posts);
    }

}
