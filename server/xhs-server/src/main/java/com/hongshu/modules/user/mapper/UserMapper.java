package com.hongshu.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hongshu.modules.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT COUNT(*) FROM user WHERE deleted = 0 AND status = 1")
    long countActiveUsers();
}
