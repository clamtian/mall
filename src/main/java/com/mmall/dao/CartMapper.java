package com.mmall.dao;

import com.mmall.pojo.Cart;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List selectCartByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    Cart selectCartByUserIdProductId(Integer userId, Integer productId);

    int deleteByUserIdProductIds(Integer userId,List productList);

    int checkedOrUncheckedProduct(Integer userId,Integer productId,Integer checked);

    int selectCartProductCount(Integer userId);

}