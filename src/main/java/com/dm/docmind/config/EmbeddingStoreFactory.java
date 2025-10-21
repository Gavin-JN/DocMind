package com.dm.docmind.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pinecone.PineconeEmbeddingStore;
import dev.langchain4j.store.embedding.pinecone.PineconeServerlessIndexConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmbeddingStoreFactory {

    @Autowired
    private EmbeddingModel embeddingModel;

    /**
     * 根据用户ID创建对应的 EmbeddingStore（对应不同 namespace）
     */



    public EmbeddingStore<TextSegment> createForUser(String userId) {



        return PineconeEmbeddingStore.builder()
                .apiKey(System.getenv("PINECONE_KEY"))
                .index("dmmind-index") // ✅ 所有用户共用一个 Pinecone Index
                .nameSpace("user_" + userId) // ✅ 每个用户一个命名空间
                .createIndex(PineconeServerlessIndexConfig.builder()
                        .cloud("AWS")
                        .region("us-east-1")
                        .dimension(embeddingModel.dimension())
                        .build())
                .build();
    }
}
