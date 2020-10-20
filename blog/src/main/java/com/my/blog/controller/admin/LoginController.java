package com.my.blog.controller.admin;

import com.my.blog.pojo.User;
import com.my.blog.service.UserService;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String loginPage(){
        return "admin/login";
    }

    //登录
    @PostMapping("login")
    public String login(@RequestParam String username, @RequestParam String password,
                        HttpSession session, RedirectAttributes attributes){
        User user = userService.checkUser(username,password);
        if(user !=null){
            user.setPassword(null);
            session.setAttribute("user",user); //将user放到session域中
            return "admin/index";
        }else{
            attributes.addFlashAttribute("message","用户名和密码错误！");//重定向返回错误信息给页面，不能使用Model
            return "redirect:/admin"; //登录失败，重定向到登录页面
        }
    }

    //注销
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        return "redirect:/admin";
    }
}
