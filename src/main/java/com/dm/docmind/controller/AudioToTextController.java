package com.dm.docmind.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dm.docmind.commonResponse.CommonResponse;
//import com.dm.docmind.context.GlobalUserContext;
import com.dm.docmind.embedding.UploadText;
import com.dm.docmind.tool.OssUploader;
import com.dm.docmind.tool.TingwuTranscriptionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@RequestMapping("/toText")
@RestController
public class AudioToTextController {

    @Autowired
    private UploadText uploadText;

    @PostMapping("/upload")
    public CommonResponse<Object> uploadAudio(@RequestParam MultipartFile file,HttpSession session) {
        try {
            // 保存上传的音频文件到临时目录
            String fileName = file.getOriginalFilename();
            String tempDir = System.getProperty("java.io.tmpdir");
            String filePath = tempDir + "/" + fileName;

            file.transferTo(new java.io.File(filePath));

            // 调用转文字功能
            return toText(filePath, 2,session);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResponse.createForError("音频上传失败: " + e.getMessage());
        }
    }

    @RequestMapping("/transform")
    public CommonResponse<Object> toText(@RequestParam String filePath, @RequestParam int people, HttpSession session) throws Exception {
        String fileUrl = OssUploader.uploadFile(
                "oss-cn-beijing.aliyuncs.com",
                System.getenv("TINGWU_ACCESS_KEY_ID"), // AccessKeyId
                System.getenv("TINGWU_ACCESS_KEY_SECRET"), // AccessKeySecret
                "my-bucket",
                "audio/test.mp3",
                filePath);
        String taskId = TingwuTranscriptionUtil.transcribe(fileUrl, 2);
        System.out.println("任务已提交，TaskId = " + taskId);

        // 3. 轮询任务状态（最多 5 分钟）
        int maxAttempts = 100;
        int attempt = 0;
        while (attempt++ < maxAttempts) {
            Thread.sleep(3000);
            String statusJson = TingwuTranscriptionUtil.queryTask(taskId);
            JSONObject statusObj = JSON.parseObject(statusJson);
            String status = statusObj.getJSONObject("Data").getString("TaskStatus");
            System.out.println("当前任务状态: " + status);

            if ("SUCCESS".equals(status) || "COMPLETED".equals(status)) {
                // 在轮询到 COMPLETED 状态后
                String transcriptionUrl = statusObj
                        .getJSONObject("Data")
                        .getJSONObject("Result")
                        .getString("Transcription")
                        .trim();

                String fullText = TingwuTranscriptionUtil.extractTranscriptionText(transcriptionUrl);
                System.out.println("✅ 转写结果：\n" + fullText);

                try {
                    String userId =session.getAttribute("userId").toString();
                    uploadText.UploadKnowledgeLibraryByText(fullText, userId);
                    return CommonResponse.createForSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    return CommonResponse.createForError(e.getMessage());
                }
            } else if ("FAILED".equals(status)) {
                System.err.println("❌ 任务失败，详情: " + statusJson);
                return CommonResponse.createForError("转写任务失败" + statusJson);
            } else {
                System.out.println("任务正在进行中...");
            }
        }
        return CommonResponse.createForError("任务超时（超过 5 分钟未完成）");
    }

}
