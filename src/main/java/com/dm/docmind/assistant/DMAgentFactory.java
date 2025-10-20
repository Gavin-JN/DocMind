package com.dm.docmind.assistant;

import com.dm.docmind.config.ContentRetrieverFactory;
import dev.langchain4j.community.model.dashscope.QwenStreamingChatModel;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DMAgentFactory {

    @Resource
    private QwenStreamingChatModel qwenStreamingChatModel;

    @Autowired
    private EmbeddingStore embeddingStore;

    @Autowired
    private ChatMemoryProvider chatMemoryProvider;

    @Autowired
    private ContentRetrieverFactory contentRetrieverFactory;

    private ContentRetriever contentRetriever;

    public DMAgent createAgent(String userId) {

        contentRetriever = contentRetrieverFactory.getContentRetriever(userId);

        return AiServices.builder(DMAgent.class)
                .streamingChatModel(qwenStreamingChatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .contentRetriever(contentRetriever)
                .build();
    }


}
