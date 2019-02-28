package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
     * 获取品类子节点
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Category>> getCategory(Integer categoryId) {
        List<Category> category = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(category.isEmpty()){
            return ServerResponse.createByErrorMessage("查询失败，没有该品类");
        }
        return ServerResponse.createBySuccess("查询成功",category);
    }

    /**
     * 增加品类
     * @param parentId
     * @param categoryName
     * @return
     */
    @Override
    public ServerResponse<String> addCategory(Integer parentId, String categoryName) {
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("添加成功");
        }
        return ServerResponse.createByErrorMessage("添加失败");
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
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage("更新成功");
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    /**
     * 获取当前分类id及递归子节点categoryId
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> getDeepCategory(Integer categoryId) {
        if(categoryId == null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Set<Category> categoryIdsSet = Sets.newHashSet();
        ServerResponse response = findChildrenIds(categoryIdsSet,categoryId);
        if(!response.isSuccess()){
            return response;
        }
        List<Integer> categoryIds = Lists.newArrayList();
        for(Category category : categoryIdsSet){
            categoryIds.add(category.getId());
        }
        return ServerResponse.createBySuccess("查询成功",categoryIds);
    }


    public ServerResponse findChildrenIds(Set categoryIdsSet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category == null){
            return ServerResponse.createByErrorMessage("查询失败，没有此品类");
        }
        categoryIdsSet.add(category);
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(category.getId());
        for(Category categoryItem : categoryList){
            findChildrenIds(categoryIdsSet,categoryItem.getId());
        }
        return ServerResponse.createBySuccess();
    }

    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildrenIds(categorySet,categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }
}
