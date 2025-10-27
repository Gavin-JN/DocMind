package com.dm.docmind.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
@TableName("knowledge")
public class Knowledge {
    @TableField("userId")
    String userId;
    @TableField("knowledgeId")
    String knowledgeId;
    @TableField("knowledgeName")
    String knowledgeName;
    @TableField("sequence")
    int sequence;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(String knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public String getKnowledgeName() {
        return knowledgeName;
    }

    public void setKnowledgeName(String knowledgeName) {
        this.knowledgeName = knowledgeName;
    }

    public int getIndex() {
        return sequence;
    }

    public void setIndex(int sequence) {
        this.sequence = sequence;
    }
}
