package com.dm.docmind.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dm.docmind.entity.Knowledge;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgeMapper extends BaseMapper<Knowledge> {
}
