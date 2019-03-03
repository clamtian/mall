package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by lucky on 2019/1/15.
 */

/**
 * 商品分类管理
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

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
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        ServerResponse response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return response;
        }
        response = iCategoryService.addCategory(parentId,categoryName);
        return response;
    }

    /**
     * 修改品类模块
     * @param categoryId
     * @param categoryName
     * @param session
     * @return
     */
    @RequestMapping(value="set_category_name.do",method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(Integer categoryId,String categoryName,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        ServerResponse response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return response;
        }
        response = iCategoryService.setCategoryName(categoryId,categoryName);
        return response;
    }
    /**
     * 获取品类子节点（不递归）
     * @param categoryId
     * @param session
     * @return
     */
    @RequestMapping(value="get_category.do" ,method=RequestMethod.POST)
    @ResponseBody
    public ServerResponse getCategory(@RequestParam(value="categoryId",defaultValue = "0") Integer categoryId, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        ServerResponse response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return response;
        }
        response = iCategoryService.getCategory(categoryId);
        return response;
    }

    /**
     * 获取当前分类id及递归子节点categoryId
     * @param categoryId
     * @param session
     * @return
     */
    @RequestMapping(value="get_deep_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDeepCategory(Integer categoryId,HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        ServerResponse response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return response;
        }
        response = iCategoryService.selectCategoryAndChildrenById(categoryId);
        return response;
    }
}
