package com.zaki.usercenter.mapper;

import com.zaki.usercenter.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 13277
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2024-03-20 14:36:30
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




