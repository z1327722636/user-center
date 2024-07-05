package com.zaki.usercenter.model.domain.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author zaki
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -8869680004348050973L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
