package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucky on 2019/2/21.
 */

@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 新增或更新产品
     * @param product
     * @return
     */
    public ServerResponse saveOrUpdateProduct(Product product){
        if(product != null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subImageArray = product.getSubImages().split(",");
                if(subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            if(product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if(rowCount > 0){
                    return ServerResponse.createBySuccessMessage(ResponseCode.SUCCESS.getDesc());
                }
                return ServerResponse.createBySuccessMessage(ResponseCode.ERROR.getDesc());
            }else{
                int rowCount = productMapper.insert(product);
                if(rowCount > 0){
                    return ServerResponse.createBySuccessMessage(ResponseCode.SUCCESS.getDesc());
                }
                return ServerResponse.createBySuccessMessage(ResponseCode.ERROR.getDesc());
            }
        }
        return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getDesc());
    }

    /**
     * 更新产品上下架状态
     * @param productId
     * @param status
     * @return
     */
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status){
        if(productId == null || status == null){
            return ServerResponse.createByErrorMessageCode(ResponseCode.ILLEGAL_ARGUMENT.getDesc(), ResponseCode.ILLEGAL_ARGUMENT.getCode());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount > 0){
            return ServerResponse.createBySuccessMessage(ResponseCode.SUCCESS.getDesc());
        }else{
            return ServerResponse.createByErrorMessage(ResponseCode.ERROR.getDesc());
        }
    }

    /**
     * 获取商品详情
     * @param productId
     * @return
     */
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return ServerResponse.createByErrorMessageCode(ResponseCode.ILLEGAL_ARGUMENT.getDesc(),
                    ResponseCode.ILLEGAL_ARGUMENT.getCode());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage(ResponseCode.ERROR.getDesc());
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage(ResponseCode.NOT_ON_SALE.getDesc());
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 后台商品列表动态分页
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 商品搜索
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }
    /**
     * 产品搜索及动态排序List
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum,
                                                                int pageSize, String orderBy){
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorMessageCode(ResponseCode.ILLEGAL_ARGUMENT.getDesc(), ResponseCode.ILLEGAL_ARGUMENT.getCode());
        }

        List<Category> categoryList = new ArrayList<>();
        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                //没有该分类,并且还没有关键字,这个时候返回一个空的结果集,不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        List<Integer> categoryIdList = new ArrayList<>();
        for(Category category : categoryList){
            categoryIdList.add(category.getId());
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ?
                null : keyword, categoryList.size() == 0 ? null : categoryIdList);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }
    /**
     * 组装商品详情VO
     * @param product
     * @return
     */
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //读取配置文件
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);//默认根节点
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    /**
     * 组装商品listVO
     * @param product
     * @return
     */
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }
}
