package com.zaki.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zaki.usercenter.common.BaseResponse;
import com.zaki.usercenter.common.ErrorCode;
import com.zaki.usercenter.common.ResultUtils;
import com.zaki.usercenter.exception.BusinessException;
import com.zaki.usercenter.model.domain.User;
import com.zaki.usercenter.model.domain.request.UserLoginRequest;
import com.zaki.usercenter.model.domain.request.UserRegisterRequest;
import com.zaki.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.zaki.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.zaki.usercenter.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户接口
 *
 * @anthor zaki
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
//            throw new BusinessException(ErrorCode.NULL_ERROR)
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        //基础校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        //基础校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);

    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(userService.userLogout(request));
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        return ResultUtils.success(userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList()));
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //为逻辑删除
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObjecct = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObjecct;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        //TODO 优化：如果后面添加封号，用户就不可校验用户是否合法
        //查数据库
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    private boolean isAdmin(HttpServletRequest request) {
        //仅管理员可查询
        Object userObjecct = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObjecct;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}

