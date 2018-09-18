package com.mmall.controller.backend;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by 谭皓文 on 2018/8/13.
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "addCategory.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest request, Category category,
                                      @RequestParam(value = "parent",defaultValue = "0") int parentId){
//        String loginToken = CookieUtil.readLoginToken(request);
//        if (StringUtils.isEmpty(loginToken)) {
//            return ServerResponse.createByErrorMessage("用户未登录,无法获取用户信息");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userJsonStr,User.class);
//        if(user == null){
//            return ServerResponse.createByErrorMessage("用户未登录");
//        }
//        //this user is admin?
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            //add
            return iCategoryService.addCategory(category.getName(),parentId);
//        }else{
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }
    }
    @RequestMapping(value = "setCategoryName.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(HttpServletRequest request,Integer categoryId,
                                          String categoryName){
//        String loginToken = CookieUtil.readLoginToken(request);
//        if (StringUtils.isEmpty(loginToken)) {
//            return ServerResponse.createByErrorMessage("用户未登录,无法获取用户信息");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User currentUser = JsonUtil.string2Obj(userJsonStr,User.class);
//        if(currentUser == null){
//            return ServerResponse.createByErrorMessage("用户未登录");
//        }
//        if(iUserService.checkAdminRole(currentUser).isSuccess()){
            //update
            return iCategoryService.updateCategoryName(categoryId,categoryName);
//        }else{
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }
    }
    @RequestMapping(value = "getChildrenParallelCategory.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpServletRequest request,
                                                      @RequestParam(value = "categoryId",defaultValue = "0")
                                                      Integer categoryId){
//        String loginToken = CookieUtil.readLoginToken(request);
//        if (StringUtils.isEmpty(loginToken)) {
//            return ServerResponse.createByErrorMessage("用户未登录,无法获取用户信息");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User currentUser = JsonUtil.string2Obj(userJsonStr,User.class);
//        if(currentUser == null){
//            return ServerResponse.createByErrorMessage("用户未登录");
//        }
//        if(iUserService.checkAdminRole(currentUser).isSuccess()){
            //update
            return iCategoryService.getChildrenParalleCategory(categoryId);
//        }else{
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }
    }
    @RequestMapping(value = "getCategoryAndDeepChildrenCategory.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(HttpServletRequest request,
                                                                   @RequestParam(value = "categoryId",defaultValue = "0")
                                                      Integer categoryId){
//        String loginToken = CookieUtil.readLoginToken(request);
//        if (StringUtils.isEmpty(loginToken)) {
//            return ServerResponse.createByErrorMessage("用户未登录,无法获取用户信息");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User currentUser = JsonUtil.string2Obj(userJsonStr,User.class);
//        if(currentUser == null){
//            return ServerResponse.createByErrorMessage("用户未登录");
//        }
//        if(iUserService.checkAdminRole(currentUser).isSuccess()){
//            //update
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
//        }else{
//            return ServerResponse.createByErrorMessage("无权限操作");
//        }
    }

}
