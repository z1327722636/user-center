package com.zaki.usercenter.service;

import com.zaki.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import static com.zaki.usercenter.constant.UserConstant.USER_LOGIN_STATE;
import static com.zaki.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 13277
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2024-03-20 14:36:30
 */
@Service
public interface UserService extends IService<User> {
    /**
     * 用户注释
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 用户确认密码
     * @return 新用户的 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);
}

