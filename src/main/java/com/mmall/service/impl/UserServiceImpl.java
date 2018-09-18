package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * Created by 谭皓文 on 2018/8/10.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int result = userMapper.checkUsername(username);
        if(result == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //todo 密码登陆md5
        User user = userMapper.selectLogin(username,MD5Util.MD5EncodeUtf8(password));

        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
//        int resultCount = userMapper.checkUsername(user.getUsername());
//        if(resultCount > 0){
//            return ServerResponse.createByErrorMessage("用户名已经存在");
//        }

        ServerResponse validResponse = this.checkVaild(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkVaild(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){

            return validResponse;
        }
//        resultCount = userMapper.checkEmail(user.getEmail());
//        if(resultCount > 0){
//
//            return  ServerResponse.createByErrorMessage("邮箱已经存在");
//        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        //md5
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }

        return ServerResponse.createByErrorMessage("注册成功");


    }

    @Override
    public ServerResponse<String> checkVaild(String str, String type) {
        if(StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已经存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){

                    return  ServerResponse.createByErrorMessage("邮箱已经存在");
                }
            }
        }else {
            return  ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createByErrorMessage("检验成功");
    }

    public ServerResponse selectQuestion(String username){
        ServerResponse vaildResponse = this.checkVaild(username,Const.USERNAME);
        if(vaildResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    public  ServerResponse<String> checkAnswer(String username,String question,String answer){
        int result = userMapper.checkAnswer(username,question,answer);
        if(result > 0){
            String forgetToken = UUID.randomUUID().toString();
//            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            RedisShardedPoolUtil.setEx(TokenCache.TOKEN_PREFIX+username,Const.RedisCacheExtime.REDIS_SESSION_EXTIME,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return  ServerResponse.createByErrorMessage("问题答案错误");
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken) throws ExecutionException {
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误,token需要传递");
        }
        ServerResponse validResponse = this.checkVaild(Const.USERNAME,username);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名为空");
        }
//        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        String token = RedisShardedPoolUtil.get(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token过期");
        }
        if(StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username,passwordNew);
            if(resultCount > 0){
                return ServerResponse.createBySuccess("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误请重新匹配");
        }
        return  ServerResponse.createByErrorMessage("修改失败");

    }

    public ServerResponse resetPassword(String passwordOld,String passwordNew,User user){
        //检查用户和密码是否匹配
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(passwordNew);
        int UpdateCount = userMapper.updateByPrimaryKeySelective(user);
        if(UpdateCount > 0){
            return ServerResponse.createBySuccess("重置密码成功");
        }
        return ServerResponse.createByErrorMessage("重置失败");
    }
    public ServerResponse updateInformation(User user){
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已经存在，请重新输入");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0){
            return ServerResponse.createBySuccess("更新用户信息成功");
        }
        return ServerResponse.createByErrorMessage("更新用户失败");
    }

    public  ServerResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /**
     *校验用户是否为管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

}
