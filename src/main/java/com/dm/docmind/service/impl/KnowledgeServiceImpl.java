package com.dm.docmind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dm.docmind.entity.Knowledge;
import com.dm.docmind.persistence.KnowledgeMapper;
import com.dm.docmind.service.KnowledgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mao19
 */
@Service
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    private KnowledgeMapper knowledgeMapper;

    @Override
    public boolean addKnowledge(Knowledge knowledge) {
        return knowledgeMapper.insert(knowledge) > 0;
    }

    @Override
    public List<Knowledge> getAllKonwledegsByUserId(String userId) {
        QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<Knowledge> knowledges=knowledgeMapper.selectList(queryWrapper);
        return knowledges;
    }

    @Override
    public boolean removeKnowledgeById(String knowledgeId) {
        QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("knowledgeId", knowledgeId);
        return knowledgeMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean removeKnowledgeByUserId(String userId) {
        QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        return knowledgeMapper.delete(queryWrapper) > 0;

    }

    @Override
    public Knowledge getKnowledgeById(String userId, String knowledgeId) {
        QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("knowledgeId", knowledgeId);
        Knowledge knowledge=knowledgeMapper.selectOne(queryWrapper);
        return knowledge;
    }

    @Override
    public boolean removeKnowledgeByUserIdAndKnowledgeName(String userId, String knowledgeName) {
        QueryWrapper<Knowledge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("knowledgeName", knowledgeName);
        return knowledgeMapper.delete(queryWrapper)>0;
    }


}
