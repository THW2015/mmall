package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * Created by 谭皓文 on 2018/8/12.
 */
@Controller
@RequestMapping("/manager/user/")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse response = iUserService.login(username,password);
        if (response.isSuccess()){
            User user = (User) response.getData();
            if(user.getRole() == Const.Role.Role_ADMIN){
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            }else{
                return ServerResponse.createByErrorMessage("权限不够");
            }
        }
        return response;
    }

}
