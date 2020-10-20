package com.my.blog.controller;

import com.my.blog.pojo.Comment;
import com.my.blog.pojo.User;
import com.my.blog.service.BlogService;
import com.my.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    @Value("${comment.avatar}")
    private String avatar;

    //博客评论列表展示
    @GetMapping("comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model){
        model.addAttribute("comments",commentService.listCommentByBlogId(blogId));
        System.out.println(avatar);
        return "blog::commentList"; //局部刷新
    }

    //添加评论并保存
    @PostMapping("/comments")
    public String post(Comment comment, HttpSession session){
        Long blogId = comment.getBlog().getId();
        comment.setBlog(blogService.getBlog(blogId)); //设置评论的的博客信息

        User user = (User) session.getAttribute("user");
        if(user!=null){
            comment.setAvatar(user.getAvatar());//设置管理员头像
            comment.setAdminComment(true);
            //comment.setNickname(user.getNickname());
        }else {
            comment.setAvatar(avatar);  //设置普通评论头像
        }
        commentService.saveComment(comment);
        return "redirect:/comments/"+comment.getBlog().getId();
    }
}
