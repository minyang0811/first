package com.my.blog.controller.admin;

import com.my.blog.pojo.Blog;
import com.my.blog.pojo.BlogQuery;
import com.my.blog.pojo.User;
import com.my.blog.service.BlogService;
import com.my.blog.service.TagService;
import com.my.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.Id;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class BlogController {

    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    //博客查看页面
    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size=2,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog, Model model){
        model.addAttribute("types",typeService.listType());
        model.addAttribute("page",blogService.listBlog(pageable,blog));
        return LIST;
    }

    //博客搜索查询
    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size=2,sort = {"updateTime"},direction = Sort.Direction.DESC) Pageable pageable,
                         BlogQuery blog, Model model){

        model.addAttribute("page",blogService.listBlog(pageable,blog));
        return "admin/blogs::blogList"; //局部刷新
    }

    //跳转到新增页面
    @GetMapping("/blogs/input")
    public String input(Model model){
        setTypeAndTag(model);

        model.addAttribute("blog",new Blog());
        return INPUT;
    }

    private void setTypeAndTag(Model model){
        //初始化分类和标签
        model.addAttribute("types",typeService.listType());
        model.addAttribute("tags",tagService.listTag());
    }

    //跳转到修改页面
    @GetMapping("/blogs/{id}/input")
    public String editInput(@PathVariable Long id, Model model){
        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);
        blog.init(); //拿到 tagsId
        model.addAttribute("blog",blog);
        return INPUT;
    }

    //新增和修改的操作
    @PostMapping("/blogs")
    public String blog(Blog blog, HttpSession session, RedirectAttributes attributes){

        blog.setUser((User) session.getAttribute("user"));
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));

        Blog b;
        if(blog.getId()==null){
            b = blogService.saveBlog(blog);  //新增
        }else{
            b = blogService.updateBlog(blog.getId(),blog); //修改
        }

        if(b == null){
            attributes.addFlashAttribute("message","操作失败");
        }else{
            attributes.addFlashAttribute("message","操作成功");
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message","删除成功！");
        return REDIRECT_LIST;
    }
}
