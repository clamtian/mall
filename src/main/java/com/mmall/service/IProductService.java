package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

/**
 * Created by lucky on 2019/2/21.
 */
public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);
}
