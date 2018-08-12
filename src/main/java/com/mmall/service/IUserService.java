package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

import java.util.concurrent.ExecutionException;

/**
 * Created by 谭皓文 on 2018/8/10.
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password);


    ServerResponse<String> register(User user);

    ServerResponse<String> checkVaild(String str, String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username,String passwordNew,
                                               String forgetToken)throws ExecutionException;

    ServerResponse resetPassword(String passwordOld,String passwordNew,User user);

    ServerResponse updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);
}
