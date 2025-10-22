package com.dm.docmind.service;

import com.dm.docmind.commonResponse.CommonResponse;

public interface ChatMemoryService {

    public CommonResponse<Object> clear(long messageId);

}