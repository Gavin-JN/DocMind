package com.dm.docmind.service;

import com.dm.docmind.entity.Knowledge;

import java.util.List;

public interface KnowledgeService {

    public boolean addKnowledge(Knowledge knowledge);

    public List<Knowledge> getAllKonwledegsByUserId(String userId);

    public boolean removeKnowledgeById(String knowledgeId);

    public boolean removeKnowledgeByUserId(String userId);

    public Knowledge getKnowledgeById(String userId,String knowledgeId);

    public boolean removeKnowledgeByUserIdAndKnowledgeName(String userId, String knowledgeName);

}
