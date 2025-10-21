package com.dm.docmind.controller;


import com.dm.docmind.assistant.DMAgentFactory;
import com.dm.docmind.commonResponse.CommonResponse;
//import com.dm.docmind.context.GlobalUserContext;
import com.dm.docmind.embedding.UploadText;
import com.dm.docmind.entity.ChatForm;
import com.dm.docmind.mongo.MongoChatMemoryStore;
import jakarta.servlet.http.HttpSession;
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

    @Autowired
    private MongoChatMemoryStore chatMemoryStore;


    @PostMapping("/loadFile")
    public CommonResponse<Object> loadFile(@RequestParam MultipartFile file, HttpSession session) {
        try {
            String userId = session.getAttribute("userId").toString();
            uploadText.UploadKnowledgeLibraryByFile(file, userId);
            return CommonResponse.createForSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.createForError(e.getMessage());
        }
    }

    @PostMapping("/loadPath")
    public CommonResponse<Object> loadPath(@RequestParam String path,HttpSession session) {
        try {
            String userId = session.getAttribute("userId").toString();
            uploadText.UploadKnowledgeLibraryByPath(path, userId);
            return CommonResponse.createForSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.createForError(e.getMessage());
        }
    }

    @PostMapping("/loadText")
    public CommonResponse<Object> loadText(@RequestParam String text,HttpSession session) {
        try {
            String userId = session.getAttribute("userId").toString();
            uploadText.UploadKnowledgeLibraryByText(text, userId);
            return CommonResponse.createForSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.createForError(e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public CommonResponse<Object> clear(HttpSession session) {
        try {
            String userId = session.getAttribute("userId").toString();
            uploadText.clealKnowledgeLibrary(userId);
            return CommonResponse.createForSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.createForError(e.getMessage());
        }
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody ChatForm chatForm,HttpSession session) {
        String userId =session.getAttribute("userId").toString();
        System.out.println(userId);
        return factory.createAgent(userId).chat(chatForm.getMemoryId(), chatForm.getMessage());
    }

    @PostMapping(value = "/online_chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> onlineChat(@RequestBody ChatForm chatForm,HttpSession session) {
        String userId = session.getAttribute("userId").toString();
        return factory.createAgent(userId).onlineChat(chatForm.getMemoryId(), chatForm.getMessage());
    }

}
