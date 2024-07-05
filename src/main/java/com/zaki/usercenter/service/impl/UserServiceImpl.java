package com.zaki.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zaki.usercenter.common.ErrorCode;
import com.zaki.usercenter.common.ResultUtils;
import com.zaki.usercenter.exception.BusinessException;
import com.zaki.usercenter.model.domain.User;
import com.zaki.usercenter.service.UserService;
import com.zaki.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zaki.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 13277
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-03-20 14:36:30
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;
    /**
     * 盐值 混淆密码
     */
    private static String SALT = "zaki";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度不能小于4");
        }
        if (userPassword.length() < 8 && checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8");
        }
        //账户不能包含特殊字符
        String vailPattern = "[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]";
        Matcher matcher = Pattern.compile(vailPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能包含特殊字符");
        }
        //密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码不相同");
        }
        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
//        long count = this.count(queryWrapper);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户已经存在，不用重复注册");
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"账户不能重复");
        }

        //加密
        String newPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        //插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(newPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();

    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户和密码不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户长度不能小于4");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于8");
        }
        //账户不能包含特殊字符
        String vailPattern = "[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]";
        Matcher matcher = Pattern.compile(vailPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户不能包含特殊字符");
        }

        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        //查询用户是否存在（用mybatis-plus的方法查询）
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();//构建查询语句的条件
//        queryWrapper.eq("userAccount", userAccount);
//        queryWrapper.eq("userPassword", encryptPassword);
//        User user = userMapper.selectOne(queryWrapper);//通过mapper来查询
//        if (user == null) {
//            log.info("user login failed, userAccount cannot match userPassword");
//            throw new BusinessException(ErrorCode.NOT_LOGIN,"没有注册过");
//        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
// 先查用户是否存在
        queryWrapper.eq("userAccount", userAccount);
        User user = userMapper.selectOne(queryWrapper);

// 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount does not exist");
            throw new BusinessException(ErrorCode.NOT_LOGIN, "用户不存在");
        }

// 用户存在，再查密码是否正确
        if (!encryptPassword.equals(user.getUserPassword())) {
            log.info("user login failed, incorrect password for userAccount");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }

        // 3.用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;

    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        User safetyUser = new User();
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




