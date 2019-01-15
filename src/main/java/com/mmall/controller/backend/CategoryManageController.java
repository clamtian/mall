package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by lucky on 2019/1/15.
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 判断当前登录用户是否有效
     * @param session
     * @return
     */
    public ServerResponse judgeUser(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }else if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return ServerResponse.createByErrorMessage("当前用户不是管理员，无操作权限");
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 获取品类模块
     * @param categoryId
     * @param session
     * @return
     */
    @RequestMapping(value="get_category.do" ,method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse getCategory(@RequestParam(value="categoryId",defaultValue = "0") Integer categoryId, HttpSession session){
        ServerResponse response = judgeUser(session);
        if(!response.isSuccess()){
            return response;
        }
        response = iCategoryService.getCategory(categoryId);
        return response;
    }

    /**
     * 添加品类模块
     * @param parentId
     * @param categoryName
     * @param session
     * @return
     */
    @RequestMapping(value="add_category.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(@RequestParam(value="parentId",defaultValue = "0")Integer parentId,String categoryName,HttpSession session){
        ServerResponse response = judgeUser(session);
        if(!response.isSuccess()){
            return response;
        }
        response = iCategoryService.addCategory(parentId,categoryName);
        return response;
    }

    /**
     * 更新品类模块
     * @param categoryId
     * @param categoryName
     * @param session
     * @return
     */
    @RequestMapping(value="set_category_name.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(Integer categoryId,String categoryName,HttpSession session){
        ServerResponse response = judgeUser(session);
        if(!response.isSuccess()){
            return response;
        }
        response = iCategoryService.setCategoryName(categoryId,categoryName);
        return response;
    }

    @RequestMapping(value="get_deep_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDeepCategory(Integer categoryId,HttpSession session){
        ServerResponse response = judgeUser(session);
        if(!response.isSuccess()){
            return response;
        }
        response = iCategoryService.getDeepCategory(categoryId);
        return response;
    }
}
