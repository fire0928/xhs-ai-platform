package com.hongshu.modules.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hongshu.modules.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT COUNT(*) FROM user WHERE deleted = 0 AND status = 1")
    long countActiveUsers();

    /** 今日活跃用户数：今天注册的用户 + 今天调用过AI的用户 */
    @Select("SELECT COUNT(DISTINCT u.id) FROM user u " +
            "LEFT JOIN api_call_log acl ON u.id = acl.user_id AND DATE(acl.create_time) = CURDATE() " +
            "WHERE u.deleted = 0 AND u.status = 1 " +
            "AND (DATE(u.register_time) = CURDATE() OR acl.id IS NOT NULL)")
    long countDau();
}
