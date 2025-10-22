package com.dm.docmind.service.impl;

import com.dm.docmind.commonResponse.CommonResponse;
import com.dm.docmind.mongo.MongoChatMemoryStore;
import com.dm.docmind.service.ChatMemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatMemoryServiceImpl implements ChatMemoryService {

    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;

    @Override
    public CommonResponse<Object> clear(long messageId) {
        mongoChatMemoryStore.deleteMessages(messageId);
        return CommonResponse.createForSuccess();
    }
}
