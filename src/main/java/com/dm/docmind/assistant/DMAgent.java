package com.dm.docmind.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface DMAgent {

    @SystemMessage(fromResource = "prompt-template.txt")
    Flux<String> chat(@MemoryId Long memoryId, @UserMessage String userMessage);

    @SystemMessage(fromResource = "prompt-template-online.txt")
    Flux<String> onlineChat(@MemoryId Long memoryId, @UserMessage String userMessage);

}
