package com.dm.docmind.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentRetrieverFactory {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Autowired
    private EmbeddingStoreFactory embeddingStoreFactory;

    public ContentRetriever getContentRetriever(String userId) {
        //创建一个 EmbeddingStoreContentRetriever 对象，用于从嵌入存储中检索内容
        return EmbeddingStoreContentRetriever
                .builder()
                //设置用于生成嵌入向量的嵌入模型
                .embeddingModel(embeddingModel)
                //指定要使用的嵌入存储
                .embeddingStore(embeddingStoreFactory.createForUser(userId))
                //设置最大检索结果数量，这里表示最多返回10条匹配结果
                .maxResults(10)
                //设置最小得分阈值，只有得分大于等于0.8的结果才返回
                .minScore(0.8)
                //构件最终的 EmbeddingStoreContentRetriever
                .build();
    }
}
