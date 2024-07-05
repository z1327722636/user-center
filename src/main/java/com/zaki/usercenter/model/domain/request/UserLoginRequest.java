package com.zaki.usercenter.model.domain.request;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -1893380277912227716L;
    private String userAccount;
    private String userPassword;
}
