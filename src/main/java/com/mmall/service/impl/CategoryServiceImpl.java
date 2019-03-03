package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by lucky on 2019/1/15.
 */

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 增加品类
     * @param parentId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse<String> addCategory(Integer parentId, String categoryName) {
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage(ResponseCode.SUCCESS.getDesc());
        }
        return ServerResponse.createByErrorMessage(ResponseCode.ERROR.getDesc());
    }

    /**
     * 修改品类名字
     * @param categoryId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse<String> setCategoryName(Integer categoryId, String categoryName) {
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage(ResponseCode.SUCCESS.getDesc());
        }
        return ServerResponse.createByErrorMessage(ResponseCode.ERROR.getDesc());
    }
    /**
     * 获取品类子节点
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getCategory(Integer categoryId) {
        List<Category> category = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(category.isEmpty()){
            return ServerResponse.createByErrorMessage(ResponseCode.ERROR.getDesc());
        }
        return ServerResponse.createBySuccess(ResponseCode.SUCCESS.getDesc(),category);
    }

    /**
     * 获取当前分类id及递归子节点categoryId
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Category>> selectCategoryAndChildrenById(Integer categoryId) {
        if(categoryId == null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Set<Category> categorySet = Sets.newHashSet();
        ServerResponse response = this.findChildrenIds(categorySet, categoryId);
        if(!response.isSuccess()){
            return response;
        }
        List<Category> categoryIds = Lists.newArrayList();
        for(Category category : categorySet){
            categoryIds.add(category);
        }
        return ServerResponse.createBySuccess(ResponseCode.SUCCESS.getDesc(),categoryIds);
    }

    /**
     * 查找子类节点
     * @param categorySet
     * @param categoryId
     * @return
     */
    private ServerResponse findChildrenIds(Set categorySet, Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category == null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        categorySet.add(category);
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(category.getId());
        for(Category categoryItem : categoryList){
            findChildrenIds(categorySet, categoryItem.getId());
        }
        return ServerResponse.createBySuccess(categorySet);
    }
}
