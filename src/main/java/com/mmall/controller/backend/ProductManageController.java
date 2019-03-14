package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by lucky on 2019/2/21.
 */

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     * 新增或更新产品
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        ServerResponse response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return response;
        }
        return iProductService.saveOrUpdateProduct(product);
    }

    /**
     * 更新产品上下架状态
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("set_product_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        ServerResponse response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return response;
        }
        return iProductService.setSaleStatus(productId, status);
    }

    /**
     * 获取商品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        ServerResponse response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return response;
        }
        return iProductService.getProductDetail(productId);
    }

    /**
     * 后台商品列表动态分页
     * @param session
     * @param pageNum  第几页
     * @param pageSize 页面容量
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session,
                                  @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        ServerResponse response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return response;
        }
        return iProductService.getProductList(pageNum, pageSize);
    }

    /**
     * 商品搜索
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,String productName,Integer productId,
                                        @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        ServerResponse response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return response;
        }
        return iProductService.searchProduct(productName, productId, pageNum, pageSize);
    }

    /**
     * SpringMVC文件上传
     * @param session
     * @param files
     * @param request
     * @return
     */
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file",required = false) MultipartFile[] files, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        ServerResponse response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return response;
        }
        /**
         * 查找upload的绝对路径
         * 在Tomcat的webapps下，有则加载，无则创建
         * upload用于暂时存储上传的文件
         * 上传完成后会删除
         */
        String path = request.getSession().getServletContext().getRealPath("upload");
        HashSet<String> set = iFileService.upload(files, path);
        Map fileMap = Maps.newHashMap();
        for(String targetFileName : set){
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            fileMap.put(targetFileName,url);
        }

        return ServerResponse.createBySuccess(fileMap);
    }
}
