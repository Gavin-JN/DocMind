package com.dm.docmind.controller;


import com.dm.docmind.assistant.DMAgentFactory;
import com.dm.docmind.commonResponse.CommonResponse;
import com.dm.docmind.context.GlobalUserContext;
import com.dm.docmind.embedding.UploadText;
import com.dm.docmind.entity.ChatForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/dm")

public class DMController {

    @Autowired
    private DMAgentFactory factory;

    @Autowired
    private UploadText uploadText;


    @PostMapping("/loadFile")
    public CommonResponse<Object> loadFile(@RequestParam MultipartFile file) {
        try {
            String userId = GlobalUserContext.getUserId();
            uploadText.UploadKnowledgeLibraryByFile(file, userId);
            return CommonResponse.createForSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.createForError(e.getMessage());
        }
    }

    @PostMapping("/loadPath")
    public CommonResponse<Object> loadPath(@RequestParam String path) {
        try {
            String userId = GlobalUserContext.getUserId();
            uploadText.UploadKnowledgeLibraryByPath(path, userId);
            return CommonResponse.createForSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.createForError(e.getMessage());
        }
    }

    @PostMapping("/loadText")
    public CommonResponse<Object> loadText(@RequestParam String text) {
        try {
            String userId = GlobalUserContext.getUserId();
            uploadText.UploadKnowledgeLibraryByText(text, userId);
            return CommonResponse.createForSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.createForError(e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public CommonResponse<Object> clear() {
        try {
            String userId = GlobalUserContext.getUserId();
            uploadText.clealKnowledgeLibrary(userId);
            return CommonResponse.createForSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.createForError(e.getMessage());
        }
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody ChatForm chatForm) {
        String userId = GlobalUserContext.getUserId();
        return factory.createAgent(userId).chat(chatForm.getMemoryId(), chatForm.getMessage());
    }

    @PostMapping(value = "/online_chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> onlineChat(@RequestBody ChatForm chatForm) {
        String userId = GlobalUserContext.getUserId();
        return factory.createAgent(userId).onlineChat(chatForm.getMemoryId(), chatForm.getMessage());
    }
}
