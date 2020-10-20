package com.my.blog.controller.admin;

import com.my.blog.pojo.Type;
import com.my.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TypeController {

    @Autowired
    private TypeService typeService;

    //管理分类页面，列出所有类型
    @GetMapping("/types")
    public String types(@PageableDefault(size = 3,sort = {"id"},direction = Sort.Direction.DESC)
                                    Pageable pageable, Model model){
        model.addAttribute("page",typeService.listType(pageable));
        return "admin/types";
    }

    //来到新增分类页面
    @GetMapping("/types/input")
    public String input(Model model){
        model.addAttribute("type",new Type());
        return "admin/types-input";
    }

    //来到修改页面（其实和新增页面共用一个页面）
    @GetMapping("/types/{id}/input")
    public String editInput(@PathVariable Long id, Model model){
        model.addAttribute("type",typeService.getType(id));
        return "admin/types-input";
    }

    //新增
    @PostMapping("/types")
    public String post(@Valid Type type, BindingResult bindingResult, RedirectAttributes attributes){
        Type type1 = typeService.getTypeByName(type.getName());
        if(type1!=null){
            bindingResult.rejectValue("name","nameError","不能添加重复分类");
        }
        if(bindingResult.hasErrors()){
            return "admin/types-input";
        }

        Type t = typeService.saveType(type);
        if(t==null){
            attributes.addFlashAttribute("message","新增失败！");
        }else{
            attributes.addFlashAttribute("message","新增成功！");
        }
        return "redirect:/admin/types";
    }

    //修改
    @PostMapping("/types/{id}")
    public String editPost(@Valid Type type, BindingResult bindingResult,@PathVariable Long id, RedirectAttributes attributes){
        Type type1 = typeService.getTypeByName(type.getName());
        if(type1!=null){
            bindingResult.rejectValue("name","nameError","不能添加重复分类");
        }
        if(bindingResult.hasErrors()){
            return "admin/types-input";
        }

        Type t = typeService.updateType(id,type);
        if(t==null){
            attributes.addFlashAttribute("message","更新失败！");
        }else{
            attributes.addFlashAttribute("message","更新成功！");
        }
        return "redirect:/admin/types";
    }

    @GetMapping("/types/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes){
        typeService.deleteType(id);
        attributes.addFlashAttribute("message","删除成功！");
        return "redirect:/admin/types";
    }

}
