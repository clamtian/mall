package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    User selectLogin(@Param("username") String username,@Param("password") String password);

    int checkEmail(String email);

    int checkAnswer(String username,String question,String answer);

    int updatePasswordByUsername(String username,String MD5password);

    String selectQuestionByUsername(@Param("username") String username);

    int checkPassword(Integer id,String password);

    int checkEmailByUserId(String email,Integer id);
}