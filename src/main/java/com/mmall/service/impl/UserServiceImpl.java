package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Created by lucky on 2019/1/6.
 * 用户服务层
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if(resultCount == 0){//用户名不存在
            return ServerResponse.createByErrorMessageCode(ResponseCode.NO_USER.getDesc(), ResponseCode.NO_USER.getCode());
        }
        User user = userMapper.selectLogin(username, MD5Util.MD5EncodeUtf8(password));
        if(user == null){//密码错误
            return ServerResponse.createByErrorMessageCode(ResponseCode.PASSWORD_ERROR.getDesc(), ResponseCode.PASSWORD_ERROR.getCode());
        }
        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);//将密码置空，防止密码发送到前台页面
        return ServerResponse.createBySuccess(ResponseCode.SUCCESS.getDesc(), user);
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> register(User user) {//Mvc数据绑定，将前台信息包装为User传入
        ServerResponse validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if(!validResponse.isSuccess()){//用户名校验失败
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){//邮箱校验失败
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);//设置用户角色为前台用户
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage(ResponseCode.ERROR.getDesc());
        }
        return ServerResponse.createBySuccessMessage(ResponseCode.SUCCESS.getDesc());
    }

    /**
     * 通过用户名取得密保问题
     * @param username
     * @return
     */
    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse response = this.checkValid(username, Const.USERNAME);
        if(response.isSuccess()){
            return ServerResponse.createByErrorMessage(ResponseCode.NO_USER.getDesc());
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(question == null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return ServerResponse.createBySuccessMessage(question);
    }

    /**
     * 验证密保答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount > 0){
            //说明问题及问题答案是这个用户的,并且是正确的
            String forgetToken = UUID.randomUUID().toString();//生成随机Token码
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);//返回Token码至浏览器
        }
        return ServerResponse.createByErrorMessage(ResponseCode.ANSWER_ERROR.getDesc());
    }

    /**
     * 重设密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){//Token为空时
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        ServerResponse response = this.checkValid(username, Const.USERNAME);
        if(response.isSuccess()){
            return ServerResponse.createByErrorMessage(ResponseCode.NO_USER.getDesc());
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage(ResponseCode.OVERTIME.getDesc());
        }
        if(StringUtils.equals(forgetToken,token)){
            String MD5password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, MD5password);
            if(rowCount > 0){
                return ServerResponse.createBySuccessMessage(ResponseCode.SUCCESS.getDesc());
            }else{
                return ServerResponse.createByErrorMessage(ResponseCode.ERROR.getDesc());
            }
        }else{
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
    }

    /**
     * 重设密码
     * @param user
     * @param passwordNew
     * @param passwordOld
     * @return
     */
    @Override
    public ServerResponse<String> resetPassword(User user, String passwordNew, String passwordOld) {
        int checkCount = userMapper.checkPassword(user.getId(), MD5Util.MD5EncodeUtf8(passwordOld));
        if(checkCount == 0){
            return ServerResponse.createByErrorMessage(ResponseCode.PASSWORD_ERROR.getDesc());
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount == 0){
            return ServerResponse.createByErrorMessage(ResponseCode.ERROR.getDesc());
        }
        return ServerResponse.createBySuccessMessage(ResponseCode.SUCCESS.getDesc());
    }

    /**
     * 更新个人信息
     * @param user
     * @return
     */
    @Override
    public ServerResponse<User> updateInformation(User user) {
        int checkCount = userMapper.checkEmail(user.getEmail());
        if(checkCount > 0){
            return ServerResponse.createByErrorMessage(ResponseCode.EMILE_EXIST.getDesc());
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            return ServerResponse.createBySuccess(ResponseCode.SUCCESS.getDesc(), updateUser);
        }
        return ServerResponse.createByErrorMessage(ResponseCode.ERROR.getDesc());
    }

    /**
     * 判断当前登录用户是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getDesc());
        }else if(!user.getRole().equals(Const.Role.ROLE_ADMIN)){
            return ServerResponse.createByErrorMessage(ResponseCode.NO_RIGHT.getDesc());
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 校验用户名和邮箱是否存在
     * @param str
     * @param type
     * @return
     */
    private ServerResponse<String> checkValid(String str, String type) {
        if(StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage(ResponseCode.USER_EXIST.getDesc());
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage(ResponseCode.EMILE_EXIST.getDesc());
                }
            }
        }else{
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        return ServerResponse.createBySuccess();
    }
}
