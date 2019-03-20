package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by lucky on 2019/1/6.
 */

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;
    /**
     * 用户登录模块
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(HttpServletRequest request, HttpServletResponse httpResponse, String username, String password, HttpSession session){
        ServerResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            CookieUtil.writeLoginToken(httpResponse, session.getId());
            String s = CookieUtil.readCookie(request);
            System.out.println(s);
            session.setAttribute(Const.CURRENT_USER, response.getData());
            RedisPoolUtil.set("SESSION:" + session.getId() , username);
            RedisPoolUtil.expire("SESSION:" + session.getId(), Integer.parseInt(PropertiesUtil.getProperty("redisexpire")));
        }
        return response;
    }

    /**
     * 用户退出登录模块
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccessMessage(ResponseCode.SUCCESS.getDesc());
    }

    /**
     * 用户注册模块
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){//Mvc数据绑定，将前台信息包装为User传入
        ServerResponse response = iUserService.register(user);
        return response;
    }

    /**
     * 获取用户信息模块
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request, HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);//获得当前登录用户
        String s = CookieUtil.readCookie(request);
        System.out.println(s);
        if(user != null){//将User作为Data传回前台
            return ServerResponse.createBySuccess(user);
        }

        return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getDesc());
    }

    /**
     * 忘记密码
     * 输入用户名获得密保问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
        ServerResponse<String> response = iUserService.selectQuestion(username);
        return response;
    }

    /**
     * 验证密保答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer){
        ServerResponse<String> response = iUserService.checkAnswer(username, question, answer);
        return response;
    }

    /**
     * 密保答案验证成功后重设密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username ,String passwordNew, String forgetToken){
        ServerResponse<String> response = iUserService.forgetResetPassword(username, passwordNew, forgetToken);
        return response;
    }

    /**
     * 重设密码
     * @param session
     * @param passwordNew
     * @param passwordOld
     * @return
     */
    @RequestMapping(value = "reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session,String passwordNew,String passwordOld){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){//判断用户是否登录
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<String> response = iUserService.resetPassword(user, passwordNew, passwordOld);
        return response;
    }

    /**
     * 更新个人信息
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session,User user){
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getDesc());
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
}
