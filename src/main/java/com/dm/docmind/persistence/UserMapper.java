package com.dm.docmind.persistence;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dm.docmind.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {
}
