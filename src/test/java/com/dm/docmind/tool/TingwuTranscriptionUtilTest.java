package com.dm.docmind.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import static org.junit.jupiter.api.Assertions.fail;

class TingwuTranscriptionUtilTest {

    @Test
    void test() throws Exception {
        String fileUrl = OssUploader.uploadFile(
                "oss-cn-beijing.aliyuncs.com",
                System.getenv("TINGWU_ACCESS_KEY_ID"),     // AccessKeyId
                System.getenv("TINGWU_ACCESS_KEY_SECRET"), // AccessKeySecret
                "my-bucket",
                "audio/test.mp3",
                "D:\\te.m4a"
        );

        // 2. 提交转写任务
//        String result = TingwuTranscriptionUtil.transcribe(fileUrl, 2);
//        JSONObject json = JSON.parseObject(result);
//        String taskId = json.getJSONObject("Data").getString("TaskId");
        String taskId=TingwuTranscriptionUtil.transcribe(fileUrl, 2);;
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
                return;
            } else if ("FAILED".equals(status)) {
                System.err.println("❌ 任务失败，详情: " + statusJson);
                fail("转写任务失败");
            } else {
                System.out.println("任务正在进行中...");
            }
        }

        fail("任务超时（超过 5 分钟未完成）");
    }
}