package com.dm.docmind.controller;

import com.dm.docmind.commonResponse.CommonResponse;
import com.dm.docmind.service.ChatMemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author mao19
 */
@CrossOrigin(origins = "*")
@RequestMapping("/chatMemory")
@RestController
public class ChatMemoryController {

    @Autowired
    private ChatMemoryService chatMemoryService;

    @DeleteMapping("/clear")
    public CommonResponse<Object> clear(@RequestParam long memoryId) {
        return chatMemoryService.clear(memoryId);
    }

}
