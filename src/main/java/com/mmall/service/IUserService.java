package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by lucky on 2019/1/6.
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    ServerResponse<String> forgetResetPassword(String username,String question,String answer);

    ServerResponse<String> resetPassword(User user,String passwordNew,String passwordOld);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer id);
}
