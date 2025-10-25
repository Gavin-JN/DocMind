package com.dm.docmind.embedding;

import com.dm.docmind.config.EmbeddingStoreFactory;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class KnowledgeManage {

    @Autowired
    private EmbeddingStoreFactory embeddingStoreFactory;

    public void removeKnowledgeByIds(List<String> ids,String userId){
        EmbeddingStore<TextSegment> embeddingStore=embeddingStoreFactory.createForUser(userId);
        for(String id:ids){
            embeddingStore.remove(id);
        }
    }

}
